package org.irods.jargon.dataone.reposervice;

import java.util.Date;
import java.util.List;

import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.DataObject;

/**
 * Abstraction of management of the DataOne repository (objects to list,
 * maintenance of status of DataOne published objects)
 * 
 * @author mconway
 *
 */
public interface DataOneRepoServiceAO {

	/**
	 * Returns a complete list of Unique Identifiers that are associated with
	 * iRODS objects that are exposed to DataONE
	 * 
	 * @return <code>List<<code>Identifier</code>></code>
	 * @throws JargonException
	 */
	public List<Identifier> getListOfDataoneExposedIdentifiers() throws JargonException;

	/**
	 * Returns an iRODS Data Object for the DataONE Identifier object specified
	 * 
	 * @return <code>List<<code>DataObject</code>></code>
	 * @throws JargonException
	 */
	public DataObjectListResponse getListOfDataoneExposedDataObjects(Date fromDate, Date toDate,
			ObjectFormatIdentifier formatId, Boolean replicaStatus, Integer start, Integer count)
			throws JargonException;

	/**
	 * TODO:// why is this not just last mod from data object? mcc
	 * 
	 * Get last modified data for a data object
	 * 
	 * @param dataObject
	 *            {@link DataObject} to determine mod date
	 * @return {@link Date} last modded
	 * @throws JargonException
	 */
	public Date getLastModifiedDateForDataObject(final DataObject dataObject) throws JargonException;

	/**
	 * Get a string representing the MIME type of the data object using the
	 * technique or metadata in the target system
	 * 
	 * @param dataObject
	 *            {@link DataObject}
	 * @return <code>String</code> with the MIME type
	 * @throws FileNotFoundException
	 * @throws JargonException
	 */
	public String dataObjectFormat(final DataObject dataObject) throws FileNotFoundException, JargonException;

}
