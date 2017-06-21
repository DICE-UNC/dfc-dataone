package org.irods.jargon.dataone.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.dataone.service.types.v1.ObjectInfo;
import org.dataone.service.types.v1.ObjectList;

@XmlRootElement(name = "objectList")
public class MNObjectList {

	@XmlAttribute
	private int count;

	@XmlAttribute
	private int start;

	@XmlAttribute
	private int total;

	private List<MNObjectInfo> objectInfo;

	public void setCnt(final int cnt) {
		count = cnt;
	}

	public void setStrt(final int strt) {
		start = strt;
	}

	public void setTot(final int tot) {
		total = tot;
	}

	public List<MNObjectInfo> getObjectInfo() {
		return objectInfo;
	}

	public void setObjectInfo(final List<MNObjectInfo> objectInfo) {
		this.objectInfo = objectInfo;
	}

	public void copy(final ObjectList objectList) {

		if (objectList == null) {
			throw new IllegalArgumentException(
					"MNObjectList::copy - ObjectList is null");
		}

		count = objectList.getCount();
		start = objectList.getStart();
		total = objectList.getTotal();

		if (objectList.getObjectInfoList() != null) {
			List<ObjectInfo> newObjectInfoList = objectList.getObjectInfoList();
			objectInfo = new ArrayList<MNObjectInfo>();

			for (ObjectInfo oi : newObjectInfoList) {
				MNObjectInfo mnObjectInfo = new MNObjectInfo();
				mnObjectInfo.copy(oi);
				objectInfo.add(mnObjectInfo);
			}
		}

	}

}
