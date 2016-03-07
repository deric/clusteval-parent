/**
 * 
 */
package de.clusteval.data.statistics;

import de.clusteval.utils.ClustEvalException;

/**
 * @author Christian Wiwie
 *
 */
public class RunDataStatisticCalculateException
		extends
			StatisticCalculateException {

	/**
	 * @param message
	 */
	public RunDataStatisticCalculateException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RunDataStatisticCalculateException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public RunDataStatisticCalculateException(Throwable cause) {
		super(cause);
	}

}
