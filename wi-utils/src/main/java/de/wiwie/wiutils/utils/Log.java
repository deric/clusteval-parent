/**
 * 
 */
package de.wiwie.wiutils.utils;

import java.io.PrintStream;

import de.clusteval.utils.Formatter;

/**
 * @author wiwie
 * 
 */
public class Log {

	public enum LOG_LEVEL {
		NONE, QUIET, STATUS, ALL
	}
	public enum PRIORITY {
		INFORMATION, PROGRESS, IMPORTANT, WARNING, ERROR, FATAL
	}

	private static PrintStream stream = System.out;
	private static LOG_LEVEL logLevel, beforeMute;
	private static PRIORITY defaultPriority = PRIORITY.INFORMATION;
	private static String linePrefix = "";
	private static String prefixChar = "|\t";
	private static IntList groups = new IntList();
	private static boolean atNewLine = true;

	static {
		if (Log.logLevel == null)
			Log.logLevel = LOG_LEVEL.ALL;
	}

	public static void changeLogLevel(final LOG_LEVEL level) {
//		if (Log.logLevel != null) {
//			return;
//		}
		Log.logLevel = level;
	}

	public static void mute() {
		if (Log.beforeMute != null)
			return;
		Log.beforeMute = Log.logLevel;
		Log.logLevel = LOG_LEVEL.NONE;
	}

	public static void unmute() {
		if (Log.beforeMute == null)
			return;
		Log.logLevel = Log.beforeMute;
		Log.beforeMute = null;
	}

	public static int beginGroup() {
		linePrefix += prefixChar;
		final int groupNo = groups.size();
		groups.add(groupNo);
		return groupNo;
	}

	public static void endGroup(final int groupNo) {
		if (groupNo != groups.get(groups.size() - 1)) {
			throw new IllegalArgumentException("Wrong group number");
		}
		linePrefix = linePrefix.substring(0,
				linePrefix.length() - prefixChar.length());
		groups.remove(groups.size() - 1);
	}

	public static void logTo(final PrintStream stream) {
		Log.stream = stream;
	}

	public static boolean print(final String line) {
		return Log.print(line, Log.defaultPriority);
	}

	public static boolean print(final String line, final PRIORITY priority) {
		if (Log.logLevel == LOG_LEVEL.NONE) {
			return false;
		} else if (Log.logLevel == LOG_LEVEL.QUIET) {
			if (priority.ordinal() < PRIORITY.IMPORTANT.ordinal()
					|| Log.groups.size() > 3) {
				return false;
			}
		} else if (Log.logLevel == LOG_LEVEL.STATUS) {
			if (priority.ordinal() < PRIORITY.PROGRESS.ordinal()) {
				return false;
			}
		} else if (Log.logLevel == LOG_LEVEL.ALL) {
			// accept all
		}
		if (atNewLine) {
			Log.stream.print("[" + Formatter.currentTimeAsString() + "]" + "\t"
					+ linePrefix);
		}
		Log.stream.print(line);
		atNewLine = line.endsWith(System.getProperty("line.separator"));
		return true;
	}

	public static boolean println() {
		return Log.println("");
	}

	public static boolean println(final Object obj) {
		if (obj instanceof PRIORITY) {
			return Log.println("", (PRIORITY) obj);
		} else {
			return Log.println(obj.toString());
		}
	}

	public static boolean println(final String line) {
		return Log.println(line, Log.defaultPriority);
	}

	public static boolean println(final String line, final PRIORITY priority) {
		boolean printed = Log.print(
				line + System.getProperty("line.separator"), priority);
		if (printed)
			atNewLine = true;
		return printed;
	}

	public static void setDefaultPriority(final PRIORITY prior) {
		Log.defaultPriority = prior;
	}
}
