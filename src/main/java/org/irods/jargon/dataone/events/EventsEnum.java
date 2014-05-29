package org.irods.jargon.dataone.events;

import org.dataone.service.types.v1.Event;

// Use this class to convert iRODS/Databook type events to DataONE events
public enum EventsEnum {
	
	CREATE(Event.CREATE, "put"),
	READ(Event.READ, "get"),
	UPDATE(Event.UPDATE, "overwrite"),
	DELETE(Event.DELETE, "delete"),
	REPLICATE(Event.REPLICATE, "replicate"),
	SYNCHRONIZATION_FAILED(Event.SYNCHRONIZATION_FAILED, "synch_failure"),;
	//REPLICATION_FAILED(),; not supported yet
	
	private String databookEvent;
	private Event dataoneEvent;	
	
	EventsEnum(Event dataoneEvent, String databookEvent) {	
	}

	
	public Event getDataoneEvent() {
		return dataoneEvent;
	}
	public void setDataoneEvent(Event dataoneEvent) {
		this.dataoneEvent = dataoneEvent;
	}

	public String getDatabookEvent() {
		return databookEvent;
	}
	public void setDatabookEvent(String databookEvent) {
		this.databookEvent = databookEvent;
	}
	
	public static EventsEnum valueOfFromDataOne(Event e) {
		switch (e) {
		case CREATE:
			return CREATE;
		case READ:
			return READ;
		case UPDATE:
			return UPDATE;
		case DELETE:
			return DELETE;
		case REPLICATE:
			return REPLICATE;
		case SYNCHRONIZATION_FAILED:
			return SYNCHRONIZATION_FAILED;
		//case REPLICATION_FAILED: not supported yet
		default:
			return null;
		}
	}
	
	public static EventsEnum valueOfFromDatabook(String e) {
		if (e.equals("put"))
			return CREATE;
		else if (e.equals("get"))
			return READ;
		else if (e.equals("overwrite"))
			return UPDATE;
		else if (e.equals("delete"))
			return DELETE;
		else if (e.equals("replicate"))
			return REPLICATE;
		else if (e.equals("synch_failure"))
			return SYNCHRONIZATION_FAILED;
		//else if (e.equals("?"))
			//return REPLICATION_FAILED; not supported yet
		else
			return null;
	}

}