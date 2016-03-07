/**
 * 
 */
package de.clusteval.cluster.paramOptimization;

import de.clusteval.program.ParameterSet;

/**
 * @author Christian Wiwie
 * 
 */
public class ParameterSetAlreadyEvaluatedException extends Exception {

	final protected long iterationNumber;
	final protected long previousIterationNumber;
	final protected ParameterSet paramSet;

	/**
	 * 
	 */
	public ParameterSetAlreadyEvaluatedException(final long iterationNumber,
			final long previousIterationNumber, final ParameterSet paramSet) {
		super();
		this.iterationNumber = iterationNumber;
		this.previousIterationNumber = previousIterationNumber;
		this.paramSet = paramSet;
	}

	public long getIterationNumber() {
		return iterationNumber;
	}

	public long getPreviousIterationNumber() {
		return previousIterationNumber;
	}

	public ParameterSet getParameterSet() {
		return paramSet;
	}
}
