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
package de.clusteval.utils;

import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.stats.IStatistic;
import de.clusteval.data.statistics.RunStatisticCalculateException;
import de.clusteval.data.statistics.StatisticCalculateException;
import de.clusteval.api.repository.RepositoryObject;
import java.io.File;
import org.rosuda.REngine.REngineException;

/**
 * Together with every statistic class comes a calculator class, which is a
 * factory class for the corresponding statistic. The calculator has a
 * {@link #calculate()} method, which calculates, stores and returns a statistic
 * object.
 *
 * @author Christian Wiwie
 * @param <T>
 *
 */
public abstract class StatisticCalculator<T extends IStatistic> extends RepositoryObject {

    /**
     * This attribute holds the statistic, after {@link #calculate()} has been
     * invoked.
     */
    protected T lastResult;

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public StatisticCalculator(IRepository repository, long changeDate,
            File absPath) throws RegisterException {
        super(repository, true, changeDate, absPath);
    }

    /**
     * The copy constructor of statistic calculators
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public StatisticCalculator(final StatisticCalculator<T> other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.RepositoryObject#clone()
     */
    @Override
    public abstract StatisticCalculator<T> clone();

    /**
     * Calculate the result. This method stores the calculated result in the
     * {@link #lastResult} attribute for later usage, e.g. in
     * {@link #writeOutputTo(File)}.
     *
     * @return The calculated statistic.
     * @throws StatisticCalculateException
     */
    public T calculate() throws StatisticCalculateException {
        this.lastResult = calculateResult();
        return this.lastResult;
    }

    /**
     * Overwrite this method in subclasses to provide your own statistic
     * calculator type.
     *
     * @return The calculated statistic.
     * @throws IllegalArgumentException
     * @throws RunStatisticCalculateException
     */
    protected abstract T calculateResult() throws StatisticCalculateException;

    /**
     * @param absFolderPath The absolute path to the folder where the statistic
     *                      should be written to.
     * @throws REngineException
     * @throws RNotAvailableException
     * @throws InterruptedException
     */
    public abstract void writeOutputTo(final File absFolderPath)
            throws RException, InterruptedException;

    /**
     * @return The statistic calculated during the last {@link #calculate()}
     *         invocation.
     */
    public T getStatistic() {
        return this.lastResult;
    }
}
