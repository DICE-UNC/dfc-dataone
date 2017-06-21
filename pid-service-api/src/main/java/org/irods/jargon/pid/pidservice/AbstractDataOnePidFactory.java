/**
 * 
 */
package org.irods.jargon.pid.pidservice;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.configuration.PublicationContext;

/**
 * Factory to create pid services
 * 
 * @author mcc
 *
 */
public abstract class AbstractDataOnePidFactory implements DataOnePidServiceFactory {

	/* (non-Javadoc)
	 * @see org.irods.jargon.pid.pidservice.DataOnePidFactory#instance(org.irods.jargon.dataone.configuration.PublicationContext, org.irods.jargon.core.connection.IRODSAccount)
	 */
	@Override
	public abstract UniqueIdAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount);

}
