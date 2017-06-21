package org.irods.jargon.dataone.configuration;

import java.io.File;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.utils.LocalFileUtils;
import org.irods.jargon.dataone.events.DataOneEventServiceAO;
import org.irods.jargon.dataone.reposervice.DataOneRepoServiceAO;
import org.irods.jargon.pid.pidservice.UniqueIdAO;
import org.junit.Assert;
import org.junit.Test;

public class PluginDiscoveryServiceTest {

	@Test
	public void testInit() throws Exception {
		PublicationContext publicationContext = new PublicationContext();
		File rescFile = LocalFileUtils.getClasspathResourceAsFile("/dummyjars");
		Assert.assertNotNull("no file path", rescFile);
		RestConfiguration restConfiguration = new RestConfiguration();
		restConfiguration.setPluginJarLocation(rescFile.getAbsolutePath());
		publicationContext.setRestConfiguration(restConfiguration);
		PluginDiscoveryService pluginDiscoveryService = new PluginDiscoveryService();
		pluginDiscoveryService.setPublicationContext(publicationContext);
		pluginDiscoveryService.init();
		Assert.assertNotNull("no pid factory", pluginDiscoveryService.getDataOnePidServiceFactory());
		Assert.assertNotNull("no event factory", pluginDiscoveryService.getDataOneEventServiceFactory());
		Assert.assertNotNull("no repo factory", pluginDiscoveryService.getDataOneRepoServiceFactory());

	}

	@Test
	public void testInstanceEventService() throws Exception {
		IRODSAccount dummyAccount = new IRODSAccount("xxx", 1247, "xxx", "xxx", "xxx", "xxx", "xxx");
		PublicationContext publicationContext = new PublicationContext();
		File rescFile = new File("/home/mcc/d1plugin");
		Assert.assertNotNull("no file path", rescFile);
		RestConfiguration restConfiguration = new RestConfiguration();
		restConfiguration.setPluginJarLocation(rescFile.getAbsolutePath());
		publicationContext.setRestConfiguration(restConfiguration);
		PluginDiscoveryService pluginDiscoveryService = new PluginDiscoveryService();
		pluginDiscoveryService.setPublicationContext(publicationContext);
		pluginDiscoveryService.init();
		DataOneEventServiceAO actual = pluginDiscoveryService.instanceEventService(dummyAccount);
		Assert.assertNotNull("no impl created", actual);
	}

	@Test
	public void testInstanceUniqueIdService() throws Exception {
		IRODSAccount dummyAccount = new IRODSAccount("xxx", 1247, "xxx", "xxx", "xxx", "xxx", "xxx");
		PublicationContext publicationContext = new PublicationContext();
		File rescFile = new File("/home/mcc/d1plugin");
		Assert.assertNotNull("no file path", rescFile);
		RestConfiguration restConfiguration = new RestConfiguration();
		restConfiguration.setPluginJarLocation(rescFile.getAbsolutePath());
		publicationContext.setRestConfiguration(restConfiguration);
		PluginDiscoveryService pluginDiscoveryService = new PluginDiscoveryService();
		pluginDiscoveryService.setPublicationContext(publicationContext);
		pluginDiscoveryService.init();
		UniqueIdAO actual = pluginDiscoveryService.instanceUniqueIdService(dummyAccount);
		Assert.assertNotNull("no impl created", actual);
	}

	@Test
	public void testInstanceRepoService() throws Exception {
		IRODSAccount dummyAccount = new IRODSAccount("xxx", 1247, "xxx", "xxx", "xxx", "xxx", "xxx");
		PublicationContext publicationContext = new PublicationContext();
		File rescFile = new File("/home/mcc/d1plugin");
		Assert.assertNotNull("no file path", rescFile);
		RestConfiguration restConfiguration = new RestConfiguration();
		restConfiguration.setPluginJarLocation(rescFile.getAbsolutePath());
		publicationContext.setRestConfiguration(restConfiguration);
		PluginDiscoveryService pluginDiscoveryService = new PluginDiscoveryService();
		pluginDiscoveryService.setPublicationContext(publicationContext);
		pluginDiscoveryService.init();
		DataOneRepoServiceAO actual = pluginDiscoveryService.instanceRepoService(dummyAccount);
		Assert.assertNotNull("no impl created", actual);
	}

}
