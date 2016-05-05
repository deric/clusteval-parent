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
import de.clusteval.api.cluster.ClusteringQualityMeasureR;
import de.clusteval.api.Matrix;
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
import de.clusteval.api.data.RelativeDataSet;
import de.clusteval.utils.ArraysExt;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Christian Wiwie
 */
@RLibraryRequirement(requiredRLibraries = {"cluster"})
@ServiceProvider(service = ClusteringEvaluation.class)
public class SilhouetteValueRClusteringQualityMeasure extends ClusteringQualityMeasureR {

    public SilhouetteValueRClusteringQualityMeasure() {
        super();
    }

    /**
     * The copy constructor for this measure.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public SilhouetteValueRClusteringQualityMeasure(
            final SilhouetteValueRClusteringQualityMeasure other)
            throws RegisterException {
        super(other);
    }

    @Override
    public String getName() {
        return "Silhouette Value (R)";
    }

    @Override
    public ClustEvalValue getQualityOfClusteringHelper(
            final IClustering clustering, IClustering gsClustering,
            final IDataConfig dataConfig, final IRengine rEngine)
            throws IllegalArgumentException,
                   InterruptedException,
                   RException {

        if (clustering.getClusters().size() < 2) {
            return ClustEvalValue.getForDouble(-1.0);
        }

        RelativeDataSet dataSet = (RelativeDataSet) (dataConfig
                .getDatasetConfig().getDataSet().getInStandardFormat());

        Matrix simMatrix = dataSet.getDataSetContent();

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

        rEngine.eval("library(cluster)");
        rEngine.eval("sil <- silhouette(x=clusterIds,dmatrix=sim)");
        RExpr exp = rEngine.eval("summary(sil)$avg.width");
        if (exp != null) {
            result = exp.asDouble();
        } else {
            result = this.getMinimum();
        }

        return ClustEvalValue.getForDouble(result);
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
