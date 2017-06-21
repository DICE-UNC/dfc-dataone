/**
 * 
 */
package org.irods.jargon.dataone.events;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.configuration.PublicationContext;

/**
 * Factory interface that will discover and create the event service
 * 
 * @author mconway
 *
 */
public interface DataOneEventServiceFactory {

	public DataOneEventServiceAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount);

}
