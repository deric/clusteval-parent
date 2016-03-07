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

import de.clusteval.cluster.paramOptimization.ParameterOptimizationMethod;
import de.clusteval.cluster.paramOptimization.ParameterOptimizationMethodFinderThread;
import de.clusteval.context.Context;
import de.clusteval.context.ContextFinderThread;
import de.clusteval.data.dataset.DataSetConfig;
import de.clusteval.data.dataset.DataSetConfigFinderThread;
import de.clusteval.data.goldstandard.GoldStandardConfig;
import de.clusteval.data.goldstandard.GoldStandardConfigFinderThread;
import de.clusteval.data.statistics.DataStatistic;
import de.clusteval.data.statistics.DataStatisticFinderThread;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.threading.SupervisorThread;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramConfigFinderThread;
import de.clusteval.run.statistics.RunDataStatistic;
import de.clusteval.run.statistics.RunDataStatisticFinderThread;
import de.clusteval.run.statistics.RunStatistic;
import de.clusteval.run.statistics.RunStatisticFinderThread;
import de.clusteval.utils.Finder;
import de.clusteval.utils.FinderThread;

/**
 * A thread that uses a {@link RunFinder} to check the repository for new runs.
 * 
 * @author Christian Wiwie
 * 
 */
public class RunFinderThread extends FinderThread<Run> {

	/**
	 * @param supervisorThread
	 * @param repository
	 *            The repository to check for new runs.
	 * @param checkOnce
	 *            If true, this thread only checks once for new runs.
	 * 
	 */
	public RunFinderThread(final SupervisorThread supervisorThread,
			final Repository repository, final boolean checkOnce) {
		super(supervisorThread, repository, Run.class, 30000, checkOnce);
	}

	/**
	 * @param supervisorThread
	 * @param repository
	 *            The repository to check for new runs.
	 * @param sleepTime
	 *            The time between two checks.
	 * @param checkOnce
	 *            If true, this thread only checks once for new runs.
	 * 
	 */
	public RunFinderThread(final SupervisorThread supervisorThread,
			final Repository repository, final long sleepTime,
			final boolean checkOnce) {
		super(supervisorThread, repository, Run.class, sleepTime, checkOnce);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.FinderThread#beforeFind()
	 */
	@Override
	protected void beforeFind() {

		if (!this.repository.isInitialized(DataSetConfig.class))
			this.supervisorThread.getThread(DataSetConfigFinderThread.class)
					.waitFor();

		if (!this.repository.isInitialized(GoldStandardConfig.class))
			this.supervisorThread.getThread(
					GoldStandardConfigFinderThread.class).waitFor();

		if (!this.repository.isInitialized(ProgramConfig.class))
			this.supervisorThread.getThread(ProgramConfigFinderThread.class)
					.waitFor();

		if (!this.repository.isInitialized(DataStatistic.class))
			this.supervisorThread.getThread(DataStatisticFinderThread.class)
					.waitFor();

		if (!this.repository.isInitialized(RunDataStatistic.class))
			this.supervisorThread.getThread(RunDataStatisticFinderThread.class)
					.waitFor();

		if (!this.repository.isInitialized(RunStatistic.class))
			this.supervisorThread.getThread(RunStatisticFinderThread.class)
					.waitFor();

		if (!this.repository.isInitialized(ParameterOptimizationMethod.class))
			this.supervisorThread.getThread(
					ParameterOptimizationMethodFinderThread.class).waitFor();

		if (!this.repository.isInitialized(Context.class))
			this.supervisorThread.getThread(ContextFinderThread.class)
					.waitFor();

		super.beforeFind();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.FinderThread#getFinder()
	 */
	@Override
	protected Finder<Run> getFinder() throws RegisterException {
		return new RunFinder(repository);
	}
}
