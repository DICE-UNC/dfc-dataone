/**
 * 
 */
package org.irods.jargon.dataone.reposervice.handle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.query.AVUQueryElement;
import org.irods.jargon.core.query.AVUQueryElement.AVUQueryPart;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.core.query.QueryConditionOperators;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoAO;
import org.irods.jargon.dataone.reposervice.DataObjectListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mconway
 *
 */
public class HandleRepoServiceAOImpl extends AbstractDataOneRepoAO {

	private Properties properties;
	private String propertiesFilename = "d1client.properties";
	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * @param publicationContext
	 */
	public HandleRepoServiceAOImpl(PublicationContext publicationContext) {
		super(publicationContext);
		loadProperties();
	}

	@Override
	public List<Identifier> getListOfDataoneExposedIdentifiers() throws JargonException {

		AVUQueryElement avuQuery1 = null;
		AVUQueryElement avuQuery2 = null;

		String handleAttr = properties.getProperty("irods.dataone.publish_entity_metadata_attr");
		String handleValue = properties.getProperty("irods.dataone.publish_entity_metadata_value");

		List<Identifier> identifiers = new ArrayList<Identifier>();

		try {
			avuQuery1 = AVUQueryElement.instanceForValueQuery(AVUQueryPart.ATTRIBUTE, QueryConditionOperators.EQUAL,
					handleAttr);

			avuQuery2 = AVUQueryElement.instanceForValueQuery(AVUQueryPart.VALUE, QueryConditionOperators.EQUAL,

					handleValue);

		} catch (JargonQueryException e) {
			log.error("getListOfDataoneExposedIdentifiers: failed to create AVU query elements");
			return identifiers;
		}

		DataObjectAO dataObjectAO = this.getPublicationContext().getIrodsAccessObjectFactory()
				.getDataObjectAO(irodsAccount);
		List<DataObject> dataObjects = new ArrayList<DataObject>();
		try {
			dataObjects = dataObjectAO.findDomainByMetadataQuery(Arrays.asList(avuQuery1, avuQuery2));
		} catch (JargonQueryException e) {
			log.error("getListOfDataoneExposedIdentifiers: failed to execute AVU query");
			return identifiers;
		}

		// restrict start date for now to 2/10/16
		Date afterDate = new Date(Long.parseLong("1455062400") * 1000);
		for (DataObject dataObject : dataObjects) {
			if (dataObject.getCreatedAt().after(afterDate)) {
				identifiers.add(getIdentifierFromDataObject(dataObject));
			}
		}

		log.info("returning identifiers: {}", identifiers);
		return identifiers;
	}

	@Override
	public DataObjectListResponse getListOfDataoneExposedDataObjects(final Date fromDate, final Date toDate,
			final ObjectFormatIdentifier formatId, final Boolean replicaStatus, final Integer start,
			final Integer count) throws JargonException {

		int total = 0;

		DataObjectListResponse dataObjectListResponse = new DataObjectListResponse();

		List<AVUQueryElement> avuQueryList = createAVUQueryElementList(fromDate, toDate, formatId);

		DataObjectAO dataObjectAO = this.getPublicationContext().getIrodsAccessObjectFactory()
				.getDataObjectAO(irodsAccount);
		List<DataObject> dataObjects = new ArrayList<DataObject>();

		try {
			dataObjects = dataObjectAO.findDomainByMetadataQuery(avuQueryList, start);
			// save original size of set returned
			total = dataObjects.size();
		} catch (JargonQueryException e) {
			log.error("test_getListOfDataoneExposedDataObjects: failed to execute AVU query");
			return dataObjectListResponse;
		}

		// truncate list if count is specified and it is less than the total
		List<DataObject> finalDataObjects = new ArrayList<DataObject>();
		if ((count >= 0) && (count < total)) {
			finalDataObjects = dataObjects.subList(0, count);
		} else {
			finalDataObjects = dataObjects;
		}

		dataObjectListResponse.setTotal(total);
		dataObjectListResponse.setCount(finalDataObjects.size());
		dataObjectListResponse.setDataObjects(finalDataObjects);

		return dataObjectListResponse;
	}

