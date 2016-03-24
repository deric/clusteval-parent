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
package de.clusteval.run.runnable;

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.exceptions.RunIterationException;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.api.stats.IDataStatistic;
import de.clusteval.data.DataConfig;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.run.DataAnalysisRun;
import de.clusteval.run.Run;
import de.clusteval.run.result.DataAnalysisRunResult;
import java.io.File;
import java.util.List;

/**
 * A type of analysis runnable, that corresponds to {@link DataAnalysisRun} and
 * is responsible for analysing a data configuration (dataset and goldstandard).
 *
 * @author Christian Wiwie
 *
 */
public class DataAnalysisRunRunnable extends
        AnalysisRunRunnable<IDataStatistic, DataAnalysisRunResult, DataAnalysisIterationWrapper, DataAnalysisIterationRunnable> {

    /**
     * The data configuration to be analysed by this runnable.
     */
    protected IDataConfig dataConfig;

    protected int currentIteration = -1;

    /**
     * @param runScheduler   The run scheduler that the newly created runnable
     *                       should be passed to and executed by.
     *
     * @param run            The run this runnable belongs to.
     * @param runIdentString The unique identification string of the run which
     *                       is used to store the results in a unique folder to avoid overwriting.
     * @param dataConfig     The data configuration to be analysed by this runnable.
     * @param statistics     The statistics that should be assessed during execution
     *                       of this runnable.
     * @param isResume       True, if this run is a resumption of a previous execution
     *                       or a completely new execution.
     */
    public DataAnalysisRunRunnable(RunSchedulerThread runScheduler, Run run,
            String runIdentString, final boolean isResume,
            IDataConfig dataConfig, List<IDataStatistic> statistics) {
        super(run, runIdentString, statistics, isResume);
        this.dataConfig = dataConfig;
        this.future = runScheduler.registerRunRunnable(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see run.runnable.AnalysisRunRunnable#createRunResult()
     */
    @Override
    protected DataAnalysisRunResult createRunResult() throws RegisterException {
        return new DataAnalysisRunResult(this.getRun().getRepository(),
                System.currentTimeMillis(), new File(analysesFolder),
                this.runThreadIdentString, run);
    }

    /*
     * (non-Javadoc)
     *
     * @see run.runnable.AnalysisRunRunnable#afterRun()
     */
    @Override
    public void afterRun() {
        super.afterRun();
        result.put(this.dataConfig, results);
        this.getRun().getResults().add(result);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.runnable.AnalysisRunRunnable#beforeRun()
     */
    @Override
    public void beforeRun() {
        super.beforeRun();

        IDataSet current = this.dataConfig.getDatasetConfig().getDataSet();

        IDataSet ds;
        try {
            ds = current.preprocessAndConvertTo(this.run.getContext(), this.run
                    .getContext().getStandardInputFormat(), this.dataConfig
                    .getDatasetConfig()
                    .getConversionInputToStandardConfiguration(),
                    this.dataConfig.getDatasetConfig()
                    .getConversionStandardToInputConfiguration());

            this.dataConfig = new DataConfig(this.dataConfig);
            this.dataConfig.getDatasetConfig().setDataSet(ds);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "The given data configuration could not be converted.");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.run.runnable.AnalysisRunRunnable#createIterationRunnable
     * (de.clusteval.run.runnable.AnalysisIterationWrapper)
     */
    @Override
    protected DataAnalysisIterationRunnable createIterationRunnable(
            DataAnalysisIterationWrapper iterationWrapper) {
        return new DataAnalysisIterationRunnable(iterationWrapper);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.run.runnable.AnalysisRunRunnable#createIterationWrapper()
     */
    @Override
    protected DataAnalysisIterationWrapper createIterationWrapper() {
        return new DataAnalysisIterationWrapper();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.run.runnable.AnalysisRunRunnable#decorateIterationWrapper
     * (de.clusteval.run.runnable.AnalysisIterationWrapper, int)
     */
    @Override
    protected void decorateIterationWrapper(
            DataAnalysisIterationWrapper iterationWrapper, int currentPos)
            throws RunIterationException {
        super.decorateIterationWrapper(iterationWrapper, currentPos);
        iterationWrapper.setDataConfig(dataConfig);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.runnable.RunRunnable#hasNextIteration()
     */
    @Override
    public boolean hasNextIteration() {
        return currentIteration < this.statistics.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.runnable.RunRunnable#consumeNextIteration()
     */
    @Override
    public int consumeNextIteration() throws RunIterationException {
        return ++currentIteration;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.run.runnable.RunRunnable#doRunIteration(de.clusteval.run
     * .runnable.IterationWrapper)
     */
    @Override
    public void doRunIteration(DataAnalysisIterationWrapper iterationWrapper)
            throws RunIterationException {
        DataAnalysisIterationRunnable iterationRunnable = this
                .createIterationRunnable(iterationWrapper);

        this.submitIterationRunnable(iterationRunnable);
    }

    public IDataConfig getDataConfig() {
        return this.dataConfig;
    }
}
