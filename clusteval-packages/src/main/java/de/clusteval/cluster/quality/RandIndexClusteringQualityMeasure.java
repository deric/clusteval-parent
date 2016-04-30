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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import de.clusteval.api.cluster.ClustEvalValue;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.framework.repository.Repository;

/**
 * @author Christian Wiwie
 *
 */
public class RandIndexClusteringQualityMeasure extends ClusteringQualityMeasure {

    /**
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public RandIndexClusteringQualityMeasure(Repository repo, boolean register,
            long changeDate, File absPath,
            ClusteringQualityMeasureParameters parameters) throws RegisterException {
        super(repo, register, changeDate, absPath, parameters);
    }

    /**
     * The copy constructor for this measure.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public RandIndexClusteringQualityMeasure(
            final RandIndexClusteringQualityMeasure other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see cluster.quality.ClusteringQualityMeasure#getAlias()
     */
    @Override
    public String getAlias() {
        return "Rand Index";
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cluster.quality.ClusteringQualityMeasure#getQualityOfClustering(cluster
     * .Clustering, data.DataConfig)
     */
    @SuppressWarnings("unused")
    @Override
    public ClustEvalValue getQualityOfClustering(
            IClustering clustering, IClustering gsClustering,
            IDataConfig dataConfig) {

        double tp = 0.0;
        double fp = 0.0;
        double tn = 0.0;
        double fn = 0.0;

        /*
         * Ensure, that goldstandard contains only objects, that are also in the
         * dataset. Otherwise precision will be calculated incorrectly, because
         * it directly depends on the number of items in a cluster in the
         * goldstandard.
         */
        Set<ClusterItem> gsClusterItems = new HashSet<>(
                gsClustering.getClusterItems());
        Set<ClusterItem> clusterItems = new HashSet<>(
                clustering.getClusterItems());
        gsClusterItems.removeAll(clusterItems);
        for (ClusterItem onlyInGs : gsClusterItems) {
            gsClustering.removeClusterItem(onlyInGs);
        }

        /*
         * Ensure, that clustering contains only objects, that are also in the
         * goldstandard.
         */
        gsClusterItems = new HashSet<ClusterItem>(
                gsClustering.getClusterItems());
        clusterItems.removeAll(gsClusterItems);
        for (ClusterItem onlyInClustering : clusterItems) {
            clustering.removeClusterItem(onlyInClustering);
        }

        ClusterItem[] items = gsClusterItems.toArray(new ClusterItem[0]);
        /*
         * Iterate over all pairs
         */
        for (int i = 0; i < items.length; i++) {
            ClusterItem first = items[i];
            for (int j = i + 1; j < items.length; j++) {
                ClusterItem second = items[j];

                // TODO: no fuzzy support yet
                boolean gsSame = first
                        .getFuzzyClusters()
                        .keySet()
                        .iterator()
                        .next()
                        .equals(second.getFuzzyClusters().keySet().iterator()
                                .next());
                boolean clusteringSame = clustering
                        .getClusterForItem(first)
                        .keySet()
                        .iterator()
                        .next()
                        .equals(clustering.getClusterForItem(second).keySet()
                                .iterator().next());
                if (gsSame && clusteringSame) {
                    tp++;
                } else if (gsSame && !clusteringSame) {
                    fn++;
                } else if (!gsSame && !clusteringSame) {
                    tn++;
                } else if (!gsSame && clusteringSame) {
                    fp++;
                }
            }
        }

        return ClustEvalValue.getForDouble((tp + tn)
                / (tp + tn + fp + fn));
    }

    /*
     * (non-Javadoc)
     *
     * @see cluster.quality.ClusteringQualityMeasure#getMinimum()
     */
    @Override
    public double getMinimum() {
        return 0.0;
    }

    /*
     * (non-Javadoc)
     *
     * @see cluster.quality.ClusteringQualityMeasure#getMaximum()
     */
    @Override
    public double getMaximum() {
        return 1.0;
    }

    /*
     * (non-Javadoc)
     *
     * @see cluster.quality.ClusteringQualityMeasure#requiresGoldstandard()
     */
    @Override
    public boolean requiresGoldstandard() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cluster.quality.ClusteringQualityMeasure#isBetterThanHelper(cluster.quality
     * .ClustEvalValue,
     * cluster.quality.ClustEvalValue)
     */
    @Override
    public boolean isBetterThanHelper(
            ClustEvalValue quality1,
            ClustEvalValue quality2) {
        return quality1.getValue() > quality2.getValue();
    }

    /* (non-Javadoc)
     * @see de.clusteval.cluster.quality.ClusteringQualityMeasure#supportsFuzzyClusterings()
     */
    @Override
    public boolean supportsFuzzyClusterings() {
        return false;
    }
}
