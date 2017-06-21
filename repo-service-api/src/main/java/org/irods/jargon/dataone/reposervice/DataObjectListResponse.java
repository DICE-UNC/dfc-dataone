package org.irods.jargon.dataone.reposervice;

import java.util.List;

import org.irods.jargon.core.pub.domain.DataObject;

public class DataObjectListResponse {
	
	List<DataObject> dataObjects;
	int total;
	int count;
	
	public List<DataObject> getDataObjects() {
		return dataObjects;
	}
	public void setDataObjects(List<DataObject> dataObjects) {
		this.dataObjects = dataObjects;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
