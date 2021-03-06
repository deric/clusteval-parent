/*******************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.clusteval.cluster.quality;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;

import de.clusteval.cluster.Cluster;
import de.clusteval.cluster.ClusterItem;
import de.clusteval.cluster.Clustering;
import de.clusteval.data.DataConfig;
import de.clusteval.framework.RLibraryRequirement;
import de.clusteval.framework.repository.MyRengine;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;

/**
 * @author Christian Wiwie
 * 
 */
@RLibraryRequirement(requiredRLibraries = {"clv"})
public class JaccardIndexRClusteringQualityMeasure
		extends
			ClusteringQualityMeasureR {

	/**
	 * @param repo
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 */
	public JaccardIndexRClusteringQualityMeasure(Repository repo,
			boolean register, long changeDate, File absPath,
			ClusteringQualityMeasureParameters parameters)
			throws RegisterException {
		super(repo, register, changeDate, absPath, parameters);
	}

	/**
	 * The copy constructor for this measure.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public JaccardIndexRClusteringQualityMeasure(
			final JaccardIndexRClusteringQualityMeasure other)
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
		return "Jaccard Index (R)";
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
	public ClusteringQualityMeasureValue getQualityOfClusteringHelper(
			Clustering clustering, Clustering gsClustering,
			DataConfig dataConfig, final MyRengine rEngine)
			throws REXPMismatchException, REngineException,
			InterruptedException {

		/*
		 * Create an array with all the cluster ids for every cluster
		 */
		Map<Cluster, Integer> clusterToId = new HashMap<Cluster, Integer>();
		for (Cluster cl : clustering.getClusters())
			clusterToId.put(cl, clusterToId.size() + 1);

		/*
		 * Create an array with all the goldstandard ids for every cluster
		 */
		Map<Cluster, Integer> gsClusterToId = new HashMap<Cluster, Integer>();
		for (Cluster cl : gsClustering.getClusters())
			gsClusterToId.put(cl, gsClusterToId.size() + 1);

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
		rEngine.eval("jaccard <- clv.Jaccard(stdext)");
		REXP exp = rEngine.eval("jaccard");
		if (exp != null)
			result = exp.asDouble();
		else
			result = this.getMinimum();

		return ClusteringQualityMeasureValue.getForDouble(result);
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
	 * .ClusteringQualityMeasureValue,
	 * cluster.quality.ClusteringQualityMeasureValue)
	 */
	@Override
	protected boolean isBetterThanHelper(
			ClusteringQualityMeasureValue quality1,
			ClusteringQualityMeasureValue quality2) {
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
