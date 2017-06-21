package org.irods.jargon.dataone.plugin;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.dataone.configuration.PublicationContext;

/**
 * 
 * @author mconway
 * 
 */
public class AbstractDataOnePlugin {

	private final IRODSAccount irodsAccount;

	/**
	 * Represents a handle to iRODS connection and general configuration
	 * information
	 */
	private final PublicationContext publicationContext;

	/**
	 * @param irodsAccount
	 * @param publicationContext
	 */
	public AbstractDataOnePlugin(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super();
		this.irodsAccount = irodsAccount;
		this.publicationContext = publicationContext;
	}

	/**
	 * @return the publicationContext
	 */
	protected PublicationContext getPublicationContext() {
		return publicationContext;
	}

	/**
	 * @return the irodsAccount
	 */
	public IRODSAccount getIrodsAccount() {
		return irodsAccount;
	}

}
