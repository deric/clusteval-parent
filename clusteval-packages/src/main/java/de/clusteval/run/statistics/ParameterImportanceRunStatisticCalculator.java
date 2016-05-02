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
import de.clusteval.api.program.ParameterSet;
import de.clusteval.api.program.RegisterException;
import de.clusteval.data.statistics.RunStatisticCalculateException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.run.result.ParameterOptimizationResult;
import de.clusteval.run.result.RunResult;
import de.clusteval.utils.ArraysExt;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Wiwie
 *
 */
public class ParameterImportanceRunStatisticCalculator
        extends
        RunStatisticCalculator<ParameterImportanceRunStatistic> {

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param uniqueRunIdentifier
     * @throws RegisterException
     */
    public ParameterImportanceRunStatisticCalculator(Repository repository,
            long changeDate, File absPath, final String uniqueRunIdentifier)
            throws RegisterException {
        super(repository, changeDate, absPath, uniqueRunIdentifier);
    }

    /**
     * The copy constructor for this statistic calculator.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public ParameterImportanceRunStatisticCalculator(
            final ParameterImportanceRunStatisticCalculator other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see run.statistics.RunStatisticCalculator#calculateResult()
     */
    @Override
    protected ParameterImportanceRunStatistic calculateResult()
            throws RunStatisticCalculateException {

        try {

            List<ParameterOptimizationResult> results = new ArrayList<>();

            ParameterOptimizationResult.parseFromRunResultFolder2(
                    this.repository,
                    new File(FileUtils.buildPath(
                            this.repository.getBasePath(RunResult.class),
                            this.uniqueRunIdentifiers)), results, false, false,
                    false);

            Map<Pair<String, String>, Map<String, Double>> paramImportances = new HashMap<>();

            for (ParameterOptimizationResult result : results) {
                try {
                    result.loadIntoMemory();

                    String dataConfig = result.getDataConfig().getName();
                    String programConfig = result.getProgramConfig().getName();

                    // paramName x paramValue -> validityIndex x ( ...,
                    // qualities,
                    // ...)
                    Map<Pair<String, String>, Map<ClusteringEvaluation, List<ClustEvalValue>>> paramQualities = new HashMap<>();

                    for (Pair<ParameterSet, ClusteringQualitySet> qualities : result) {
                        for (String paramName : qualities.getFirst().keySet()) {
                            Pair<String, String> p = Pair.getPair(programConfig
                                    + ":" + paramName, qualities.getFirst()
                                    .get(paramName));

                            Map<ClusteringEvaluation, List<ClustEvalValue>> quals;
                            if (paramQualities.containsKey(p)) {
                                quals = paramQualities.get(p);
                            } else {
                                quals = new HashMap<ClusteringEvaluation, List<ClustEvalValue>>();
                                paramQualities.put(p, quals);
                            }
                            for (ClusteringEvaluation m : qualities
                                    .getSecond().keySet()) {
                                if (!quals.containsKey(m)) {
                                    quals.put(m,
                                            new ArrayList<ClustEvalValue>());
                                }
                                quals.get(m).add(qualities.getSecond().get(m));
                            }
                        }
                    }

                    Map<Pair<String, String>, Map<String, Double>> paramQualitiesMean = new HashMap<>();

                    // average over qualities for a certain parameter value
                    for (Pair<String, String> p : paramQualities.keySet()) {
                        Map<ClusteringEvaluation, List<ClustEvalValue>> quals = paramQualities
                                .get(p);

                        for (ClusteringEvaluation measure : quals.keySet()) {
                            List<Double> q = new ArrayList<>();
                            for (int i = 0; i < quals.get(measure).size(); i++) {
                                if (quals.get(measure).get(i).isTerminated()) {
                                    q.add(quals.get(measure).get(i).getValue());
                                }
                            }
                            double mean = ArraysExt.mean(ArraysExt
                                    .toPrimitive(q.toArray(new Double[0])));

                            if (!paramQualitiesMean.containsKey(p)) {
                                paramQualitiesMean.put(p,
                                        new HashMap<String, Double>());
                            }
                            // TODO
                            assert !paramQualitiesMean.get(p).containsKey(
                                    measure.toString()) : "Measure war schon enthalten";
                            if (!Double.isNaN(mean)) {
                                paramQualitiesMean.get(p).put(
                                        measure.toString(), mean);
                            }
                        }
                    }

                    // merge for different param values into one list
                    Map<String, Map<String, List<Double>>> paramQualitiesLists = new HashMap<>();
                    for (Pair<String, String> p : paramQualitiesMean.keySet()) {
                        String paramName = p.getFirst();
                        if (!paramQualitiesLists.containsKey(paramName)) {
                            paramQualitiesLists.put(paramName,
                                    new HashMap<String, List<Double>>());
                        }
                        Map<String, List<Double>> map = paramQualitiesLists
                                .get(paramName);

                        for (String measure : paramQualitiesMean.get(p)
                                .keySet()) {
                            if (!map.containsKey(measure)) {
                                map.put(measure, new ArrayList<Double>());
                            }
                            map.get(measure).add(
                                    paramQualitiesMean.get(p).get(measure));
                        }
                    }

                    // calculate variances
                    Map<String, Map<String, Double>> paramQualitiesVariances = new HashMap<>();

                    for (String param : paramQualitiesLists.keySet()) {
                        if (!paramQualitiesVariances.containsKey(param)) {
                            paramQualitiesVariances.put(param,
                                    new HashMap<>());
                        }
                        Map<String, Double> map = paramQualitiesVariances
                                .get(param);
                        // for every measure
                        for (String measure : paramQualitiesLists.get(param)
                                .keySet()) {
                            List<Double> quals = paramQualitiesLists.get(param)
                                    .get(measure);
                            double var = ArraysExt.variance(ArraysExt
                                    .toPrimitive(quals.toArray(new Double[0])));
                            map.put(measure, var);
                        }
                    }

                    for (String paramName : paramQualitiesVariances.keySet()) {
                        Pair<String, String> p = Pair.getPair(paramName,
                                dataConfig);
                        if (!paramImportances.containsKey(p)) {
                            paramImportances.put(p,
                                    new HashMap<String, Double>());
                        }
                        for (String measure : paramQualitiesVariances.get(
                                paramName).keySet()) {
                            assert !paramImportances.get(p)
                                    .containsKey(measure);
                            paramImportances.get(p).put(
                                    measure,
                                    paramQualitiesVariances.get(paramName).get(
                                    measure));
                        }
                    }
                } finally {
                    result.unloadFromMemory();
                }
            }

            return new ParameterImportanceRunStatistic(repository, false,
                    changeDate, absPath, paramImportances);
        } catch (Exception e) {
            throw new RunStatisticCalculateException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see run.statistics.RunStatisticCalculator#getStatistic()
     */
    @Override
    public ParameterImportanceRunStatistic getStatistic() {
        return this.lastResult;
    }

    /*
     * (non-Javadoc)
     *
     * @see utils.StatisticCalculator#writeOutputTo(java.io.File)
     */
    @Override
    public void writeOutputTo(File absFolderPath) {
        // TODO needed ?
    }
}
