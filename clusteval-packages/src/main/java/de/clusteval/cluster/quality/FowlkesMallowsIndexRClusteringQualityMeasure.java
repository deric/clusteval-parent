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

import de.clusteval.api.cluster.ClusteringEvaluationParameters;
import de.clusteval.api.cluster.ClustEvalValue;
import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RExpr;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"clv"})
public class FowlkesMallowsIndexRClusteringQualityMeasure extends ClusteringQualityMeasureR {

    /**
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public FowlkesMallowsIndexRClusteringQualityMeasure(IRepository repo,
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
    public FowlkesMallowsIndexRClusteringQualityMeasure(
            final FowlkesMallowsIndexRClusteringQualityMeasure other)
            throws RegisterException {
        super(other);
    }

    @Override
    public String getAlias() {
        return "Fowlkes Mallows Index (R)";
    }

    @Override
    public ClustEvalValue getQualityOfClusteringHelper(
            IClustering clustering, IClustering gsClustering,
            IDataConfig dataConfig, final IRengine rEngine)
            throws InterruptedException, RException {

        /*
         * Create an array with all the cluster ids for every cluster
         */
        Map<Cluster, Integer> clusterToId = new HashMap<>();
        for (Cluster cl : clustering.getClusters()) {
            clusterToId.put(cl, clusterToId.size() + 1);
        }

        /*
         * Create an array with all the goldstandard ids for every cluster
         */
        Map<Cluster, Integer> gsClusterToId = new HashMap<>();
        for (Cluster cl : gsClustering.getClusters()) {
            gsClusterToId.put(cl, gsClusterToId.size() + 1);
        }

        Set<ClusterItem> items = clustering.getClusterItems();

        int[] clusterIds = new int[items.size()];
        int[] gsClusterIds = new int[items.size()];
        Iterator<ClusterItem> itemIter = items.iterator();
        for (int i = 0; i < items.size(); i++) {
            ClusterItem item = itemIter.next();

            /*
             * TODO: Take the first one, does not work for fuzzy clusters
             */
            Cluster cluster = item.getFuzzyClusters().keySet().iterator()
                    .next();
            Cluster clazz = gsClustering
                    .getClusterForItem(
                            gsClustering.getClusterItemWithId(item.getId()))
                    .keySet().iterator().next();
            clusterIds[i] = clusterToId.get(cluster);
            gsClusterIds[i] = gsClusterToId.get(clazz);
        }

        /*
         * Pass the arrays to R
         */
        double result;
        rEngine.assign("clusterIds", clusterIds);
        rEngine.eval("names(clusterIds) <- as.character(1:" + clusterIds.length
                + ")");
        rEngine.assign("goldstandardIds", gsClusterIds);
        rEngine.eval("names(goldstandardIds) <- as.character(1:"
                + gsClusterIds.length + ")");

        rEngine.eval("library(clv)");
        rEngine.eval("stdext <- std.ext(clusterIds, goldstandardIds)");
        rEngine.eval("folkes <- clv.Folkes.Mallows(stdext)");
        RExpr exp = rEngine.eval("folkes");
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
