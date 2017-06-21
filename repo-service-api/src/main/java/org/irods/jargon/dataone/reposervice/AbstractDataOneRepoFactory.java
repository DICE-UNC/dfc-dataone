/**
 * 
 */
package org.irods.jargon.dataone.reposervice;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.configuration.PublicationContext;

/**
 * Factory to create repo services
 * 
 * @author mcc
 *
 */
public abstract class AbstractDataOneRepoFactory implements DataOneRepoServiceFactory {

	/* (non-Javadoc)
	 * @see org.irods.jargon.dataone.reposervice.DataOneRepoFactory#instance(org.irods.jargon.dataone.configuration.PublicationContext, org.irods.jargon.core.connection.IRODSAccount)
	 */
	@Override
	public abstract DataOneRepoServiceAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount);

}
