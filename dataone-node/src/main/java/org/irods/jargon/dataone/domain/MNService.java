package org.irods.jargon.dataone.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.dataone.service.types.v1.Service;

@XmlAccessorType(XmlAccessType.NONE)
public class MNService {

	@XmlAttribute
	private String name;
	@XmlAttribute
	private String version;
	@XmlAttribute
	private boolean available;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(final boolean available) {
		this.available = available;
	}

	public void copy(final Service service) {
		if (service == null) {
			throw new IllegalArgumentException(
					"MNService::copy - Service is null");
		}

		if (service.getName() != null) {
			name = service.getName();
		}

		if (service.getVersion() != null) {
			version = service.getVersion();
		}

		if (service.getAvailable() != null) {
			available = service.getAvailable().booleanValue();
		}
	}

}
