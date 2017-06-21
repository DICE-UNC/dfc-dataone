/**
 * 
 */
package org.irods.jargon.dataone.def.event.persist.dao.domain;

import org.hibernate.annotations.ColumnTransformer;
import org.irods.jargon.dataone.events.EventsEnum;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * 
 * @author mcc
 *
 */
@Entity
@Table(name = "event_log")
public class AccessLog {

	@Id()
	@GeneratedValue
	@Column(name = "id", columnDefinition = "uuid", updatable = false)
	private UUID id;

	@Column(name = "permanent_id", nullable = true)
	private String permanentId;

	@Column(name = "ip_address", nullable = true)
	private String ipAddress;

	@Column(name = "irods_path", nullable = true)
	private String irodsPath;

	@Column(name = "user_agent", nullable = true)
	private String userAgent;

	@Column(name = "subject", nullable = true)
	private String subject;

	@Column(name = "event", columnDefinition = "event_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private EventsEnum event;

	@Column(name = "date_logged")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date dateAdded;

	@Column(name = "node_identifier", nullable = false)
	private String nodeIdentifier;

	/**
	 * 
	 */
	public AccessLog() {
	}

	/**
	 * @return the dateAdded
	 */
	public Date getDateAdded() {
		return dateAdded;
	}

	/**
	 * @param dateAdded
	 *            the dateAdded to set
	 */
	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	/**
	 * 
	 * 
	 * /**
	 * 
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the userAgent
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * @param userAgent
	 *            the userAgent to set
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the nodeIdentifier
	 */
	public String getNodeIdentifier() {
		return nodeIdentifier;
	}

	/**
	 * @param nodeIdentifier
	 *            the nodeIdentifier to set
	 */
	public void setNodeIdentifier(String nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}

	/**
	 * @return the permanentId
	 */
	public String getPermanentId() {
		return permanentId;
	}

	/**
	 * @param permanentId
	 *            the permanentId to set
	 */
	public void setPermanentId(String permanentId) {
		this.permanentId = permanentId;
	}

	/**
	 * @return the irodsPath
	 */
	public String getIrodsPath() {
		return irodsPath;
	}

	/**
	 * @param irodsPath
	 *            the irodsPath to set
	 */
	public void setIrodsPath(String irodsPath) {
		this.irodsPath = irodsPath;
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccessLog [");
		if (id != null) {
			builder.append("id=").append(id).append(", ");
		}
		if (permanentId != null) {
			builder.append("permanentId=").append(permanentId).append(", ");
		}
		if (ipAddress != null) {
			builder.append("ipAddress=").append(ipAddress).append(", ");
		}
		if (irodsPath != null) {
			builder.append("irodsPath=").append(irodsPath).append(", ");
		}
		if (userAgent != null) {
			builder.append("userAgent=").append(userAgent).append(", ");
		}
		if (subject != null) {
			builder.append("subject=").append(subject).append(", ");
		}
		if (event != null) {
			builder.append("event=").append(event).append(", ");
		}
		if (dateAdded != null) {
			builder.append("dateAdded=").append(dateAdded).append(", ");
		}
		if (nodeIdentifier != null) {
			builder.append("nodeIdentifier=").append(nodeIdentifier);
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the event
	 */
	public EventsEnum getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(EventsEnum event) {
		this.event = event;
	}

}
