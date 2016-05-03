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

import de.clusteval.api.cluster.ClusteringEvaluationParameters;
import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.cluster.ClustEvalValue;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RLibraryInferior;
import de.clusteval.api.r.ROperationNotSupported;
import de.clusteval.framework.repository.RepositoryObject;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * This type of clustering quality measure uses the R framework to calculate
 * cluster validities.
 *
 * @author Christian Wiwie
 *
 */
public abstract class ClusteringQualityMeasureR extends RepositoryObject implements ClusteringEvaluation, RLibraryInferior {

    protected ClusteringEvaluationParameters parameters;

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
            final ClusteringEvaluationParameters parameters)
            throws RegisterException {
        super(repo, false, changeDate, absPath);

        this.parameters = parameters;

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
    public ClustEvalValue getQualityOfClustering(
            IClustering clustering, IClustering goldStandard,
            IDataConfig dataConfig, final IRengine rEngine)
            throws InvalidDataSetFormatVersionException,
                   IllegalArgumentException, InterruptedException, RException,
                   ROperationNotSupported, RNotAvailableException {
        try {
            try {
                return getQualityOfClusteringHelper(clustering, goldStandard, dataConfig, rEngine);
            } catch (RException e) {
                this.log.warn("R-framework (" + this.getClass().getSimpleName()
                        + "): " + rEngine.getLastError());
                ClustEvalValue min = ClustEvalValue
                        .getForDouble(this.getMinimum());
                ClustEvalValue max = ClustEvalValue
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

    @Override
    public ClustEvalValue getQualityOfClustering(IClustering clustering, IClustering gsClustering, IDataConfig dataConfig)
            throws RNotAvailableException, ROperationNotSupported,
                   InvalidDataSetFormatVersionException, IllegalArgumentException,
                   InterruptedException {
        try {
            IRengine rEngine = repository.getRengineForCurrentThread();
            return getQualityOfClustering(clustering, gsClustering, dataConfig, rEngine);
        } catch (RException ex) {
            throw new RNotAvailableException(ex.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public ClusteringQualityMeasureR clone() {
        try {
            return this.getClass().getConstructor(this.getClass())
                    .newInstance(this);
        } catch (IllegalArgumentException | SecurityException | InstantiationException |
                 IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class "
                + this.getClass().getSimpleName() + " failed");
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * This method compares two values of this clustering quality measure and
     * returns true, if the first one is better than the second one.
     *
     * @param quality1 The first quality value.
     * @param quality2 The second quality value.
     * @return True, if quality1 is better than quality2
     */
    @Override
    public final boolean isBetterThan(ClustEvalValue quality1,
            ClustEvalValue quality2) {
        if (!quality1.isTerminated) {
            return false;
        }
        if (!quality2.isTerminated) {
            return true;
        }
        // 06.05.2014: if this quality is NaN, the new one is always considered
        // better
        if (Double.isNaN(quality2.getValue())) {
            return true;
        }
        // 06.05.2014: if the new quality is NaN, the old one is considered
        // better (if it is not NaN)
        if (Double.isNaN(quality1.getValue())) {
            return false;
        }
        return isBetterThanHelper(quality1, quality2);
    }

}
