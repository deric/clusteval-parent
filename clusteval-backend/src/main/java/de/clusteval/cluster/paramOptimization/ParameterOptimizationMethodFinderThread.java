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
package de.clusteval.cluster.paramOptimization;

import de.clusteval.api.opt.IParameterOptimizationMethod;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.run.ISupervisorThread;
import de.clusteval.utils.FinderThread;

/**
 * @author Christian Wiwie
 *
 */
public class ParameterOptimizationMethodFinderThread extends FinderThread<IParameterOptimizationMethod> {

    /**
     * @param supervisorThread
     * @param repository
     *                         The repository to check for new parameter optimization
     *                         methods.
     * @param sleepTime
     *                         The time between two checks.
     * @param checkOnce
     *                         If true, this thread only checks once for new parameter
     *                         optimization methods.
     *
     */
    public ParameterOptimizationMethodFinderThread(
            final ISupervisorThread supervisorThread,
            final IRepository repository, final long sleepTime,
            final boolean checkOnce) {
        super(supervisorThread, repository, IParameterOptimizationMethod.class,
                sleepTime, checkOnce);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FinderThread#getFinder()
     */
    @Override
    public ParameterOptimizationMethodFinder getFinder() throws RegisterException {
        return new ParameterOptimizationMethodFinder(repository);
    }
}
