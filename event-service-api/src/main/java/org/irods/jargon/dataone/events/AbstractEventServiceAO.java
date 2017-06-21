/**
 * 
 */
package org.irods.jargon.dataone.events;

import java.util.Date;

import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.Log;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.InvalidArgumentException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.plugin.AbstractDataOnePlugin;

/**
 * Abstract superclass for the event service that accepts context and
 * configuration information from the member node service
 * 
 * @author mconway
 *
 */
public abstract class AbstractEventServiceAO extends AbstractDataOnePlugin implements DataOneEventServiceAO {

	/**
	 * Default constructor with required values
	 * 
	 * @param irodsAccount
	 *            {@link IRODSAccount} for the current iRODS connection
	 * @param publicationContext
	 *            {@link PublicationContext} with configuration and other
	 *            information
	 */
	public AbstractEventServiceAO(IRODSAccount irodsAccount, PublicationContext publicationContext) {
		super(irodsAccount, publicationContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.events.EventServiceAO#getLogs(java.util.Date,
	 * java.util.Date, org.irods.jargon.dataone.events.EventsEnum,
	 * java.lang.String, int, int)
	 */
	@Override
	public abstract Log getLogs(Date fromDate, Date toDate, EventsEnum event, String pidFilter, int startIdx,
			int count);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.events.EventServiceAO#recordEvent(org.dataone
	 * .service.types.v1.Event, org.dataone.service.types.v1.Identifier,
	 * java.lang.String)
	 */
	@Override
	public abstract void recordEvent(Event event, Identifier id, String description)
			throws InvalidArgumentException, JargonException, ServiceFailure;
}
