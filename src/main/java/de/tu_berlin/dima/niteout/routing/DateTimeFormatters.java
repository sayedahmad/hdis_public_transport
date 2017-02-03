package de.tu_berlin.dima.niteout.routing;

import java.time.format.DateTimeFormatter;

public final class DateTimeFormatters {

	public final static DateTimeFormatter ISO_LOCAL_DATE_TIME_NO_SECONDS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
	public final static DateTimeFormatter ISO_LOCAL_DATE_TIME_NO_NANOSECONDS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

}
