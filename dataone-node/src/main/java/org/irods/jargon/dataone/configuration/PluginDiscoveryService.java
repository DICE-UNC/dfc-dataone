/**
 * 
 */
package org.irods.jargon.dataone.configuration;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.events.DataOneEventServiceAO;
import org.irods.jargon.dataone.events.DataOneEventServiceFactory;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoFactory;
import org.irods.jargon.dataone.reposervice.DataOneRepoServiceAO;
import org.irods.jargon.dataone.reposervice.DataOneRepoServiceFactory;
import org.irods.jargon.dataone.utils.PropertiesLoader;
import org.irods.jargon.pid.pidservice.AbstractDataOnePidFactory;
import org.irods.jargon.pid.pidservice.DataOnePidServiceFactory;
import org.irods.jargon.pid.pidservice.UniqueIdAO;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.context.DefaultContextLoader;
import org.xeustechnologies.jcl.context.JclContext;

/**
 * Service to locate and surface provided publisher plug-ins. Handles all the
 * classloader and reflection tasks.
 * 
 * @author Mike Conway - DICE
 *
 */
@Component
public class PluginDiscoveryService {

	private JarClassLoader jcl;
	private List<URL> urls = new ArrayList<>();
	@Autowired
	private PublicationContext publicationContext;

	private DataOneEventServiceFactory dataOneEventServiceFactory;
	private DataOnePidServiceFactory dataOnePidServiceFactory;
	private DataOneRepoServiceFactory dataOneRepoServiceFactory;

	public static final Logger log = LoggerFactory.getLogger(PluginDiscoveryService.class);

	public PluginDiscoveryService() {
	}

	public DataOneEventServiceAO instanceEventService(IRODSAccount irodsAccount) throws PluginNotFoundException {
		log.info("instanceEventService()");
		log.info("dataOneEventServiceFactory:{}", dataOneEventServiceFactory);
		log.info("publicationContext:{}", getPublicationContext());
		log.info("irodsAccount:{}", irodsAccount);
		return dataOneEventServiceFactory.instance(getPublicationContext(), irodsAccount);
	}

	public UniqueIdAO instanceUniqueIdService(IRODSAccount irodsAccount) throws PluginNotFoundException {
		log.info("instancePidService()");
		return dataOnePidServiceFactory.instance(getPublicationContext(), irodsAccount);
	}

	public DataOneRepoServiceAO instanceRepoService(IRODSAccount irodsAccount) throws PluginNotFoundException {
		log.info("instancePidService()");
		return dataOneRepoServiceFactory.instance(getPublicationContext(), irodsAccount);
	}

	@SuppressWarnings("unchecked")
	private final <T> Class<T> loadImplClass(Class<?> clazz) throws PluginNotFoundException {

		/*
		 * see: https://github.com/ronmamo/reflections
		 */

		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setUrls(urls).addClassLoader(JclContext.get()).addScanners(new SubTypesScanner(),
				new TypeAnnotationsScanner());
		Reflections reflections = new Reflections(configurationBuilder);

		Set<?> mechanisms = reflections.getSubTypesOf(clazz);

		// .getTypesAnnotatedWith(clazz,
		// true);
		log.info("mechanisms:{}", mechanisms);

		if (mechanisms.isEmpty()) {
			throw new PluginNotFoundException("no plugin found for class:" + clazz.getName());
		}

		return (Class<T>) mechanisms.iterator().next();

	}

	/**
	 * Initialize publisher based on (required) configuration
	 * 
	 * @throws PluginNotFoundException
	 * 
	 * @throws PublicationException
	 */

	@PostConstruct
	public void init() throws PluginNotFoundException {
		log.info("init()");
		if (publicationContext == null) {
			throw new PluginRuntimeException("init() cannot be called, no provided publicationContext");
		}

		if (publicationContext.getRestConfiguration().getPluginJarLocation() == null
				|| publicationContext.getRestConfiguration().getPluginJarLocation().isEmpty()) {
			throw new PluginRuntimeException("no jar file plugin directory specified");
		}

		log.info("scanning for plugin jars at:{}", publicationContext.getRestConfiguration().getPluginJarLocation());
		loadCandidateClasspaths(publicationContext.getRestConfiguration().getPluginJarLocation());
		initializeFactories();

	}

