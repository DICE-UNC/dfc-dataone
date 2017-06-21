package org.irods.jargon.dataone.domain;

import javax.xml.bind.annotation.XmlType;

import org.dataone.service.types.v1.Checksum;
import org.dataone.service.types.v1.ObjectInfo;
import org.irods.jargon.dataone.utils.ISO8601;

@XmlType(propOrder = { "identifier", "formatId", "checksum",
		"dateSysMetadataModified", "size" })
public class MNObjectInfo {

	private String identifier;
	private String formatId;
	private MNChecksum checksum;
	private String dateSysMetadataModified;
	private long size;

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

	public MNChecksum getChecksum() {
		return checksum;
	}

	public void setChecksum(final MNChecksum checksum) {
		this.checksum = checksum;
	}

	public String getDateSysMetadataModified() {
		return dateSysMetadataModified;
	}

	public void setDateSysMetadataModified(final String dateSysMetadataModified) {
		this.dateSysMetadataModified = dateSysMetadataModified;
	}

	public long getSize() {
		return size;
	}

	public void setSize(final long size) {
		this.size = size;
	}

	public void copy(final ObjectInfo objectInfo) {

		if (objectInfo == null) {
			throw new IllegalArgumentException(
					"MNObjectInfo::copy - ObjectInfo is null");
		}

		if (objectInfo.getIdentifier() != null) {
			identifier = objectInfo.getIdentifier().getValue();
		}

		if (objectInfo.getFormatId() != null) {
			formatId = objectInfo.getFormatId().getValue();
		}

		if (objectInfo.getChecksum() != null) {
			Checksum newChecksum = objectInfo.getChecksum();
			checksum = new MNChecksum();
			checksum.copy(newChecksum);
		}

		if (objectInfo.getDateSysMetadataModified() != null) {
			dateSysMetadataModified = ISO8601.convertToGMTString(objectInfo
					.getDateSysMetadataModified());
		}

		if (objectInfo.getSize() != null) {
			size = objectInfo.getSize().longValue();
		}

	}

}
