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
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.clusteval.utils.ArraysExt;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.data.dataset.RelativeDataSet;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.framework.repository.Repository;

/**
 * @author Christian Wiwie
 */
@RLibraryRequirement(requiredRLibraries = {"clv"})
public class DunnIndexRClusteringQualityMeasure extends ClusteringQualityMeasureR {

    /**
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public DunnIndexRClusteringQualityMeasure(Repository repo,
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
    public DunnIndexRClusteringQualityMeasure(
            final DunnIndexRClusteringQualityMeasure other)
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
        return "Dunn Index (R)";
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
    public ClustEvalValue getQualityOfClusteringHelper(
            final IClustering clustering, IClustering gsClustering,
            final IDataConfig dataConfig, final IRengine rEngine)
            throws IllegalArgumentException, RException, InterruptedException {

        RelativeDataSet dataSet = (RelativeDataSet) (dataConfig
                .getDatasetConfig().getDataSet().getInStandardFormat());

        SimilarityMatrix simMatrix = dataSet.getDataSetContent();

        /*
         * Create an array with all the cluster ids for every item
         */
        Map<Cluster, Integer> clusterToId = new HashMap<Cluster, Integer>();
        for (Cluster cl : clustering.getClusters()) {
            clusterToId.put(cl, clusterToId.size() + 1);
        }

        int[] clusterIds = new int[simMatrix.getIds().size()];
        Map<String, Integer> keyToId = simMatrix.getIds();
        Iterator<ClusterItem> itemIter = clustering.getClusterItems()
                .iterator();
        while (itemIter.hasNext()) {
            ClusterItem item = itemIter.next();
            /*
             * TODO: Take the first one, does not work for fuzzy clusters
             */
            clusterIds[keyToId.get(item.getId())] = clusterToId.get(item
                    .getFuzzyClusters().keySet().iterator().next());
        }
        double[][] similarities = simMatrix.toArray();
        /*
         * Convert to dissimilarities
         */
        similarities = ArraysExt.subtract(simMatrix.getMaxValue(),
                similarities, true);

        /*
         * Pass the arrays to R
         */
        double result;
        rEngine.assign("clusterIds", clusterIds);
        rEngine.eval("names(clusterIds) <- as.character(1:"
                + similarities.length + ")");

        rEngine.assign("sim", similarities);
        rEngine.eval("rownames(sim) <- as.character(1:" + similarities.length
                + ")");
        rEngine.eval("colnames(sim) <- as.character(1:" + similarities.length
                + ")");

        rEngine.eval("library(clv)");
        rEngine.eval("diss <- cls.scatt.diss.mx(sim, clusterIds)");
        rEngine.eval("dunn <- clv.Dunn(diss,'average','average')");
        RExpr exp = rEngine.eval("dunn");
        if (exp != null) {
            result = exp.asDouble();
        } else {
            result = this.getMinimum();
        }

        return ClustEvalValue.getForDouble(result);
    }

    /*
     * (non-Javadoc)
     *
     * @see cluster.quality.ClusteringQualityMeasure#getMinimum()
     */
    @Override
    public double getMinimum() {
        return Double.NEGATIVE_INFINITY;
    }

    /*
     * (non-Javadoc)
     *
     * @see cluster.quality.ClusteringQualityMeasure#getMaximum()
     */
    @Override
    public double getMaximum() {
        return Double.POSITIVE_INFINITY;
    }

    /*
     * (non-Javadoc)
     *
     * @see cluster.quality.ClusteringQualityMeasure#requiresGoldstandard()
     */
    @Override
    public boolean requiresGoldstandard() {
        return false;
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

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.cluster.quality.ClusteringQualityMeasure#
     * supportsFuzzyClusterings()
     */
    @Override
    public boolean supportsFuzzyClusterings() {
        return false;
    }
}
