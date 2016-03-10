/**
 * 
 */
package de.clusteval.run.runnable;

import de.clusteval.api.exceptions.ClustEvalException;

/**
 * @author Christian Wiwie
 *
 */
public class RunIterationException extends ClustEvalException {

	/**
	 * @param message
	 * @param cause
	 */
	public RunIterationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RunIterationException(String message, Throwable cause) {
		super(message, cause);
	}

}
