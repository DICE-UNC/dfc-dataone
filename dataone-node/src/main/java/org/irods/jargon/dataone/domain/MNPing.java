package org.irods.jargon.dataone.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.dataone.service.types.v1.Ping;

@XmlAccessorType(XmlAccessType.NONE)
public class MNPing {

	@XmlAttribute
	private boolean success;

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(final boolean success) {
		this.success = success;
	}

	public void copy(final Ping ping) {

		if (ping == null) {
			throw new IllegalArgumentException("MNPing::copy - Ping is null");
		}

		if (ping.getSuccess() != null) {
			success = ping.getSuccess().booleanValue();
		}
	}

}
