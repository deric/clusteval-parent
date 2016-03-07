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
package de.clusteval.data.dataset;

import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.dataset.format.DataSetFormatFinderThread;
import de.clusteval.data.dataset.type.DataSetType;
import de.clusteval.data.dataset.type.DataSetTypeFinderThread;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.threading.SupervisorThread;
import de.clusteval.utils.FinderThread;

/**
 * @author Christian Wiwie
 * 
 */
public class DataSetFinderThread extends FinderThread<DataSet> {

	/**
	 * @param supervisorThread
	 * @param framework
	 * @param checkOnce
	 * 
	 */
	public DataSetFinderThread(final SupervisorThread supervisorThread,
			final Repository framework, final boolean checkOnce) {
		super(supervisorThread, framework, DataSet.class, 30000, checkOnce);
	}

	/**
	 * @param supervisorThread
	 * @param framework
	 * @param sleepTime
	 * @param checkOnce
	 * 
	 */
	public DataSetFinderThread(final SupervisorThread supervisorThread,
			final Repository framework, final long sleepTime,
			final boolean checkOnce) {
		super(supervisorThread, framework, DataSet.class, sleepTime, checkOnce);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.FinderThread#beforeFind()
	 */
	@Override
	protected void beforeFind() {

		if (!this.repository.isInitialized(DataSetFormat.class))
			this.supervisorThread.getThread(DataSetFormatFinderThread.class)
					.waitFor();

		if (!this.repository.isInitialized(DataSetType.class))
			this.supervisorThread.getThread(DataSetTypeFinderThread.class)
					.waitFor();
		super.beforeFind();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.FinderThread#getFinder()
	 */
	@Override
	protected DataSetFinder getFinder() throws RegisterException {
		return new DataSetFinder(repository);
	}
}
