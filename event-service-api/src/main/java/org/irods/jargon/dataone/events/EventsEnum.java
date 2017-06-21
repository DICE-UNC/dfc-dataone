package org.irods.jargon.dataone.events;

import org.dataone.service.types.v1.Event;

// Use this class to convert iRODS/Databook type events to DataONE events
public enum EventsEnum {
	FAILED(Event.REPLICATION_FAILED, "failed"), CREATE(Event.CREATE, "put"), READ(
			Event.READ, "get"), UPDATE(Event.UPDATE, "overwrite"), DELETE(
			Event.DELETE, "delete"), REPLICATE(Event.REPLICATE, "replicate"), SYNCHRONIZATION_FAILED(
			Event.SYNCHRONIZATION_FAILED, "synch_failure"), ;
	// REPLICATION_FAILED(),; not supported yet

	private String internalEvent;
	private Event dataoneEvent;

	EventsEnum(final Event dataoneEvent, final String databookEvent) {
		this.internalEvent = databookEvent;
		this.dataoneEvent = dataoneEvent;
	}

	public Event getDataoneEvent() {
		return dataoneEvent;
	}

	public void setDataoneEvent(final Event dataoneEvent) {
		this.dataoneEvent = dataoneEvent;
	}

	public String getInternalEvent() {
		return internalEvent;
	}

	public void setInternalEvent(final String internalEvent) {
		this.internalEvent = internalEvent;
	}

	public static EventsEnum valueOfFromDataOne(final Event e) {
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
			// case REPLICATION_FAILED: not supported yet
		default:
			return READ;
		}
	}

	public static EventsEnum valueFromInternal(final String e) {
		if (e.equals("put") || e.equals("data object put")) {
			return CREATE;
		} else if (e.equals("get") || e.equals("data object get")) {
			return READ;
		} else if (e.equals("overwrite") || e.equals("data object overwrite")) {
			return UPDATE;
		} else if (e.equals("delete") || e.equals("data object delete")) {
			return DELETE;
		} else if (e.equals("replicate") || e.equals("data object replicate")) {
			return REPLICATE;
		} else if (e.equals("synch_failure")
				|| e.equals("data object synch_failure")) {
			return SYNCHRONIZATION_FAILED;
			// else if (e.equals("?"))
			// return REPLICATION_FAILED; not supported yet
		} else {
			return FAILED;
		}
	}

}