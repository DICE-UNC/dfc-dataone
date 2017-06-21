package org.irods.jargon.dataone.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.dataone.service.types.v1.Log;
import org.dataone.service.types.v1.LogEntry;

@XmlRootElement(name = "log")
public class MNLog {
	@XmlAttribute
	private String count;

	@XmlAttribute
	private String start;

	@XmlAttribute
	private String total;

	private List<MNLogEntry> logEntry;

	public List<MNLogEntry> getLogEntry() {
		return logEntry;
	}

	public void setLogEntry(final List<MNLogEntry> logEntry) {
		this.logEntry = logEntry;
	}

	public void copy(final Log log) {

		if (log == null) {
			throw new IllegalArgumentException("MNLogy::copy - Log is null");
		}

		count = String.valueOf(log.getCount());
		start = String.valueOf(log.getStart());
		total = String.valueOf(log.getTotal());

		if (log.getLogEntryList() != null) {
			List<LogEntry> logEntryList = log.getLogEntryList();
			logEntry = new ArrayList<MNLogEntry>();

			for (LogEntry l : logEntryList) {
				MNLogEntry mnLogEntry = new MNLogEntry();
				mnLogEntry.copy(l);
				logEntry.add(mnLogEntry);
			}
		}
	}

}
