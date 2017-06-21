package org.irods.jargon.dataone.domain;

import org.dataone.service.types.v1.Permission;
import org.irods.jargon.core.protovalues.FilePermissionEnum;

public enum MNPermissionEnum {
	READ("read", FilePermissionEnum.READ, Permission.READ), WRITE("write",
			FilePermissionEnum.WRITE, Permission.WRITE), CHANGE(
			"changePermission", FilePermissionEnum.OWN,
			Permission.CHANGE_PERMISSION);

	private String permission;
	private FilePermissionEnum irodsPermissionEnum;
	private Permission dataOnePermissionEnum;

	private MNPermissionEnum(final String p, final FilePermissionEnum i,
			final Permission d) {
		permission = p;
		irodsPermissionEnum = i;
		dataOnePermissionEnum = d;
	}

	public String getPermission() {
		return permission;
	}

	public FilePermissionEnum getIrodsPermissionEnum() {
		return irodsPermissionEnum;
	}

	public Permission getDataOnePermissionEnum() {
		return dataOnePermissionEnum;
	}

	public static MNPermissionEnum valueForWeb(final Permission p) {
		switch (p) {
		case READ:
			return MNPermissionEnum.READ;
		case WRITE:
			return MNPermissionEnum.WRITE;
		case CHANGE_PERMISSION:
			return MNPermissionEnum.CHANGE;
		default:
			return MNPermissionEnum.READ;
		}
	}

	public static FilePermissionEnum valueForIrods(final Permission p) {
		switch (p) {
		case READ:
			return FilePermissionEnum.READ;
		case WRITE:
			return FilePermissionEnum.WRITE;
		case CHANGE_PERMISSION:
			return FilePermissionEnum.OWN;
		default:
			return FilePermissionEnum.READ;
		}
	}

	public static Permission valueForDataOne(final FilePermissionEnum p) {
		switch (p) {
		case READ:
			return Permission.READ;
		case WRITE:
			return Permission.WRITE;
		case OWN:
			return Permission.CHANGE_PERMISSION;
		default:
			return Permission.READ;
		}
	}
}
