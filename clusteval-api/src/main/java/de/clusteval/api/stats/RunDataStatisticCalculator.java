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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Wiwie
 * @param <T>
 *
 */
public abstract class RunDataStatisticCalculator<T extends RunDataStatistic> extends StatisticCalculator<T> {

    protected List<String> uniqueRunIdentifiers;
    protected List<String> uniqueDataIdentifiers;

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param uniqueRunIdentifiers
     * @param uniqueDataIdentifiers
     * @throws RegisterException
     */
    public RunDataStatisticCalculator(IRepository repository, long changeDate,
            File absPath, final List<String> uniqueRunIdentifiers,
            final List<String> uniqueDataIdentifiers) throws RegisterException {
        super(repository, changeDate, absPath);
        this.uniqueRunIdentifiers = uniqueRunIdentifiers;
        this.uniqueDataIdentifiers = uniqueDataIdentifiers;
    }

    /**
     * The copy constructor of run data statistic calculators.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public RunDataStatisticCalculator(final RunDataStatisticCalculator<T> other)
            throws RegisterException {
        super(other);

        this.uniqueRunIdentifiers = new ArrayList<>(other.uniqueRunIdentifiers);
        this.uniqueDataIdentifiers = new ArrayList<>(other.uniqueDataIdentifiers);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.StatisticCalculator#clone()
     */
    @SuppressWarnings("unchecked")
    @Override
    public RunDataStatisticCalculator<T> clone() {
        try {
            return this.getClass()
                    .getConstructor(RunDataStatisticCalculator.class)
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
     * @see de.wiwie.wiutils.utils.StatisticCalculator#calculateResult()
     */
    @Override
    protected abstract T calculateResult()
            throws RunStatisticCalculateException;

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.StatisticCalculator#getStatistic()
     */
    @Override
    public abstract T getStatistic();
}
