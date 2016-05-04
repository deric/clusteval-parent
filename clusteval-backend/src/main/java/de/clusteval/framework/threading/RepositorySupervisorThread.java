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
package de.clusteval.framework.threading;

import de.clusteval.api.data.DataConfig;
import de.clusteval.api.data.DataSetConfig;
import de.clusteval.api.data.DataSetFormat;
import de.clusteval.api.data.DistanceMeasure;
import de.clusteval.api.data.GoldStandardConfig;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.RunResultFormat;
import de.clusteval.cluster.paramOptimization.ParameterOptimizationMethodFinderThread;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.data.DataConfigFinderThread;
import de.clusteval.data.dataset.DataSetConfigFinderThread;
import de.clusteval.data.dataset.DataSetFinderThread;
import de.clusteval.data.dataset.generator.DataSetGenerator;
import de.clusteval.data.dataset.generator.DataSetGeneratorFinderThread;
import de.clusteval.data.goldstandard.GoldStandardConfigFinderThread;
import de.clusteval.data.preprocessing.DataPreprocessorFinderThread;
import de.clusteval.data.statistics.DataStatistic;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramConfigFinderThread;
import de.clusteval.program.r.RProgram;
import de.clusteval.program.r.RProgramFinderThread;
import de.clusteval.run.Run;
import de.clusteval.run.RunFinderThread;
import de.clusteval.run.result.RunResult;
import de.clusteval.run.result.RunResultFinderThread;
import de.clusteval.run.result.postprocessing.RunResultPostprocessorFinderThread;
import de.clusteval.api.stats.RunDataStatistic;
import de.clusteval.run.statistics.RunDataStatisticFinderThread;
import de.clusteval.api.stats.RunStatistic;
import java.util.Map;

/**
 * A type of supervisor thread which creates the standard set of threads
 * responsible for the initialization of a standard {@link IRepository}. In
 * detail threads of the following type are created, started and kept alive:
 *
 * <ul>
 * <li><b>DataSetFinderThread</b>: A thread which checks
 * {@link IRepository#basePath} for new datasets (see {@link DataSet}).</li>
 * <li><b>DistanceMeasureFinderThread</b>: A thread which checks
 * {@link IRepository#distanceMeasureBasePath} for new distance measures (see
 * {@link DistanceMeasure}).</li>
 * <li><b>DataStatisticFinderThread</b>: A thread which checks
 * {@link IRepository#dataStatisticBasePath} for new data statistics (see
 * {@link DataStatistic}).</li>
 * <li><b>RunStatisticFinderThread</b>: A thread which checks
 * {@link IRepository#runStatisticBasePath} for new run statistics (see
 * {@link RunStatistic}).</li>
 * <li><b>RunDataStatisticFinderThread</b>: A thread which checks
 * {@link IRepository#runDataStatisticBasePath} for new run data statistics (see
 * {@link RunDataStatistic}).</li>
 * <li><b>RunResultFormatFinderThread</b>: A thread which checks
 * {@link IRepository#runResultFormatBasePath} for new runresult formats (see
 * {@link RunResultFormat}).</li>
 * <li><b>ClusteringQualityMeasureFinderThread</b>: A thread which checks
 * {@link IRepository#clusteringQualityMeasureBasePath} for new clustering
 * quality measures (see {@link ClusteringQualityMeasure}).</li>
 * <li><b>ParameterOptimizationMethodFinderThread</b>: A thread which checks
 * {@link IRepository#parameterOptimizationMethodBasePath} for new parameter
 * optimization methods (see {@link DataSetFormat}).</li>
 * <li><b>DataSetConfigFinderThread</b>: A thread which checks
 * {@link IRepository#dataSetConfigBasePath} for new dataset configurations (see
 * {@link DataSetConfig}).</li>
 * <li><b>GoldStandardConfigFinderThread</b>: A thread which checks
 * {@link IRepository#goldStandardConfigBasePath} for new goldstandard
 * configurations (see {@link GoldStandardConfig}).</li>
 * <li><b>DataConfigFinderThread</b>: A thread which checks
 * {@link IRepository#dataConfigBasePath} for new data configurations (see
 * {@link DataConfig}).</li>
 * <li><b>RProgramFinderThread</b>: A thread which checks
 * {@link IRepository#programBasePath} for new RPrograms (see
 * {@link RProgram}).</li>
 * <li><b>ProgramConfigFinderThread</b>: A thread which checks
 * {@link IRepository#programConfigBasePath} for new program configurations (see
 * {@link ProgramConfig}).</li>
 * <li><b>DataSetGeneratorFinderThread</b>: A thread which checks
 * {@link IRepository#dataSetGeneratorBasePath} for new dataset generators (see
 * {@link DataSetGenerator}).</li>
 * <li><b>RunFinderThread</b>: A thread which checks
 * {@link IRepository#runBasePath} for new runs (see {@link Run}).</li>
 * <li><b>RunSchedulerThread</b>: A thread which is responsible for scheduling,
 * starting and terminating runs. It can be controlled with its
 * {@link RunSchedulerThread#schedule(String, String)},
 * {@link RunSchedulerThread#scheduleResume(String, String)} and
 * {@link RunSchedulerThread#terminate(String, String)} methods.</li>
 * </ul>
 *
 * If the boolean parameter <b>checkForRunResults</b> is true, additionally the
 * following threads are started and kept alive:
 *
 * <ul>
 * <li><b>RunResultFinderThread</b>: A thread which checks
 * {@link IRepository#runResultBasePath} for new run results (see
 * {@link RunResult}).</li>
 * </ul>
 *
 * @author Christian Wiwie
 *
 */
