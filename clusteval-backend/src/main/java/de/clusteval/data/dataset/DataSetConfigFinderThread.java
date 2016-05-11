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
import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.ISupervisorThread;
import de.clusteval.utils.Finder;
import de.clusteval.utils.FinderThread;

/**
 * @author Christian Wiwie
 *
 */
public class DataSetConfigFinderThread extends FinderThread<IDataSetConfig> {

    /**
     * @param supervisorThread
     * @param repository
     *                         The repository to check for new dataset configurations.
     * @param sleepTime
     *                         The time between two checks.
     * @param checkOnce
     *                         If true, this thread only checks once for new dataset
     *                         configurations.
     *
     */
    public DataSetConfigFinderThread(final ISupervisorThread supervisorThread,
            final IRepository repository, final long sleepTime,
            final boolean checkOnce) {
        super(supervisorThread, repository, IDataSetConfig.class, sleepTime,
                checkOnce);
    }

    @Override
    protected void beforeFind() {
        if (!this.repository.isInitialized(IDataSet.class)) {
            this.supervisorThread.getThread(DataSetFinderThread.class)
                    .waitFor();
        }

        super.beforeFind();
    }

    @Override
    public Finder<IDataSetConfig> getFinder() throws RegisterException {
        return new DataSetConfigFinder(repository);
    }
}
