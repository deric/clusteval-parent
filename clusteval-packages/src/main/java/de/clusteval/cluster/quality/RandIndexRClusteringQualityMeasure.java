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
import de.clusteval.api.cluster.ClusteringEvaluationParameters;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.repository.IRepository;
import de.clusteval.data.dataset.RelativeDataSet;
import de.clusteval.utils.ArraysExt;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"clv"})
public class RandIndexRClusteringQualityMeasure
        extends
        ClusteringQualityMeasureR {

    /**
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public RandIndexRClusteringQualityMeasure(IRepository repo,
            boolean register, long changeDate, File absPath,
            ClusteringEvaluationParameters parameters)
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
    public RandIndexRClusteringQualityMeasure(
            final RandIndexRClusteringQualityMeasure other)
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
        return "Rand Index (R)";
    }

    @Override
    public ClustEvalValue getQualityOfClusteringHelper(
            IClustering clustering, IClustering gsClustering,
            IDataConfig dataConfig, final IRengine rEngine)
            throws InterruptedException, RException {

        RelativeDataSet dataSet = (RelativeDataSet) (dataConfig
                .getDatasetConfig().getDataSet().getInStandardFormat());

        SimilarityMatrix simMatrix = dataSet.getDataSetContent();

        /*
         * Create an array with all the cluster ids for every item
         */
        Map<Cluster, Integer> clusterToId = new HashMap<>();
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

        /*
         * Create an array with all the goldstandard ids for every item
         */
        Map<Cluster, Integer> gsClusterToId = new HashMap<>();
        for (Cluster cl : gsClustering.getClusters()) {
            gsClusterToId.put(cl, gsClusterToId.size() + 1);
        }

        int[] gsClusterIds = new int[simMatrix.getIds().size()];
        keyToId = simMatrix.getIds();
        itemIter = gsClustering.getClusterItems().iterator();
        while (itemIter.hasNext()) {
            ClusterItem item = itemIter.next();
            /*
             * TODO: Take the first one, does not work for fuzzy clusters
             */
            gsClusterIds[keyToId.get(item.getId())] = gsClusterToId.get(item
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
        rEngine.assign("goldstandardIds", gsClusterIds);
        rEngine.eval("names(goldstandardIds) <- as.character(1:"
                + similarities.length + ")");

        rEngine.assign("sim", similarities);
        rEngine.eval("rownames(sim) <- as.character(1:" + similarities.length
                + ")");
        rEngine.eval("colnames(sim) <- as.character(1:" + similarities.length
                + ")");

        rEngine.eval("library(clv)");
        rEngine.eval("stdext <- std.ext(clusterIds, goldstandardIds)");
        rEngine.eval("rand <- clv.Rand(stdext)");
        RExpr exp = rEngine.eval("rand");
        if (exp != null) {
            result = exp.asDouble();
        } else {
            result = this.getMinimum();
        }

        return ClustEvalValue.getForDouble(result);
    }

    @Override
    public double getMinimum() {
        return 0.0;
    }

    @Override
    public double getMaximum() {
        return 1.0;
    }

    @Override
    public boolean requiresGoldstandard() {
        return true;
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
