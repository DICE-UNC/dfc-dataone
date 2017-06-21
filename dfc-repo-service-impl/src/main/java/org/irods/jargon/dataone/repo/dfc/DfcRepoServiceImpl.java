/**
 * 
 */
package org.irods.jargon.dataone.repo.dfc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;
import org.irods.jargon.core.query.AVUQueryElement;
import org.irods.jargon.core.query.AVUQueryElement.AVUQueryPart;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.core.query.MetaDataAndDomainData;
import org.irods.jargon.core.query.QueryConditionOperators;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.reposervice.AbstractDataOneRepoServiceAO;
import org.irods.jargon.dataone.reposervice.DataObjectListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mcc
 *
 */
public class DfcRepoServiceImpl extends AbstractDataOneRepoServiceAO {

	public DfcRepoServiceImpl(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
	}

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public DataObjectListResponse getListOfDataoneExposedDataObjects(final Date fromDate, final Date toDate,
			final ObjectFormatIdentifier formatId, final Boolean replicaStatus, final Integer start,
			final Integer count) throws JargonException {

		// get complete list of exposed data objects
		// TODO: see if this can be done differently to just request list
		// using filter items
		DataObjectListResponse dataObjectListResponse = new DataObjectListResponse();
		List<DataObject> dataObjectListTmp = new ArrayList<>();
		List<DataObject> dataObjectListFinal = new ArrayList<>();
		List<Identifier> ids = getListOfDataoneExposedIdentifiers();

		log.info("finding list of data objects with list of {} identifiers", ids.size());
		// TODO: what is this?
		for (Identifier id : ids) {
			DataObject dataObject;
			dataObject = null; // TODO://recast
								// getDataObjectFromIdentifier(id);

			if (dataObject != null) {
				Date modifiedDate = dataObject.getUpdatedAt();

				if (matchesFromDate(fromDate, modifiedDate) && matchesToDate(toDate, modifiedDate) &&
				// matchesFormatId(formatId, dataObject) && support not
				// required
						matchesReplicaStatus(replicaStatus)) {
					dataObjectListTmp.add(dataObject);
				}
			}
		}
		// save total before filtering with start and count
		dataObjectListResponse.setTotal(dataObjectListTmp.size());

		// now filter this list according to start and count
		if (dataObjectListTmp.size() > 0) {
			DataObject[] dataObjectArray = dataObjectListTmp.toArray(new DataObject[dataObjectListTmp.size()]);
			int end = start + count;
			for (int i = start; i < dataObjectArray.length && i < end; i++) {
				dataObjectListFinal.add(dataObjectArray[i]);
			}
		}

		dataObjectListResponse.setCount(dataObjectListFinal.size());
		dataObjectListResponse.setDataObjects(dataObjectListFinal);

		log.info("returning list of dataObjects: {}", dataObjectListFinal.toString());
		return dataObjectListResponse;
	}

	private boolean matchesFromDate(final Date fromDate, final Date dataObjectModDate) {

		if (fromDate == null || fromDate.getTime() <= 0) {
			log.info("fromDate : returning matches");
			return true;
		}

		if (fromDate.getTime() <= dataObjectModDate.getTime()) {
			log.info("fromDate : returning matches");
			return true;
		}

		log.info("fromDate : returning no match");
		return false;
	}

	private boolean matchesToDate(final Date toDate, final Date dataObjectModDate) {

		if (toDate == null || toDate.getTime() <= 0) {
			log.info("toDate : returning matches");
			return true;
		}

		if (toDate.getTime() >= dataObjectModDate.getTime()) {
			log.info("toDate : returning matches");
			return true;
		}

		log.info("toDate : returning no match");
		return false;
	}

	private boolean matchesFormatId(final ObjectFormatIdentifier formatId, final DataObject dataObject)
			throws JargonException, JargonQueryException {

		String dataFormat = dataObjectFormat(dataObject);

		if ((formatId.getValue() == null) || (formatId.getValue().equals(dataFormat))) {
			return true;
		}
		return false;
	}

	private boolean matchesReplicaStatus(final Boolean replicaStatus) {

		// replication is always set to false for now
		if (replicaStatus == null || replicaStatus == false) {
			log.info("replicastatus : returning matches");
			return true;
		}

		log.info("toDate : returning no match");
		return false;
	}

