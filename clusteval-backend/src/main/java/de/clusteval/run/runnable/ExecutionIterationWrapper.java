/**
 * 
 */
package de.clusteval.run.runnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.clusteval.program.ParameterSet;
import de.clusteval.program.ProgramConfig;
import de.clusteval.run.result.ClusteringRunResult;

public class ExecutionIterationWrapper extends IterationWrapper {

	/**
	 * A temporary variable holding a file object pointing to the absolute path
	 * of the current clustering output file during execution of the runnable
	 */
	private File clusteringResultFile;

	/**
	 * A temporary variable holding a file object pointing to the absolute path
	 * of the current clustering quality output file during execution of the
	 * runnable
	 */
	private File resultQualityFile;

	/**
	 * This number indicates the current iteration performed by the runnable
	 * object.
	 * 
	 * <p>
	 * This is only larger than 1, if we are in PARAMETER_OPTIMIZATION mode.
	 * Then the optimization method will determine, how often we iterate in
	 * total and this attribute will be increased by the runnable after every
	 * iteration.
	 */
	private int optId;

	/**
	 * A map containing the parameters of {@link #runParams} and additionally
	 * internal parameters like file paths that are used throughout execution of
	 * this runnable.
	 */
	final private Map<String, String> effectiveParams;

	/**
	 * The internal parameters are parameters, that cannot be directly
	 * influenced by the user, e.g. the absolute input or output path.
	 */
	final protected Map<String, String> internalParams;

	protected ClusteringRunResult clusteringRunResult;

	protected ClusteringRunResult convertedClusteringRunResult;

	protected ParameterSet parameterSet;

	protected ProgramConfig programConfig;

	protected String[] invocation;

	public String[] getInvocation() {
		return invocation;
	}

	public void setInvocation(String[] invocation) {
		this.invocation = invocation;
	}

	/**
	 * 
	 */
	public ExecutionIterationWrapper() {
		super();
		this.internalParams = new HashMap<String, String>();
		this.effectiveParams = new HashMap<String, String>();
	}

	protected File getClusteringResultFile() {
		return clusteringResultFile;
	}

	protected void setClusteringResultFile(File clusteringResultFile) {
		this.clusteringResultFile = clusteringResultFile;
	}

	protected File getResultQualityFile() {
		return resultQualityFile;
	}

	protected void setResultQualityFile(File resultQualityFile) {
		this.resultQualityFile = resultQualityFile;
	}

	protected int getOptId() {
		return optId;
	}

	protected void setOptId(int optId) {
		this.optId = optId;
	}

	protected Map<String, String> getEffectiveParams() {
		return effectiveParams;
	}

	protected Map<String, String> getInternalParams() {
		return internalParams;
	}

	protected ClusteringRunResult getClusteringRunResult() {
		return clusteringRunResult;
	}

	protected ClusteringRunResult getConvertedClusteringRunResult() {
		return convertedClusteringRunResult;
	}

	protected void setClusteringRunResult(
			ClusteringRunResult clusteringRunResult) {
		this.clusteringRunResult = clusteringRunResult;
	}

	protected ProgramConfig getProgramConfig() {
		return programConfig;
	}

	protected void setProgramConfig(ProgramConfig programConfig) {
		this.programConfig = programConfig;
	}

	protected void setConvertedClusteringRunResult(
			ClusteringRunResult clusteringRunResult) {
		this.convertedClusteringRunResult = clusteringRunResult;
	}

	protected ParameterSet getParameterSet() {
		return parameterSet;
	}

	protected void setParameterSet(ParameterSet parameterSet) {
		this.parameterSet = parameterSet;
	}
}