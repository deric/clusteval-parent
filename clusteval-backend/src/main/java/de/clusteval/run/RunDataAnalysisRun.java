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
package de.clusteval.run;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.clusteval.context.Context;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.run.runnable.RunDataAnalysisRunRunnable;
import de.clusteval.run.runnable.RunRunnable;
import de.clusteval.run.statistics.RunDataStatistic;

/**
 * A type of analysis run that conducts analyses on both run results and data
 * inputs together.
 * 
 * <p>
 * A run data analysis run has a list of unique run analysis run identifiers in
 * {@link #uniqueRunAnalysisRunIdentifiers} and a list of unique data analysis
 * run identifiers in {@link #uniqueDataAnalysisRunIdentifiers}, that should be
 * assessed during execution of the run. Additionally they inherit a list of run
 * statistics in {@link AnalysisRun#statistics} that should be assessed for
 * every pair of run analysis and data analysis run identifier.
 * 
 * @author Christian Wiwie
 * 
 */
public class RunDataAnalysisRun extends AnalysisRun<RunDataStatistic> {

	/**
	 * A list of unique run analysis run identifiers to be assessed during this
	 * run.
	 */
	protected List<String> uniqueRunAnalysisRunIdentifiers;

	/**
	 * A list of unique data analysis run identifiers to be assessed during this
	 * run.
	 */

	protected List<String> uniqueDataAnalysisRunIdentifiers;

	/**
	 * @param repository
	 *            The repository this run should be registered at.
	 * @param context
	 * @param name
	 *            The name of this run.
	 * @param changeDate
	 *            The date this run was performed.
	 * @param absPath
	 *            The absolute path to the file on the filesystem that
	 *            corresponds to this run.
	 * @param uniqueRunAnalysisRunIdentifiers
	 *            The list of unique run analysis run identifiers, that should
	 *            be assessed during execution of the run.
	 * @param uniqueDataAnalysisRunIdentifiers
	 *            The list of unique data analysis run identifiers, that should
	 *            be assessed during execution of the run.
	 * @param statistics
	 *            The statistics that should be assessed for the objects of
	 *            analysis.
	 * @throws RegisterException
	 */
	public RunDataAnalysisRun(Repository repository, final Context context,
			long changeDate, File absPath,
			List<String> uniqueRunAnalysisRunIdentifiers,
			List<String> uniqueDataAnalysisRunIdentifiers,
			List<RunDataStatistic> statistics) throws RegisterException {
		super(repository, context, changeDate, absPath, statistics);
		this.uniqueRunAnalysisRunIdentifiers = uniqueRunAnalysisRunIdentifiers;
		this.uniqueDataAnalysisRunIdentifiers = uniqueDataAnalysisRunIdentifiers;

		if (this.register()) {
			for (RunDataStatistic statistic : this.statistics) {
				// added 21.03.2013
				statistic.register();
				statistic.addListener(this);
			}
		}
	}

	/**
	 * Copy constructor of run data analysis runs.
	 * 
	 * @param other
	 *            The run to be cloned.
	 * @throws RegisterException
	 */
	protected RunDataAnalysisRun(final RunDataAnalysisRun other)
			throws RegisterException {
		super(other);

		this.uniqueRunAnalysisRunIdentifiers = new ArrayList<String>();
		for (String s : other.uniqueRunAnalysisRunIdentifiers)
			this.uniqueRunAnalysisRunIdentifiers.add(s);

		this.uniqueDataAnalysisRunIdentifiers = new ArrayList<String>();
		for (String s : other.uniqueDataAnalysisRunIdentifiers)
			this.uniqueDataAnalysisRunIdentifiers.add(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.AnalysisRun#cloneStatistics(java.util.List)
	 */
	@Override
	protected List<RunDataStatistic> cloneStatistics(
			List<RunDataStatistic> statistics) {
		final List<RunDataStatistic> result = new ArrayList<RunDataStatistic>();

		for (RunDataStatistic st : statistics)
			result.add(st.clone());

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.RunAnalysisRun#clone()
	 */
	@Override
	public RunDataAnalysisRun clone() {
		try {
			return new RunDataAnalysisRun(this);
		} catch (RegisterException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.Run#getNumberOfRunRunnables()
	 */
	@Override
	protected int getNumberOfRunRunnables() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.Run#createAndAddRunnableForResumePair(framework.RunScheduler,
	 * int)
	 */
	@SuppressWarnings("unused")
	@Override
	protected RunRunnable createAndScheduleRunnableForResumePair(
			RunSchedulerThread runScheduler, int p) {

		/*
		 * We only operate on this copy, in order to avoid multithreading
		 * problems.
		 */
		// changed 13.02.2013
		// RunDataAnalysisRun runCopy = this.clone();
		RunDataAnalysisRun runCopy = this;

		List<String> uniqueRunAnalysisRunIdentifier = this
				.getUniqueRunAnalysisRunIdentifiers();
		List<String> uniqueDataAnalysisRunIdentifier = this
				.getUniqueDataAnalysisRunIdentifiers();

		/*
		 * Start a thread with the invocation line and a path to the log file.
		 * The RunThread redirects all the output of the pr ogram into the
		 * logFile.
		 */
		final RunDataAnalysisRunRunnable t = new RunDataAnalysisRunRunnable(
				runScheduler, runCopy, runIdentString, true,
				uniqueRunAnalysisRunIdentifier,
				uniqueDataAnalysisRunIdentifier, runCopy.getStatistics());
		return t;
	}

	@Override
	protected RunRunnable createAndScheduleRunnableForRunPair(
			RunSchedulerThread runScheduler, @SuppressWarnings("unused") int p) {

		/*
		 * We only operate on this copy, in order to avoid multithreading
		 * problems.
		 */
		RunDataAnalysisRun runCopy = this.clone();

		List<String> uniqueRunAnalysisRunIdentifier = this
				.getUniqueRunAnalysisRunIdentifiers();
		List<String> uniqueDataAnalysisRunIdentifier = this
				.getUniqueDataAnalysisRunIdentifiers();

		/*
		 * Start a thread with the invocation line and a path to the log file.
		 * The RunThread redirects all the output of the pr ogram into the
		 * logFile.
		 */
		final RunDataAnalysisRunRunnable t = new RunDataAnalysisRunRunnable(
				runScheduler, runCopy, runIdentString, false,
				uniqueRunAnalysisRunIdentifier,
				uniqueDataAnalysisRunIdentifier, runCopy.getStatistics());
		return t;
	}

	/**
	 * @return the run identifiers
	 */
	public List<String> getUniqueRunAnalysisRunIdentifiers() {
		return this.uniqueRunAnalysisRunIdentifiers;
	}

	/**
	 * @return the dataConfigs
	 */
	public List<String> getUniqueDataAnalysisRunIdentifiers() {
		return this.uniqueDataAnalysisRunIdentifiers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.Run#terminate()
	 */
	@Override
	public boolean terminate() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see run.Run#getUpperLimitProgress()
	 */
	@Override
	protected long getUpperLimitProgress() {
		return this.statistics.size() * 100;
	}
}
