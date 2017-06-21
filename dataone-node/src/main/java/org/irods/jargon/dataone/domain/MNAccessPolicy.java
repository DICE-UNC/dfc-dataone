package org.irods.jargon.dataone.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "subject", "permission" })
public class MNAccessPolicy {

	private String subject; // MNSubject type?
	private List<String> permission;

	public MNAccessPolicy() {

	}

	@XmlElement(name = "permission")
	public List<String> getPermission() {
		return permission;
	}

	public void setPermission(final List<String> p) {
		permission = p;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

}
