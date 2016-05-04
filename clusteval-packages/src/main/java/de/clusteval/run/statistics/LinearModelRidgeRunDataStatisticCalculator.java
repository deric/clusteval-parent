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
package de.clusteval.run.statistics;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.Pair;
import de.clusteval.api.cluster.ClustEvalValue;
import de.clusteval.api.cluster.ClusteringQualitySet;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.RunResultParseException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.IProgram;
import de.clusteval.api.program.ParameterSet;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IRunResult;
import de.clusteval.api.stats.IDataStatistic;
import de.clusteval.api.stats.UnknownDataStatisticException;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.data.statistics.DoubleValueDataStatistic;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.run.InvalidRunModeException;
import de.clusteval.run.RunException;
import de.clusteval.run.result.DataAnalysisRunResult;
import de.clusteval.run.result.ParameterOptimizationResult;
import de.clusteval.utils.FileUtils;
import de.clusteval.utils.InvalidConfigurationFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;
import org.openide.util.Exceptions;

/**
 * @author Christian Wiwie
 *
 */
public class LinearModelRidgeRunDataStatisticCalculator
        extends
        RunDataStatisticRCalculator<LinearModelRidgeRunDataStatistic> {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param runIdentifiers
     * @param dataIdentifiers
     * @throws RegisterException
     */
    public LinearModelRidgeRunDataStatisticCalculator(IRepository repository,
            long changeDate, File absPath, final List<String> runIdentifiers,
            final List<String> dataIdentifiers) throws RegisterException {
        super(repository, changeDate, absPath, runIdentifiers, dataIdentifiers);
    }

    /**
     * The copy constructor for this statistic calculator.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public LinearModelRidgeRunDataStatisticCalculator(
            final LinearModelRidgeRunDataStatisticCalculator other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see run.statistics.RunDataStatisticCalculator#calculateResult()
     */
    @Override
    public LinearModelRidgeRunDataStatistic calculateResultHelper(
            final IRengine rEngine) throws IllegalArgumentException, RegisterException, RunResultParseException, FileNotFoundException {

        /*
         * Get clustering results
         */
        List<ParameterOptimizationResult> runResults = new ArrayList<>();
        for (String runIdentifier : this.uniqueRunIdentifiers) {
            List<ParameterOptimizationResult> results = new ArrayList<>();

            try {
                ParameterOptimizationResult.parseFromRunResultFolder2(
                        this.repository,
                        new File(FileUtils.buildPath(
                                this.repository.getBasePath(IRunResult.class),
                                runIdentifier)), results, false, false, false);
                runResults.addAll(results);
            } catch (IOException | UnknownRunResultFormatException | UnknownDataSetFormatException |
                    UnknownClusteringQualityMeasureException | InvalidRunModeException | UnknownParameterOptimizationMethodException |
                    NoOptimizableProgramParameterException | UnknownProgramParameterException | UnknownGoldStandardFormatException |
                    InvalidConfigurationFileException | RepositoryAlreadyExistsException | InvalidRepositoryException |
                    NoRepositoryFoundException | GoldStandardNotFoundException | InvalidOptimizationParameterException |
                    GoldStandardConfigurationException | DataSetConfigurationException | DataSetNotFoundException |
                    DataSetConfigNotFoundException | GoldStandardConfigNotFoundException | DataConfigurationException |
                    DataConfigNotFoundException | RunException | UnknownDataStatisticException | UnknownProgramTypeException |
                    UnknownRProgramException | IncompatibleParameterOptimizationMethodException | UnknownDistanceMeasureException |
                    UnknownRunStatisticException | RepositoryConfigNotFoundException | RepositoryConfigurationException |
                    ConfigurationException | RegisterException | NumberFormatException | NoDataSetException | UnknownRunDataStatisticException | RunResultParseException | UnknownDataPreprocessorException | IncompatibleDataSetConfigPreprocessorException | IncompatibleContextException | UnknownParameterType | InterruptedException | UnknownRunResultPostprocessorException | UnknownDataRandomizerException e) {
                Exceptions.printStackTrace(e);
            } catch (UnknownProviderException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        try {
            for (ParameterOptimizationResult result : runResults) {
                result.loadIntoMemory();
            }

            /*
             * Get data configs common for all data analysis runs
             */
            final List<DataAnalysisRunResult> dataResults = new ArrayList<>();
            List<IDataConfig> commonDataConfigs = new ArrayList<>();
            for (String dataIdentifier : this.uniqueDataIdentifiers) {
                try {
                    DataAnalysisRunResult dataResult = DataAnalysisRunResult
                            .parseFromRunResultFolder(
                                    this.repository,
                                    new File(FileUtils.buildPath(
                                            this.repository
                                            .getBasePath(IRunResult.class),
                                            dataIdentifier)));
                    if (dataResult != null) {
                        dataResults.add(dataResult);
                        commonDataConfigs.addAll(dataResult.getDataConfigs());
                    }
                } catch (Exception e) {
                }
            }

            try {
                for (DataAnalysisRunResult result : dataResults) {
                    result.loadIntoMemory();
                }

                List<String> commonDataConfigNames = new ArrayList<>();
                for (IDataConfig first : commonDataConfigs) {
                    commonDataConfigNames.add(first.getName());
                }
                commonDataConfigNames = new ArrayList<String>(
                        new LinkedHashSet<String>(commonDataConfigNames));

                /*
                 * Get data statistics calculated for dataconfigs
                 */
                // mapping from dataconfig,dataStatisticName -> data statistic
                final Set<String> dataStatisticNames = new HashSet<String>();

                final Map<String, Map<String, IDataStatistic>> calculatedDataStatistics = new HashMap<>();
                for (DataAnalysisRunResult dataResult : dataResults) {
                    for (IDataConfig dataConfig : dataResult.getDataConfigs()) {
                        final List<IDataStatistic> dataStatistics = dataResult
                                .getDataStatistics(dataConfig);

                        // take only data statistics with a double value
                        for (IDataStatistic ds : dataStatistics) {
                            if (ds instanceof DoubleValueDataStatistic) {
                                dataStatisticNames.add(ds.getClass()
                                        .getSimpleName());
                                // just overwrite old ones, assuming, that they
                                // are
                                // the
                                // same
                                if (!calculatedDataStatistics
                                        .containsKey(dataConfig.getName())) {
                                    calculatedDataStatistics
                                            .put(dataConfig.getName(),
                                                    new HashMap<>());
                                }
                                calculatedDataStatistics.get(
                                        dataConfig.getName()).put(
                                                ds.getClass().getSimpleName(), ds);
                            }
                        }
                    }
                }
                /*
                 * find data statistics common for all data configs in all data
                 * analysis runs
                 */
                final List<String> commonDataStatisticNames = new ArrayList<>(
                        new LinkedHashSet<String>(dataStatisticNames));
                for (String dataConfig : commonDataConfigNames) {
                    commonDataStatisticNames.retainAll(calculatedDataStatistics
                            .get(dataConfig).keySet());
                }

                final int colNum = commonDataStatisticNames.size();

                if (colNum == 0) {
                    return null;
                }

                /*
                 * Build up the input matrix X
                 */
                final int rowNum = commonDataConfigs.size();
                if (rowNum > 0 && colNum > 0) {

                    // fill matrix X
                    double[][] x = new double[rowNum][colNum];

                    for (int row = 0; row < rowNum; row++) {
                        final IDataConfig dc = commonDataConfigs.get(row);

                        for (int col = 0; col < colNum; col++) {
                            final IDataStatistic ds = calculatedDataStatistics
                                    .get(dc.getName()).get(
                                    commonDataStatisticNames.get(col));

                            DoubleValueDataStatistic dds = (DoubleValueDataStatistic) ds;
                            x[row][col] = Double.isNaN(dds.getValue())
                                          ? null
                                          : dds.getValue();

                        }
                    }

                    /*
                     * Build vector y for every program and quality measure.
                     * ProgramFullName x ClusteringQualityMeasureName ->
                     * QualitiesOfProgramOnDataConfigs
                     */
                    final Map<Pair<String, String>, Double[]> yMap = new HashMap<>();

                    // iterate over run results
                    for (ParameterOptimizationResult paramResult : runResults) {
                        final IProgram p = paramResult.getMethod()
                                .getProgramConfig().getProgram();
                        final IDataConfig dc = paramResult.getDataConfig();

                        final String programName = p.getFullName();
                        final String dataConfigName = dc.getName();

                        // get qualities for this run result
                        final Map<ClusteringEvaluation, ParameterSet> optParamSet = paramResult
                                .getOptimalParameterSets();
                        for (ClusteringEvaluation measure : optParamSet
                                .keySet()) {
                            final String measureName = measure.toString();

                            final ClusteringQualitySet qualSet = paramResult
                                    .get(optParamSet.get(measure));
                            double value = qualSet.get(measure).isTerminated()
                                           ? qualSet.get(measure).getValue()
                                           : measure.getMinimum();
                            final Pair<String, String> pair = Pair.getPair(
                                    programName, measureName);
                            if (!(yMap.containsKey(pair))) {
                                yMap.put(pair, new Double[rowNum]);
                            }
                            int ind = commonDataConfigNames
                                    .indexOf(dataConfigName);
                            if (ind != -1) {
                                if (yMap.get(pair)[ind] == null
                                        || measure
                                        .isBetterThan(ClustEvalValue
                                                .getForDouble(value),
                                                ClustEvalValue
                                                .getForDouble(yMap
                                                        .get(pair)[ind]))) {
                                    yMap.get(pair)[ind] = value;
                                }
                            }
                        }
                    }

                    Map<Pair<String, String>, double[]> coeffMap = new HashMap<>();

                    /**
                     * Train models
                     */
                    for (Pair<String, String> pair : yMap.keySet()) {
                        final Double[] y = yMap.get(pair);

                        // take only rows (data configurations), which are not
                        // null
                        // in y
                        int newRowCount = 0;
                        for (Double d : y) {
                            if (d != null) {
                                newRowCount++;
                            }
                        }
                        final double[][] newX = new double[newRowCount][colNum];
                        final double[] newY = new double[newRowCount];

                        int currentPos = 0;
                        for (int i = 0; i < y.length; i++) {
                            if (y[i] != null) {
                                newX[currentPos] = x[i];
                                newY[currentPos] = y[i];
                                currentPos++;
                            }
                        }

                        try {
                            rEngine.assign("x", newX);
                            rEngine.assign("y", newY);
                            rEngine.eval("library(MASS)");
                            rEngine.eval("model <- lm.ridge(y ~ x,lambda=1)");
                            RExpr result = rEngine.eval("coef(model)");
                            double[] coeffs = result.asDoubles();

                            coeffMap.put(pair, coeffs);
                        } catch (RException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return new LinearModelRidgeRunDataStatistic(repository,
                            false, changeDate, absPath,
                            commonDataStatisticNames, coeffMap);
                }

                return null;
            } finally {
                for (DataAnalysisRunResult result : dataResults) {
                    result.unloadFromMemory();
                }
            }
        } finally {
            for (ParameterOptimizationResult result : runResults) {
                result.unloadFromMemory();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see run.statistics.RunStatisticCalculator#getStatistic()
     */
    @Override
    public LinearModelRidgeRunDataStatistic getStatistic() {
        return this.lastResult;
    }

    /*
     * (non-Javadoc)
     *
     * @see utils.StatisticCalculator#writeOutputTo(java.io.File)
     */
    @SuppressWarnings("unused")
    @Override
    public void writeOutputTo(File absFolderPath) {
    }

}
