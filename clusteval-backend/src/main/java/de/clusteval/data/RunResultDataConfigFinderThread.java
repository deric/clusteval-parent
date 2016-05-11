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
package de.clusteval.data;

import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.data.IGoldStandardConfig;
import de.clusteval.api.repository.IRepository;
import de.clusteval.data.dataset.RunResultDataSetConfigFinderThread;
import de.clusteval.data.goldstandard.GoldStandardConfigFinderThread;
import de.clusteval.framework.threading.SupervisorThread;

/**
 * @author Christian Wiwie
 *
 */
public class RunResultDataConfigFinderThread extends DataConfigFinderThread {

    /**
     * @param supervisorThread
     * @param repository
     *                         The repository to check for new data configurations.
     * @param checkOnce
     *                         If true, this thread only checks once for new data
     *                         configurations.
     *
     */
    public RunResultDataConfigFinderThread(
            final SupervisorThread supervisorThread,
            final IRepository repository, final boolean checkOnce) {
        super(supervisorThread, repository, 30000, checkOnce);
    }

    /**
     * @param supervisorThread
     * @param repository
     *                         The repository to check for new runs.
     * @param sleepTime
     *                         The time between two checks.
     * @param checkOnce
     *                         If true, this thread only checks once for new runs.
     *
     */
    public RunResultDataConfigFinderThread(
            final SupervisorThread supervisorThread,
            final IRepository repository, final long sleepTime,
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
        if (!this.repository.isInitialized(IDataSetConfig.class)) {
            this.supervisorThread.getThread(
                    RunResultDataSetConfigFinderThread.class).waitFor();
        }

        if (!this.repository.isInitialized(IGoldStandardConfig.class)) {
            this.supervisorThread.getThread(GoldStandardConfigFinderThread.class).waitFor();
        }
        this.log.debug("Checking for DataConfigs...");
    }
}
