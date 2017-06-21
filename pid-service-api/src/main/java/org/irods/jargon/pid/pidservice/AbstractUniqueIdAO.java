/**
 * 
 */
package org.irods.jargon.pid.pidservice;

import org.dataone.service.types.v1.Identifier;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.domain.DataObject;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.plugin.AbstractDataOnePlugin;

/**
 * Abstract superclass for a pid handler that will accept the provided context
 * from the DataONE service when created by the discovery mechanism
 * 
 * @author mconway
 *
 */
public abstract class AbstractUniqueIdAO extends AbstractDataOnePlugin implements UniqueIdAO {

	/**
	 * Default constructor with required values
	 * 
	 * @param irodsAccount
	 *            {@link IRODSAccount} for the current iRODS connection
	 * @param publicationContext
	 *            {@link PublicationContext} with configuration and other
	 *            information
	 */
	public AbstractUniqueIdAO(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.pid.pidservice.UniqueIdAO#getIdentifierFromDataObject
	 * (org.irods.jargon.core.pub.domain.DataObject)
	 */
	@Override
	public abstract Identifier getIdentifierFromDataObject(DataObject dataObject) throws JargonException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.pid.pidservice.UniqueIdAO#getDataObjectFromIdentifier
	 * (org.dataone.service.types.v1.Identifier)
	 */
	@Override
	public abstract DataObject getDataObjectFromIdentifier(Identifier identifier) throws JargonException;

}
