/**
 * 
 */
package org.irods.jargon.dataone.def.event.persist.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.irods.jargon.dataone.def.event.persist.dao.domain.AccessLog;
import org.irods.jargon.dataone.events.EventLoggingException;
import org.irods.jargon.dataone.events.EventsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Impl of an access log
 * 
 * @author mcc
 *
 */
public class AccessLogDAOImpl implements AccessLogDAO {

	private static final Logger log = LoggerFactory.getLogger(AccessLogDAOImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 */
	public AccessLogDAOImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO#save(org.
	 * irods.jargon.dataone.def.event.persist.dao.domain.AccessLog)
	 */
	@Override
	public void save(AccessLog accessLog) throws EventLoggingException {

		log.info("save accessLog:{}", accessLog);

		try {
			sessionFactory.getCurrentSession().saveOrUpdate(accessLog);
		} catch (Exception e) {
			log.error("error in save(AccessLog)", e);
			throw new EventLoggingException("Failed save(accessLog)", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO#findById(java
	 * .lang.Long)
	 */
	@Override
	public AccessLog findById(Long id) throws EventLoggingException {

		log.info("find accessLog:{}", id);

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AccessLog.class);
		return (AccessLog) criteria.uniqueResult();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.irods.jargon.dataone.def.event.persist.dao.AccessLogDAO#delete(org.
	 * irods.jargon.dataone.def.event.persist.dao.domain.AccessLog)
	 */
	@Override
	public void delete(AccessLog accessLog) throws EventLoggingException {
		log.info("entering delete(AccessLog)");

		try {
			sessionFactory.getCurrentSession().delete(accessLog);
		} catch (Exception e) {
			log.error("error in delete(AccessLog)", e);
			throw new EventLoggingException("Failed delete(AccessLog)", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccessLog> find(Date start, Date end, EventsEnum eventType, String pid, int offset, int limit)
			throws EventLoggingException {
		log.info("find start:{}, end:{}, eventType:{}, pid:{}, offset:{}, limit:{}", start, end, eventType, pid, offset,
				limit);

		List<AccessLog> results;
		try {
			Criteria crit = sessionFactory.getCurrentSession().createCriteria(AccessLog.class);
			if (start != null) {
				crit.add(Restrictions.ge("dateAdded", start));
			}
			if (end != null) {
				crit.add(Restrictions.le("dateAdded", end));
			}
			if (eventType != null) {
				crit.add(Restrictions.eq("event", eventType));
			}
			if (pid != null) {
				crit.add(Restrictions.eq("permanentId", pid));
			}
			crit.setFirstResult(offset);
			crit.setMaxResults(limit);
			results = crit.list();
		} catch (Exception e) {
			log.error("error in find(start, end, eventType, offset, limit)", e);
			throw new EventLoggingException("failed find(start, end, eventType, offset, limit)", e);
		}
		return results;
	}

	/**
	 * @return the log
	 */
	public static Logger getLog() {
		return log;
	}

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
