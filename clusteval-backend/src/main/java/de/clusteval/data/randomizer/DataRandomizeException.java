/**
 * 
 */
package de.clusteval.data.randomizer;

import de.clusteval.api.exceptions.ClustEvalException;

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
