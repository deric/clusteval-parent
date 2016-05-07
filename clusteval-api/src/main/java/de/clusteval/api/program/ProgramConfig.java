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
package de.clusteval.api.program;

import de.clusteval.api.data.DataSetFormat;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryEvent;
import de.clusteval.api.repository.RepositoryObject;
import de.clusteval.api.repository.RepositoryRemoveEvent;
import de.clusteval.api.repository.RepositoryReplaceEvent;
import de.clusteval.api.run.IRunResultFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A program configuration encapsulates a program together with options and
 * settings.
 *
 * <p>
 * A program configuration corresponds to and is parsed from a file on the
 * filesystem in the corresponding folder of the repository (see
 * {@link IRepository#programConfigBasePath} and {@link ProgramConfigFinder}).
 *
 * <p>
 * There are several options, that can be specified in the program configuration
 * file (see {@link #parseFromFile(File)}).
 *
 *
 * @author Christian Wiwie
 *
 */
public class ProgramConfig extends RepositoryObject implements IProgramConfig {

    /**
     * A helper method for cloning a list of program configurations.
     *
     * @param programConfigs The list of program configurations to clone.
     * @return The list containing the cloned program configurations of the
     *         input list.
     */
    public static List<IProgramConfig> cloneProgramConfigurations(
            final List<IProgramConfig> programConfigs) {
        List<IProgramConfig> result = new ArrayList<>();

        for (IProgramConfig programConfig : programConfigs) {
            result.add(programConfig.clone());
        }

        return result;
    }

    /**
     * The program this configuration belongs to.
     */
    protected IProgram program;

    /**
     * This is the default invocation line used to invoke the program, when this
     * program configuration is used together with some data configuration.
     *
     * <p>
     * This invocation line is used, if
     * <ul>
     * <li>there is a goldstandard in the data configuration</li>
     * <li>the run is not of type parameter optimization</li>
     * </ul>
     */
    protected String invocationFormat;

    /**
     * This invocation line is used, if
     * <ul>
     * <li>there is no goldstandard in the data configuration</li>
     * <li>and the run is not of type parameter optimization</li>
     * </ul>
     */
    protected String invocationFormatWithoutGoldStandard;

    /**
     * This invocation line is used, if
     * <ul>
     * <li>there is a goldstandard in the data configuration</li>
     * <li>and the run is of type parameter optimization</li>
     * </ul>
     */
    protected String invocationFormatParameterOptimization;

    /**
     * This invocation line is used, if
     * <ul>
     * <li>there is no goldstandard in the data configuration</li>
     * <li>and the run is of type parameter optimization</li>
     * </ul>
     */
    protected String invocationFormatParameterOptimizationWithoutGoldStandard;

    /**
     * This list holds all dataset formats that are compatible with the
     * encapsulated program, i.e. input formats this program is able to read.
     */
    protected Set<IDataSetFormat> compatibleDataSetFormats;

    /**
     * The output format of the program
     */
    protected IRunResultFormat outputFormat;

    /**
     * A list holding all parameters of the program.
     */
    protected List<IProgramParameter<?>> params;

    /**
     * A list holding all optimizable parameter of the program. Optimizable
     * parameters are those parameters, that can in principle be optimized in
     * parameter optimization runs (see {@link ParameterOptimizationRun}).
     */
    protected List<IProgramParameter<?>> optimizableParameters;

    /**
     * This boolean indicates, whether the encapsulated program requires a
     * normalized dataset, i.e. similarities between 0 and 1. This is then
     * handled before the data is passed to the clustering method.
     */
    protected boolean expectsNormalizedDataSet;

    /**
     * The maximal time this program config should be executed. The execution is
     * terminated when this time is reached.
     */
    protected int maxExecutionTimeMinutes;

    /**
     * Instantiates a new program config.
     *
     * @param repository                                               The repository this program configuration should be
     *                                                                 registered at.
     * @param register                                                 A boolean indicating whether to register this program
     *                                                                 configuration at the repository.
     * @param changeDate                                               The change date of this program configuration is used
     *                                                                 for equality checks.
     * @param absPath                                                  The absolute path of this program configuration.
     * @param program                                                  The program this program configuration belongs to.
     * @param outputFormat                                             The output format of the program.
     * @param compatibleDataSetFormats                                 A list of compatible dataset formats of
     *                                                                 the encapsulated program.
     * @param invocationFormat                                         The invocation line for runs with goldstandard
     *                                                                 and without parameter optimization
     * @param invocationFormatWithoutGoldStandard                      The invocation line for runs
     *                                                                 without goldstandard and without parameter optimization
     * @param invocationFormatParameterOptimization                    The invocation line for runs
     *                                                                 with goldstandard and with parameter optimization
     * @param invocationFormatParameterOptimizationWithoutGoldStandard The
     *                                                                 invocation line for runs without goldstandard and with parameter
     *                                                                 optimization
     * @param params                                                   The parameters of the program.
     * @param optimizableParameters                                    The parameters of the program, that can be
     *                                                                 optimized.
     * @param expectsNormalizedDataSet                                 Whether the encapsulated program requires
     *                                                                 normalized input.
     * @param maxExecutionTimeMinutes
     * @throws RegisterException
     */
    public ProgramConfig(
            final IRepository repository,
            final boolean register,
            final long changeDate,
            final File absPath,
            final IProgram program,
            final IRunResultFormat outputFormat,
            final Set<IDataSetFormat> compatibleDataSetFormats,
            final String invocationFormat,
            final String invocationFormatWithoutGoldStandard,
            final String invocationFormatParameterOptimization,
            final String invocationFormatParameterOptimizationWithoutGoldStandard,
            final List<IProgramParameter<?>> params,
            final List<IProgramParameter<?>> optimizableParameters,
            final boolean expectsNormalizedDataSet,
            final int maxExecutionTimeMinutes) throws RegisterException {
        super(repository, false, changeDate, absPath);

        this.program = program;
        this.outputFormat = outputFormat;
        this.compatibleDataSetFormats = compatibleDataSetFormats;

        this.invocationFormat = invocationFormat;
        this.invocationFormatWithoutGoldStandard = invocationFormatWithoutGoldStandard;
        this.invocationFormatParameterOptimization = invocationFormatParameterOptimization;
        this.invocationFormatParameterOptimizationWithoutGoldStandard = invocationFormatParameterOptimizationWithoutGoldStandard;

        this.params = params;
        this.optimizableParameters = optimizableParameters;

        this.expectsNormalizedDataSet = expectsNormalizedDataSet;
        this.maxExecutionTimeMinutes = maxExecutionTimeMinutes;

        if (register && this.register()) {
            this.program.register();
            this.program.addListener(this);

            for (IDataSetFormat dsFormat : this.compatibleDataSetFormats) {
                dsFormat.register();
                dsFormat.addListener(this);
            }

            outputFormat.register();
            outputFormat.addListener(this);
        }
    }

    /**
     * The copy constructor of program configurations.
     *
     * @param programConfig The program configuration to be cloned.
     * @throws RegisterException
     */
    public ProgramConfig(IProgramConfig programConfig) throws RegisterException {
        super(programConfig);

        this.program = programConfig.getProgram().duplicate();
        this.outputFormat = programConfig.getOutputFormat().clone();
        this.compatibleDataSetFormats = DataSetFormat
                .cloneDataSetFormats(programConfig.getCompatibleDataSetFormats());

        this.invocationFormat = programConfig.getInvocationFormat(false);
        this.invocationFormatWithoutGoldStandard = programConfig.getInvocationFormat(true);
        this.invocationFormatParameterOptimization = programConfig.getInvocationFormatParameterOptimization(false);
        this.invocationFormatParameterOptimizationWithoutGoldStandard = programConfig.getInvocationFormatParameterOptimization(true);

        this.params = ProgramParameter.cloneParameterList(programConfig.getParams());
        this.optimizableParameters = ProgramParameter
                .cloneParameterList(programConfig.getOptimizableParams());

        this.expectsNormalizedDataSet = programConfig.expectsNormalizedDataSet();
        this.maxExecutionTimeMinutes = programConfig.getMaxExecutionTimeMinutes();
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.RepositoryObject#clone()
     */
    @Override
    public ProgramConfig clone() {
        try {
            return new ProgramConfig(this);
        } catch (RegisterException e) {
            // should not occur
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return True, if the encapsulated program requires normalized input data,
     *         false otherwise.
     * @see #expectsNormalizedDataSet
     */
    @Override
    public boolean expectsNormalizedDataSet() {
        return this.expectsNormalizedDataSet;
    }

    @Override
    public int getMaxExecutionTimeMinutes() {
        return this.maxExecutionTimeMinutes;
    }

    @Override
    public void setMaxExecutionTimeMinutes(final int maxExecutionTimeMinutes) {
        this.maxExecutionTimeMinutes = maxExecutionTimeMinutes;
    }

    /**
     * This method returns the invocation line format for non
     * parameter-optimization runs.
     *
     * @param withoutGoldStandard This boolean indicates, whether this method
     *                            returns the invocation format for the case with- or without goldstandard.
     *
     * @return The invocation line format
     */
    public String getInvocationFormat(boolean withoutGoldStandard) {
        if (withoutGoldStandard) {
            return invocationFormatWithoutGoldStandard;
        }
        return invocationFormat;
    }

    /**
     * Internal Parameter Optimization is an alternative for parameter
     * optimization, in that the program handles the parameter optimization
     * itself. In this case, the framework invokes the program only once.
     *
     * @return True, if the encapsulated program supports internal parameter
     *         optimization, false otherwise.
     */
    public boolean supportsInternalParameterOptimization() {
        return invocationFormatParameterOptimization != null;
    }

    /**
     * This method returns the invocation line format for parameter-optimization
     * runs.
     *
     * @param withoutGoldStandard This boolean indicates, whether this method
     *                            returns the invocation format for the case with- or without goldstandard.
     *
     * @return The invocation line format
     */
    public String getInvocationFormatParameterOptimization(boolean withoutGoldStandard) {
        if (withoutGoldStandard) {
            return invocationFormatParameterOptimizationWithoutGoldStandard;
        }
        return invocationFormatParameterOptimization;
    }

    /**
     *
     * @return The list of parameters of the encapsulated program.
     * @see #params
     */
    @Override
    public List<IProgramParameter<?>> getParams() {
        return params;
    }

    /**
     *
     * @return The list of optimizable parameters of the encapsulated program.
     * @see #optimizableParameters
     */
    @Override
    public List<IProgramParameter<?>> getOptimizableParams() {
        return optimizableParameters;
    }

    /**
     * This method returns the program parameter with the given id and throws an
     * exception, of none such parameter exists.
     *
     * @param id The name the parameter should have.
     * @throws UnknownProgramParameterException
     * @return The program parameter with the appropriate name
     */
    public IProgramParameter<?> getParamWithId(final String id)
            throws UnknownProgramParameterException {
        for (IProgramParameter<?> param : this.params) {
            if (param.getName().equals(id)) {
                return param;
            }
        }
        throw new UnknownProgramParameterException(
                "The program parameter with id \"" + id + "\" is unknown.");
    }

    /**
     *
     * @return The encapsulated program.
     * @see #program
     */
    @Override
    public IProgram getProgram() {
        return program;
    }

    /**
     *
     * @return The compatible dataset input formats of the encapsulated program.
     * @see #compatibleDataSetFormats
     */
    @Override
    public Set<IDataSetFormat> getCompatibleDataSetFormats() {
        return compatibleDataSetFormats;
    }

    /**
     *
     * @return The output format of the encapsulated program.
     * @see #outputFormat
     */
    @Override
    public IRunResultFormat getOutputFormat() {
        return this.outputFormat;
    }

    /**
     * @return The name of the program configuration is the name of the file
     *         without extension.
     */
    @Override
    public String getName() {
        return this.absPath.getName().replace(".config", "");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getName();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.RepositoryObject#notify(utils.RepositoryEvent)
     */
    @Override
    public void notify(RepositoryEvent e) throws RegisterException {
        if (e instanceof RepositoryReplaceEvent) {
            RepositoryReplaceEvent event = (RepositoryReplaceEvent) e;
            if (event.getOld().equals(this)) {
                super.notify(event);

                for (IProgramParameter<?> param : params) {
                    param.unregister();
                }
            } else if (event.getOld().equals(program)) {
                event.getOld().removeListener(this);
                this.log.info("ProgramConfig "
                        + this
                        + ": Program reloaded due to modifications in filesystem");
                event.getReplacement().addListener(this);
                // added 06.07.2012
                this.program = (Program) event.getReplacement();
            }
        } else if (e instanceof RepositoryRemoveEvent) {
            RepositoryRemoveEvent event = (RepositoryRemoveEvent) e;
            if (event.getRemovedObject().equals(this)) {
                super.notify(event);

                for (IProgramParameter<?> param : params) {
                    param.unregister();
                }
            } else if (event.getRemovedObject().equals(program)) {
                event.getRemovedObject().removeListener(this);
                this.log.info("ProgramConfig " + this
                        + ": Removed, because Program " + program
                        + " was removed.");
                RepositoryRemoveEvent newEvent = new RepositoryRemoveEvent(
                        this);
                this.unregister();
                this.notify(newEvent);
            } // a dataset format class changed
            else if (this.compatibleDataSetFormats.contains(event
                    .getRemovedObject())) {
                event.getRemovedObject().removeListener(this);
                this.log.info("ProgramConfig " + this
                        + ": Removed, because DataSetFormat "
                        + event.getRemovedObject() + " has changed.");
                RepositoryRemoveEvent newEvent = new RepositoryRemoveEvent(
                        this);
                this.unregister();
                this.notify(newEvent);
            } // the runresult format class changed
            else if (this.outputFormat.equals(event.getRemovedObject())) {
                event.getRemovedObject().removeListener(this);
                this.log.info("ProgramConfig " + this
                        + ": Removed, because RunResultFormat "
                        + event.getRemovedObject() + " has changed.");
                RepositoryRemoveEvent newEvent = new RepositoryRemoveEvent(
                        this);
                this.unregister();
                this.notify(newEvent);
            }
        }
    }

    /**
     * TODO: merge this and {@link #getParamWithId(String)}
     *
     * @param name the name
     * @return the parameter for name
     */
    @Override
    public IProgramParameter<?> getParameterForName(final String name) {
        IProgramParameter<?> pa = null;
        for (int i = 0; i < this.getParams().size(); i++) {
            pa = this.getParams().get(i);
            if (pa.toString().equals(name) || pa.getName().equals(name)) {
                return pa;
            }
        }
        return null;
    }
}