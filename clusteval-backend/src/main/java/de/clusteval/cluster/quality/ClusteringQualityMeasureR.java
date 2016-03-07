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
import java.io.IOException;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;

import de.clusteval.cluster.Clustering;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.format.InvalidDataSetFormatVersionException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.goldstandard.format.UnknownGoldStandardFormatException;
import de.clusteval.framework.repository.MyRengine;
import de.clusteval.framework.repository.RException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.utils.RNotAvailableException;

/**
 * This type of clustering quality measure uses the R framework to calculate
 * cluster validities.
 * 
 * @author Christian Wiwie
 * 
 */
public abstract class ClusteringQualityMeasureR
		extends
			ClusteringQualityMeasure {

	/**
	 * Instantiates a new R clustering quality measure.
	 * 
	 * @param repo
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @param parameters
	 * @throws RegisterException
	 */
	public ClusteringQualityMeasureR(final Repository repo,
			final boolean register, final long changeDate, final File absPath,
			final ClusteringQualityMeasureParameters parameters)
			throws RegisterException {
		super(repo, false, changeDate, absPath, parameters);

		if (register)
			this.register();
	}

	/**
	 * The copy constructor of R clustering quality measures.
	 * 
	 * @param other
	 *            The quality measure to clone.
	 * @throws RegisterException
	 */
	public ClusteringQualityMeasureR(final ClusteringQualityMeasureR other)
			throws RegisterException {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.cluster.quality.ClusteringQualityMeasure#getQualityOfClustering
	 * (de.clusteval.cluster.Clustering, de.clusteval.cluster.Clustering,
	 * de.clusteval.data.DataConfig)
	 */
	@Override
	public final ClusteringQualityMeasureValue getQualityOfClustering(
			Clustering clustering, Clustering goldStandard,
			DataConfig dataConfig) throws UnknownGoldStandardFormatException,
			UnknownDataSetFormatException, IOException,
			InvalidDataSetFormatVersionException, RNotAvailableException,
			InterruptedException {
		try {
			MyRengine rEngine = repository.getRengineForCurrentThread();
			try {
				try {
					return getQualityOfClusteringHelper(clustering,
							goldStandard, dataConfig, rEngine);
				} catch (REXPMismatchException e) {
					// handle this type of exception as an REngineException
					throw new RException(rEngine, e.getMessage());
				}
			} catch (REngineException e) {
				this.log.warn("R-framework (" + this.getClass().getSimpleName()
						+ "): " + rEngine.getLastError());
				ClusteringQualityMeasureValue min = ClusteringQualityMeasureValue
						.getForDouble(this.getMinimum());
				ClusteringQualityMeasureValue max = ClusteringQualityMeasureValue
						.getForDouble(this.getMaximum());
				if (this.isBetterThan(max, min))
					return min;
				return max;
			} finally {
				rEngine.clear();
			}
		} catch (RserveException e) {
			throw new RNotAvailableException(e.getMessage());
		}
	}

	protected abstract ClusteringQualityMeasureValue getQualityOfClusteringHelper(
			Clustering clustering, Clustering goldStandard,
			DataConfig dataConfig, final MyRengine rEngine)
			throws UnknownGoldStandardFormatException,
			UnknownDataSetFormatException, IOException,
			InvalidDataSetFormatVersionException, REngineException,
			IllegalArgumentException, REXPMismatchException,
			InterruptedException;
}
