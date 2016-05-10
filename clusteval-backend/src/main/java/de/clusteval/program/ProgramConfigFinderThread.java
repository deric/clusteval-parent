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
package de.clusteval.program;

import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.ISupervisorThread;
import de.clusteval.utils.Finder;
import de.clusteval.utils.FinderThread;

/**
 * @author Christian Wiwie
 *
 */
public class ProgramConfigFinderThread extends FinderThread<IProgramConfig> {

    /**
     * @param supervisorThread
     * @param repository
     *                         The repository to check for new program configurations.
     * @param sleepTime
     *                         The time between two checks.
     * @param checkOnce
     *                         If true, this thread only checks once for new program
     *                         configurations.
     *
     */
    public ProgramConfigFinderThread(final ISupervisorThread supervisorThread,
            final IRepository repository, final long sleepTime,
            final boolean checkOnce) {
        super(supervisorThread, repository, IProgramConfig.class, sleepTime,
                checkOnce);
    }

    @Override
    protected void beforeFind() {
        super.beforeFind();
    }

    @Override
    public Finder<IProgramConfig> getFinder() throws RegisterException {
        return new ProgramConfigFinder(repository);
    }
}
