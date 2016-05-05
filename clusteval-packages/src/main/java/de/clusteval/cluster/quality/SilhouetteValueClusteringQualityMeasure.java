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
package de.clusteval.cluster.quality;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.cluster.ClusteringQualityMeasure;
import de.clusteval.api.Matrix;
import de.clusteval.api.cluster.ClustEvalValue;
import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.RelativeDataSet;
import de.clusteval.api.program.RegisterException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Christian Wiwie
 */
@ServiceProvider(service = ClusteringEvaluation.class)
public class SilhouetteValueClusteringQualityMeasure extends ClusteringQualityMeasure {

    public SilhouetteValueClusteringQualityMeasure() {
        super();
    }

    /**
     * The copy constructor for this measure.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public SilhouetteValueClusteringQualityMeasure(
            final SilhouetteValueClusteringQualityMeasure other)
            throws RegisterException {
        super(other);
    }

    @Override
    public String getName() {
        return "Silhouette Value (Java)";
    }

    @Override
    public ClustEvalValue getQualityOfClustering(final IClustering clustering, IClustering gsClustering,
            final IDataConfig dataConfig) throws IllegalArgumentException {

        if (clustering.getClusters().size() < 2) {
            return ClustEvalValue.getForDouble(-1.0);
        }

        RelativeDataSet dataSet = (RelativeDataSet) (dataConfig
                .getDatasetConfig().getDataSet().getInStandardFormat());

        Matrix simMatrix = dataSet.getDataSetContent();

        double result = 0.0;

        for (ClusterItem item : clustering.getClusterItems()) {
            result += getQualityOfDatum(simMatrix, clustering, item);
        }
        result /= clustering.getClusterItems().size();

        return ClustEvalValue.getForDouble(result);
    }

    private double getQualityOfDatum(final Matrix simMatrix,
            final IClustering clustering, final ClusterItem item) {
        Set<Cluster> ownClusters = item.getFuzzyClusters().keySet();
        Set<Cluster> otherClusters = new HashSet<>(clustering.getClusters());
        otherClusters.removeAll(ownClusters);

        double a;
        if (ownClusters.iterator().next().size() > 1) {
            a = getAverageDissimilarityToFuzzyClusters(simMatrix, item,
                    ownClusters);
        } else {
            return 0.0;
        }
        double b = Double.MAX_VALUE;
        for (Cluster other : otherClusters) {
            b = Math.min(
                    b,
                    getAverageDissimilarityToFuzzyCluster(simMatrix, item,
                            other));
        }

        /*
         * Avoid division by zero in extreme cases.
         */
        return (a == b && a == 0 ? 0 : (b - a) / Math.max(a, b));
    }

    private double getAverageDissimilarityToFuzzyClusters(
            final Matrix simMatrix, final ClusterItem item,
            final Set<Cluster> clusters) {
        double result = 0.0;

        for (Cluster cl : clusters) {
            result += getAverageDissimilarityToFuzzyCluster(simMatrix, item, cl);
        }

        return result;
    }

    private double getAverageDissimilarityToFuzzyCluster(
            final Matrix simMatrix, final ClusterItem item,
            final Cluster fuzzyCluster) {
        double result = 0.0;

        String itemId = item.getId();
        for (Map.Entry<ClusterItem, Float> other : fuzzyCluster.getFuzzyItems()
                .entrySet()) {
            ClusterItem otherItem = other.getKey();
            if (otherItem.equals(item)) {
                continue;
            }
            result += (simMatrix.getMaxValue()
                    - simMatrix.getSimilarity(itemId, otherItem.getId())) * other.getValue();
        }
        // TODO
        if (fuzzyCluster.contains(item)) {
            result /= fuzzyCluster.fuzzySize() - 1;
            // result *= fuzzyCluster.getFuzzyItems().get(item);
        } else {
            result /= fuzzyCluster.fuzzySize();
        }

        return result;
    }

    @Override
    public double getMinimum() {
        return -1.0;
    }

    @Override
    public double getMaximum() {
        return 1.0;
    }

    @Override
    public boolean requiresGoldstandard() {
        return false;
    }

    @Override
    public boolean isBetterThanHelper(
            ClustEvalValue quality1,
            ClustEvalValue quality2) {
        return quality1.getValue() > quality2.getValue();
    }

    @Override
    public boolean supportsFuzzyClusterings() {
        return false;
    }
}
