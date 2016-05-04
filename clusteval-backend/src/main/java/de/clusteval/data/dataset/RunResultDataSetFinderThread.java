/** *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ***************************************************************************** */
package de.clusteval.data.dataset;

import de.clusteval.api.data.IDataSet;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.framework.threading.SupervisorThread;

/**
 * @author Christian Wiwie
 *
 */
public class RunResultDataSetFinderThread extends DataSetFinderThread {

    /**
     * @param supervisorThread
     * @param framework
     * @param sleepTime
     * @param checkOnce
     *
     */
    public RunResultDataSetFinderThread(
            final SupervisorThread supervisorThread,
            final IRepository framework, final long sleepTime,
            final boolean checkOnce) {
        super(supervisorThread, framework, sleepTime, checkOnce);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FinderThread#getFinder()
     */
    @Override
    public RunResultDataSetFinder getFinder() throws RegisterException {
        return new RunResultDataSetFinder(repository);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FinderThread#afterFind()
     */
    @Override
    protected void afterFind() {
        this.repository.setInitialized(IDataSet.class);
    }

}
