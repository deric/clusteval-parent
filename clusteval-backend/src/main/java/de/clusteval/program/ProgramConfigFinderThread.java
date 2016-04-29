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

import de.clusteval.api.IContext;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRProgram;
import de.clusteval.api.run.ISupervisorThread;
import de.clusteval.context.ContextFinderThread;
import de.clusteval.program.r.RProgramFinderThread;
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

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FinderThread#beforeFind()
     */
    @Override
    protected void beforeFind() {

        if (!this.repository.isInitialized(IRProgram.class)) {
            this.supervisorThread.getThread(RProgramFinderThread.class)
                    .waitFor();
        }

        if (!this.repository.isInitialized(IContext.class)) {
            this.supervisorThread.getThread(ContextFinderThread.class)
                    .waitFor();
        }
        super.beforeFind();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FinderThread#getFinder()
     */
    @Override
    public Finder<IProgramConfig> getFinder() throws RegisterException {
        return new ProgramConfigFinder(repository);
    }
}
