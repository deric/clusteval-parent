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
package de.clusteval.api.opt;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.IContext;
import de.clusteval.api.Pair;
import de.clusteval.api.cluster.ClusteringQualitySet;
import de.clusteval.api.data.AbsoluteDataSetFormat;
import de.clusteval.api.data.DataSetFormat;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.exceptions.RunResultParseException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.program.ProgramParameter;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryEvent;
import de.clusteval.api.repository.RepositoryRemoveEvent;
import de.clusteval.api.run.ExecutionRun;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IRunResult;
import de.clusteval.api.run.IRunResultPostprocessor;
import de.clusteval.api.run.IRunRunnable;
import de.clusteval.api.run.IScheduler;
import de.clusteval.api.run.IncompatibleParameterOptimizationMethodException;
import de.clusteval.api.run.OptStatus;
import de.clusteval.api.run.RunRunnableFactory;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A type of execution run that performs several clusterings with different
 * parameter sets determined in an automatized way for every pair of program and
 * data configuration.
 *
 * <p>
 * The evaluated parameter sets during a parameter optimization for one pair of
 * program and data configuration are determined by the corresponding
 * {@link ParameterOptimizationMethod} stored in {@link #optimizationMethods}.
 *
 * <p>
 * Every evaluated parameter set is stored in the
 * {@link #optimizationParameters} attribute, such that evaluation of the
 * results is possible after termination of the run.
 *
 * <p>
 * The results of the clusterings evaluated for every parameter set are also
 * stored in the {@link ParameterOptimizationMethod} object.
 *
 * @author Christian Wiwie
 *
 */
public class ParameterOptimizationRun extends ExecutionRun {

    /**
     * This method verifies compatibility between a parameter optimization
     * method, the data input format and the program configuration.
     *
     * <p>
     * Some parameter optimization do only work for certain programs, e.g.
     * {@link GapStatisticParameterOptimizationMethod } works only for
     * {@link KMeansClusteringRProgram} and {@link AbsoluteDataSetFormat}.
     *
     * @param dataConfigs
     * @param programConfigs
     * @param optimizationMethods
     * @throws IncompatibleParameterOptimizationMethodException
     *
     */
    public static void checkCompatibilityParameterOptimizationMethod(
            final List<ParameterOptimizationMethod> optimizationMethods, final List<IProgramConfig> programConfigs,
            final List<IDataConfig> dataConfigs) throws IncompatibleParameterOptimizationMethodException {
        for (ParameterOptimizationMethod method : optimizationMethods) {
            if (!method.getCompatibleDataSetFormatBaseClasses().isEmpty()) {
                // for every datasetformat we check, whether it class is
                // compatible
                for (IDataConfig dataConfig : dataConfigs) {
                    Class<? extends IDataSetFormat> dataSetFormatClass = dataConfig.getDatasetConfig().getDataSet()
                            .getDataSetFormat().getClass();
                    boolean compatible = false;
                    for (Class<? extends DataSetFormat> parentClass : method.getCompatibleDataSetFormatBaseClasses()) {
                        if (parentClass.isAssignableFrom(dataSetFormatClass)) {
                            compatible = true;
                            break;
                        }
                    }
                    if (!compatible) {
                        throw new IncompatibleParameterOptimizationMethodException("The ParameterOptimizationMethod "
                                + method.getClass().getSimpleName() + " cannot be applied to the dataset "
                                + dataConfig.getDatasetConfig().getDataSet() + " with the format "
                                + dataSetFormatClass.getSimpleName());
                    }
                }
            }

            if (!method.getCompatibleProgramNames().isEmpty()) {
                // for every program we check, whether it class is
                // compatible
                for (IProgramConfig programConfig : programConfigs) {
                    String programName = programConfig.getProgram().getMajorName();
                    boolean compatible = method.getCompatibleProgramNames().contains(programName);
                    if (!compatible) {
                        throw new IncompatibleParameterOptimizationMethodException(
                                "The ParameterOptimizationMethod " + method.getClass().getSimpleName()
                                + " cannot be applied to the program " + programName);
                    }
                }
            }
        }
    }

    /**
     * This list holds another list of optimization parameters for every program
     * configuration. These optimization parameters are to be optimized by this
     * run.
     */
    protected List<List<IProgramParameter<?>>> optimizationParameters;

    /**
     * This list holds the parameter optimization methods for every pair of
     * program and data configuration. These method objects control and keep
     * track of the parameter sets and the results.
     */
    protected List<ParameterOptimizationMethod> optimizationMethods;

    /**
     * New objects of this type are automatically registered at the repository.
     *
     * @param repository             the repository
     * @param context
     * @param changeDate             The date this run was performed.
     * @param absPath                The absolute path to the file on the filesystem that
     *                               corresponds to this run.
     * @param programConfigs         The program configurations of the new run.
     * @param dataConfigs            The data configurations of the new run.
     * @param qualityMeasures        The clustering quality measures of the new run.
     * @param parameterValues        The parameter values of this run.
     * @param optimizationParameters The parameters that are to be optimized
     *                               during this run.
     * @param optimizationMethods    The parameter optimization methods determines
     *                               which parameter sets are to be evaluated and stores the results.
     * @param postProcessors
     * @param maxExecutionTimes
     * @throws RegisterException
     */
    public ParameterOptimizationRun(final IRepository repository, final IContext context, final long changeDate,
            final File absPath, final List<IProgramConfig> programConfigs, final List<IDataConfig> dataConfigs,
            final List<ClusteringEvaluation> qualityMeasures,
            final List<Map<IProgramParameter<?>, String>> parameterValues,
            final List<List<IProgramParameter<?>>> optimizationParameters,
            final List<ParameterOptimizationMethod> optimizationMethods,
            final List<IRunResultPostprocessor> postProcessors, final Map<String, Integer> maxExecutionTimes)
            throws RegisterException {
        super(repository, context, false, changeDate, absPath, programConfigs, dataConfigs, qualityMeasures,
                parameterValues, postProcessors, maxExecutionTimes);

        this.optimizationParameters = optimizationParameters;
        this.optimizationMethods = optimizationMethods;

        if (this.register()) {
            // register this Run at all dataconfigs and programconfigs
            for (IDataConfig dataConfig : this.dataConfigs) {
                dataConfig.addListener(this);
            }
            for (IProgramConfig programConfig : this.programConfigs) {
                programConfig.addListener(this);
            }
            for (ParameterOptimizationMethod method : this.optimizationMethods) {
                method.addListener(this);
            }

            for (ClusteringEvaluation measure : this.qualityMeasures) {
                // added 21.03.2013: measures are only registered here, if this
                // run has been registered
                measure.register();
                measure.addListener(this);
            }
        }
    }

    /**
     * Copy constructor of parameter optimization runs.
     *
     * @param otherRun The parameter optimization run to be cloned.
     * @throws RegisterException
     */
    protected ParameterOptimizationRun(final ParameterOptimizationRun otherRun) throws RegisterException {
        super(otherRun);
        this.optimizationMethods = ParameterOptimizationMethod.cloneOptimizationMethods(otherRun.optimizationMethods);
        this.optimizationParameters = ProgramParameter.cloneParameterListList(otherRun.optimizationParameters);
    }

    /*
     * (non-Javadoc)
     *
     * @see run.ExecutionRun#createRunRunnableFor(framework.RunScheduler,
     * run.Run, program.ProgramConfig, data.DataConfig, java.lang.String,
     * boolean)
     */
    @Override
    protected IRunRunnable createRunRunnableFor(IScheduler runScheduler, IRun run,
            IProgramConfig programConfig, IDataConfig dataConfig, String runIdentString, boolean isResume,
            Map<IProgramParameter<?>, String> runParams) throws UnknownProviderException {

        // 06.04.2013: changed from indexOf to this manual search, because at
        // this point the passed programConfig and dataConfig are moved clones
        // of the originals in #runPairs
        int p = -1;
        for (int i = 0; i < ((ParameterOptimizationRun) run).getRunPairs().size(); i++) {
            Pair<IProgramConfig, IDataConfig> pair = ((ParameterOptimizationRun) run).getRunPairs().get(i);
            if (pair.getFirst().getName().equals(programConfig.getName())
                    && pair.getSecond().getName().equals(dataConfig.getName())) {
                p = i;
                break;
            }
        }

        ParameterOptimizationMethod optimizationMethod = ((ParameterOptimizationRun) run).getOptimizationMethods()
                .get(p);
        IRunRunnable t = RunRunnableFactory.parseFromString("ParameterOptimizationRunRunnable");

        t.init(runScheduler, run, programConfig,
                dataConfig, optimizationMethod, runIdentString, isResume, runParams);
        run.addSubProgress(t.getProgress(), 10000);
        return t;
    }

    /*
     * (non-Javadoc)
     *
     * @see run.Run#clone()
     */
    @Override
    public ParameterOptimizationRun clone() {
        try {
            return new ParameterOptimizationRun(this);
        } catch (RegisterException e) {
            // should not occur
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see run.ExecutionRun#notify(framework.repository.RepositoryEvent)
     */
    @Override
    public void notify(RepositoryEvent e) throws RegisterException {
        super.notify(e);
        if (e instanceof RepositoryRemoveEvent) {
            RepositoryRemoveEvent event = (RepositoryRemoveEvent) e;
            if (optimizationMethods.contains(event.getRemovedObject())) {
                event.getRemovedObject().removeListener(this);
                this.log.info("Run " + this + ": Removed, because ParameterOptimizationMethod "
                        + event.getRemovedObject() + " has changed.");
                RepositoryRemoveEvent newEvent = new RepositoryRemoveEvent(this);
                this.unregister();
                this.notify(newEvent);
            }
        }
    }

    /**
     * @return A list of parameter lists for every program configuration, that
     *         are to be optimized.
     * @see #optimizationParameters
     */
    public List<List<IProgramParameter<?>>> getOptimizationParameters() {
        return this.optimizationParameters;
    }

    /**
     * @return A list with optimization methods. One method for every program.
     * @see #optimizationMethods
     */
    public List<ParameterOptimizationMethod> getOptimizationMethods() {
        return this.optimizationMethods;
    }

    @Override
    public OptStatus getOptimizationStatus() {
        OptStatus result = new OptStatus();
        try {
            for (IRunRunnable thread : this.runnables) {
                Pair<String, String> configs = Pair.getPair(thread.getProgramConfig().toString(),
                        thread.getDataConfig().toString());

                IRunResult paramOptRes = thread.getOptimizationMethod().getResult();

                boolean isInMemory = paramOptRes.isInMemory();
                if (!isInMemory) {
                    try {
                        paramOptRes.loadIntoMemory();
                        isInMemory = paramOptRes.isInMemory();
                    } catch (RunResultParseException e) {
                        isInMemory = false;
                    }
                }
                try {

                    // measure -> best qualities
                    Map<String, Pair<Map<String, String>, String>> qualities = new HashMap<>();
                    // has the runnable already initialized the optimization
                    // method
                    // and result?
                    if (isInMemory) {
                        // get the best achieved qualities
                        ClusteringQualitySet bestQuals = thread.getOptimizationMethod().getResult()
                                .getOptimalCriterionValue();
                        // get the optimal parameter values
                        Map<ClusteringEvaluation, ParameterSet> bestParams = thread.getOptimizationMethod()
                                .getResult().getOptimalParameterSets();

                        // measure -> best parameters
                        Map<ClusteringEvaluation, Map<String, String>> bestParamsMap = new HashMap<>();
                        for (ClusteringEvaluation measure : bestParams.keySet()) {
                            ParameterSet pSet = bestParams.get(measure);
                            Map<String, String> tmp = new HashMap<>();
                            for (String p : pSet.keySet()) {
                                tmp.put(p.toString(), pSet.get(p));
                            }

                            bestParamsMap.put(measure, tmp);
                        }

                        for (ClusteringEvaluation measure : bestQuals.keySet()) {
                            qualities.put(measure.getName(),
                                    Pair.getPair(bestParamsMap.get(measure), bestQuals.get(measure).toString()));
                        }
                    }

                    result.put(configs, new Pair<>((double) thread.getProgress().getPercent(), qualities));
                } finally {
                    if (!isInMemory) {
                        paramOptRes.unloadFromMemory();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}