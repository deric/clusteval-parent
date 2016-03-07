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
package de.clusteval.data;

import de.clusteval.data.dataset.DataSetConfig;
import de.clusteval.data.dataset.DataSetConfigFinderThread;
import de.clusteval.data.goldstandard.GoldStandardConfig;
import de.clusteval.data.goldstandard.GoldStandardConfigFinderThread;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.threading.SupervisorThread;
import de.clusteval.utils.Finder;
import de.clusteval.utils.FinderThread;

/**
 * @author Christian Wiwie
 * 
 */
public class DataConfigFinderThread extends FinderThread<DataConfig> {

	/**
	 * @param supervisorThread
	 * @param repository
	 *            The repository to check for new data configurations.
	 * @param checkOnce
	 *            If true, this thread only checks once for new data
	 *            configurations.
	 * 
	 */
	public DataConfigFinderThread(final SupervisorThread supervisorThread,
			final Repository repository, final boolean checkOnce) {
		super(supervisorThread, repository, DataConfig.class, 30000, checkOnce);
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
	public DataConfigFinderThread(final SupervisorThread supervisorThread,
			final Repository repository, final long sleepTime,
			final boolean checkOnce) {
		super(supervisorThread, repository, DataConfig.class, sleepTime,
				checkOnce);
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
		super.beforeFind();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.FinderThread#getFinder()
	 */
	@Override
	protected Finder<DataConfig> getFinder() throws RegisterException {
		return new DataConfigFinder(repository);
	}
}
