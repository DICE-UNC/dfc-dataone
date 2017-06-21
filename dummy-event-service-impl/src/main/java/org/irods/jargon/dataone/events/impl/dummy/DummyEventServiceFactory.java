/**
 * 
 */
package org.irods.jargon.dataone.events.impl.dummy;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.events.AbstractDataOneEventServiceFactory;
import org.irods.jargon.dataone.events.DataOneEventServiceAO;

/**
 * @author mcc
 *
 */
public class DummyEventServiceFactory extends AbstractDataOneEventServiceFactory {

	/**
	 * 
	 */
	public DummyEventServiceFactory() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.irods.jargon.dataone.events.AbstractDataOneEventServiceFactory#
	 * instance(org.irods.jargon.dataone.configuration.PublicationContext,
	 * org.irods.jargon.core.connection.IRODSAccount)
	 */
	@Override
	public DataOneEventServiceAO instance(PublicationContext publicationContext, IRODSAccount irodsAccount) {
		return new DummyEventServiceAOImpl(irodsAccount, publicationContext);
	}

}
