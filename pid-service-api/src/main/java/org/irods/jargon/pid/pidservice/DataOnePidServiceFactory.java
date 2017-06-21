package org.irods.jargon.pid.pidservice;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.configuration.PublicationContext;

public interface DataOnePidServiceFactory {

	/**
	 * Create an instance of the pid service
	 * 
	 * @param publicationContext
	 *            {@link PublicationContext} with configuration information
	 * @param irodsAccount
	 *            {@link IRODSAccount} associated with the instance
	 * @return
	 */
	UniqueIdAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount);

}