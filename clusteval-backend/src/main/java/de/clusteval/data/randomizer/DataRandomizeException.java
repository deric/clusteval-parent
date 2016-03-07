/**
 * 
 */
package de.clusteval.data.randomizer;

import de.clusteval.utils.ClustEvalException;

/**
 * @author Christian Wiwie
 *
 */
public class DataRandomizeException extends ClustEvalException {

	/**
	 * @param message
	 */
	public DataRandomizeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DataRandomizeException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DataRandomizeException(String message, Throwable cause) {
		super(message, cause);
	}
}
