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

import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.run.Run;
import de.clusteval.run.RunAnalysisRun;
import de.clusteval.run.result.RunAnalysisRunResult;
import de.clusteval.run.statistics.RunStatistic;

/**
 * A type of analysis runnable, that corresponds to {@link RunAnalysisRun} and
 * is responsible for analysing a run result.
 * 
 * @author Christian Wiwie
 * 
 */
public class RunAnalysisRunRunnable
		extends
			AnalysisRunRunnable<RunStatistic, RunAnalysisRunResult, RunAnalysisIterationWrapper, RunAnalysisIterationRunnable> {

	/**
	 * The unique identifier of a run result run identifier.
	 */
	protected String uniqueRunAnalysisRunIdentifier;

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
	 * @param uniqueRunIdentifier
	 *            The unique identifier of a run result run identifier.
	 * @param statistics
	 *            The statistics that should be assessed during execution of
	 *            this runnable.
	 * @param isResume
	 *            True, if this run is a resumption of a previous execution or a
	 *            completely new execution.
	 */
	public RunAnalysisRunRunnable(RunSchedulerThread runScheduler, Run run,
			String runIdentString, final boolean isResume,
			String uniqueRunIdentifier, List<RunStatistic> statistics) {
		super(run, runIdentString, statistics, isResume);
		this.uniqueRunAnalysisRunIdentifier = uniqueRunIdentifier;
		this.future = runScheduler.registerRunRunnable(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.runnable.AnalysisRunRunnable#createRunResult()
	 */
	@Override
	protected RunAnalysisRunResult createRunResult() throws RegisterException {
		return new RunAnalysisRunResult(this.getRun().getRepository(), true,
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
		result.put(this.runThreadIdentString, results);
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
	protected RunAnalysisIterationWrapper createIterationWrapper() {
		return new RunAnalysisIterationWrapper();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.run.runnable.RunRunnable#createIterationRunnable(de.clusteval
	 * .run.runnable.IterationWrapper)
	 */
	@Override
	protected RunAnalysisIterationRunnable createIterationRunnable(
			RunAnalysisIterationWrapper iterationWrapper) {
		return new RunAnalysisIterationRunnable(iterationWrapper);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.run.runnable.RunRunnable#doRunIteration(de.clusteval.run
	 * .runnable.IterationWrapper)
	 */
	@Override
	protected void doRunIteration(RunAnalysisIterationWrapper iterationWrapper)
			throws RunIterationException {
		RunAnalysisIterationRunnable iterationRunnable = this
				.createIterationRunnable(iterationWrapper);

		this.submitIterationRunnable(iterationRunnable);
	}

	public String getRunIdentifier() {
		return this.uniqueRunAnalysisRunIdentifier;
	}
}
