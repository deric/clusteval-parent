/**
 * *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 *****************************************************************************
 */
package de.clusteval.api.stats;

import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * A data statistic is a {@link IStatistic}, which summarizes properties of data
 * sets. Data statistics are assessed by a {@link DataAnalysisRun}.
 * <p/>
 *
 * @author Christian Wiwie
 *
 */
public abstract class DataStatistic extends Statistic implements IDataStatistic {

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public DataStatistic(IRepository repository, boolean register,
            long changeDate, File absPath) throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * The copy constructor of data statistics.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public DataStatistic(final DataStatistic other) throws RegisterException {
        super(other);
    }

    @Override
    public final DataStatistic clone() {
        try {
            return this.getClass().getConstructor(this.getClass())
                    .newInstance(this);
        } catch (IllegalArgumentException | SecurityException | InstantiationException |
                 IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class "
                + this.getClass().getSimpleName() + " failed");
        return null;
    }

}
