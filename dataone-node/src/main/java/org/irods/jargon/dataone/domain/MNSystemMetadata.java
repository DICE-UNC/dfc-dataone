package org.irods.jargon.dataone.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.dataone.service.types.v1.AccessRule;
import org.dataone.service.types.v1.Permission;
import org.dataone.service.types.v1.SystemMetadata;
import org.irods.jargon.dataone.utils.ISO8601;

@XmlRootElement(name = "systemMetadata")
@XmlType(propOrder = { "serialVersion", "identifier", "formatId", "size",
		"checksum", "submitter", "rightsHolder", "accessPolicy",
		"replicationPolicy", "obsoletes", "obsoletedBy", "archived",
		"dateUploaded", "dateSysMetadataModified", "originMemberNode",
		"authoritativeMemberNode" })
public class MNSystemMetadata {

	private long serialVersion;
	private String identifier;
	private String formatId;
	private long size;
	private MNChecksum checksum;
	// private MNSubject submitter; NOTE: a Subject is just a string
	// private MNSubject rightsHolder;
	private String submitter;
	private String rightsHolder;
	private List<MNAccessPolicy> accessPolicy;
	private MNReplicationPolicy replicationPolicy;
	private String obsoletes;
	private String obsoletedBy;
	private boolean archived;
	private String dateUploaded;
	private String dateSysMetadataModified;
	private String originMemberNode;
	private String authoritativeMemberNode;

	public MNSystemMetadata() {

	}

	@XmlElementWrapper(name = "accessPolicy")
	@XmlElement(name = "allow")
	public List<MNAccessPolicy> getAccessPolicy() {
		return accessPolicy;
	}

	public void setAccessPolicy(final List<MNAccessPolicy> ap) {
		accessPolicy = ap;
	}

	public long getSerialVersion() {
		return serialVersion;
	}

	public void setSerialVersion(final long serialVersion) {
		this.serialVersion = serialVersion;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

	public String getFormatId() {
		return formatId;
	}

	public void setFormatId(final String formatId) {
		this.formatId = formatId;
	}

	public long getSize() {
		return size;
	}

	public void setSize(final long size) {
		this.size = size;
	}

	public MNChecksum getChecksum() {
		return checksum;
	}

	public void setChecksum(final MNChecksum checksum) {
		this.checksum = checksum;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(final String submitter) {
		this.submitter = submitter;
	}

	public String getRightsHolder() {
		return rightsHolder;
	}

	public void setRightsHolder(final String rightsHolder) {
		this.rightsHolder = rightsHolder;
	}

	public MNReplicationPolicy getReplicationPolicy() {
		return replicationPolicy;
	}

	public void setReplicationPolicy(final MNReplicationPolicy replicationPolicy) {
		this.replicationPolicy = replicationPolicy;
	}

	public String getObsoletes() {
		return obsoletes;
	}

	public void setObsoletes(final String obsoletes) {
		this.obsoletes = obsoletes;
	}

	public String getObsoletedBy() {
		return obsoletedBy;
	}

	public void setObsoletedBy(final String obsoletedBy) {
		this.obsoletedBy = obsoletedBy;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(final boolean archived) {
		this.archived = archived;
	}

	public String getDateUploaded() {
		return dateUploaded;
	}

	public void setDateUploaded(final String dateUploaded) {
		this.dateUploaded = dateUploaded;
	}

	public String getDateSysMetadataModified() {
		return dateSysMetadataModified;
	}

	public void setDateSysMetadataModified(final String dateSysMetadataModified) {
		this.dateSysMetadataModified = dateSysMetadataModified;
	}

	public String getOriginMemberNode() {
		return originMemberNode;
	}

	public void setOriginMemberNode(final String originMemberNode) {
		this.originMemberNode = originMemberNode;
	}

	public String getAuthoritativeMemberNode() {
		return authoritativeMemberNode;
	}

	public void setAuthoritativeMemberNode(final String authoritativeMemberNode) {
		this.authoritativeMemberNode = authoritativeMemberNode;
	}

	public void copy(final SystemMetadata metadata) {
		if (metadata == null) {
			throw new IllegalArgumentException(
					"MNSystemMetadata::copy - SystemMetadata is null");
		}

		if (metadata.getSerialVersion() != null) {
			serialVersion = metadata.getSerialVersion().longValue();
		}

		if (metadata.getIdentifier() != null) {
			identifier = metadata.getIdentifier().getValue();
		}

		if (metadata.getFormatId() != null) {
			formatId = metadata.getFormatId().getValue();
		}

		if (metadata.getSize() != null) {
			size = metadata.getSize().longValue();
		}

		if (metadata.getChecksum() != null) {
			MNChecksum checksum = new MNChecksum();
			checksum.setValue(metadata.getChecksum().getValue());
			checksum.setAlgorithm(metadata.getChecksum().getAlgorithm());
			this.checksum = checksum;
		}

		if (metadata.getSubmitter() != null) {
			submitter = metadata.getSubmitter().getValue();
		}

		if (metadata.getRightsHolder() != null) {
			rightsHolder = metadata.getRightsHolder().getValue();
		}

		if ((metadata.getAccessPolicy() != null)
				&& (metadata.getAccessPolicy().sizeAllowList()) > 0) {
			List<MNAccessPolicy> accessPolicies = new ArrayList<MNAccessPolicy>();
			for (AccessRule rule : metadata.getAccessPolicy().getAllowList()) {
				MNAccessPolicy policy = new MNAccessPolicy();
				policy.setSubject(rule.getSubject(0).getValue());
				List<String> permissions = new ArrayList<String>();
				for (Permission p : rule.getPermissionList()) {
					permissions.add(p.xmlValue());
				}
				policy.setPermission(permissions);
				accessPolicies.add(policy);
			}
			accessPolicy = accessPolicies;
		}

		if (metadata.getReplicationPolicy() != null) {
			replicationPolicy = new MNReplicationPolicy();
			replicationPolicy.setReplicationAllowed(metadata
					.getReplicationPolicy().getReplicationAllowed());
		}

		if (metadata.getObsoletes() != null) {
			obsoletes = metadata.getObsoletes().getValue();
		}

		if (metadata.getObsoletedBy() != null) {
			obsoletedBy = metadata.getObsoletedBy().getValue();
		}

		if (metadata.getArchived() != null) {
			archived = metadata.getArchived();
		}

		dateUploaded = ISO8601.convertToGMTString(metadata.getDateUploaded());
		dateSysMetadataModified = ISO8601.convertToGMTString(metadata
				.getDateSysMetadataModified());

		if (metadata.getOriginMemberNode() != null) {
			originMemberNode = metadata.getOriginMemberNode().getValue();
		}

		if (metadata.getAuthoritativeMemberNode() != null) {
			authoritativeMemberNode = metadata.getAuthoritativeMemberNode()
					.getValue();
		}

		return;
	}

}
