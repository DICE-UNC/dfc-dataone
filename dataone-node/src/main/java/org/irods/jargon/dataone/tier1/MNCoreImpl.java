package org.irods.jargon.dataone.tier1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dataone.service.exceptions.InsufficientResources;
import org.dataone.service.exceptions.InvalidRequest;
import org.dataone.service.exceptions.InvalidToken;
import org.dataone.service.exceptions.NotAuthorized;
import org.dataone.service.exceptions.NotImplemented;
import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.mn.tier1.v1.MNCore;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Log;
import org.dataone.service.types.v1.Node;
import org.dataone.service.types.v1.NodeState;
import org.dataone.service.types.v1.Ping;
import org.dataone.service.types.v1.Session;
import org.dataone.service.types.v1.Subject;
import org.dataone.service.types.v1.Synchronization;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.EnvironmentalInfoAO;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.PluginDiscoveryService;
import org.irods.jargon.dataone.configuration.PluginNotFoundException;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.events.DataOneEventServiceAO;
import org.irods.jargon.dataone.events.EventsEnum;
import org.irods.jargon.dataone.utils.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MNCoreImpl implements MNCore {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private final PublicationContext publicationContext;
	private final PluginDiscoveryService pluginDiscoveryService;
	// private final MNCoreModel mnCoreModel;

	public MNCoreImpl(final PublicationContext publicationContext,
			final PluginDiscoveryService pluginDiscoveryService) {
		this.publicationContext = publicationContext;
		this.pluginDiscoveryService = pluginDiscoveryService;
	}

	@Override
	public Date ping() throws NotImplemented, ServiceFailure, InsufficientResources {

		try {
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(publicationContext.getRestConfiguration());

			EnvironmentalInfoAO environmentalInfoAO = publicationContext.getIrodsAccessObjectFactory()
					.getEnvironmentalInfoAO(irodsAccount);

			long currentTime = environmentalInfoAO.getIRODSServerCurrentTime();
			return new Date(currentTime);

		} catch (Exception e) {
			log.error("ping failed: {}", e.getMessage());
			throw new ServiceFailure("2042", "failed to contact iRODS server");
		} finally {
			publicationContext.getIrodsAccessObjectFactory().closeSessionAndEatExceptions();
		}
	}

	@Override
	public Log getLogRecords(final Date fromDate, final Date toDate, final Event event, final String pidFilter,
			final Integer startIdx, final Integer count)
			throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {

		log.info("getLogRecords: elasticsearch implementation");
		EventsEnum eventsEnum = null;
		if (event != null) {
			eventsEnum = EventsEnum.valueOfFromDataOne(event);
		}

		try {
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(publicationContext.getRestConfiguration());
			DataOneEventServiceAO eventService = pluginDiscoveryService.instanceEventService(irodsAccount);
			return eventService.getLogs(fromDate, toDate, eventsEnum, pidFilter, startIdx, count);
		} catch (PluginNotFoundException e) {
			log.error("error processing event plugin", e);
			throw new ServiceFailure("1490", "retrieval of log records failed");
		} catch (JargonException e) {
			log.error("error processing event ", e);
			throw new ServiceFailure("1490", "retrieval of log records failed");
		}

	}

	@Override
	public Node getCapabilities() throws NotImplemented, ServiceFailure {

		Node node = new Node();

		Ping ping = new Ping();
		ping.setSuccess(true);
		node.setState(NodeState.UP);

		try {
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(publicationContext.getRestConfiguration());
			EnvironmentalInfoAO environmentalInfoAO = publicationContext.getIrodsAccessObjectFactory()
					.getEnvironmentalInfoAO(irodsAccount);

			environmentalInfoAO.getIRODSServerProperties().getServerBootTime();

		} catch (Exception e) {
			log.error("getCapabilities: iRODS server is not running");
			ping.setSuccess(false);
			node.setState(NodeState.DOWN);
		}

		// get properties
		PropertiesLoader pl = new PropertiesLoader();
		String subjectString = new String();
		subjectString += pl.getProperty("irods.dataone.subject-string");
		String contactSubjectString = new String();
		contactSubjectString += pl.getProperty("irods.dataone.contact-subject-string");

		List<Subject> subjects = new ArrayList<>();
		Subject subject = new Subject();
		subject.setValue(subjectString);
		subjects.add(subject);
		List<Subject> contactSubjects = new ArrayList<>();
		Subject contactSubject = new Subject();
		contactSubject.setValue(contactSubjectString);
		contactSubjects.add(contactSubject);

		node.setPing(ping);
		node.setSubjectList(subjects);
		node.setContactSubjectList(contactSubjects);

		Synchronization sync = new Synchronization();
		// TODO: put correct dates here, pull from RepoService
		sync.setLastCompleteHarvest(new Date());
		sync.setLastHarvested(new Date());
		node.setSynchronization(sync);

		log.info("returning node: {}", node.toString());

		return node;
	}

	@Override
	public Log getLogRecords(final Session session, final Date date1, final Date date2, final Event event,
			final String s, final Integer integer1, final Integer integer2)
			throws InvalidRequest, InvalidToken, NotAuthorized, NotImplemented, ServiceFailure {

		throw new NotImplemented("1461", "Authenticated getLogRecords not implemented");
	}

}
