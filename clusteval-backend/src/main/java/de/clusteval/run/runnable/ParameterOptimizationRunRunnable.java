/*******************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.clusteval.run.runnable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.wiwie.wiutils.utils.Triple;
import de.clusteval.cluster.paramOptimization.IDivergingParameterOptimizationMethod;
import de.clusteval.cluster.paramOptimization.NoParameterSetFoundException;
import de.clusteval.cluster.paramOptimization.ParameterOptimizationException;
import de.clusteval.cluster.paramOptimization.ParameterOptimizationMethod;
import de.clusteval.cluster.paramOptimization.ParameterSetAlreadyEvaluatedException;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.cluster.quality.ClusteringQualityMeasureValue;
import de.clusteval.cluster.quality.ClusteringQualitySet;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.format.IncompatibleDataSetFormatException;
import de.clusteval.data.dataset.format.InvalidDataSetFormatVersionException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.goldstandard.IncompleteGoldStandardException;
import de.clusteval.data.goldstandard.format.UnknownGoldStandardFormatException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.program.ParameterSet;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.run.ParameterOptimizationRun;
import de.clusteval.run.Run;
import de.clusteval.run.result.ParameterOptimizationResult;
import de.clusteval.run.result.RunResultParseException;
import de.clusteval.utils.InternalAttributeException;
import de.clusteval.utils.plot.Plotter;
import de.wiwie.wiutils.file.FileUtils;

/**
 * A type of an execution runnable, that corresponds to
 * {@link ParameterOptimizationRun}s and is therefore responsible for performing
 * several clusterings iteratively.
 * 
 * <p>
 * In {@link #doRun()} the optimization method {@link #optimizationMethod}
 * determines, how many iterations are to be performed.
 * 
 * @author Christian Wiwie
 * 
 */
public class ParameterOptimizationRunRunnable extends ExecutionRunRunnable {

	/**
	 * This attribute is set to some instance of an parameter optimization
	 * method, that will determine the sequence of parameter sets during the
	 * optimization process.
	 */
	protected ParameterOptimizationMethod optimizationMethod;

	/**
	 * A temporary variable holding the last consumed parameter set for the next
	 * iteration.
	 */
	protected ParameterSet lastConsumedParamSet;

	/**
	 * @param runScheduler
	 *            The run scheduler that the newly created runnable should be
	 *            passed to and executed by.
	 * @param run
	 *            The run this runnable belongs to.
	 * @param runIdentString
	 *            The unique identification string of the run which is used to
	 *            store the results in a unique folder to avoid overwriting.
	 * @param programConfig
	 *            The program configuration encapsulating the program executed
	 *            by this runnable.
	 * @param dataConfig
	 *            The data configuration used by this runnable.
	 * @param optimizationMethod
	 *            The optimization method which determines the parameter sets
	 *            during the optimization process and stores the results.
	 * @param isResume
	 *            True, if this run is a resumption of a previous execution or a
	 *            completely new execution.
	 */
	public ParameterOptimizationRunRunnable(RunSchedulerThread runScheduler,
			Run run, ProgramConfig programConfig, DataConfig dataConfig,
			ParameterOptimizationMethod optimizationMethod,
			String runIdentString, boolean isResume,
			Map<ProgramParameter<?>, String> runParams) {
		super(run, programConfig, dataConfig, runIdentString, isResume,
				runParams);

		this.optimizationMethod = optimizationMethod;
		if (optimizationMethod != null) {
			this.optimizationMethod.setResume(isResume);
		}
		this.future = runScheduler.registerRunRunnable(this);
	}

