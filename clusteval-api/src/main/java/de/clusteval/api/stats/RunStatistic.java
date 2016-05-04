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
package de.clusteval.api.stats;

import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * A run statistic is a {@link Statistic}, which summarizes properties of
 * clustering run results. Run statistics are assessed by a
 * {@link RunAnalysisRun}.
 * <p/>
 *

 *
 * @author Christian Wiwie
 *
 */
public abstract class RunStatistic extends Statistic implements IRunStatistic {

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public RunStatistic(IRepository repository, boolean register, long changeDate, File absPath) throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * The copy constructor of run statistics.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public RunStatistic(final RunStatistic other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public final RunStatistic clone() {
        try {
            return this.getClass().getConstructor(this.getClass())
                    .newInstance(this);
        } catch (IllegalArgumentException | SecurityException |
                InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class "
                + this.getClass().getSimpleName() + " failed");
        return null;
    }

}
