/**
 * 
 */
package org.irods.jargon.dataone.def.event.persist.dao;

import org.irods.jargon.dataone.def.event.persist.dao.domain.AccessLog;
import org.irods.jargon.dataone.events.EventLoggingException;
import org.irods.jargon.dataone.events.EventsEnum;

import java.util.Date;
import java.util.List;

/**
 * DAO interface for Access log
 * 
 * @author Mike Conway
 * 
 */
public interface AccessLogDAO {

	/**
	 * Save the <code>AccessLog</code> entry in the audit database
	 * 
	 * @param accessLog
	 *            {@link AccessLog} entry containing the event
	 * @throws EventLoggingException
	 */
	void save(AccessLog accessLog) throws EventLoggingException;

	/**
	 * locate the access log entry by ID
	 * 
	 * @param id
	 *            <code>Long</code> with the access id
	 * @return {@link AccessLog} or <code>null</code>
	 * @throws EventLoggingException
	 */
	AccessLog findById(Long id) throws EventLoggingException;

	/**
	 * Delete the given entry
	 * 
	 * @param accessLog
	 *            {@link AccessLog} to delete
	 * @throws EventLoggingException
	 */
	void delete(AccessLog accessLog) throws EventLoggingException;

	/**
	 * Finds a set of access log entries.
	 *
	 * @param start
	 *            the start date for the search, if any
	 * @param end
	 *            the end date for the search, if any
	 * @param eventType
	 *            the selected event type, if any
	 * @param pid
	 *            the permanent identifier, if any
	 * @param offset
	 *            the starting offset for paged listings
	 * @param limit
	 *            the maximum number of results for paged listings.
	 *
	 * @return the list of matching events
	 * @throws EventLoggingException if an error occurs in the query
	 */
	List<AccessLog> find(Date start, Date end, EventsEnum eventType, String pid, int offset, int limit)
			throws EventLoggingException;
}
