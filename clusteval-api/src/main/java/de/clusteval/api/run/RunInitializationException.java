/**
 * 
 */
package de.clusteval.api.run;

import de.clusteval.api.exceptions.ClustEvalException;

/**
 * @author Christian Wiwie
 *
 */
public class RunInitializationException extends ClustEvalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1559515081561854953L;

	/**
	 * @param message
	 */
	public RunInitializationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RunInitializationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RunInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

}