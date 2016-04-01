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

import de.clusteval.api.repository.IRepository;
import de.clusteval.run.statistics.RunDataStatistic;
import de.clusteval.run.statistics.RunDataStatisticCalculator;
import de.clusteval.utils.StatisticCalculator;
import de.wiwie.wiutils.file.FileUtils;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Christian Wiwie
 *
 */
public class RunDataAnalysisIterationRunnable extends AnalysisIterationRunnable<RunDataStatistic, RunDataAnalysisIterationWrapper> {

    /**
     * @param iterationWrapper
     */
    public RunDataAnalysisIterationRunnable(RunDataAnalysisIterationWrapper iterationWrapper) {
        super(iterationWrapper);
    }

    public String getRunIdentifier() {
        return this.iterationWrapper.getRunIdentifier();
    }

    public String getDataAnalysisIdentifier() {
        return this.iterationWrapper.uniqueDataAnalysisRunIdentifier;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.run.runnable.AnalysisIterationRunnable#beforeStatisticCalculate
     * ()
     */
    protected void beforeStatisticCalculate() {
        this.log.info("Run " + this.getRun() + " - (" + this.getRunIdentifier()
                + "," + this.getDataAnalysisIdentifier() + ") Analysing "
                + this.getStatistic().getIdentifier());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * run.runnable.AnalysisRunRunnable#getStatisticCalculator(java.lang.Class)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected StatisticCalculator<RunDataStatistic> getStatisticCalculator()
            throws SecurityException, NoSuchMethodException,
                   IllegalArgumentException, InstantiationException,
                   IllegalAccessException, InvocationTargetException {
        Class<? extends RunDataStatisticCalculator> calcClass = getRun()
                .getRepository().getRunDataStatisticCalculator(
                        this.getStatistic().getClass().getName());
        Constructor<? extends RunDataStatisticCalculator> constr = calcClass
                .getConstructor(IRepository.class, long.class, File.class, List.class, List.class);
        RunDataStatisticCalculator calc = constr.newInstance(this.getRun()
                .getRepository(), calcFile.lastModified(), calcFile,
                this.iterationWrapper.getRunIdentifier(), this.iterationWrapper
                .getUniqueDataAnalysisRunIdentifier());
        return calc;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.runnable.AnalysisIterationRunnable#getOutputPath()
     */
    @Override
    protected String getOutputPath() {
        return FileUtils.buildPath(this.iterationWrapper.getAnalysesFolder(),
                this.getStatistic().getIdentifier() + ".txt");
    }
}
