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

import de.clusteval.api.cluster.ClustEvalValue;
import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Christian Wiwie
 */
public class TransClustF2ClusteringQualityMeasure
        extends
        ClusteringQualityMeasure {

    /**
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public TransClustF2ClusteringQualityMeasure(IRepository repo,
            boolean register, long changeDate, File absPath,
            ClusteringQualityMeasureParameters parameters)
            throws RegisterException {
        super(repo, register, changeDate, absPath, parameters);
    }

    /**
     * The copy constructor for this measure.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public TransClustF2ClusteringQualityMeasure(
            final TransClustF2ClusteringQualityMeasure other)
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
        return "F2-Score";
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * cluster.quality.ClusteringQualityMeasure#getQualityOfClustering(cluster
     * .Clustering, data.goldstandard.GoldStandard)
     */
    @SuppressWarnings("unused")
    @Override
    public ClustEvalValue getQualityOfClustering(
            final IClustering clustering, IClustering gsClustering,
            final IDataConfig dataConfig) {

        double fmeasure = 0;

        Set<ClusterItem> gsClusterItems = new HashSet<>(gsClustering.getClusterItems());
        Set<ClusterItem> clusterItems = new HashSet<>(clustering.getClusterItems());
        gsClusterItems.removeAll(clusterItems);
        for (ClusterItem onlyInGs : gsClusterItems) {
            gsClustering.removeClusterItem(onlyInGs);
        }

        /*
         * Ensure, that clustering contains only objects, that are also in the
         * goldstandard.
         */
        gsClusterItems = new HashSet<>(
                gsClustering.getClusterItems());
        clusterItems.removeAll(gsClusterItems);
        for (ClusterItem onlyInClustering : clusterItems) {
            clustering.removeClusterItem(onlyInClustering);
        }

        final float proteins = (float) clustering.fuzzySize();

        for (Cluster gsCluster : gsClustering) {
            final float proteinsInReference = gsCluster.fuzzySize();
            // final double maxValue = findMax(clustering, gsCluster);
            final double maxValue = findMax2(clustering, gsCluster);
            fmeasure += (maxValue * proteinsInReference);
        }
        fmeasure /= proteins;

        return ClustEvalValue.getForDouble(fmeasure);
    }

    /**
     * Find max.
     *
     * @param proteinsInReference
     *                            the proteins in reference
     * @param clustering
     *                            the clustering
     * @param gsCluster
     *                            the gs cluster
     * @return the double
     */
    private static double findMax2(final IClustering clustering,
            final Cluster gsCluster) {
        double max = 0;
        double maxCommon = 0;

        for (Cluster cluster : clustering) {
            double common = 0;

            // performance reasons
            if (gsCluster.size() < cluster.size()) {
                common = calculateCommonProteins(gsCluster, cluster);
            } else {
                common = calculateCommonProteins(cluster, gsCluster);
            }

            final double tp = common;
            final double fp = cluster.fuzzySize() - common;
            final double fn = gsCluster.fuzzySize() - common;
            final double precision = (tp / (tp + fp));
            final double recall = (tp / (tp + fn));
            final double fmeasure = 5 * (recall * precision)
                    / (recall + 4 * precision);

            if (fmeasure > max) {
                max = fmeasure;
                maxCommon = common;
            }
        }
        if (maxCommon == 0 && gsCluster.size() == 1) {
            return 1;
        }
        return max;
    }

    /**
     * Calculate common proteins.
     *
     * @param c1
     *           the c1
     * @param c2
     *           the c2
     * @return the float
     */
    private static float calculateCommonProteins(final Cluster c1,
            final Cluster c2) {
        float common = 0;

        Map<ClusterItem, Float> items = c1.getFuzzyItems();

        for (ClusterItem item : items.keySet()) {
            if (c2.contains(item)) {
                common += Math.min(items.get(item), c2.getFuzzyItems()
                        .get(item));
            }
        }
        return common;
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
