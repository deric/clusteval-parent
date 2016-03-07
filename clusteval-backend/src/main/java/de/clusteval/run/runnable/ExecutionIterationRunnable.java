/**
 * 
 */
package de.clusteval.run.runnable;

import de.clusteval.run.ExecutionRun;
import de.clusteval.run.result.NoRunResultFormatParserException;

/**
 * @author Christian Wiwie
 * 
 */
public abstract class ExecutionIterationRunnable
		extends
			IterationRunnable<ExecutionIterationWrapper> {

	protected Process proc;

	protected NoRunResultFormatParserException noRunResultException;

	/**
	 * @param iterationWrapper
	 */
	public ExecutionIterationRunnable(
			final ExecutionIterationWrapper iterationWrapper) {
		super(iterationWrapper);
	}

	/**
	 * @return the noRunResultException
	 */
	public NoRunResultFormatParserException getNoRunResultException() {
		return noRunResultException;
	}

	public int getIterationNumber() {
		return this.iterationWrapper.getOptId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.runnable.IterationRunnable#getRun()
	 */
	@Override
	public ExecutionRun getRun() {
		return (ExecutionRun) super.getRun();
	}

	public Process getProcess() {
		return this.proc;
	}

}
