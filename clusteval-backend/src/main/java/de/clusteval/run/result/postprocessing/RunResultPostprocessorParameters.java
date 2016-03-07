/**
 * 
 */
package de.clusteval.run.result.postprocessing;

import java.util.HashMap;

/**
 * @author Christian Wiwie
 *
 */
public class RunResultPostprocessorParameters extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6949276396401908242L;

	/**
	 * 
	 */
	public RunResultPostprocessorParameters() {
		super();
	}

	/**
	 * @param other
	 */
	public RunResultPostprocessorParameters(
			final RunResultPostprocessorParameters other) {
		super();
		this.putAll(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#clone()
	 */
	@Override
	public RunResultPostprocessorParameters clone() {
		return new RunResultPostprocessorParameters(this);
	}

}
