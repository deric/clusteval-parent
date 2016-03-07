package de.wiwie.wiutils.format;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.text.DateFormatter;

public class Formatter {

	public static String currentTimeAsString() {
		return Formatter.currentTimeAsString(false);
	}

	public static String currentTimeAsString(boolean withDate) {
		return Formatter.currentTimeAsString(withDate, null, null);
	}

	public static String currentTimeAsString(boolean withDate, String pattern,
			Locale locale) {
		Locale current = Locale.getDefault();
		if (locale != null) {
			Locale.setDefault(locale);
		}
		DateFormatter formatter = new DateFormatter();
		if (withDate)
			formatter.setFormat(new SimpleDateFormat(pattern, locale));
		else
			formatter.setFormat(DateFormat.getTimeInstance());
		Date d = new Date(System.currentTimeMillis());
		String result;
		try {
			result = formatter.valueToString(d);
		} catch (ParseException e) {
			e.printStackTrace();
			result = "";
		}
		if (locale != null) {
			Locale.setDefault(current);
		}
		return result;
	}

	/**
	 * @param timeInMs
	 * @return
	 */
	public static String formatMsToDuration(long timeInMs) {
		return Formatter.formatMsToDuration(timeInMs, true);
	}

	/**
	 * @param timeInMs
	 * @param showMilliseconds
	 * @return
	 */

	public static String formatMsToDuration(long timeInMs,
			boolean showMilliseconds) {
		if (timeInMs == 0) {
			if (showMilliseconds)
				return "0ms";
			return "0s";
		}

		final StringBuilder sb = new StringBuilder();

		final long ms = timeInMs % 1000;
		if (showMilliseconds)
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
