# Testing setup notes

### maven profile

add the following to your maven settings.xml profile in your home/.m2 directory

```xml  

<profile>
			<id>dataone</id>
			<properties>
				<test.plugin.jar.location>/home/mconway/temp/dataonejar</test.plugin.jar.location>
			</properties>
		</profile>
	
	
	
```

That .jar directory will be the place to put the .jar files for your pid and event service plugins.  These are discovered by the PluginDiscoveryService. 

### build and place your plugin jars in the test plugin jar location

Dummy plugins are being built, including the dummy-event-service-impl included in this repo.  These dummy jar files can be copied out of the target directory of the dummy subproject 
in order to run the unit and integration tests that depend on successful lookup

### sample unit test

Check out the PluginDiscoveryServiceTest unit test for examples of testing the loading of a plugin, as in this simple case:

```java

@Test
	public void testInstanceEventService() throws Exception {
		IRODSAccessObjectFactory aof = Mockito
				.mock(IRODSAccessObjectFactory.class);
		PublicationContext publicationContext = new PublicationContext();
		RestConfiguration restConfiguration = new RestConfiguration();
		restConfiguration.setPluginJarLocation(dataOneTestHelper
				.getPluginJarLocation(dataOneProperties));
		publicationContext.setRestConfiguration(restConfiguration);
		publicationContext.setIrodsAccessObjectFactory(aof);
		PluginDiscoveryService pluginDiscoveryService = new PluginDiscoveryService();
		pluginDiscoveryService.setPublicationContext(publicationContext);
		pluginDiscoveryService.init();

		EventServiceAO eventServiceAO = pluginDiscoveryService
				.instanceEventService();
		Assert.assertNotNull("no eventServiceAO", eventServiceAO);
	}


```