	/**
	 * Look up and instantiate service factories, part of the startup process
	 */
	private void initializeFactories() throws PluginNotFoundException {
		log.info("initializeFactories()");

		log.info("pid factory...");
		Class<AbstractDataOnePidFactory> clazz = loadImplClass(AbstractDataOnePidFactory.class);
		try {
			Constructor<?> ctor = clazz.getConstructor();
			dataOnePidServiceFactory = (DataOnePidServiceFactory) ctor.newInstance(new Object[] {});
			log.info("dataOnePidServiceFactory success");
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			log.error("cannot find appropriate plugin", e);
			throw new PluginNotFoundException(e);
		}

		log.info("event factory...");
		/*
		 * Class<AbstractDataOneEventServiceFactory> clazzEvent =
		 * loadImplClass(AbstractDataOneEventServiceFactory.class); try {
		 * Constructor<?> ctor = clazzEvent.getConstructor();
		 * dataOneEventServiceFactory = (DataOneEventServiceFactory)
		 * ctor.newInstance(new Object[] {});
		 * log.info("dataOneEventServiceFactory success"); } catch
		 * (NoSuchMethodException | SecurityException | InstantiationException |
		 * IllegalAccessException | IllegalArgumentException |
		 * InvocationTargetException e) {
		 * log.error("cannot find appropriate plugin", e); throw new
		 * PluginNotFoundException(e); }
		 */

		PropertiesLoader propertiesLoader = new PropertiesLoader();
		Properties props = propertiesLoader.getProperties();
		String eventFactoryClassName = props.getProperty("plugin.factory.event");
		if (eventFactoryClassName == null || eventFactoryClassName.isEmpty()) {
			log.error("cannot find appropriate plugin class name config in properties");
			throw new PluginNotFoundException("cannot find event factory plugin class name in properties");
		}

		log.info("load event factory class:{}", eventFactoryClassName);
		try {
			Class eventClass = Class.forName(eventFactoryClassName.trim());
			dataOneEventServiceFactory = (DataOneEventServiceFactory) eventClass.newInstance();
			log.info("event factory created");

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			log.error("cannot find appropriate plugin", e);
			throw new PluginNotFoundException(e);
		}

		log.info("repo factory...");
		Class<AbstractDataOneRepoFactory> clazzRepo = loadImplClass(AbstractDataOneRepoFactory.class);
		try {
			Constructor<?> ctor = clazzRepo.getConstructor();
			dataOneRepoServiceFactory = (DataOneRepoServiceFactory) ctor.newInstance(new Object[] {});
			log.info("dataOneRepoServiceFactory success");
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			log.error("cannot find appropriate plugin", e);
			throw new PluginNotFoundException(e);
		}

		log.info("plugin factories loaded");

	}

	private void loadCandidateClasspaths(String libDir) {

		/*
		 * See https://github.com/kamranzafar/JCL for usage of JCL
		 */

		File dependencyDirectory = new File(libDir);
		File[] files = dependencyDirectory.listFiles();
		ArrayList<URI> uris = new ArrayList<>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(".jar")) {
				log.info("adding jar:{} to candidates", files[i]);
				uris.add(files[i].toURI());
			}
		}

		log.info("creating jar class loader...");
		jcl = new JarClassLoader();

		for (URI uri : uris) {
			log.info("adding uri for jar:{}", uri);
			try {
				jcl.add(uri.toURL());
				urls.add(uri.toURL()); // testing outside jcl FIXME: decide!
			} catch (MalformedURLException e) {
				log.error("malformed url for jar file:{}", uri, e);
				throw new PluginRuntimeException("error loading jar file", e);
			}
		}

		if (JclContext.get() == null) {
			DefaultContextLoader context = new DefaultContextLoader(jcl);
			context.loadContext();
		}

	}

	/**
	 * @return the publicationContext
	 */
	public PublicationContext getPublicationContext() {
		return publicationContext;
	}

	/**
	 * @param publicationContext
	 *            the publicationContext to set
	 */
	public void setPublicationContext(PublicationContext publicationContext) {
		this.publicationContext = publicationContext;
	}

	/**
	 * @return the dataOneEventServiceFactory
	 */
	DataOneEventServiceFactory getDataOneEventServiceFactory() {
		return dataOneEventServiceFactory;
	}

	/**
	 * @param dataOneEventServiceFactory
	 *            the dataOneEventServiceFactory to set
	 */
	void setDataOneEventServiceFactory(DataOneEventServiceFactory dataOneEventServiceFactory) {
		this.dataOneEventServiceFactory = dataOneEventServiceFactory;
	}

	/**
	 * @return the dataOnePidServiceFactory
	 */
	DataOnePidServiceFactory getDataOnePidServiceFactory() {
		return dataOnePidServiceFactory;
	}

	/**
	 * @param dataOnePidServiceFactory
	 *            the dataOnePidServiceFactory to set
	 */
	void setDataOnePidServiceFactory(DataOnePidServiceFactory dataOnePidServiceFactory) {
		this.dataOnePidServiceFactory = dataOnePidServiceFactory;
	}

	/**
	 * @return the dataOneRepoServiceFactory
	 */
	DataOneRepoServiceFactory getDataOneRepoServiceFactory() {
		return dataOneRepoServiceFactory;
	}

	/**
	 * @param dataOneRepoServiceFactory
	 *            the dataOneRepoServiceFactory to set
	 */
	void setDataOneRepoServiceFactory(DataOneRepoServiceFactory dataOneRepoServiceFactory) {
		this.dataOneRepoServiceFactory = dataOneRepoServiceFactory;
	}

}
