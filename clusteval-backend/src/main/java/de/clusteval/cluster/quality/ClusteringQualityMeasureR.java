/**
 * *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 *****************************************************************************
 */
package de.clusteval.cluster.quality;

import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.cluster.quality.ClusteringQualityMeasureValue;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import java.io.File;
import java.io.IOException;

/**
 * This type of clustering quality measure uses the R framework to calculate
 * cluster validities.
 *
 * @author Christian Wiwie
 *
 */
public abstract class ClusteringQualityMeasureR extends ClusteringQualityMeasure {

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
    public ClusteringQualityMeasureR(final IRepository repo,
            final boolean register, final long changeDate, final File absPath,
            final ClusteringQualityMeasureParameters parameters)
            throws RegisterException {
        super(repo, false, changeDate, absPath, parameters);

        if (register) {
            this.register();
        }
    }

    /**
     * The copy constructor of R clustering quality measures.
     *
     * @param other The quality measure to clone.
     * @throws RegisterException
     */
    public ClusteringQualityMeasureR(final ClusteringQualityMeasureR other) throws RegisterException {
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
            IClustering clustering, IClustering goldStandard,
            IDataConfig dataConfig) throws UnknownGoldStandardFormatException,
                                           UnknownDataSetFormatException, IOException,
                                           InvalidDataSetFormatVersionException, RNotAvailableException,
                                           InterruptedException {
        try {
            IRengine rEngine = repository.getRengineForCurrentThread();
            try {
                return getQualityOfClusteringHelper(clustering, goldStandard, dataConfig, rEngine);
            } catch (RException e) {
                this.log.warn("R-framework (" + this.getClass().getSimpleName()
                        + "): " + rEngine.getLastError());
                ClusteringQualityMeasureValue min = ClusteringQualityMeasureValue
                        .getForDouble(this.getMinimum());
                ClusteringQualityMeasureValue max = ClusteringQualityMeasureValue
                        .getForDouble(this.getMaximum());
                if (this.isBetterThan(max, min)) {
                    return min;
                }
                return max;
            } catch (IllegalArgumentException ex) {
                log.error(ex.getMessage(), ex);
            } finally {
                rEngine.clear();
            }
        } catch (RException e) {
            throw new RNotAvailableException(e.getMessage());
        }
        return null;
    }
}
