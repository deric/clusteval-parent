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

/**
 * @author Christian Wiwie
 *
 */
public abstract class DoubleValueDataStatistic extends DataStatistic {

    protected double value;

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @param value
     * @throws RegisterException
     */
    public DoubleValueDataStatistic(IRepository repository, boolean register,
            long changeDate, File absPath, double value)
            throws RegisterException {
        super(repository, false, changeDate, absPath);
        this.value = value;

        if (register) {
            this.register();
        }
    }

    /**
     * The copy constructor for this statistic.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public DoubleValueDataStatistic(final DoubleValueDataStatistic other)
            throws RegisterException {
        super(other);
        this.value = other.value;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Statistic#parseFromString(java.lang.String)
     */
    @Override
    public void parseFromString(String contents) {
        this.value = Double.valueOf(contents);
    }

    /**
     * @return The double value of this statistic.
     */
    public double getValue() {
        return this.value;
    }

    /*
     * (non-Javadoc)
     *
     * @see data.statistics.DataStatistic#toString()
     */
    @Override
    public String toString() {
        return this.value + "";
    }

}
