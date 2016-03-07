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
package de.clusteval.cluster.quality;

import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.threading.SupervisorThread;
import de.clusteval.utils.FinderThread;

/**
 * @author Christian Wiwie
 * 
 */
public class ClusteringQualityMeasureFinderThread
		extends
			FinderThread<ClusteringQualityMeasure> {

	/**
	 * @param supervisorThread
	 * @param repository
	 *            The repository to check for new clustering quality measures.
	 * @param checkOnce
	 *            If true, this thread only checks once for new clustering
	 *            quality measures.
	 * 
	 */
	public ClusteringQualityMeasureFinderThread(
			final SupervisorThread supervisorThread,
			final Repository repository, final boolean checkOnce) {
		super(supervisorThread, repository, ClusteringQualityMeasure.class,
				30000, checkOnce);
	}

	/**
	 * @param supervisorThread
	 * @param repository
	 *            The repository to check for new clustering quality measures.
	 * @param sleepTime
	 *            The time between two checks.
	 * @param checkOnce
	 *            If true, this thread only checks once for new clustering
	 *            quality measures.
	 * 
	 */
	public ClusteringQualityMeasureFinderThread(
			final SupervisorThread supervisorThread,
			final Repository repository, final long sleepTime,
			final boolean checkOnce) {
		super(supervisorThread, repository, ClusteringQualityMeasure.class,
				sleepTime, checkOnce);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.FinderThread#getFinder()
	 */
	@Override
	protected ClusteringQualityMeasureFinder getFinder()
			throws RegisterException {
		return new ClusteringQualityMeasureFinder(repository);
	}
}
