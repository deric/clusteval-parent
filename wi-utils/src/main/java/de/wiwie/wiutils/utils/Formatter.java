package de.wiwie.wiutils.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.text.DateFormatter;

// TODO: Auto-generated Javadoc
/**
 * The Class Formatter.
 */
public class Formatter {

	/**
	 * Current time as string.
	 *
	 * @return the string
	 */
	public static String currentTimeAsString() {
		DateFormatter formatter = new DateFormatter();
		formatter.setFormat(DateFormat.getTimeInstance());
		Date d = new Date(System.currentTimeMillis());
		String result;
		try {
			result = formatter.valueToString(d);
		} catch (ParseException e) {
			e.printStackTrace();
			result = "";
		}
		return result;
	}

	/**
	 * Format ms to duration.
	 *
	 * @param timeInMs the time in ms
	 * @return the string
	 */
	public static String formatMsToDuration(long timeInMs) {
		if (timeInMs == 0) {
			return "0ms";
		}

		final StringBuilder sb = new StringBuilder();

		final long ms = timeInMs % 1000;
		sb.insert(0, (timeInMs >= 1000 ? " " : "") + ms + "ms");
		timeInMs /= 1000;

		if (timeInMs > 0) {
			final long s = timeInMs % 60;
			sb.insert(0, (timeInMs >= 60 ? " " : "") + s + "s");
			timeInMs /= 60;

			if (timeInMs > 0) {
				final long m = timeInMs % 60;
				sb.insert(0, (timeInMs >= 60 ? " " : "") + m + "m");
				timeInMs /= 60;

				if (timeInMs > 0) {
					final long h = timeInMs % 24;
					sb.insert(0, (timeInMs >= 24 ? " " : "") + h + "h");
					timeInMs /= 24;

					if (timeInMs > 0) {
						final long d = timeInMs % 365;
						sb.insert(0, (timeInMs >= 365 ? " " : "") + d + "d");
						timeInMs /= 365;

						if (timeInMs > 0) {
							final long y = timeInMs;
							sb.insert(0, y + "y");
						}
					}
				}
			}
		}
		return sb.toString();
	}
}
