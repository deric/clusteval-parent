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
package de.clusteval.run;

import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.data.IGoldStandardConfig;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.ISupervisorThread;
import de.clusteval.data.dataset.DataSetConfigFinderThread;
import de.clusteval.data.goldstandard.GoldStandardConfigFinderThread;
import de.clusteval.program.ProgramConfigFinderThread;
import de.clusteval.utils.Finder;
import de.clusteval.utils.FinderThread;

/**
 * A thread that uses a {@link RunFinder} to check the repository for new runs.
 *
 * @author Christian Wiwie
 *
 */
public class RunFinderThread extends FinderThread<IRun> {

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
    public RunFinderThread(final ISupervisorThread supervisorThread,
            final IRepository repository, final long sleepTime,
            final boolean checkOnce) {
        super(supervisorThread, repository, IRun.class, sleepTime, checkOnce);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FinderThread#beforeFind()
     */
    @Override
    protected void beforeFind() {

        if (!this.repository.isInitialized(IDataSetConfig.class)) {
            this.supervisorThread.getThread(DataSetConfigFinderThread.class)
                    .waitFor();
        }

        if (!this.repository.isInitialized(IGoldStandardConfig.class)) {
            this.supervisorThread.getThread(
                    GoldStandardConfigFinderThread.class).waitFor();
        }

        if (!this.repository.isInitialized(IProgramConfig.class)) {
            this.supervisorThread.getThread(ProgramConfigFinderThread.class)
                    .waitFor();
        }

        super.beforeFind();
    }

    @Override
    public Finder<IRun> getFinder() throws RegisterException {
        return new RunFinder(repository);
    }
}
