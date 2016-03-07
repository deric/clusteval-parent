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
import de.clusteval.data.DataConfig;
import de.clusteval.data.RunResultDataConfigFinderThread;
import de.clusteval.data.dataset.DataSetConfig;
import de.clusteval.data.dataset.RunResultDataSetConfigFinderThread;
import de.clusteval.data.goldstandard.GoldStandardConfig;
import de.clusteval.data.goldstandard.GoldStandardConfigFinderThread;
import de.clusteval.data.statistics.DataStatistic;
import de.clusteval.data.statistics.DataStatisticFinderThread;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.threading.SupervisorThread;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramConfigFinderThread;

/**
 * A thread that uses a {@link RunFinder} to check the runresult repository for
 * new runs.
 * 
 * @author Christian Wiwie
 * 
 */
public class RunResultRunFinderThread extends RunFinderThread {

	/**
	 * @param supervisorThread
	 * @param repository
	 *            The repository to check for new runs.
	 * @param checkOnce
	 *            If true, this thread only checks once for new runs.
	 * 
	 */
	public RunResultRunFinderThread(final SupervisorThread supervisorThread,
			final Repository repository, final boolean checkOnce) {
		super(supervisorThread, repository, 30000, checkOnce);
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
	public RunResultRunFinderThread(final SupervisorThread supervisorThread,
			final Repository repository, final long sleepTime,
			final boolean checkOnce) {
		super(supervisorThread, repository, sleepTime, checkOnce);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.FinderThread#beforeFind()
	 */
	@Override
	protected void beforeFind() {

		if (!this.repository.isInitialized(DataSetConfig.class))
			this.supervisorThread.getThread(
					RunResultDataSetConfigFinderThread.class).waitFor();

		if (!this.repository.isInitialized(GoldStandardConfig.class))
			this.supervisorThread.getThread(
					GoldStandardConfigFinderThread.class).waitFor();
		
		if (!this.repository.isInitialized(DataConfig.class))
			this.supervisorThread.getThread(
					RunResultDataConfigFinderThread.class).waitFor();


		if (!this.repository.isInitialized(ProgramConfig.class))
			this.supervisorThread.getThread(ProgramConfigFinderThread.class)
					.waitFor();

		if (!this.repository.isInitialized(DataStatistic.class))
			this.supervisorThread.getThread(DataStatisticFinderThread.class)
					.waitFor();

		if (!this.repository.isInitialized(ParameterOptimizationMethod.class))
			this.supervisorThread.getThread(
					ParameterOptimizationMethodFinderThread.class).waitFor();

		if (!this.repository.isInitialized(Context.class))
			this.supervisorThread.getThread(ContextFinderThread.class)
					.waitFor();

		this.log.debug("Checking for Runs...");
	}
}
