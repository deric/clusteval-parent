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
import java.util.Map;

import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.format.IncompatibleDataSetFormatException;
import de.clusteval.data.dataset.format.InvalidDataSetFormatVersionException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.goldstandard.IncompleteGoldStandardException;
import de.clusteval.data.goldstandard.format.UnknownGoldStandardFormatException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.run.Run;
import de.clusteval.utils.InternalAttributeException;

/**
 * @author Christian Wiwie
 * 
 */
public class InternalParameterOptimizationRunRunnable
		extends
			ExecutionRunRunnable {

	protected boolean hasNext = true;

	/**
	 * @param runScheduler
	 * @param run
	 * @param programConfig
	 * @param dataConfig
	 * @param runIdentString
	 * @param isResume
	 */
	public InternalParameterOptimizationRunRunnable(
			RunSchedulerThread runScheduler, Run run,
			ProgramConfig programConfig, DataConfig dataConfig,
			String runIdentString, boolean isResume,
			Map<ProgramParameter<?>, String> runParams) {
		super(run, programConfig, dataConfig, runIdentString, isResume,
				runParams);
		this.future = runScheduler.registerRunRunnable(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.runnable.ExecutionRunRunnable#getInvocationFormat()
	 */
	@Override
	protected String getInvocationFormat() {
		return programConfig
				.getInvocationFormatParameterOptimization(!dataConfig
						.hasGoldStandardConfig());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.runnable.ExecutionRunRunnable#beforeClustering()
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
		super.decorateIterationWrapper(iterationWrapper, currentPos);
		iterationWrapper.setOptId(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.runnable.ExecutionRunRunnable#handleMissingRunResult()
	 */
	@Override
	protected void handleMissingRunResult(
			final ExecutionIterationWrapper iterationWrapper) {
		this.log.info(this.getRun()
				+ " ("
				+ this.programConfig
				+ ","
				+ this.dataConfig
				+ ") The result of this run could not be found. Please consult the log files of the program");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.runnable.RunRunnable#hasNextIteration()
	 */
	@Override
	protected boolean hasNextIteration() {
		return this.hasNext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.runnable.RunRunnable#consumeNextIteration()
	 */
	@Override
	protected int consumeNextIteration() throws RunIterationException {
		this.hasNext = false;
		return 1;
	}
}
