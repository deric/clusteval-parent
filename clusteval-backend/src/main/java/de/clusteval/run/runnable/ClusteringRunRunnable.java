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
package de.clusteval.run.runnable;

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.IncompatibleDataSetFormatException;
import de.clusteval.api.exceptions.IncompleteGoldStandardException;
import de.clusteval.api.exceptions.InternalAttributeException;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.exceptions.RunIterationException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.ParameterOptimizationMethod;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IScheduler;
import de.clusteval.run.ClusteringRun;
import de.clusteval.run.result.ClusteringRunResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * A type of an execution runnable, that corresponds to {@link ClusteringRun}
 * and is therefore responsible for performing only a single clustering.
 *
 * <p>
 * In {@link #doRun()} a ClusteringRunRunnable executes only a single iteration.
 *
 * @author Christian Wiwie
 *
 */
public class ClusteringRunRunnable extends ExecutionRunRunnable {

    protected boolean hasNext = true;
    protected boolean finished;
    public static final String NAME = "ClusteringRunRunnable";

    /**
     * @param runScheduler
     *                       The run scheduler that the newly created runnable should be
     *                       passed to and executed by.
     * @param run
     *                       The run this runnable belongs to.
     * @param runIdentString
     *                       The unique identification string of the run which is used to
     *                       store the results in a unique folder to avoid overwriting.
     * @param programConfig
     *                       The program configuration encapsulating the program executed
     *                       by this runnable.
     * @param dataConfig
     *                       The data configuration used by this runnable.
     * @param isResume
     *                       True, if this run is a resumption of a previous execution or a
     *                       completely new execution.
     * @param runParams
     */
    public ClusteringRunRunnable(IScheduler runScheduler, IRun run,
            IProgramConfig programConfig, IDataConfig dataConfig,
            String runIdentString, boolean isResume,
            Map<IProgramParameter<?>, String> runParams) {
        super(run, programConfig, dataConfig, runIdentString, isResume,
                runParams);
        this.future = runScheduler.registerRunRunnable(this);
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init(IScheduler runScheduler, IRun run, IProgramConfig programConfig, IDataConfig dataConfig, ParameterOptimizationMethod optimizationMethod, String runIdentString, boolean isResume, Map<IProgramParameter<?>, String> runParams) {
        super.init(run, programConfig, dataConfig, runIdentString, isResume, runParams);
        this.future = runScheduler.registerRunRunnable(this);
    }

    @Override
    public void beforeRun() throws IllegalArgumentException, IOException,
                                   InvalidDataSetFormatException, RegisterException,
                                   InternalAttributeException, IncompatibleDataSetFormatException,
                                   UnknownGoldStandardFormatException,
                                   IncompleteGoldStandardException, InterruptedException, UnknownProviderException {
        super.beforeRun();

        if (!new File(completeQualityOutput).exists() || !isResume) {
            writeHeaderIntoCompleteFile(completeQualityOutput);
        }

        // count lines in completeQualityOutput;
        // if the file contains at least 2 lines, it means that the result
        // is
        // already there and we do not need to execute this runnable
        BufferedReader br = new BufferedReader(new FileReader(
                completeQualityOutput));
        int noLines = 0;
        while (br.ready()) {
            br.readLine();
            noLines++;
        }
        br.close();
        this.finished = noLines >= 2;
        if (this.finished) {
            ClusteringRunResult res = ClusteringRunResult
                    .parseFromRunResultCompleteFile(this.run.getRepository(),
                            (ClusteringRun) this.getRun(), this.dataConfig,
                            this.programConfig,
                            new File(completeQualityOutput), false);

            synchronized (getRun().getResults()) {
                getRun().getResults().add(res);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.runnable.RunRunnable#hasNextIteration()
     */
    @Override
    public boolean hasNextIteration() {
        return !finished && this.hasNext;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.runnable.RunRunnable#consumeNextIteration()
     */
    @Override
    public int consumeNextIteration() {
        this.hasNext = false;
        return 1;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.run.runnable.RunRunnable#decorateIterationWrapper(de.clusteval
     * .run.runnable.IterationWrapper, int)
     */
    @Override
    protected void decorateIterationWrapper(
            ExecutionIterationWrapper iterationWrapper, int currentPos)
            throws RunIterationException {
        iterationWrapper.setOptId(1);
        super.decorateIterationWrapper(iterationWrapper, currentPos);
    }

    /*
     * (non-Javadoc)
     *
     * @see run.runnable.ExecutionRunRunnable#handleMissingRunResult()
     */
    @Override
    protected void handleMissingRunResult(
            final ExecutionIterationWrapper iterationWrapper) {
        this.log.info(this.getRun()
                + " ("
                + this.programConfig
                + ","
                + this.dataConfig
                + ") The result of this run could not be found. Please consult the log files of the program");

        super.handleMissingRunResult(iterationWrapper);
    }

    @Override
    public ParameterOptimizationMethod getOptimizationMethod() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
