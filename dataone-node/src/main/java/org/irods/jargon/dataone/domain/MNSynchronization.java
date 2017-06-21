package org.irods.jargon.dataone.domain;

import javax.xml.bind.annotation.XmlType;

import org.dataone.service.types.v1.Schedule;
import org.dataone.service.types.v1.Synchronization;
import org.irods.jargon.dataone.utils.ISO8601;

@XmlType(propOrder = { "schedule", "lastHarvested", "lastCompleteHarvest" })
public class MNSynchronization {

	private String lastHarvested;
	private String lastCompleteHarvest;
	private MNSchedule schedule;

	public MNSynchronization() {
		schedule = new MNSchedule();
	}

	public String getLastHarvested() {
		return lastHarvested;
	}

	public void setLastHarvested(final String lastHarvested) {
		this.lastHarvested = lastHarvested;
	}

	public String getLastCompleteHarvest() {
		return lastCompleteHarvest;
	}

	public void setLastCompleteHarvest(final String lastCompleteHarvest) {
		this.lastCompleteHarvest = lastCompleteHarvest;
	}

	public MNSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(final MNSchedule schedule) {
		this.schedule = schedule;
	}

	public void copy(final Synchronization synch) {

		if (synch == null) {
			throw new IllegalArgumentException(
					"MNSynchronization::copy - Synchronization is null");
		}

		if (synch.getLastHarvested() != null) {
			lastHarvested = ISO8601
					.convertToGMTString(synch.getLastHarvested());
		}

		if (synch.getLastCompleteHarvest() != null) {
			lastCompleteHarvest = ISO8601.convertToGMTString(synch
					.getLastCompleteHarvest());
		}

		if (synch.getSchedule() != null) {
			Schedule s = synch.getSchedule();
			schedule = new MNSchedule();
			schedule.copy(s);
		}
	}

}
