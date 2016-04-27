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
package de.clusteval.data.dataset.type;

import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.api.run.ISupervisorThread;
import de.clusteval.utils.FinderThread;

/**
 * @author Christian Wiwie
 *
 */
public class DataSetTypeFinderThread extends FinderThread<DataSetType> {

    /**
     * @param supervisorThread
     * @param framework
     * @param sleepTime
     * @param checkOnce
     *
     */
    public DataSetTypeFinderThread(final ISupervisorThread supervisorThread,
            final IRepository framework, final long sleepTime,
            final boolean checkOnce) {
        super(supervisorThread, framework, DataSetType.class, sleepTime,
                checkOnce);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FinderThread#beforeFind()
     */
    @Override
    protected void beforeFind() {
        this.log.debug("Checking for new DataSetTypes...");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FinderThread#afterFind()
     */
    @Override
    protected void afterFind() {
        repository.setInitialized(DataSetType.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FinderThread#getFinder()
     */
    @Override
    protected DataSetTypeFinder getFinder() throws RegisterException {
        return new DataSetTypeFinder(repository);
    }
}
