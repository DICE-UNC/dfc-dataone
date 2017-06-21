package org.irods.jargon.dataone.domain;

import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.dataone.service.types.v1.Schedule;
import org.irods.jargon.dataone.utils.PropertiesLoader;

@XmlAccessorType(XmlAccessType.NONE)
public class MNSchedule {

	@XmlAttribute
	private String hour;
	@XmlAttribute
	private String mday;
	@XmlAttribute
	private String min;
	@XmlAttribute
	private String mon;
	@XmlAttribute
	private String sec;
	@XmlAttribute
	private String wday;
	@XmlAttribute
	private String year;

	public MNSchedule() {
		initializeProperties();
	}

	private void initializeProperties() {
		PropertiesLoader loader = new PropertiesLoader();
		Properties prop = loader.getProperties();

		hour = prop.getProperty("irods.dataone.sync.schedule.hour");
		mday = prop.getProperty("irods.dataone.sync.schedule.mday");
		min = prop.getProperty("irods.dataone.sync.schedule.min");
		mon = prop.getProperty("irods.dataone.sync.schedule.mon");
		sec = prop.getProperty("irods.dataone.sync.schedule.sec");
		wday = prop.getProperty("irods.dataone.sync.schedule.wday");
		year = prop.getProperty("irods.dataone.sync.schedule.year");

	}

	// public String getHour() {
	// return hour;
	// }
	// public void setHour(String hour) {
	// this.hour = hour;
	// }
	//
	// public String getMday() {
	// return mday;
	// }
	// public void setMday(String mday) {
	// this.mday = mday;
	// }
	//
	// public String getMin() {
	// return min;
	// }
	// public void setMin(String min) {
	// this.min = min;
	// }
	//
	// public String getMon() {
	// return mon;
	// }
	// public void setMon(String mon) {
	// this.mon = mon;
	// }
	//
	// public String getSec() {
	// return sec;
	// }
	// public void setSec(String sec) {
	// this.sec = sec;
	// }
	//
	// public String getWday() {
	// return wday;
	// }
	// public void setWday(String wday) {
	// this.wday = wday;
	// }
	//
	// public String getYear() {
	// return year;
	// }
	// public void setYear(String year) {
	// this.year = year;
	// }

	public void copy(final Schedule s) {

		if (s == null) {
			return;
			// TODO: log something here?
		}

		if (s.getHour() != null) {
			hour = s.getHour();
		}

		if (s.getMday() != null) {
			mday = s.getMday();
		}

		if (s.getMin() != null) {
			min = s.getMin();
		}

		if (s.getMon() != null) {
			mon = s.getMon();
		}

		if (s.getSec() != null) {
			sec = s.getSec();
		}

		if (s.getWday() != null) {
			wday = s.getWday();
		}

		if (s.getYear() != null) {
			year = s.getYear();
		}
	}

}
