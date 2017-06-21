package org.irods.jargon.dataone.events.def.indexer;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public class EventConverterUtilTest {

	@Test
	public void testJsonToLoggingEvent() throws Exception {
		String myFile = "/sample-messages/msg1.json";

		String jsonString = new String(Files.readAllBytes(Paths.get(getClass().getResource(myFile).toURI())));
		EventConverterUtil converter = new EventConverterUtil();
		LoggingEvent actual = converter.loggingEventFromPayload(jsonString.getBytes());
		Assert.assertNotNull("no logging event returned", actual);

	}

}