public class RepositorySupervisorThread extends SupervisorThread {

    /**
     * @param repository
     *                           The repository this thread belongs to.
     * @param threadSleepTimes
     *                           The sleep times of the created threads.
     * @param checkOnce
     *                           A boolean indicating, whether this thread should only check
     *                           once and terminate afterwards.
     * @param checkForRunResults
     *                           Whether this thread should check for run results in the
     *                           repository.
     */
    @SuppressWarnings({"unchecked"})
    public RepositorySupervisorThread(final IRepository repository,
            Map<String, Long> threadSleepTimes, final boolean checkOnce,
            final boolean checkForRunResults) {

        /**
         * TODO: finder threads could be discovered automatically (via finder factory)
         */
        super(repository, checkForRunResults
                          ? createList(
                        //DataSetFormatFinderThread.class,
                        //RunResultFormatFinderThread.class,
                        //ContextFinderThread.class,
                        //DataSetTypeFinderThread.class,
                        DataSetFinderThread.class,
                        DataPreprocessorFinderThread.class,
                        RunResultPostprocessorFinderThread.class,
                        //DistanceMeasureFinderThread.class,
                        //DataStatisticFinderThread.class,
                        //RunStatisticFinderThread.class,
                        RunDataStatisticFinderThread.class,
                        //ClusteringQualityMeasureFinderThread.class,
                        ParameterOptimizationMethodFinderThread.class,
                        DataSetConfigFinderThread.class,
                        GoldStandardConfigFinderThread.class,
                        DataConfigFinderThread.class,
                        RProgramFinderThread.class,
                        ProgramConfigFinderThread.class, RunFinderThread.class,
                        DataSetGeneratorFinderThread.class,
                        //DataRandomizerFinderThread.class,
                        RunResultFinderThread.class) : createList(
                        // normal elements
                        //DataSetFormatFinderThread.class,
                        //RunResultFormatFinderThread.class,
                        //ContextFinderThread.class,
                        //DataSetTypeFinderThread.class,
                        DataSetFinderThread.class,
                        DataPreprocessorFinderThread.class,
                        RunResultPostprocessorFinderThread.class,
                        //DistanceMeasureFinderThread.class,
                        // DataStatisticFinderThread.class,
                        //RunStatisticFinderThread.class,
                        RunDataStatisticFinderThread.class,
                        //ClusteringQualityMeasureFinderThread.class,
                        ParameterOptimizationMethodFinderThread.class,
                        DataSetConfigFinderThread.class,
                        GoldStandardConfigFinderThread.class,
                        DataConfigFinderThread.class,
                        RProgramFinderThread.class,
                        ProgramConfigFinderThread.class, RunFinderThread.class,
                        DataSetGeneratorFinderThread.class
                ), threadSleepTimes,
                checkOnce);

        this.threads.put(RunSchedulerThread.class, new RunSchedulerThread(this,
                this.repository, ClustevalBackendServer
                .getBackendServerConfiguration().getNumberOfThreads()));
        this.start();
    }
}
