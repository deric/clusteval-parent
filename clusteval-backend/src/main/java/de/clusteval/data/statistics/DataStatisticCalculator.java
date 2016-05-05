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
package de.clusteval.data.statistics;

import de.clusteval.api.stats.DataStatistic;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.utils.StatisticCalculator;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * This class is parent class of all different kind of analyses on a DataConfig.
 * This analyses can be performed unrelated to clustering, since it only
 * requires the dataset (and optionally the goldstandard).
 *
 * @author Christian Wiwie
 * @param <T>
 *
 */
public abstract class DataStatisticCalculator<T extends DataStatistic> extends StatisticCalculator<T> {

    protected IDataConfig dataConfig;

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param dataConfig
     * @throws RegisterException
     */
    public DataStatisticCalculator(IRepository repository, long changeDate,
            File absPath, final IDataConfig dataConfig) throws RegisterException {
        super(repository, changeDate, absPath);
        this.dataConfig = dataConfig;
    }

    /**
     * The copy constructor of data statistic calculators.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public DataStatisticCalculator(final DataStatisticCalculator<T> other)
            throws RegisterException {
        super(other);
        this.dataConfig = other.dataConfig.clone();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @SuppressWarnings("unchecked")
    @Override
    public DataStatisticCalculator<T> clone() {
        try {
            return this.getClass()
                    .getConstructor(DataStatisticCalculator.class)
                    .newInstance(this);
        } catch (IllegalArgumentException | SecurityException | InstantiationException |
                IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class "
                + this.getClass().getSimpleName() + " failed");
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.StatisticCalculator#calculate()
     */
    @Override
    public T calculate() throws StatisticCalculateException {
        return super.calculate();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.StatisticCalculator#calculate()
     */
    @Override
    protected abstract T calculateResult() throws DataStatisticCalculateException;

    @Override
    public T getStatistic() {
        return super.getStatistic();
    }
}
