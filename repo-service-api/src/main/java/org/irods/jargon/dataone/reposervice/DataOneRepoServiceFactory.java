package org.irods.jargon.dataone.reposervice;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.configuration.PublicationContext;

public interface DataOneRepoServiceFactory {

	/**
	 * Create an instance of the pid service
	 * 
	 * @param publicationContext
	 *            {@link PublicationContext} with configuration information
	 * @param irodsAccount
	 *            {@link IRODSAccount} associated with the instance
	 * @return
	 */
	DataOneRepoServiceAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount);

}