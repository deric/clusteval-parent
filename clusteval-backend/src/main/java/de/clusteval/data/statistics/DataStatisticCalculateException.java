/**
 * 
 */
package de.clusteval.data.statistics;


/**
 * @author Christian Wiwie
 *
 */
public class DataStatisticCalculateException extends StatisticCalculateException {

	/**
	 * @param message
	 */
	public DataStatisticCalculateException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DataStatisticCalculateException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public DataStatisticCalculateException(Throwable cause) {
		super(cause);
	}

}