	// TODO: do we need this method?
	@Override
	public List<Identifier> getListOfDataoneExposedIdentifiers() throws JargonException {

		List<Identifier> identifiers = new ArrayList<>();

		// FIXME: recast
		return identifiers;
	}

	@Override
	public Date getLastModifiedDateForDataObject(DataObject dataObject) throws JargonException {
		long epoch = 0;
		AVUQueryElement avuQuery = null;
		List<AVUQueryElement> avuQueryList = new ArrayList<>();
		List<MetaDataAndDomainData> metadataAndDomainDataList = new ArrayList<>();
		String dateAttr = "StartDateTime";

		try {
			avuQuery = AVUQueryElement.instanceForValueQuery(AVUQueryPart.ATTRIBUTE, QueryConditionOperators.EQUAL,
					dateAttr);
			avuQueryList.add(avuQuery);

			DataObjectAO dataObjectAO = this.getPublicationContext().getIrodsAccessObjectFactory()
					.getDataObjectAO(this.getIrodsAccount());

			metadataAndDomainDataList = dataObjectAO.findMetadataValuesForDataObjectUsingAVUQuery(avuQueryList,
					dataObject.getAbsolutePath());
		} catch (JargonQueryException e) {
			log.error("error querying metadata", e);
			throw new JargonException(e);
		}

		String value = null;
		for (MetaDataAndDomainData data : metadataAndDomainDataList) {
			value = data.getAvuValue();
			// just take the 1st one
			break;
		}
		epoch = Long.parseLong(value);
		// need to convert it 2 milliseconds for Java date
		Date theDate = new Date(epoch * 1000);

		return theDate;
	}

	@Override
	public String dataObjectFormat(DataObject dataObject) throws FileNotFoundException, JargonException {
		String format = getDataObjectFormatFromMetadata(dataObject);
		if (format == null) {
			format = getDataObjectMimeType(dataObject);
		}

		return format;

	}

	private String getDataObjectMimeType(DataObject dataObject) throws JargonException {
		String mimeType = null;
		String filename = dataObject.getAbsolutePath();

		DefaultDetector typeDetector = new DefaultDetector();
		IRODSFile irodsFile = this.getPublicationContext().getIrodsAccessObjectFactory()
				.getIRODSFileFactory(this.getIrodsAccount()).instanceIRODSFile(filename);
		IRODSFileInputStream irodsStream = this.getPublicationContext().getIrodsAccessObjectFactory()
				.getIRODSFileFactory(this.getIrodsAccount()).instanceIRODSFileInputStream(irodsFile);
		InputStream stream = new BufferedInputStream(irodsStream);
		Metadata metadata = new Metadata();
		metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, filename);

		MediaType type;
		try {
			type = typeDetector.detect(stream, metadata);
		} catch (IOException e) {
			throw new FileNotFoundException("Cannot stream file in order to detect file type");
		}

		// if mime type is returned as "application/x-netcdf" change to
		// DataONE accepted name: "netCDF-4"
		mimeType = type.toString();
		if (mimeType.equals("application/x-netcdf")) {
			mimeType = "netCDF-4";
		}

		return mimeType;
	}

	private String getDataObjectFormatFromMetadata(final DataObject dataObject)
			throws FileNotFoundException, JargonException {

		String dataFormat = null;
		String formatAttr = "Format";
		List<AVUQueryElement> avuQueryList = new ArrayList<>();

		// List<MetaDataAndDomainData> result =
		// dataObjectAO.findMetadataValuesForDataObject(dataObject.getAbsolutePath());
		List<MetaDataAndDomainData> result;
		try {
			AVUQueryElement avuQuery = AVUQueryElement.instanceForValueQuery(AVUQueryPart.ATTRIBUTE,
					QueryConditionOperators.EQUAL, formatAttr);
			avuQueryList.add(avuQuery);

			DataObjectAO dataObjectAO = this.getPublicationContext().getIrodsAccessObjectFactory()
					.getDataObjectAO(this.getIrodsAccount());
			result = dataObjectAO.findMetadataValuesForDataObjectUsingAVUQuery(avuQueryList,
					dataObject.getAbsolutePath());
		} catch (JargonQueryException e) {
			log.error("metadata query error", e);
			throw new JargonException("cannot query for metadata", e);
		}

		for (MetaDataAndDomainData metadata : result) {
			if (metadata.getAvuAttribute().equals("Format")) {
				dataFormat = metadata.getAvuValue();
				break;
			}
		}

		return dataFormat;

	}

}
