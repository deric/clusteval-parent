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
package de.clusteval.run.statistics;

import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.run.ISupervisorThread;
import de.clusteval.api.stats.IRunDataStatistic;
import de.clusteval.utils.FinderThread;

/**
 * @author Christian Wiwie
 *
 */
public class RunDataStatisticFinderThread extends FinderThread<IRunDataStatistic> {

    /**
     * @param supervisorThread
     * @param framework
     * @param sleepTime
     * @param checkOnce
     *
     */
    public RunDataStatisticFinderThread(
            final ISupervisorThread supervisorThread,
            final IRepository framework, final long sleepTime,
            final boolean checkOnce) {
        super(supervisorThread, framework, IRunDataStatistic.class, sleepTime,
                checkOnce);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FinderThread#getFinder()
     */
    @Override
    public RunDataStatisticFinder getFinder() throws RegisterException {
        return new RunDataStatisticFinder(repository);
    }
}
