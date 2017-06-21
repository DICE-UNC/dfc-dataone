package org.irods.jargon.dataone.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.dataone.service.exceptions.SynchronizationFailed;

@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class MNError {

	@XmlAttribute
	private String detailCode;
	@XmlAttribute
	private String errorCode;
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String pid;
	@XmlElement
	private String description;

	public MNError() {
	}

	public String getDetailCode() {
		return detailCode;
	}

	public void setDetailCode(final String detailCode) {
		this.detailCode = detailCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(final String errorCode) {
		this.errorCode = errorCode;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(final String pid) {
		this.pid = pid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public SynchronizationFailed copyToSynchronizationFailed() {
		SynchronizationFailed syncFailed = new SynchronizationFailed(
				detailCode, description);

		syncFailed.setPid(pid);

		return syncFailed;

	}
}
