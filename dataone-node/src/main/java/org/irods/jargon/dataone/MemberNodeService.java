package org.irods.jargon.dataone;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXB;

import org.dataone.service.exceptions.InsufficientResources;
import org.dataone.service.exceptions.InvalidRequest;
import org.dataone.service.exceptions.InvalidToken;
import org.dataone.service.exceptions.NotAuthorized;
import org.dataone.service.exceptions.NotFound;
import org.dataone.service.exceptions.NotImplemented;
import org.dataone.service.exceptions.ServiceFailure;
import org.dataone.service.exceptions.SynchronizationFailed;
import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.DescribeResponse;
import org.dataone.service.types.v1.Event;
import org.dataone.service.types.v1.Identifier;
import org.dataone.service.types.v1.Log;
import org.dataone.service.types.v1.Node;
import org.dataone.service.types.v1.ObjectFormatIdentifier;
import org.dataone.service.types.v1.ObjectList;
import org.dataone.service.types.v1.SystemMetadata;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.dataone.auth.RestAuthUtils;
import org.irods.jargon.dataone.configuration.PluginDiscoveryService;
import org.irods.jargon.dataone.configuration.PluginNotFoundException;
import org.irods.jargon.dataone.configuration.PublicationContext;
import org.irods.jargon.dataone.domain.MNChecksum;
import org.irods.jargon.dataone.domain.MNError;
import org.irods.jargon.dataone.domain.MNLog;
import org.irods.jargon.dataone.domain.MNNode;
import org.irods.jargon.dataone.domain.MNObjectList;
import org.irods.jargon.dataone.domain.MNSystemMetadata;
import org.irods.jargon.dataone.events.DataOneEventServiceAO;
import org.irods.jargon.dataone.tier1.MNCoreImpl;
import org.irods.jargon.dataone.tier1.MNReadImpl;
import org.irods.jargon.dataone.utils.ISO8601;
import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Lisa Stillwell - RENCI, UNC @ Chapel Hill Date: 10/4/13 Time: 2:22 PM
 */

