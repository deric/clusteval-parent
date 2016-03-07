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
import java.util.List;

import de.wiwie.wiutils.utils.Pair;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.run.Run;
import de.clusteval.run.RunDataAnalysisRun;
import de.clusteval.run.result.RunDataAnalysisRunResult;
import de.clusteval.run.statistics.RunDataStatistic;

/**
 * A type of analysis runnable, that corresponds to {@link RunDataAnalysisRun}
 * and is responsible for analysing a run result together with a data analysis
 * result.
 * 
 * @author Christian Wiwie
 * 
 */
public class RunDataAnalysisRunRunnable
		extends
			AnalysisRunRunnable<RunDataStatistic, RunDataAnalysisRunResult, RunDataAnalysisIterationWrapper, RunDataAnalysisIterationRunnable> {

	/**
	 * The identifiers of run analysis run results.
	 */
	protected List<String> uniqueRunAnalysisRunIdentifier;

	/**
	 * The identifiers of data analysis run results.
	 */
	protected List<String> uniqueDataAnalysisRunIdentifier;

	protected int currentIteration = -1;

	/**
	 * @param runScheduler
	 *            The run scheduler that the newly created runnable should be
	 *            passed to and executed by.
	 * 
	 * @param run
	 *            The run this runnable belongs to.
	 * @param runIdentString
	 *            The unique identification string of the run which is used to
	 *            store the results in a unique folder to avoid overwriting.
	 * @param uniqueRunAnalysisRunIdentifier
	 *            The identifiers of run analysis run results.
	 * @param uniqueDataAnalysisRunIdentifier
	 *            The identifiers of data analysis run results.
	 * @param statistics
	 *            The statistics that should be assessed during execution of
	 *            this runnable.
	 * @param isResume
	 *            True, if this run is a resumption of a previous execution or a
	 *            completely new execution.
	 */
	public RunDataAnalysisRunRunnable(RunSchedulerThread runScheduler, Run run,
			String runIdentString, final boolean isResume,
			List<String> uniqueRunAnalysisRunIdentifier,
			final List<String> uniqueDataAnalysisRunIdentifier,
			List<RunDataStatistic> statistics) {
		super(run, runIdentString, statistics, isResume);
		this.uniqueRunAnalysisRunIdentifier = uniqueRunAnalysisRunIdentifier;
		this.uniqueDataAnalysisRunIdentifier = uniqueDataAnalysisRunIdentifier;
		this.future = runScheduler.registerRunRunnable(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.runnable.AnalysisRunRunnable#createRunResult()
	 */
	@Override
	protected RunDataAnalysisRunResult createRunResult()
			throws RegisterException {
		return new RunDataAnalysisRunResult(this.getRun().getRepository(),
				System.currentTimeMillis(), new File(analysesFolder),
				this.runThreadIdentString, run);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.runnable.AnalysisRunRunnable#afterRun()
	 */
	@Override
	public void afterRun() {
		super.afterRun();
		result.put(Pair.getPair(uniqueDataAnalysisRunIdentifier,
				uniqueRunAnalysisRunIdentifier), results);
		this.getRun().getResults().add(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.runnable.RunRunnable#hasNextIteration()
	 */
	@Override
	protected boolean hasNextIteration() {
		return this.currentIteration < this.statistics.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.run.runnable.RunRunnable#consumeNextIteration()
	 */
	@Override
	protected int consumeNextIteration() throws RunIterationException {
		return ++currentIteration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.run.runnable.AnalysisRunRunnable#createIterationWrapper()
	 */
	@Override
	protected RunDataAnalysisIterationWrapper createIterationWrapper() {
		return new RunDataAnalysisIterationWrapper();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.run.runnable.RunRunnable#createIterationRunnable(de.clusteval
	 * .run.runnable.IterationWrapper)
	 */
	@Override
	protected RunDataAnalysisIterationRunnable createIterationRunnable(
			RunDataAnalysisIterationWrapper iterationWrapper) {
		return new RunDataAnalysisIterationRunnable(iterationWrapper);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.run.runnable.RunRunnable#doRunIteration(de.clusteval.run
	 * .runnable.IterationWrapper)
	 */
	@Override
	protected void doRunIteration(
			RunDataAnalysisIterationWrapper iterationWrapper)
			throws RunIterationException {
		RunDataAnalysisIterationRunnable iterationRunnable = this
				.createIterationRunnable(iterationWrapper);

		this.submitIterationRunnable(iterationRunnable);
	}
}