	/**
	 * This method replaces the optimization parameters with the values given in
	 * the run configuration.
	 */
	protected String[] parseOptimizationParameters(String[] invocation,
			final Map<String, String> effectiveParams) {
		final String[] parsed = invocation.clone();
		try {
			// 15.04.2013: changed invocation of next() to beginning of
			// doRunIteration() in order to get the right iteration numbner
			// now here: get the parameter set created there
			// TODO: change this to iterationWrapper
			List<ParameterSet> paramSets = optimizationMethod.getResult()
					.getParameterSets();
			ParameterSet optimizationParamValues = paramSets.get(paramSets
					.size() - 1);
			for (int i = 0; i < parsed.length; i++) {
				for (String param : optimizationParamValues.keySet()) {
					parsed[i] = parsed[i].replace("%" + param + "%",
							optimizationParamValues.get(param) + "");
					effectiveParams.put(param,
							optimizationParamValues.get(param) + "");
				}
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return parsed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * run.runnable.ExecutionRunRunnable#replaceRemainingParameters(java.util
	 * .Map, java.lang.String, java.util.Map)
	 */
	@Override
	protected String[] replaceRunParameters(String[] invocation,
			final Map<String, String> effectiveParams)
			throws InternalAttributeException, RegisterException,
			NoParameterSetFoundException {
		invocation = this.parseOptimizationParameters(invocation,
				effectiveParams);
		return super.replaceRunParameters(invocation, effectiveParams);
	}

	/**
	 * @return Get the optimization method of this parameter optimization run
	 *         runnable.
	 * @see #optimizationMethod
	 */
	public ParameterOptimizationMethod getOptimizationMethod() {
		return this.optimizationMethod;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.runnable.ExecutionRunRunnable#beforeRun()
	 */
	@Override
	protected void beforeRun() throws IllegalArgumentException,
			UnknownDataSetFormatException, IOException,
			InvalidDataSetFormatVersionException, RegisterException,
			InternalAttributeException, IncompatibleDataSetFormatException,
			UnknownGoldStandardFormatException,
			IncompleteGoldStandardException, InterruptedException {
		super.beforeRun();

		if (!new File(completeQualityOutput).exists() || !isResume)
			writeHeaderIntoCompleteFile(completeQualityOutput);

		/*
		 * Pass converted data configuration to optimization method
		 */
		this.optimizationMethod.setDataConfig(this.dataConfig);
		this.optimizationMethod.setProgramConfig(this.programConfig);
		try {
			this.optimizationMethod.reset(new File(completeQualityOutput));
			if (isResume) {
				// in case of resume, we have to update the current percentage
				int iterationPercent = Math.min(
						(int) (this.optimizationMethod.getFinishedCount()
								/ (double) this.optimizationMethod
										.getTotalIterationCount() * 10000), 10000);
				this.progress.update(iterationPercent);
			}
		} catch (ParameterOptimizationException e) {
			e.printStackTrace();
		} catch (RunResultParseException e) {
			e.printStackTrace();
		}

		// this.optId = this.optimizationMethod.getCurrentCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.runnable.ExecutionRunRunnable#endRun()
	 */
	@Override
	protected void afterRun() throws InterruptedException {
		super.afterRun();

		if (this.optimizationMethod != null
				&& this.optimizationMethod.getResult() != null) {
			try {
				if (this.optimizationMethod.getResult()
						.getOptimalParameterSet() != null)
					this.log.info("Optimal Parameter set for "
							+ programConfig
							+ " & "
							+ dataConfig
							+ ":\t"
							+ this.optimizationMethod.getResult()
									.getOptimalParameterSet()
							+ ""
							+ this.optimizationMethod.getResult()
									.getOptimalCriterionValue());
				/*
				 * TODO: option, whether to plot
				 */
				Plotter.plotParameterOptimizationResult(this.optimizationMethod
						.getResult());
			} finally {
				// clear memory-hungry internal attributes of clustering results
				ParameterOptimizationResult result = this.optimizationMethod
						.getResult();
				result.unloadFromMemory();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.runnable.RunRunnable#hasNextIteration()
	 */
	@Override
	protected boolean hasNextIteration() {
		return this.optimizationMethod.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.runnable.RunRunnable#consumeNextIteration()
	 */
	@Override
	protected int consumeNextIteration() throws RunIterationException {
		try {
			this.lastConsumedParamSet = this.optimizationMethod.next();
		} catch (ParameterSetAlreadyEvaluatedException e) {
			this.log.debug(run.toString() + " (" + programConfig + ","
					+ dataConfig + ") " + "Skipping calculation of iteration "
					+ e.getParameterSet() + " (has already been assessed)");

			// if this parameter set has already been evaluated, write into
			// the complete file
			StringBuilder sb = new StringBuilder();
			sb.append(e.getIterationNumber());
			sb.append("*\t");
			sb.append(e.getPreviousIterationNumber());
			sb.append(System.getProperty("line.separator"));

			FileUtils.appendStringToFile(completeQualityOutput, sb.toString());
		} catch (Exception e) {
			throw new RunIterationException(e);
		}
		return this.optimizationMethod.getStartedCount();
	}

	@Override
	protected void doRun() throws RunIterationException {
		try {
			super.doRun();
		} catch (RunIterationException e) {
			if (e.getCause() instanceof NoParameterSetFoundException) {
				// this exception just indicates, that no parameter set has been
				// found and the parameter optimization terminated earlier than
				// expected.
				this.log.warn(e.getMessage());
			} else
				throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.run.runnable.ExecutionRunRunnable#decorateIterationWrapper
	 * (de.clusteval.run.runnable.ExecutionIterationWrapper, int)
	 */
	@Override
	protected void decorateIterationWrapper(
			ExecutionIterationWrapper iterationWrapper, int currentPos)
			throws RunIterationException {
		iterationWrapper.setParameterSet(lastConsumedParamSet);
		iterationWrapper.setOptId(this.optimizationMethod.getStartedCount());

		super.decorateIterationWrapper(iterationWrapper, currentPos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.runnable.ExecutionRunRunnable#doRunIteration()
	 */
	@Override
	protected void doRunIteration(ExecutionIterationWrapper iterationWrapper)
			throws RunIterationException {
		try {
			super.doRunIteration(iterationWrapper);
		} finally {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.runnable.ExecutionRunRunnable#handleMissingRunResult()
	 */
	@Override
	protected void handleMissingRunResult(
			final ExecutionIterationWrapper iterationWrapper) {
		if (this.optimizationMethod instanceof IDivergingParameterOptimizationMethod) {
			this.log.info(this.getRun()
					+ " ("
					+ this.programConfig
					+ ","
					+ this.dataConfig
					+ ", Iteration "
					+ iterationWrapper.getOptId()
					+ ") The result of this run could not be found. Probably the program did not converge with this parameter set.");
		} else {
			this.log.info(this.getRun()
					+ " ("
					+ this.programConfig
					+ ","
					+ this.dataConfig
					+ ", Iteration "
					+ iterationWrapper.getOptId()
					+ ") The result of this run could not be found. Please consult the log files of the program");
		}

		super.handleMissingRunResult(iterationWrapper);

		ClusteringQualitySet minimalQualities = new ClusteringQualitySet();
		for (ClusteringQualityMeasure measure : this.getRun()
				.getQualityMeasures())
			minimalQualities.put(measure,
					ClusteringQualityMeasureValue.getForNotTerminated());

		if (this.optimizationMethod instanceof IDivergingParameterOptimizationMethod) {
			((IDivergingParameterOptimizationMethod) this.optimizationMethod)
					.giveFeedbackNotTerminated(
							iterationWrapper.getParameterSet(),
							minimalQualities);
		} else {
			this.optimizationMethod.giveQualityFeedback(
					iterationWrapper.getParameterSet(), minimalQualities);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * run.runnable.ExecutionRunRunnable#afterQualityAssessment(java.util.List)
	 */
	@Override
	// 04.04.2013: adding iteration number into complete file
	protected void writeQualitiesToFile(
			List<Triple<ParameterSet, ClusteringQualitySet, Long>> qualities) {
		// in this case, the list contains only one element

		// the parameter set contains all optimizable parameters of the program,
		// not only those which actually have been optimized and are stored in
		// the optimization method. We therefore have to adapt the parameter set
		// accordingly.
		ParameterSet paramSet = new ParameterSet();
		for (ProgramParameter<?> param : optimizationMethod
				.getOptimizationParameter())
			paramSet.put(param.getName(),
					qualities.get(0).getFirst().get(param.getName()));
		this.optimizationMethod.giveQualityFeedback(paramSet, qualities.get(0)
				.getSecond());
		super.writeQualitiesToFile(qualities);

		synchronized (this) {
			// changed 25.01.2013
			int iterationPercent = Math.min((int) (this.optimizationMethod.getFinishedCount()
					/ (double) this.optimizationMethod.getTotalIterationCount() * 10000),
					10000);
			this.progress.update(iterationPercent);
		}
	}
}
