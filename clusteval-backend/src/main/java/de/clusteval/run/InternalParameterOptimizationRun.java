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
package de.clusteval.run;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.IContext;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.ExecutionRun;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IRunResultPostprocessor;
import de.clusteval.api.run.IScheduler;
import de.clusteval.run.runnable.ExecutionRunRunnable;
import de.clusteval.run.runnable.InternalParameterOptimizationRunRunnable;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * A type of execution run that does the same as
 * {@link ParameterOptimizationRun}, by using a programs internal parameter
 * optimization mode instead of doing the parameter optimization itself within
 * the framework.
 *
 * @author Christian Wiwie
 *
 */
public class InternalParameterOptimizationRun extends ExecutionRun {

    /**
     * New objects of this type are automatically registered at the repository.
     *
     * @param repository        the repository
     * @param context
     * @param changeDate        The date this run was performed.
     * @param absPath           The absolute path to the file on the filesystem that
     *                          corresponds to this run.
     * @param programConfigs    The program configurations of the new run.
     * @param dataConfigs       The data configurations of the new run.
     * @param qualityMeasures   The clustering quality measures of the new run.
     * @param parameterValues   The parameter values of this run.
     * @param postProcessors
     * @param maxExecutionTimes
     * @throws RegisterException
     */
    public InternalParameterOptimizationRun(IRepository repository,
            final IContext context, long changeDate, File absPath,
            List<IProgramConfig> programConfigs, List<IDataConfig> dataConfigs,
            List<ClusteringEvaluation> qualityMeasures,
            List<Map<IProgramParameter<?>, String>> parameterValues,
            final List<IRunResultPostprocessor> postProcessors,
            final Map<String, Integer> maxExecutionTimes)
            throws RegisterException {
        super(repository, context, true, changeDate, absPath, programConfigs,
                dataConfigs, qualityMeasures, parameterValues, postProcessors,
                maxExecutionTimes);
    }

    /**
     * Copy constructor of internal parameter optimization runs.
     *
     * @param other The internal parameter optimization run to be cloned.
     * @throws RegisterException
     */
    protected InternalParameterOptimizationRun(final InternalParameterOptimizationRun other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see run.ExecutionRun#createRunRunnableFor(framework.RunScheduler,
     * run.Run, program.ProgramConfig, data.DataConfig, java.lang.String,
     * boolean)
     */
    @Override
    protected ExecutionRunRunnable createRunRunnableFor(
            IScheduler runScheduler, IRun run,
            IProgramConfig programConfig, IDataConfig dataConfig,
            String runIdentString, boolean isResume,
            Map<IProgramParameter<?>, String> runParams) {
        return new InternalParameterOptimizationRunRunnable(runScheduler, run,
                programConfig, dataConfig, runIdentString, isResume, runParams);
    }

    /*
     * (non-Javadoc)
     *
     * @see run.ExecutionRun#clone()
     */
    @Override
    public InternalParameterOptimizationRun clone() {
        try {
            return new InternalParameterOptimizationRun(this);
        } catch (RegisterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
