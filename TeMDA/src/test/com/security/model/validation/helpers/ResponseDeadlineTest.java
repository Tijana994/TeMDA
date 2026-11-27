package test.com.security.model.validation.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.security.model.validation.helpers.ResponseDeadline;

/**
 * The default response deadline is one month after the receipt of the request (Article 12 of the GDPR).
 */
public class ResponseDeadlineTest {

	private static Date date(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month - 1, day, 10, 30);
		return calendar.getTime();
	}

	@Test
	public void deadlineIsOneMonthAfterTheRequest() {
		assertEquals(date(2024, 7, 15), ResponseDeadline.defaultFor(date(2024, 6, 15)));
	}

	@Test
	public void deadlineCrossesTheYear() {
		assertEquals(date(2025, 1, 10), ResponseDeadline.defaultFor(date(2024, 12, 10)));
	}

	@Test
	public void deadlineIsTheLastDayOfAShorterMonth() {
		assertEquals(date(2024, 2, 29), ResponseDeadline.defaultFor(date(2024, 1, 31)));
		assertEquals(date(2023, 2, 28), ResponseDeadline.defaultFor(date(2023, 1, 31)));
		assertEquals(date(2024, 4, 30), ResponseDeadline.defaultFor(date(2024, 3, 31)));
	}

	@Test
	public void requestDateIsNotChanged() {
		Date request = date(2024, 6, 15);
		ResponseDeadline.defaultFor(request);
		assertEquals(date(2024, 6, 15), request);
	}

	@Test
	public void unknownRequestDateGivesNoDeadline() {
		assertNull(ResponseDeadline.defaultFor(null));
	}
}
