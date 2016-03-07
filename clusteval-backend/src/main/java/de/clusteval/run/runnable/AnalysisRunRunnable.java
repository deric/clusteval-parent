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
import java.util.ArrayList;
import java.util.List;

import de.wiwie.wiutils.utils.ProgressPrinter;
import de.clusteval.data.statistics.DataStatistic;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.run.AnalysisRun;
import de.clusteval.run.Run;
import de.clusteval.run.result.RunResult;
import de.clusteval.utils.Statistic;
import de.wiwie.wiutils.file.FileUtils;

/**
 * A type of a runnable, that corresponds to {@link AnalysisRun} and is
 * therefore responsible for analysing a object of interest. This can be for
 * example analysis of a dataset (in case of {@link DataAnalysisRunRunnable}) or
 * of run results ({@link RunAnalysisRunRunnable}).
 * 
 * @author Christian Wiwie
 * @param <S>
 *            A type of statistic, that should be assessed and stored during
 *            execution of this runnable, e.g. {@link DataStatistic}.
 * @param <R>
 *            A type of run result, that the results of this runnable will be
 *            of.
 * 
 */
public abstract class AnalysisRunRunnable<S extends Statistic, R extends RunResult, IW extends AnalysisIterationWrapper<S>, IR extends AnalysisIterationRunnable>
		extends
			RunRunnable<IR, IW> {

	/**
	 * A list of all statistic-classes that should be assessed during execution
	 * of this runnable.
	 */
	protected List<S> statistics;

	/**
	 * The results of this runnables are stored in a list. Every result is a
	 * single statistic object. A statistic object encapsulates its assessed
	 * value itself.
	 */
	protected List<S> results;

	/**
	 * The runresult object is a wrapper object that tells the framework, that
	 * the result folder of this runnable in the repository results directory
	 * (see {@link Repository#runResultBasePath}) holds a run result.
	 */
	protected R result;

	/**
	 * A temporary variable needed during execution of this runnable.
	 */
	protected String analysesFolder;

	/**
	 * A temporary variable needed during execution of this runnable.
	 */
	protected Repository repo;

	/**
	 * @param run
	 *            The run this runnable belongs to.
	 * @param runIdentString
	 *            The unique identification string of the run which is used to
	 *            store the results in a unique folder to avoid overwriting.
	 * @param statistics
	 *            The statistics that should be assessed during execution of
	 *            this runnable.
	 * @param isResume
	 *            True, if this run is a resumption of a previous execution or a
	 *            completely new execution.
	 */
	public AnalysisRunRunnable(final Run run, final String runIdentString,
			List<S> statistics, final boolean isResume) {
		super(run, runIdentString, isResume);
		this.statistics = statistics;
		this.progress = new ProgressPrinter(10000, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.runnable.RunRunnable#beforeRun()
	 */
	@Override
	public void beforeRun() {
		if (!isResume)
			this.repo = this.getRun().getRepository();
		else
			this.repo = this.getRun().getRepository().getParent();

		new File(this.repo.getAnalysisResultsBasePath().replace(
				"%RUNIDENTSTRING", runThreadIdentString)).mkdirs();
		this.results = new ArrayList<S>();
		this.analysesFolder = FileUtils.buildPath(this.repo
				.getAnalysisResultsBasePath().replace("%RUNIDENTSTRING",
						runThreadIdentString));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.runnable.RunRunnable#afterRun()
	 */
	@Override
	public void afterRun() {
		try {
			this.result = createRunResult();
		} catch (RegisterException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void decorateIterationWrapper(final IW iterationWrapper,
			final int currentPos) throws RunIterationException {
		super.decorateIterationWrapper(iterationWrapper, currentPos);
		iterationWrapper.setStatistic(this.statistics.get(currentPos));
		iterationWrapper.setAnalysesFolder(this.analysesFolder);
		// TODO iterationWrapper.setLogfile(this.l);
		iterationWrapper.setRunnable(this);
	}

	/**
	 * This method creates a run result object encapsulating the results of this
	 * runnable which has the right subtype depending on the dynamic type of
	 * this class.
	 * 
	 * @return A runresult object encapsulating the results of this runnable.
	 * @throws RegisterException
	 */
	protected abstract R createRunResult() throws RegisterException;
}
