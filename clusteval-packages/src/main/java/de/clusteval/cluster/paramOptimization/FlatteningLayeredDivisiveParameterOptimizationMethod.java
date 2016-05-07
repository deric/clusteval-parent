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
import de.clusteval.api.Pair;
import de.clusteval.api.cluster.ClusteringQualitySet;
import de.clusteval.api.data.DataSetFormat;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.InternalAttributeException;
import de.clusteval.api.exceptions.RunResultParseException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.NoParameterSetFoundException;
import de.clusteval.api.opt.ParameterOptimizationException;
import de.clusteval.api.opt.ParameterOptimizationMethod;
import de.clusteval.api.opt.ParameterOptimizationRun;
import de.clusteval.api.opt.ParameterSet;
import de.clusteval.api.opt.ParameterSetAlreadyEvaluatedException;
import de.clusteval.api.program.DoubleProgramParameter;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.program.IntegerProgramParameter;
import de.clusteval.api.program.ProgramParameter;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.program.StringProgramParameter;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Wiwie
 *
 */
@LoadableClassParentAnnotation(parent = "DivisiveParameterOptimizationMethod")
public class FlatteningLayeredDivisiveParameterOptimizationMethod
        extends
        ParameterOptimizationMethod {

    protected int remainingIterationCount;

    /**
     * The number of layers.
     */
    protected int layerCount;
    protected int iterationsPerLayer;
    protected int currentLayer;
    protected DivisiveParameterOptimizationMethod currentDivisiveMethod;
    protected List<IProgramParameter<?>> originalParameters;
    // private int totalIterationCount;
    protected Map<String, Pair<?, ?>> paramToValueRange;

    /**
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     * @param run
     * @param programConfig
     * @param dataConfig
     * @param params
     * @param optimizationCriterion
     * @param totalIterations
     * @param isResume
     * @throws RegisterException
     */
    public FlatteningLayeredDivisiveParameterOptimizationMethod(
            final IRepository repo, final boolean register,
            final long changeDate, final File absPath,
            final ParameterOptimizationRun run,
            final IProgramConfig programConfig, final IDataConfig dataConfig,
            List<IProgramParameter<?>> params,
            ClusteringEvaluation optimizationCriterion,
            int totalIterations, final boolean isResume)
            throws RegisterException {
        super(repo, false, changeDate, absPath, run, programConfig, dataConfig,
                params, optimizationCriterion, totalIterations, isResume);
        this.originalParameters = params;
        this.layerCount = (int) Math.sqrt(this.totalIterationCount);
        this.paramToValueRange = new HashMap<String, Pair<?, ?>>();

        if (register) {
            this.register();
        }
    }

    /**
     * The copy constructor for this method.
     *
     * <p>
     * Cloning of this method does not keep potentially already initialized
     * parameter value ranges
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public FlatteningLayeredDivisiveParameterOptimizationMethod(
            final FlatteningLayeredDivisiveParameterOptimizationMethod other)
            throws RegisterException {
        super(other);

        this.originalParameters = ProgramParameter
                .cloneParameterList(other.params);
        // this.totalIterationCount = (int) ArraysExt
        // .product(this.iterationPerParameter);
        // this.layerCount = (int) Math.sqrt(this.iterationPerParameter[0]);
        // this.iterationsPerLayer = this.totalIterationCount / this.layerCount;
        this.layerCount = other.layerCount;
        this.iterationsPerLayer = other.iterationsPerLayer;
        this.paramToValueRange = new HashMap<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cluster.paramOptimization.ParameterOptimizationMethod#getNextParameterSet
     * ()
     */
    @Override
    public synchronized ParameterSet getNextParameterSet(final ParameterSet forcedParameterSet)
            throws InternalAttributeException, RegisterException, NoParameterSetFoundException, InterruptedException,
                   ParameterSetAlreadyEvaluatedException, UnknownProviderException {
        if (this.currentDivisiveMethod == null
                || (!this.currentDivisiveMethod.hasNext() && this.currentLayer < this.layerCount)) {
            boolean allParamSetsFinished = false;
            while (!allParamSetsFinished) {
                allParamSetsFinished = true;
                for (ParameterSet set : this.getResult().getParameterSets()) {
                    if (this.getResult().get(set) == null) {
                        allParamSetsFinished = false;
                        this.wait(1000);
                    }
                }
            }
            this.applyNextDivisiveMethod();
        }
        try {
            ParameterSet result = this.currentDivisiveMethod.next(
                    forcedParameterSet,
                    this.currentDivisiveMethod.getStartedCount() + 1);

            if (this.getResult().getParameterSets().contains(result)) {
                this.currentDivisiveMethod.giveQualityFeedback(result, this
                        .getResult().get(result));
            }
            return result;
        } catch (ParameterSetAlreadyEvaluatedException e) {
            // 09.05.2014: we have to adapt the iteration number of the current
            // divisive method to the iteration number of this layered method
            throw new ParameterSetAlreadyEvaluatedException(
                    ++this.currentCount, this.getResult()
                    .getIterationNumberForParameterSet(
                            e.getParameterSet()), e.getParameterSet());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cluster.paramOptimization.ParameterOptimizationMethod#initParameterValues
     * ()
     */
    @Override
    public void initParameterValues() throws ParameterOptimizationException, InternalAttributeException {
        super.initParameterValues();

        for (IProgramParameter<?> param : params) {
            this.paramToValueRange.put(param.getName(), Pair.getPair(
                    param.evaluateMinValue(dataConfig, programConfig),
                    param.evaluateMaxValue(dataConfig, programConfig)));
        }
    }

    /**
     * @throws InternalAttributeException
     * @throws RegisterException
     * @throws InterruptedException
     *
     */
    protected void applyNextDivisiveMethod() throws InternalAttributeException,
                                                    RegisterException, InterruptedException, UnknownProviderException {
        /*
         * First we take the new optimum of the last divisive layer, if there
         * was one
         */
        if (this.currentDivisiveMethod != null) {
            for (ClusteringEvaluation measure : this.currentDivisiveMethod
                    .getResult().getOptimalCriterionValue().keySet()) {
                if (measure.isBetterThan(this.currentDivisiveMethod.getResult()
                        .getOptimalCriterionValue().get(measure), this
                        .getResult().getOptimalCriterionValue().get(measure))) {
                    this.getResult()
                            .getOptimalCriterionValue()
                            .put(measure,
                                    this.currentDivisiveMethod.getResult()
                                    .getOptimalCriterionValue()
                                    .get(measure));
                    this.getResult()
                            .getOptimalParameterSets()
                            .put(measure,
                                    this.currentDivisiveMethod.getResult()
                                    .getOptimalParameterSets()
                                    .get(measure));
                }
            }
        }
        /*
         * We adapt the ranges of the parameters to control which points the
         * divisive method evaluates
         */
        List<IProgramParameter<?>> newParams = new ArrayList<>();
        for (IProgramParameter<?> p : params) {
            IProgramParameter<?> param = p.clone();
            newParams.add(param);
            // if this is the first layer or the parameter is a string parameter
            // with options, we do not change the value range
            if (this.currentDivisiveMethod != null
                    && !(param instanceof StringProgramParameter && param
                    .isOptionsSet())) {
                /*
                 * In the next layer we half the domains of every parameter
                 * centered around that point with maximal quality
                 */
                double paramOptValue = Double.valueOf(this.getResult()
                        .getOptimalParameterSets()
                        .get(this.optimizationCriterion).get(param.getName()));

                double oldMinValue;
                double oldMaxValue;

                try {
                    if (param instanceof DoubleProgramParameter) {
                        oldMinValue = (Double) (paramToValueRange.get(param
                                .getName()).getFirst());
                    } else {
                        oldMinValue = (Integer) (paramToValueRange.get(param
                                .getName()).getFirst());
                    }
                    if (param instanceof DoubleProgramParameter) {
                        oldMaxValue = (Double) (paramToValueRange.get(param
                                .getName()).getSecond());
                    } else {
                        oldMaxValue = (Integer) (paramToValueRange.get(param
                                .getName()).getSecond());
                    }
                } catch (ClassCastException e) {
                    System.out.println(param);
                    System.out.println(paramToValueRange.get(param.getName()));
                    System.out.println(paramToValueRange.get(param.getName())
                            .getFirst().getClass()
                            + " "
                            + paramToValueRange.get(param.getName())
                            .getSecond().getClass());
                    System.out.println(paramToValueRange.get(param.getName())
                            .getFirst());
                    throw e;
                }

                double oldRange = oldMaxValue - oldMinValue;

                double newMinValue = paramOptValue - oldRange / 4;
                double newMaxValue = paramOptValue + oldRange / 4;

                double origParamMinValue;
                if (param instanceof DoubleProgramParameter) {
                    origParamMinValue = ((DoubleProgramParameter) param)
                            .evaluateMinValue(dataConfig, programConfig);
                } else {
                    origParamMinValue = ((IntegerProgramParameter) param)
                            .evaluateMinValue(dataConfig, programConfig);
                }
                double origParamMaxValue;
                if (param instanceof DoubleProgramParameter) {
                    origParamMaxValue = ((DoubleProgramParameter) param)
                            .evaluateMaxValue(dataConfig, programConfig);
                } else {
                    origParamMaxValue = ((IntegerProgramParameter) param)
                            .evaluateMaxValue(dataConfig, programConfig);
                }

                /*
                 * If we are outside the old minvalue - maxvalue range, we shift
                 * the new range.
                 */
                if (newMinValue < origParamMinValue) {
                    // newMaxValue += (origParamMinValue - newMinValue);
                    newMinValue += (origParamMinValue - newMinValue);
                } else if (newMaxValue > origParamMaxValue) {
                    // newMinValue -= (newMaxValue - origParamMaxValue);
                    newMaxValue -= (newMaxValue - origParamMaxValue);
                }

                if (param.getClass().equals(DoubleProgramParameter.class)) {
                    paramToValueRange.put(param.getName(),
                            Pair.getPair(newMinValue, newMaxValue));

                    DoubleProgramParameter dp = (DoubleProgramParameter) param;
                    dp.setMinValue(String.valueOf(newMinValue));
                    dp.setMaxValue(String.valueOf(newMaxValue));
                    dp.setDefault(String.valueOf(newMinValue));
                } else if (param.getClass().equals(
                        IntegerProgramParameter.class)) {
                    paramToValueRange.put(param.getName(),
                            Pair.getPair((int) newMinValue, (int) newMaxValue));
                    IntegerProgramParameter ip = (IntegerProgramParameter) param;
                    ip.setMinValue(String.valueOf(newMinValue));
                    ip.setMaxValue(String.valueOf(newMaxValue));
                    ip.setDefault(String.valueOf(newMinValue));
                }
            }
            /*
             * If this is the first layer, we just operate on the whole ranges
             * of the parameters ( do not change them here)
             */
        }

        int newIterationsPerParameter = getNextIterationsPerLayer();
        try {
            this.currentDivisiveMethod = createDivisiveMethod(newParams,
                    newIterationsPerParameter);
            this.currentDivisiveMethod.reset(new File(this.getResult().getAbsolutePath()));
        } catch (ParameterOptimizationException | RunResultParseException e) {
            e.printStackTrace();
        }
        this.currentLayer++;
    }

    protected int getNextIterationsPerLayer() {
        int newLayerIterations;

        // 07.06.2014: set the iterations for the next layer to half, but
        // minimally to 1.
        this.iterationsPerLayer = Math.max(this.remainingIterationCount / 2, 1);

        double remainingIterationCount = this.iterationsPerLayer;
        int remainingParams = this.params.size();
        final List<Integer> iterations = new ArrayList<>();

        // parameters that have a fixed number of options
        for (int i = 0; i < params.size(); i++) {
            final IProgramParameter<?> param = this.params.get(i);
            if (param.getOptions() != null && param.getOptions().length > 0) {
                iterations.add(param.getOptions().length);
                remainingIterationCount /= param.getOptions().length;
                remainingParams--;
            }
        }

        // the iterations for the remaining parameters
        newLayerIterations = (int) Math.pow(Math.floor(Math.pow(
                remainingIterationCount, 1.0 / remainingParams)),
                remainingParams);
        for (Integer i : iterations) {
            newLayerIterations *= i;
        }

        if (currentLayer < layerCount - 1) {
            this.remainingIterationCount -= newLayerIterations;
        } else {
            /*
             * If this is the last layer, do the remaining number of iterations
             */
            this.remainingIterationCount = 0;
        }

        return newLayerIterations;
    }

    protected DivisiveParameterOptimizationMethod createDivisiveMethod(
            List<IProgramParameter<?>> newParams, int newIterationsPerParameter)
            throws ParameterOptimizationException, RegisterException {
        return new DivisiveParameterOptimizationMethod(repository, false,
                System.currentTimeMillis(), new File(
                        "DivisiveParameterOptimizationMethod"), run,
                programConfig, dataConfig, newParams, optimizationCriterion,
                newIterationsPerParameter, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see cluster.paramOptimization.ParameterOptimizationMethod#hasNext()
     */
    @Override
    public boolean hasNext() {
        boolean layerHasNext = (this.currentDivisiveMethod != null
                                ? this.currentDivisiveMethod.hasNext()
                                : false);
        if (!layerHasNext) {
            return this.currentLayer < this.layerCount;
        }
        return layerHasNext;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cluster.paramOptimization.ParameterOptimizationMethod#giveQualityFeedback
     * (java.util.Map)
     */
    @Override
    public synchronized void giveQualityFeedback(final ParameterSet paramSet, ClusteringQualitySet qualities) {
        super.giveQualityFeedback(paramSet, qualities);
        this.currentDivisiveMethod.giveQualityFeedback(paramSet, qualities);
        // wake up all threads, which are waiting for the parameter sets of the
        // last divisive method to finish.
        this.notifyAll();
    }

    @Override
    public void reset(final File absResultPath)
            throws ParameterOptimizationException, InternalAttributeException,
                   RegisterException, RunResultParseException, InterruptedException, UnknownProviderException {
        this.currentLayer = 0;
        this.remainingIterationCount = getTotalIterationCount();
        if (this.originalParameters != null) {
            this.params = this.originalParameters;
        }
        this.currentDivisiveMethod = null;
        super.reset(absResultPath);
    }

    @Override
    public int getTotalIterationCount() {
        return this.totalIterationCount;
    }

    @Override
    public List<Class<? extends DataSetFormat>> getCompatibleDataSetFormatBaseClasses() {
        return new ArrayList<Class<? extends DataSetFormat>>();
    }

    @Override
    public List<String> getCompatibleProgramNames() {
        return new ArrayList<String>();
    }
}
