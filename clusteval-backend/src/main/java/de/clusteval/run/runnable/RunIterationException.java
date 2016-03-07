/**
 * 
 */
package de.clusteval.run.runnable;

import de.clusteval.utils.ClustEvalException;

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
