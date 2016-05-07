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
package de.clusteval.cluster.paramOptimization;

import de.clusteval.api.opt.LoadableClassParentAnnotation;
import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.cluster.ClusteringQualitySet;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.InternalAttributeException;
import de.clusteval.api.exceptions.RunResultParseException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.IDivergingParameterOptimizationMethod;
import de.clusteval.api.opt.NoParameterSetFoundException;
import de.clusteval.api.opt.ParameterOptimizationException;
import de.clusteval.api.opt.ParameterOptimizationRun;
import de.clusteval.api.opt.ParameterSet;
import de.clusteval.api.opt.ParameterSetAlreadyEvaluatedException;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.program.ProgramParameter;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Wiwie
 *
 */
@LoadableClassParentAnnotation(parent = "DivisiveParameterOptimizationMethod")
public class APDivisiveParameterOptimizationMethod extends DivisiveParameterOptimizationMethod
        implements IDivergingParameterOptimizationMethod {

    protected boolean lastIterationNotTerminated;
    protected int numberTriesOnNotTerminated;
    protected DivisiveParameterOptimizationMethod iterationParamMethod;
    protected List<IProgramParameter<?>> allParams;

    /**
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     * @param run
     *                              The run this method belongs to.
     * @param programConfig
     *                              The program configuration this method was created for.
     * @param dataConfig
     *                              The data configuration this method was created for.
     * @param params
     *                              This list holds the program parameters that are to be
     *                              optimized by the parameter optimization run.
     * @param optimizationCriterion
     *                              The quality measure used as the optimization criterion (see
     *                              {@link #optimizationCriterion}).
     * @param iterationPerParameter
     *                              This array holds the number of iterations that are to be
     *                              performed for each optimization parameter.
     * @param isResume
     *                              This boolean indiciates, whether the run is a resumption of a
     *                              previous run execution or a completely new execution.
     * @throws ParameterOptimizationException
     * @throws RegisterException
     */
    public APDivisiveParameterOptimizationMethod(final IRepository repo,
            final boolean register, final long changeDate, final File absPath,
            final ParameterOptimizationRun run, IProgramConfig programConfig,
            IDataConfig dataConfig, List<IProgramParameter<?>> params,
            ClusteringEvaluation optimizationCriterion,
            int iterationPerParameter, final boolean isResume)
            throws ParameterOptimizationException, RegisterException {
        super(repo, false, changeDate, absPath, run, programConfig, dataConfig,
                getPreferenceParam(params), optimizationCriterion,
                iterationPerParameter,
                // TODO: why?
                // new int[]{iterationPerParameter[0]},
                isResume);
        this.allParams = params;
        this.numberTriesOnNotTerminated = 3; // TODO

        if (register) {
            this.register();
        }
    }

    /**
     * The copy constructor for this method.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public APDivisiveParameterOptimizationMethod(
            final APDivisiveParameterOptimizationMethod other)
            throws RegisterException {
        super(other);

        this.allParams = ProgramParameter.cloneParameterList(other.allParams);
        this.numberTriesOnNotTerminated = other.numberTriesOnNotTerminated;
    }

    /**
     * @param params
     * @return A list containing only the preference parameter.
     */
    public static List<IProgramParameter<?>> getPreferenceParam(List<IProgramParameter<?>> params) {
        /*
         * Only add the preference-parameter to this list
         */
        List<IProgramParameter<?>> result = new ArrayList<>();
        for (IProgramParameter<?> param : params) {
            if (param.getName().equals("preference")) {
                result.add(param);
                break;
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cluster.paramOptimization.LayeredDivisiveParameterOptimizationMethod#
     * hasNext()
     */
    @Override
    public boolean hasNext() {
        boolean hasNext = super.hasNext();
        if (this.lastIterationNotTerminated) {
            if (this.iterationParamMethod.hasNext()) {
                return true;
            }
        }
        return hasNext;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cluster.paramOptimization.LayeredDivisiveParameterOptimizationMethod#
     * getNextParameterSet()
     */
    @Override
    public ParameterSet getNextParameterSet(final ParameterSet forcedParameterSet)
            throws InternalAttributeException, RegisterException,
                   NoParameterSetFoundException, InterruptedException, UnknownProviderException {
        ParameterSet iterationParamSet = null;
        ParameterSet preferenceParamSet = null;

        if (this.lastIterationNotTerminated) {

            /*
             * We ensure that we have a method for the iteration parameters
             */
            if (this.iterationParamMethod == null) {
                /*
                 * If we do not have another iteration parameter, we create our
                 * next preference parameter set.
                 */
                try {
                    List<IProgramParameter<?>> iterationParams = new ArrayList<>(
                            this.allParams);
                    iterationParams.removeAll(this.params);
                    this.iterationParamMethod = new DivisiveParameterOptimizationMethod(
                            repository, false, changeDate, absPath, run,
                            programConfig, dataConfig, iterationParams,
                            optimizationCriterion, (int) Math.pow(
                                    this.numberTriesOnNotTerminated,
                                    iterationParams.size()), isResume);
                } catch (ParameterOptimizationException e) {
                    e.printStackTrace();
                }
            }
            /*
             * If we have another iteration parameter set we just merge it with
             * our current one.
             */
            if (this.iterationParamMethod.hasNext()) {
                try {
                    iterationParamSet = this.iterationParamMethod.next(
                            forcedParameterSet,
                            this.iterationParamMethod.getStartedCount() + 1);
                    preferenceParamSet = this
                            .getResult()
                            .getParameterSets()
                            .get(this.getResult().getParameterSets().size() - 1);

                    ParameterSet newParamSet = new ParameterSet();
                    newParamSet.putAll(preferenceParamSet);
                    newParamSet.putAll(iterationParamSet);
                    return newParamSet;
                } catch (NoParameterSetFoundException e) {
                } catch (ParameterSetAlreadyEvaluatedException e) {
                    // cannot happen
                }
            }
        }

        /*
         * The last iteration terminated or we have no other iteration parameter
         * left.
         */
        try {
            List<IProgramParameter<?>> iterationParams = new ArrayList<>(
                    this.allParams);
            iterationParams.removeAll(this.params);
            this.iterationParamMethod = new DivisiveParameterOptimizationMethod(
                    repository, false, changeDate, absPath, run, programConfig,
                    dataConfig, iterationParams, optimizationCriterion,
                    (int) Math.pow(this.numberTriesOnNotTerminated,
                            iterationParams.size()), isResume);
            this.iterationParamMethod.reset(new File(this.getResult().getAbsolutePath()));
            iterationParamSet = this.iterationParamMethod.next(
                    forcedParameterSet,
                    this.iterationParamMethod.getStartedCount() + 1);
            preferenceParamSet = super.getNextParameterSet(forcedParameterSet);
        } catch (ParameterOptimizationException | RunResultParseException e) {
            e.printStackTrace();
        } catch (ParameterSetAlreadyEvaluatedException e) {
            // cannot happen
        }
        ParameterSet newParamSet = new ParameterSet();
        newParamSet.putAll(iterationParamSet);
        newParamSet.putAll(preferenceParamSet);
        return newParamSet;
    }

    @Override
    public void giveFeedbackNotTerminated(final ParameterSet parameterSet,
            ClusteringQualitySet minimalQualities) {
        super.giveQualityFeedback(parameterSet, minimalQualities);
        this.iterationParamMethod.giveQualityFeedback(parameterSet,
                minimalQualities);
        lastIterationNotTerminated = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cluster.paramOptimization.LayeredDivisiveParameterOptimizationMethod#
     * giveQualityFeedback(cluster.quality.ClusteringQualitySet)
     */
    @Override
    public void giveQualityFeedback(final ParameterSet parameterSet,
            ClusteringQualitySet qualities) {
        super.giveQualityFeedback(parameterSet, qualities);
        this.lastIterationNotTerminated = false;
        this.iterationParamMethod = null;
    }
}
