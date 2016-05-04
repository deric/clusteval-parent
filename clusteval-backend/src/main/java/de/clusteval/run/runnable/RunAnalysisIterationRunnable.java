/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.run.runnable;

import de.clusteval.api.factory.StatisticCalculatorFactory;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.stats.IStatisticCalculator;
import de.clusteval.api.stats.RunStatistic;
import de.clusteval.utils.FileUtils;
import de.clusteval.utils.StatisticCalculator;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Christian Wiwie
 *
 */
public class RunAnalysisIterationRunnable extends AnalysisIterationRunnable<RunStatistic, RunAnalysisIterationWrapper> {

    /**
     * @param iterationWrapper
     */
    public RunAnalysisIterationRunnable(
            RunAnalysisIterationWrapper iterationWrapper) {
        super(iterationWrapper);
    }

    public String getRunIdentifier() {
        return this.iterationWrapper.getRunIdentifier();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.run.runnable.AnalysisIterationRunnable#beforeStatisticCalculate
     * ()
     */
    @Override
    protected void beforeStatisticCalculate() {
        this.log.info("Run " + this.getRun() + " - (" + this.getRunIdentifier()
                + ") Analysing " + getStatistic().getIdentifier());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * run.runnable.AnalysisRunRunnable#getStatisticCalculator(java.lang.Class)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected StatisticCalculator<RunStatistic> getStatisticCalculator()
            throws SecurityException, NoSuchMethodException,
                   IllegalArgumentException, InstantiationException,
                   IllegalAccessException, InvocationTargetException, UnknownProviderException {
        /* Class<? extends RunStatisticCalculator> calcClass = getRun()
         * .getRepository().getRunStatisticCalculator(
         * getStatistic().getClass().getName());
         * Constructor<? extends RunStatisticCalculator> constr = calcClass
         * .getConstructor(IRepository.class, long.class, File.class,
         * String.class); */
        IStatisticCalculator calc = StatisticCalculatorFactory.getInstance()
                .getProvider(getStatistic().getClass().getName());

        return (StatisticCalculator<RunStatistic>) calc;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.runnable.AnalysisIterationRunnable#getOutputPath()
     */
    @Override
    protected String getOutputPath() {
        return FileUtils.buildPath(this.iterationWrapper.getAnalysesFolder(),
                this.getRunIdentifier() + "_"
                + this.getStatistic().getIdentifier() + ".txt");
    }
}
