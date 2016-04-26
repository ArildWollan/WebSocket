package nettverksprosjekt.utils;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeUtility {

	/**
	 * Creates a time stamp with format [dd.mm.yyyy - hh:mm:ss] containing the
	 * exact time when the method is called.
	 * 
	 * @return A String containing the time stamp.
	 */
	public static String getTimeStamp() {

		LocalDate currentDate = LocalDate.now();
		LocalTime currentTime = LocalTime.now();

		// Get all desired time values
		String year = Integer.toString(currentDate.getYear());
		String month = Integer.toString(currentDate.getMonthValue());
		String date = Integer.toString(currentDate.getDayOfMonth());

		String hour = Integer.toString(currentTime.getHour());
		String minute = Integer.toString(currentTime.getMinute());
		String second = Integer.toString(currentTime.getSecond());

		// Format values to the desired format
		if (month.length() < 2) {
			month = "0" + month;
		}

		if (date.length() < 2) {
			date = "0" + date;
		}

		if (hour.length() < 2) {
			hour = "0" + hour;
		}

		if (minute.length() < 2) {
			minute = "0" + minute;
		}

		if (second.length() < 2) {
			second = "0" + second;
		}

		return "[" + date + "." + month + "." + year + " - " + hour + ":" + minute + ":" + second + "]";
	}
}
