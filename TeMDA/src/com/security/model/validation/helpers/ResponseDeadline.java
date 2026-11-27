package com.security.model.validation.helpers;

import java.util.Calendar;
import java.util.Date;

/**
 * Default time limit for responding to a request of a data subject (Article 12 of the GDPR):
 * one month from the receipt of the request. When the following month has no corresponding
 * day, the time limit is the last day of that month.
 */
public final class ResponseDeadline {

	public static final int DEFAULT_MONTHS = 1;

	private ResponseDeadline() {
	}

	/**
	 * Returns the default response deadline for a request received at the given date,
	 * or null when the date of the request is unknown.
	 */
	public static Date defaultFor(Date requestDate) {
		if (requestDate == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(requestDate);
		calendar.add(Calendar.MONTH, DEFAULT_MONTHS);
		return calendar.getTime();
	}
}
