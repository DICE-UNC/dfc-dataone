package org.irods.jargon.dataone.domain;

import javax.xml.bind.annotation.XmlType;

import org.dataone.service.types.v1.LogEntry;
import org.irods.jargon.dataone.utils.ISO8601;

//@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "entryId", "identifier", "ipAddress", "userAgent",
		"subject", "event", "dateLogged", "nodeIdentifier" })
public class MNLogEntry {

	private String entryId;
	private String dateLogged;
	private String event;
	private String identifier;
	private String nodeIdentifier;
	private String ipAddress;
	private String userAgent;
	private String subject;

	public String getEntryId() {
		return entryId;
	}

	public void setEntryId(final String entryId) {
		this.entryId = entryId;
	}

	public String getDateLogged() {
		return dateLogged;
	}

	public void setDateLogged(final String dateLogged) {
		this.dateLogged = dateLogged;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(final String event) {
		this.event = event;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

	public String getNodeIdentifier() {
		return nodeIdentifier;
	}

	public void setNodeIdentifier(final String nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(final String userAgent) {
		this.userAgent = userAgent;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public void copy(final LogEntry logEntry) {

		if (logEntry == null) {
			throw new IllegalArgumentException(
					"MNLogEntry::copy - LogEntry is null");
		}

		if (logEntry.getEntryId() != null) {
			entryId = logEntry.getEntryId();
		}

		if (logEntry.getDateLogged() != null) {
			dateLogged = ISO8601.convertToGMTString(logEntry.getDateLogged());
		}

		if (logEntry.getEvent() != null) {
			event = logEntry.getEvent().xmlValue();
		}

		if (logEntry.getIdentifier() != null) {
			identifier = logEntry.getIdentifier().getValue();
		}

		if (logEntry.getNodeIdentifier() != null) {
			nodeIdentifier = logEntry.getNodeIdentifier().getValue();
		}

		if (logEntry.getIpAddress() != null) {
			ipAddress = logEntry.getIpAddress();
		}

		else {
			// need to use empty string to make this tag show up
			ipAddress = new String();
		}

		if (logEntry.getUserAgent() != null) {
			userAgent = logEntry.getUserAgent();
		} else {
			// need to use empty string to make this tag show up
			userAgent = new String();
		}

		if (logEntry.getSubject() != null) {
			subject = logEntry.getSubject().getValue();
		}

	}

}
