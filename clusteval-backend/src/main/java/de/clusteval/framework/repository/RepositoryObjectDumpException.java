/**
 * 
 */
package de.clusteval.framework.repository;

import de.clusteval.utils.ClustEvalException;

/**
 * @author Christian Wiwie
 *
 */
public class RepositoryObjectDumpException extends ClustEvalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7367024507267872928L;

	/**
	 * @param message
	 */
	public RepositoryObjectDumpException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RepositoryObjectDumpException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RepositoryObjectDumpException(String message, Throwable cause) {
		super(message, cause);
	}

}
