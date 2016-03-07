/**
 * 
 */
package de.clusteval.data.statistics;

import de.clusteval.utils.ClustEvalException;

/**
 * @author Christian Wiwie
 *
 */
public class RunStatisticCalculateException extends StatisticCalculateException {

	/**
	 * @param message
	 */
	public RunStatisticCalculateException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RunStatisticCalculateException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public RunStatisticCalculateException(Throwable cause) {
		super(cause);
	}

}
