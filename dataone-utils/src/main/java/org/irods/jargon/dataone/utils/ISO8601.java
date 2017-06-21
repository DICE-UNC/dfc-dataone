package org.irods.jargon.dataone.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ISO8601 {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/** Transform Calendar to ISO 8601 string. */
	public static String fromCalendar(final Calendar calendar) {
		Date date = calendar.getTime();
		String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
				.format(date);
		// add ":" to timezone offset, like this 2013-10-10T13:13:23.000-04:00
		return formatted.substring(0, 26) + ":" + formatted.substring(26);
	}

	/** Get current date and time formatted as ISO 8601 string. */
	public static String now() {
		return fromCalendar(Calendar.getInstance());
	}

	/** Transform ISO 8601 string to Calendar. */
	// have to handle this: 2016-04-12T18:18:00+00:00
	// and this 2016-04-12T18:18:00.000+00:00
	// and this 2016-04-12T18:18:00.001
	// and this 2016-04-12T18:18:00
	// and this 2016-04-12T18:18:00+00:00
	// etc
	public static Calendar toCalendar(final String iso8601string)
			throws ParseException {
		Calendar calendar = Calendar.getInstance();
		String s = iso8601string;

		// add the GMT timezone default, if it is not explicitly specified
		if ((!iso8601string.contains("+")) && (!iso8601string.contains("-"))) {
			// GMT is default
			s = s + "+0000";
		} else {
			// need to remove ":" from timezone, if there
			int len = s.length();
			if (s.charAt(len - 3) == ':') {
				try {
					s = s.substring(0, len - 3) + s.substring(len - 2); // to
																		// get
																		// rid
																		// of
																		// the
																		// ":"
				} catch (IndexOutOfBoundsException e) {
					throw new ParseException("Invalid length", 0);
				}
			}
		}

		Date date;
		// check for milliseconds extension
		if (s.contains(".")) {
			date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(s);
		} else {
			date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
		}

		calendar.setTime(date);
		return calendar;
	}

	public static Date convertToGMT(final Date date) {
		Date gmtDate = null;

		DateFormat gmtFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		TimeZone gmtTime = TimeZone.getTimeZone("GMT");
		gmtFormat.setTimeZone(gmtTime);
		String gmtStr = gmtFormat.format(date);
		try {
			gmtDate = gmtFormat.parse(gmtStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gmtDate;
	}

	public static String convertToGMTString(final Date date) {

		DateFormat gmtFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS+");
		TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
		gmtFormat.setTimeZone(gmtTimeZone);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.setTimeZone(gmtTimeZone);
		String dateStr = gmtFormat.format(calendar.getTime());
		dateStr += "00:00";

		return dateStr;
	}
}