	private void loadProperties() {
		properties = new Properties();
		InputStream input = null;

		input = getClass().getClassLoader().getResourceAsStream(this.propertiesFilename);

		// load a properties file
		try {
			properties.load(input);
		} catch (IOException e) {
			log.error("Cannot load Member Node properties file: {}", propertiesFilename, e);
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

	private List<AVUQueryElement> createAVUQueryElementList(Date fromDate, Date toDate,
			ObjectFormatIdentifier formatId) {

		// TODO: probably should move these to properties
		String dateAttr = "StartDateTime";
		String formatAttr = "Format";
		List<AVUQueryElement> avuQueryList = new ArrayList<AVUQueryElement>();
		String handleAttr = properties.getProperty("irods.dataone.publish_entity_metadata_attr");
		String handleValue = properties.getProperty("irods.dataone.publish_entity_metadata_value");

		AVUQueryElement avuQuery;
		try {
			// DataOne exposed query
			avuQuery = AVUQueryElement.instanceForValueQuery(AVUQueryPart.ATTRIBUTE, QueryConditionOperators.EQUAL,
					handleAttr);
			avuQueryList.add(avuQuery);

			avuQuery = AVUQueryElement.instanceForValueQuery(AVUQueryPart.VALUE, QueryConditionOperators.EQUAL,

					handleValue);
			avuQueryList.add(avuQuery);

			// handle any date queries
			// restrict start date for now to 2/17/16 00:00:01 : 1455667201
			// 3-23-16 Update - now changed earliest data to 3/23/16 00:00:01 :
			// 1458691201
			// 3-31-16 Update - now changed earliest data to 3/28/16 00:00:01
			// GMT : 1459123201
			long newFromDate;
			if (fromDate != null) {
				newFromDate = java.lang.Math.max(fromDate.getTime() / 1000, Long.parseLong("1459123201"));
			} else {
				newFromDate = Long.parseLong("1459123201");
			}

			// fromDate query
			avuQuery = AVUQueryElement.instanceForValueQuery(AVUQueryPart.ATTRIBUTE, QueryConditionOperators.EQUAL,
					dateAttr);
			avuQueryList.add(avuQuery);

			avuQuery = AVUQueryElement.instanceForValueQuery(AVUQueryPart.VALUE,
					QueryConditionOperators.GREATER_THAN_OR_EQUAL_TO, Long.toString(newFromDate));

			avuQueryList.add(avuQuery);

			// toDate query
			if ((toDate != null) && ((toDate.getTime() / 1000) >= newFromDate)) {

				avuQuery = AVUQueryElement.instanceForValueQuery(AVUQueryPart.ATTRIBUTE, QueryConditionOperators.EQUAL,
						dateAttr);
				avuQueryList.add(avuQuery);

				avuQuery = AVUQueryElement.instanceForValueQuery(AVUQueryPart.VALUE, QueryConditionOperators.LESS_THAN,
						Long.toString(toDate.getTime() / 1000));
				avuQueryList.add(avuQuery);

			}

			// if ((toDate != null) && (toDate.getTime() >= newFromDate)) {
			// avuQuery = AVUQueryElement.instanceForValueQuery(
			// AVUQueryPart.ATTRIBUTE,
			// AVUQueryOperatorEnum.EQUAL,
			// dateAttr);
			// avuQueryList.add(avuQuery);
			//
			// avuQuery = AVUQueryElement.instanceForValueQuery(
			// AVUQueryPart.VALUE,
			// AVUQueryOperatorEnum.BETWEEN,
			// Long.toString(toDate.getTime()/1000));
			// avuQueryList.add(avuQuery);
			//
			// }
			// else {
			// avuQuery = AVUQueryElement.instanceForValueQuery(
			// AVUQueryPart.ATTRIBUTE,
			// AVUQueryOperatorEnum.EQUAL,
			// dateAttr);
			// avuQueryList.add(avuQuery);
			//
			// avuQuery = AVUQueryElement.instanceForValueQuery(
			// AVUQueryPart.VALUE,
			// AVUQueryOperatorEnum.GREATER_OR_EQUAL,
			// Long.toString(newFromDate));
			// avuQueryList.add(avuQuery);
			//
			// }

			// handle data format query
			if ((formatId != null) && (formatId.getValue() != null)) {

				avuQuery = AVUQueryElement.instanceForValueQuery(AVUQueryPart.ATTRIBUTE, QueryConditionOperators.EQUAL,
						formatAttr);
				avuQueryList.add(avuQuery);

				avuQuery = AVUQueryElement.instanceForValueQuery(AVUQueryPart.VALUE, QueryConditionOperators.EQUAL,
						formatId.getValue());
				avuQueryList.add(avuQuery);

			}
		} catch (JargonQueryException e) {
			log.error("createAVUQueryElementList: failed to construct AVU query");
			return avuQueryList;
		}

		return avuQueryList;
	}

}
