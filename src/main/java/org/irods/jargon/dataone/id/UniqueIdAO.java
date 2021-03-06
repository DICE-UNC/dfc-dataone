package org.irods.jargon.dataone.id;

import java.util.Date;
import java.util.List;

import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.DataObject;

// Access object for unique identifiers that are associated with iRODS Data Objects

public interface UniqueIdAO {
	
	//These methods are intended for future support of DataONE MN tiers > 1
	//public Identifier createNewIdentifier();
	//public Identifier createNewIdentifier(String identifier);
	//public void deleteIdentifier(String identifier);
	
	/**
	 * Returns a DataONE Identifier object for the iRODS Data Object specified
	 * 
	 * @param dataObject
	 *            <code>DataObject</code> of iRODS Data Object
	 *            associated with the identifier to be returned.
	 * @throws JargonException
	 */
	public Identifier getIdentifierFromDataObject(DataObject dataObject)
			throws JargonException;
	
	
	/**
	 * Returns an iRODS Data Object for the DataONE Identifier object specified
	 * 
	 * @param identifier
	 *            <code>Identifier</code> associated with the iRODS Data Object
	 *            to be returned.
	 * @throws JargonException
	 */
	public DataObject getDataObjectFromIdentifier(Identifier identifier)
			throws JargonException;
	
	
	
	/**
	 * Returns a complete list of Unique Identifiers that are associated with
	 * iRODS objects that are exposed to DataONE
	 * 
	 * @return <code>List<<code>Identifier</code>></code> 
	 * @throws JargonException
	 */
	public List<Identifier> getListOfDataoneExposedIdentifiers()
			throws JargonException;
	
	
	
	/**
	 * Returns an iRODS Data Object for the DataONE Identifier object specified
	 * 
	 * @return <code>List<<code>DataObject</code>></code> 
	 * @throws JargonException
	 */
	public DataObjectListResponse getListOfDataoneExposedDataObjects(
				Date fromDate,
				Date toDate,
				ObjectFormatIdentifier formatId,
				Boolean replicaStatus,
				Integer start,
				Integer count
			) throws JargonException;

}