@Named
@Path("/mn/v1")
public class MemberNodeService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PublicationContext publicationContext;
	@Autowired
	private PluginDiscoveryService pluginDiscoveryService;

	@GET
	@Path("/monitor/ping")
	@Produces(MediaType.TEXT_XML)
	@Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/dfc-dataone", jsonName = "dfc-dataone") })
	// public Response handlePing(@HeaderParam("Authorization") final String
	// authorization)
	public Response handlePing() throws NotImplemented, ServiceFailure, InsufficientResources, JargonException {

		if (publicationContext == null) {
			throw new IllegalArgumentException("null publicationContext");
		}

		MNCoreImpl mnCoreImpl = new MNCoreImpl(publicationContext, pluginDiscoveryService);
		Date irodsDate = mnCoreImpl.ping();

		SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		String dateStr = df.format(irodsDate);

		Response.ResponseBuilder builder = Response.ok();
		builder.header("Date", dateStr);
		builder.type("text/xml");
		builder.header("Content-Length", "0");

		return builder.build();
	}

	@GET
	@Path("/node")
	@Produces(MediaType.TEXT_XML)
	@Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/dfc-dataone", jsonName = "dfc-dataone") })
	public MNNode handleGetCapabilities() throws NotImplemented, ServiceFailure {

		MNNode nodeCapabilities = new MNNode();

		MNCoreImpl mnCoreImpl = new MNCoreImpl(publicationContext, pluginDiscoveryService);
		Node node = mnCoreImpl.getCapabilities();

		nodeCapabilities.copy(node);

		return nodeCapabilities;
	}

	@GET
	@Path("/")
	@Produces(MediaType.TEXT_XML)
	@Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/dfc-dataone", jsonName = "dfc-dataone") })
	public MNNode handleDefaultGetCapabilities() throws NotImplemented, ServiceFailure {

		MNNode nodeCapabilities = handleGetCapabilities();

		return nodeCapabilities;
	}

	// log?[fromDate={fromDate}][&toDate={toDate}][&event={event}][&pidFilter={pidFilter}][&start={start}][&count={count}]
	@GET
	@Path("/log")
	@Produces(MediaType.TEXT_XML)
	@Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/dfc-dataone", jsonName = "dfc-dataone") })
	public MNLog handleGetLogRecords(@QueryParam("fromDate") final String fromDateStr,
			@QueryParam("toDate") final String toDateStr, @QueryParam("event") final String event,
			@QueryParam("pidFilter") final String pidFilter, @DefaultValue("0") @QueryParam("start") final int start,
			@DefaultValue("500") @QueryParam("count") final int count)
			throws NotImplemented, ServiceFailure, NotAuthorized, InvalidRequest, InvalidToken {

		logger.info("/log request: fromData={} toDate={}", fromDateStr, toDateStr);
		logger.info("/log request: event={} pidFilter={}", event, pidFilter);
		logger.info("/log request: start={} count={}", start, count);

		MNCoreImpl mnCoreImpl = new MNCoreImpl(publicationContext, pluginDiscoveryService);

		// parse date strings
		Date fromDate = null;
		Date toDate = null;
		try {
			if (fromDateStr != null) {
				fromDate = ISO8601.toCalendar(fromDateStr).getTime();
			}
			if (toDateStr != null) {
				toDate = ISO8601.toCalendar(toDateStr).getTime();
			}
		} catch (ParseException e) {
			logger.error("handleListObjects: unable to parse query dates");
			throw new InvalidRequest("1480", e.getMessage());
		}

		Event e = Event.convert(event);

		Log log = mnCoreImpl.getLogRecords(fromDate, toDate, e, pidFilter, start, count);
		logger.info("returned log={}", log.toString());

		// set Log attributes in MNLog
		MNLog mnLog = new MNLog();
		mnLog.copy(log);

		return mnLog;
	}

	@GET
	@Path("/object/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void handleRead(@PathParam("id") final String pid, @Context final HttpServletResponse response)
			throws InvalidToken, ServiceFailure, NotAuthorized, NotFound, NotImplemented, InsufficientResources {

		MNReadImpl mnReadImpl = new MNReadImpl(publicationContext, pluginDiscoveryService);

		Identifier id = new Identifier();
		id.setValue(pid);
		// Had to write new method in mnReadImpl since I
		// couldn't figure out how to override the get method
		mnReadImpl.streamObject(response, id);

		try {
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(publicationContext.getRestConfiguration());
			DataOneEventServiceAO eventServiceAO = pluginDiscoveryService.instanceEventService(irodsAccount);
			eventServiceAO.recordEvent(Event.READ, id, "DataONE replication");

		} catch (PluginNotFoundException | JargonException e) {
			logger.error("Unable to log EVENT: {} for data object id: {}", Event.READ, pid);
		}

	}

	@GET
	@Path("/checksum/{id}")
	@Produces(MediaType.TEXT_XML)
	public MNChecksum handleGetChecksum(@PathParam("id") final String pid,
			@DefaultValue("MD5") @QueryParam("checksumAlgorithm") final String algorithm)
			throws InvalidToken, ServiceFailure, NotAuthorized, NotFound, NotImplemented, InvalidRequest {

		MNChecksum mnChecksum = new MNChecksum();

		if (!algorithm.toUpperCase().equals("MD5")) {
			throw new InvalidRequest("1402", "invalid checksum algorithm requested - only MD5 supported");
		}

		Identifier id = new Identifier();
		id.setValue(pid);

		MNReadImpl mnReadImpl = new MNReadImpl(publicationContext, pluginDiscoveryService);
		Checksum checksum = mnReadImpl.getChecksum(id, algorithm);

		mnChecksum.copy(checksum);

		return mnChecksum;
	}

	@GET
	@Path("/replica/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void handleReplica(@PathParam("id") final String pid, @Context final HttpServletResponse response)
			throws InvalidToken, ServiceFailure, NotAuthorized, NotFound, NotImplemented, InsufficientResources {

		MNReadImpl mnReadImpl = new MNReadImpl(publicationContext, pluginDiscoveryService);

		Identifier id = new Identifier();
		id.setValue(pid);

		// Had to write new method in mnReadImpl since I
		// couldn't figure out how to override the get method
		mnReadImpl.streamObject(response, id);

		try {
			IRODSAccount irodsAccount = RestAuthUtils
					.getIRODSAccountFromBasicAuthValues(publicationContext.getRestConfiguration());
			DataOneEventServiceAO eventServiceAO = pluginDiscoveryService.instanceEventService(irodsAccount);
			eventServiceAO.recordEvent(Event.READ, id, "DataONE replication");

		} catch (PluginNotFoundException | JargonException e) {
			logger.error("Unable to log EVENT: {} for data object id: {}", Event.READ, pid);
		}

	}

	@GET
	@Path("/meta/{id}")
	@Produces(MediaType.TEXT_XML)
	public MNSystemMetadata handleGetSystemMetadata(@PathParam("id") final String pid)
			// @Context final HttpServletResponse response)
			throws InvalidToken, ServiceFailure, NotAuthorized, NotFound, NotImplemented, InvalidRequest {

		MNSystemMetadata mnSystemMetadata = new MNSystemMetadata();

		Identifier id = new Identifier();
		id.setValue(pid);

		MNReadImpl mnReadImpl = new MNReadImpl(publicationContext, pluginDiscoveryService);
		SystemMetadata sysMetadata = mnReadImpl.getSystemMetadata(id);

		mnSystemMetadata.copy(sysMetadata);

		return mnSystemMetadata;
	}

	@HEAD
	@Path("/object/{id}")
	@Produces(MediaType.TEXT_XML)
	@Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/dfc-dataone", jsonName = "dfc-dataone") })
	public Response handleDescribe(@PathParam("id") final String pid)
			// @Context final HttpServletResponse response)
			throws NotAuthorized, NotImplemented, ServiceFailure, NotFound, InvalidToken {

		Identifier id = new Identifier();
		id.setValue(pid);

		DescribeResponse describeResponse;

		MNReadImpl mnReadImpl = new MNReadImpl(publicationContext, pluginDiscoveryService);
		try {
			describeResponse = mnReadImpl.describe(id);
		} catch (NotFound ex) {

			Response.ResponseBuilder builder = Response.ok();
			builder.status(Status.NOT_FOUND);
			builder.type(MediaType.TEXT_XML);
			builder.header("DataONE-Exception-Name", "NotFound");
			builder.header("DataONE-Exception-DetailCode", ex.getDetail_code());
			builder.header("DataONE-Exception-Description", ex.getDescription());
			builder.header("DataONE-Exception-PID", pid);

			return builder.build();
		}

		Checksum checksum = describeResponse.getDataONE_Checksum();
		String checksumStr = checksum.getAlgorithm() + "," + checksum.getValue();

		// Convert data to GMT and format for HTTP header
		DateFormat gmtFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ");
		TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
		gmtFormat.setTimeZone(gmtTimeZone);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(describeResponse.getLast_Modified());
		calendar.setTimeZone(gmtTimeZone);
		String dateStr = gmtFormat.format(calendar.getTime());
		dateStr += "GMT";

		Response.ResponseBuilder builder = Response.ok();
		builder.header("Last-Modified", dateStr);
		builder.header("Content-Length", describeResponse.getContent_Length().toString());
		builder.type(MediaType.APPLICATION_OCTET_STREAM);
		builder.header("DataONE-formatId", describeResponse.getDataONE_ObjectFormatIdentifier().getValue().toString());
		builder.header("DataONE-Checksum", checksumStr);
		builder.header("DataONE-SerialVersion", describeResponse.getSerialVersion().toString());

		return builder.build();
	}

	@POST
	@Path("/error")
	@Produces(MediaType.TEXT_XML)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Mapped(namespaceMap = { @XmlNsMap(namespace = "http://irods.org/dfc-dataone", jsonName = "dfc-dataone") })
	public Response handleSynchronizationFailed(final MultipartFormDataInput input)
			throws NotAuthorized, NotImplemented, ServiceFailure, InvalidToken {

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("message");
		if (inputParts == null || inputParts.size() == 0) {
			throw new ServiceFailure("2161", "Synch Failure Exception cannot parse message");
		}
		String str_xml = null;
		try {
			str_xml = inputParts.get(0).getBodyAsString();
		} catch (IOException e) {
			throw new ServiceFailure("2161", "Synch Failure Exception cannot extract message xml");
		}

		MNError error = JAXB.unmarshal(new StringReader(str_xml), MNError.class);
		SynchronizationFailed message = error.copyToSynchronizationFailed();

		if (message == null) {
			throw new ServiceFailure("2161", "Synch Failure Exception message is null");
		}

		MNReadImpl mnReadImpl = new MNReadImpl(publicationContext, pluginDiscoveryService);
		boolean success = mnReadImpl.synchronizationFailed(message);

		if (!success) {
			throw new ServiceFailure("2161", "Failed to log Synchronization Failure event");
		}

		Response.ResponseBuilder builder = Response.ok();

		return builder.build();
	}

	// GET /object[?fromDate={fromDate}&toDate={toDate}&formatId={formatId}
	// &replicaStatus={replicaStatus} &start={start}&count={count}]
	@GET
	@Path("/object")
	@Produces(MediaType.TEXT_XML)
	public MNObjectList handleListObjects(@QueryParam("fromDate") final String fromDateStr,
			@QueryParam("toDate") final String toDateStr, @QueryParam("formatId") final String formatIdStr,
			@QueryParam("replicaStatus") final Boolean replicaStatus,
			@DefaultValue("0") @QueryParam("start") final Integer start,
			@DefaultValue("500") @QueryParam("count") final Integer count)
			throws InvalidToken, ServiceFailure, NotAuthorized, InvalidRequest, NotImplemented {

		MNObjectList mnObjectList = new MNObjectList();

		// parse any query arguments to convert into correct formats,
		// if not handled by data type constructors
		ObjectFormatIdentifier formatId = new ObjectFormatIdentifier();
		formatId.setValue(formatIdStr);

		Date fromDate = null;
		Date toDate = null;
		try {
			if (fromDateStr != null) {
				fromDate = ISO8601.toCalendar(fromDateStr).getTime();
			}
			if (toDateStr != null) {
				toDate = ISO8601.toCalendar(toDateStr).getTime();
			}
		} catch (ParseException e) {
			logger.error("handleListObjects: unable to parse query dates");
			throw new InvalidRequest("1540", e.getMessage());
		}

		MNReadImpl mnReadImpl = new MNReadImpl(publicationContext, pluginDiscoveryService);
		ObjectList objectList = mnReadImpl.listObjects(fromDate, toDate, formatId, replicaStatus, start, count);
		mnObjectList.copy(objectList);

		return mnObjectList;
	}

	/**
	 * @return the publicationContext
	 */
	public PublicationContext getPublicationContext() {
		return publicationContext;
	}

	/**
	 * @return the pluginDiscoveryService
	 */
	public PluginDiscoveryService getPluginDiscoveryService() {
		return pluginDiscoveryService;
	}
}
