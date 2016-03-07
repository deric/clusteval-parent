/**
 * 
 */
package de.wiwie.wiutils.utils;

import java.util.StringTokenizer;

/**
 * @author Christian Wiwie
 * 
 */
public class StringExt {

	/**
	 * Merges the given strings into one string, using the delimiter.
	 * @param delimiter
	 * @param strings
	 * @return
	 */
	public static String paste(final String delimiter, String... strings) {
		StringBuilder sb = new StringBuilder();

		for (String s : strings) {
			sb.append(s);
			sb.append(delimiter);
		}

		int pos = sb.lastIndexOf(delimiter);
		sb.delete(pos, pos+delimiter.length());

		return sb.toString();
	}
	
	/**
	 * Appends a string to every string in an array.
	 * @param arr
	 * @param append
	 * @return
	 */
	public static String[] append(final String[] arr, final String append) {
		String[] result = new String[arr.length];
		
		for (int i = 0; i < result.length; i++)
			result[i] = arr[i] + append;
		
		return result;
	}

	/**
	 * This split method doesn't use a regular expression (in contrast to
	 * String.split() ). Instead it uses a StringTokenizer to iterate over the
	 * substrings and creates an array of those, which is faster in most cases.
	 * 
	 * @param s
	 *            The string to split.
	 * @param delimiter
	 *            The string which delimits the substrings
	 * @return An array of the substrings of the given string delimited by the
	 *         delimiter.
	 */
	public static String[] split(final String s, final String delimiter) {
		StringTokenizer tok = new StringTokenizer(s, delimiter);
		// if the empty string is separated, we need to manually add the string
		// to the list
		if (s.startsWith(delimiter)) {
			final String[] elems = new String[tok.countTokens() + 1];
			elems[0] = "";
			for (int i = 1; i < elems.length; i++)
				elems[i] = tok.nextToken();
			return elems;
		}

		final String[] elems = new String[tok.countTokens()];
		for (int i = 0; i < elems.length; i++)
			elems[i] = tok.nextToken();
		return elems;
	}
}
