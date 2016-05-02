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
package de.clusteval.run.result.postprocessing;

import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Christian Wiwie
 *
 */
public class FuzzyCoefficientThresholdRunResultPostprocessor extends
        RunResultPostprocessor {

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @param parameters
     * @throws RegisterException
     */
    public FuzzyCoefficientThresholdRunResultPostprocessor(
            IRepository repository, boolean register, long changeDate,
            File absPath, RunResultPostprocessorParameters parameters)
            throws RegisterException {
        super(repository, register, changeDate, absPath, parameters);
    }

    /**
     * @param other
     * @throws RegisterException
     */
    public FuzzyCoefficientThresholdRunResultPostprocessor(
            FuzzyCoefficientThresholdRunResultPostprocessor other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.run.result.postprocessing.RunResultPostprocessor#postprocess
     * (de.clusteval.cluster.Clustering)
     */
    @Override
    public IClustering postprocess(IClustering clustering) {
        // do fuzzy preprocessing (if parameter is set)
        IClustering clusteringPreprocessed = clustering.clone();

        Set<ClusterItem> itemsToBeRemoved = new HashSet<>();

        // by default we include all fuzzy coefficients;
        double threshold = parameters.containsKey("threshold") ? Double
                .valueOf(parameters.get("threshold")) : 0.0;
        // iterate over elements; only keep those fuzzy coefficients >=
        // threshold and readjust the remaining ones
        for (ClusterItem item : new HashSet<ClusterItem>(
                clusteringPreprocessed.getClusterItems())) {
            Map<Cluster, Float> coeffs = item.getFuzzyClusters();

            // identify clusters with coeff < threshold
            Set<Cluster> toBeRemoved = new HashSet<Cluster>();
            float subtracted = 0.0f;
            for (Map.Entry<Cluster, Float> e : coeffs.entrySet()) {
                if (e.getValue() < threshold) {
                    subtracted += e.getValue();
                    toBeRemoved.add(e.getKey());
                }
            }

            // remove those clusters
            for (Cluster cl : toBeRemoved) {
                clusteringPreprocessed.removeClusterItem(item, cl);
            }

            // read coeffs for remaining clusters
            if (coeffs.isEmpty()) {
                itemsToBeRemoved.add(item);
            } else {
                float toAdd = subtracted / coeffs.size();
                for (Cluster cl : new HashSet<>(coeffs.keySet())) {
                    float newCoeff = coeffs.get(cl) + toAdd;
                    cl.remove(item);
                    cl.add(item, newCoeff);
                }
            }
        }

        return clusteringPreprocessed;
    }

}
