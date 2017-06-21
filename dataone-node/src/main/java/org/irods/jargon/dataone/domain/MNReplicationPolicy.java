package org.irods.jargon.dataone.domain;

import javax.xml.bind.annotation.XmlAttribute;

public class MNReplicationPolicy {

	@XmlAttribute
	private boolean replicationAllowed;

	public MNReplicationPolicy() {

	}

	public void setReplicationAllowed(final boolean flag) {
		replicationAllowed = flag;
	}

}
