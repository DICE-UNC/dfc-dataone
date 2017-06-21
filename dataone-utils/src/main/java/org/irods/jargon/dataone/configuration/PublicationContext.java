/**
 * 
 */
package org.irods.jargon.dataone.configuration;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.dataone.utils.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Sharable context elements between components of the DataONE service
 * 
 * @author mconway
 *
 */
@Component
public class PublicationContext {
	@Autowired
	private IRODSAccessObjectFactory irodsAccessObjectFactory;

	@Autowired
	private RestConfiguration restConfiguration;

	private Properties additionalProperties;

	public static final Logger log = LoggerFactory.getLogger(PublicationContext.class);

	@PostConstruct
	public void init() {
		log.info("init()");
		PropertiesLoader loader = new PropertiesLoader();
		additionalProperties = loader.getProperties();
		log.info("etc properties loaded successfully");
	}

	/**
	 * @return the irodsAccessObjectFactory
	 */
	public IRODSAccessObjectFactory getIrodsAccessObjectFactory() {
		return irodsAccessObjectFactory;
	}

	/**
	 * @param irodsAccessObjectFactory
	 *            the irodsAccessObjectFactory to set
	 */
	public void setIrodsAccessObjectFactory(IRODSAccessObjectFactory irodsAccessObjectFactory) {
		this.irodsAccessObjectFactory = irodsAccessObjectFactory;
	}

	/**
	 * @return the restConfiguration
	 */
	public RestConfiguration getRestConfiguration() {
		return restConfiguration;
	}

	/**
	 * @param restConfiguration
	 *            the restConfiguration to set
	 */
	public void setRestConfiguration(RestConfiguration restConfiguration) {
		this.restConfiguration = restConfiguration;
	}

	/**
	 * @return the additionalProperties
	 */
	public Properties getAdditionalProperties() {
		return additionalProperties;
	}

	/**
	 * @param additionalProperties
	 *            the additionalProperties to set
	 */
	public void setAdditionalProperties(Properties additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

}
