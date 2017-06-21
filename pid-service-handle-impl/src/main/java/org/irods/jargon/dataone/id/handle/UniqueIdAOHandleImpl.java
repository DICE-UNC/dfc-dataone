package org.irods.jargon.dataone.id.handle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.dataone.service.types.v1.Identifier;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.service.AbstractJargonService;
import org.irods.jargon.pid.pidservice.UniqueIdAO;
//import org.irods.jargon.dataprofile.DataProfile;
//import org.irods.jargon.dataprofile.DataProfileService;
//import org.irods.jargon.dataprofile.DataProfileServiceImpl;
//import org.irods.jargon.dataprofile.DataTypeResolutionService;
//import org.irods.jargon.dataprofile.DataTypeResolutionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniqueIdAOHandleImpl extends AbstractJargonService implements UniqueIdAO {

	private Properties properties;
	private String propertiesFilename = "d1client.properties";
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public DataObject getDataObjectFromIdentifier(final Identifier identifier)
			throws JargonException, FileNotFoundException {

		DataObject dataObject = null;
		log.info("retrieving irods data object id from identifier: {}", identifier.getValue());
		long dataObjectId = getDataObjectIdFromDataOneIdentifier(identifier);
		log.info("got id: {}", dataObjectId);

		if (dataObjectId < 0) {
			throw new FileNotFoundException("");
		}

		try {

			DataObjectAO dataObjectAO = irodsAccessObjectFactory.getDataObjectAO(irodsAccount);
			log.info("got dataObjectAO: {}", dataObjectAO.toString());
			// Find iRODS object here from Identifier
			dataObject = dataObjectAO.findById(new Long(dataObjectId).intValue());
			if (dataObject != null) {
				log.info("found iRODS file: {}", dataObject.getAbsolutePath());
			} else {
				log.warn("did not find data object for id={}", dataObjectId);
			}

			// } catch (Exception e) {
			// log.warn("Cannot access iRODS object for id={}", dataObjectId);
			// throw e;
		} finally {
			irodsAccessObjectFactory.closeSessionAndEatExceptions();
		}

		return dataObject;
	}

	@Override
	public Identifier getIdentifierFromDataObject(final DataObject dataObject) throws JargonException {

		if (dataObject == null) {
			log.error("getIdentifierFromDataObject: dataObject is null");
			throw new JargonException("dataObject is null");
		}
		Identifier identifier = new Identifier();

		String prefix = getHandlePrefix();
		if (prefix == null) {
			log.error("getIdentifierFromDataObject: prefix is null");
			throw new JargonException("prefix is null");
		}
		String objectId = String.valueOf(dataObject.getId());

		identifier.setValue(prefix + "/" + objectId);

		return identifier;
	}

	public long getDataObjectIdFromDataOneIdentifier(final Identifier pid) {

		int idIdx = pid.getValue().indexOf("/") + 1;
		long dataObjectId;
		try {
			dataObjectId = Long.parseLong(pid.getValue().substring(idIdx));
		} catch (NumberFormatException e) {
			// set to catch illegal object identifiers for iRODS MN
			dataObjectId = -1;
		}

		log.info("getDataObjectIdFromDataOneIdentifier: returning data object id: {}", dataObjectId);
		return dataObjectId;
	}

	public String getHandlePrefix() throws JargonException {
		String prefix = null;

		prefix = properties.getProperty("irods.dataone.handle.prefix");

		return prefix;
	}

	private void loadProperties() {
		properties = new Properties();
		InputStream input = null;
		input = getClass().getClassLoader().getResourceAsStream(propertiesFilename);

		// load a properties file
		try {
			properties.load(input);
		} catch (IOException e) {
			log.error("Cannot load Member Node properties file: {}", propertiesFilename);
			log.error("IOException", e);
			properties = new Properties();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

}
