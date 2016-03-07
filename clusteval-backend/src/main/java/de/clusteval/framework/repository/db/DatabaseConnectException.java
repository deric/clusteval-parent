/**
 * 
 */
package de.clusteval.framework.repository.db;

import de.clusteval.utils.ClustEvalException;

/**
 * @author Christian Wiwie
 *
 */
public class DatabaseConnectException extends ClustEvalException {

	/**
	 * @param message
	 */
	public DatabaseConnectException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DatabaseConnectException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DatabaseConnectException(String message, Throwable cause) {
		super(message, cause);
	}

}
