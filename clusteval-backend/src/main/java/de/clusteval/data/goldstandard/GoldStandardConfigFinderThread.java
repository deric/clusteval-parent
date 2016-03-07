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
package de.clusteval.data.goldstandard;

import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.threading.SupervisorThread;
import de.clusteval.utils.Finder;
import de.clusteval.utils.FinderThread;

/**
 * @author Christian Wiwie
 * 
 */
public class GoldStandardConfigFinderThread
		extends
			FinderThread<GoldStandardConfig> {

	/**
	 * @param supervisorThread
	 * @param repository
	 *            The repository to check for new goldstandard configurations.
	 * @param checkOnce
	 *            If true, this thread only checks once for new goldstandard
	 *            configurations.
	 * 
	 */
	public GoldStandardConfigFinderThread(
			final SupervisorThread supervisorThread,
			final Repository repository, final boolean checkOnce) {
		super(supervisorThread, repository, GoldStandardConfig.class, 30000,
				checkOnce);
	}

	/**
	 * @param supervisorThread
	 * @param repository
	 *            The repository to check for new goldstandard configurations.
	 * @param sleepTime
	 *            The time between two checks.
	 * @param checkOnce
	 *            If true, this thread only checks once for new goldstandard
	 *            configurations.
	 * 
	 */
	public GoldStandardConfigFinderThread(
			final SupervisorThread supervisorThread,
			final Repository repository, final long sleepTime,
			final boolean checkOnce) {
		super(supervisorThread, repository, GoldStandardConfig.class,
				sleepTime, checkOnce);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.FinderThread#getFinder()
	 */
	@Override
	protected Finder<GoldStandardConfig> getFinder() throws RegisterException {
		return new GoldStandardConfigFinder(repository);
	}
}
