/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.api.opt;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.cluster.ClusteringQualitySet;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.run.IRunResult;
import java.util.List;
import java.util.Map;

/**
 *
 * @author deric
 */
public interface IParamOptResult extends IRunResult {

    /**
     * @return A list with all evaluated parameter sets of this optimization
     *         process.
     */
    List<ParameterSet> getParameterSets();

    /**
     * @return A list with optimization methods. One method for every program.
     * @see #optimizationMethods
     */
    List<ParameterOptimizationMethod> getOptimizationMethods();

    /**
     * @return The data configuration this method was created for.
     */
    IDataConfig getDataConfig();

    /**
     * @return The parameter set which lead to the highest clustering quality of
     *         the optimization criterion.
     */
    ParameterSet getOptimalParameterSet();

    /**
     * @return A map with the optimal parameter sets for every clustering
     *         quality measure.
     */
    Map<ClusteringEvaluation, ParameterSet> getOptimalParameterSets();

    /**
     * @param paramSet
     *                 The parameter set for which we want the resulting clustering
     *                 quality set.
     * @return The clustering quality set resulting from the given parameter
     *         set.
     */
    ClusteringQualitySet get(final ParameterSet paramSet);

    /**
     * @return The optimal quality value achieved for the optimization
     *         criterion.
     */
    ClusteringQualitySet getOptimalCriterionValue();

    long getIterationNumberForParameterSet(final ParameterSet parameterSet);

    /**
     * @return A list with all evaluated parameter sets of this optimization
     *         process.
     */
    List<Long> getIterationNumbers();

    /**
     * This method adds the given qualities for the given parameter set and
     * resulting clustering.
     *
     * @param iterationNumber
     *                        The number of the iteration.
     *
     * @param last
     *                        The parameter set for which we want to add clustering
     *                        qualities.
     * @param qualities
     *                        The qualities which we want to add for the parameter set.
     * @param clustering
     *                        The clustering resulting the given parameter set.
     * @return The old value, if this operation replaced an old mapping,
     */
    ClusteringQualitySet put(long iterationNumber, ParameterSet last, ClusteringQualitySet qualities,
            IClustering clustering);

    /**
     *
     * @param paramSet
     *                 The parameter set for which we want to know the resulting
     *                 clustering.
     * @return The clustering resulting from the given parameter set.
     */
    IClustering getClustering(final ParameterSet paramSet);

    /**
     * @return The parameter optimization method which created this result.
     */
    ParameterOptimizationMethod getMethod();

    void setRun(ParameterOptimizationRun run);

    public void setMethod(ParameterOptimizationMethod aThis);
}
