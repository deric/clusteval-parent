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
package de.clusteval.run.result;

import de.clusteval.api.SQLConfig.DB_TYPE;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.framework.threading.SupervisorThread;
import de.clusteval.utils.Finder;
import de.clusteval.utils.FinderThread;

/**
 * @author Christian Wiwie
 *
 */
public class RunResultFinderThread extends FinderThread<RunResult> {

    /**
     * @param supervisorThread
     * @param repository
     *                         The repository to check for new run results.
     * @param checkOnce
     *                         If true, this thread only checks once for new run results.
     *
     */
    public RunResultFinderThread(final SupervisorThread supervisorThread,
            IRepository repository, final boolean checkOnce) {
        super(supervisorThread, repository, RunResult.class, 30000, checkOnce);
    }

    /**
     * @param supervisorThread
     * @param repository
     *                         The repository to check for new run results.
     * @param sleepTime
     *                         The time between two checks.
     * @param checkOnce
     *                         If true, this thread only checks once for new run results.
     *
     */
    public RunResultFinderThread(final SupervisorThread supervisorThread,
            IRepository repository, long sleepTime, final boolean checkOnce) {
        super(supervisorThread, repository, RunResult.class, sleepTime,
                checkOnce);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FinderThread#beforeFind()
     */
    @Override
    protected void beforeFind() {
        try {
            while (!this.repository.isInitialized()) {
                sleep(100);
            }
        } catch (InterruptedException e) {
            this.interrupt();
        }
        super.beforeFind();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.utils.FinderThread#afterFind()
     */
    @Override
    protected void afterFind() {
        super.afterFind();
        // we refresh materialized views in case we found new stuff and we are
        // using postgresql
        if (this.currentFinder.foundInLastRun()
                && this.repository.getRepositoryConfig().getDbConfig()
                .getDatabaseType().equals(DB_TYPE.POSTGRESQL)) {
            this.repository.getDb().refreshMaterializedViews();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FinderThread#getFinder()
     */
    @Override
    protected Finder<RunResult> getFinder() throws RegisterException {
        return new RunResultFinder(repository);
    }
}
