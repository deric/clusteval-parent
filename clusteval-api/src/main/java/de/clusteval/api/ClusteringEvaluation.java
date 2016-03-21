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
package de.clusteval.api;

import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.cluster.quality.ClusteringQualityMeasureValue;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.r.RCalculationException;
import de.clusteval.api.repository.IRepositoryObject;
import java.io.IOException;

/**
 *
 * @author deric
 */
public interface ClusteringEvaluation extends IRepositoryObject {

    /**
     * This method has to be implemented in subclasses to indiciate, whether a
     * quality measure supports validating fuzzy clusterings.
     *
     * @return True, if this measure supports fuzzy clusterings, false
     *         otherwise.
     */
    boolean supportsFuzzyClusterings();

    ClusteringEvaluation clone();

    boolean isBetterThan(ClusteringQualityMeasureValue quality1,
            ClusteringQualityMeasureValue quality2);

    boolean isBetterThanHelper(ClusteringQualityMeasureValue quality1,
            ClusteringQualityMeasureValue quality2);

    /**
     * @return The minimal value of the range of possible values of this quality
     *         measure.
     */
    double getMinimum();

    /**
     * @return The maximal value of the range of possible values of this quality
     *         measure.
     */
    double getMaximum();

    /**
     * Override this method to indicate, whether the quality measure of your
     * subclass needs a goldstandard to be able to be computed.
     *
     * @return True, if this clustering quality measure requires a goldstandard
     *         to be able to assess the quality of a clustering.
     */
    boolean requiresGoldstandard();

    /**
     * This alias is used whenever this clustering quality measure is visually
     * represented and a readable name is needed.
     *
     * @return The alias of this clustering quality measure.
     */
    String getAlias();

    /**
     * Gets the quality of clustering.
     *
     * @param clustering   the clustering
     * @param goldStandard The expected goldstandard.
     * @param dataConfig   the data config
     * @return the quality of clustering
     * @throws UnknownGoldStandardFormatException   the unknown gold standard
     *                                              format exception
     * @throws UnknownDataSetFormatException
     * @throws InvalidDataSetFormatVersionException
     * @throws IOException
     * @throws RNotAvailableException
     * @throws RCalculationException
     * @throws InterruptedException
     */
    ClusteringQualityMeasureValue getQualityOfClustering(
            IClustering clustering, IClustering goldStandard,
            IDataConfig dataConfig) throws UnknownGoldStandardFormatException,
                                           UnknownDataSetFormatException, IOException,
                                           InvalidDataSetFormatVersionException, RNotAvailableException,
                                           RCalculationException, InterruptedException;

}
