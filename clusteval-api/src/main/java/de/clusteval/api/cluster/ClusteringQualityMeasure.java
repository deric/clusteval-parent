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
package de.clusteval.api.cluster;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.r.ROperationNotSupported;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryObject;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * A clustering quality measure is used to assess the quality of a
 * {@link Clustering} by invoking
 * {@link #getQualityOfClustering(Clustering, Clustering, IDataConfig)}.
 *
 * It has a range of possible values between {@link #getMinimum()} and
 * {@link #getMaximum()}.
 *
 * Some measures can only be assessed if a goldstandard is available (see
 * {@link #requiresGoldstandard()}).
 *
 * Furthermore, some measures are better when maximized and some when minimized
 * (see {@link #isBetterThan} and {@link #isBetterThanHelper} ).
 * <p>
 *
 *
 * @author Christian Wiwie
 */
public abstract class ClusteringQualityMeasure extends RepositoryObject implements ClusteringEvaluation {

    protected ClusteringEvaluationParameters parameters;

    public ClusteringQualityMeasure() {
        super();
    }

    /**
     * The copy constructor of clustering quality measures.
     *
     * @param other The quality measure to clone.
     * @throws RegisterException
     */
    public ClusteringQualityMeasure(final ClusteringQualityMeasure other)
            throws RegisterException {
        super(other);
        this.parameters = other.parameters.clone();
    }

    public void init(final IRepository repo,
            final long changeDate, final File absPath,
            final ClusteringEvaluationParameters parameters)
            throws RegisterException {
        super.init(repo, changeDate, absPath);
        this.parameters = parameters;

    }

    /**
     * This is a helper method for cloning a list of clustering quality
     * measures.
     *
     * @param qualityMeasures The quality measures to be cloned.
     * @return A list containing cloned objects of the given quality measures.
     */
    public static List<ClusteringEvaluation> cloneQualityMeasures(
            final List<ClusteringEvaluation> qualityMeasures) {
        List<ClusteringEvaluation> result = new ArrayList<>();

        for (ClusteringEvaluation qualityMeasure : qualityMeasures) {
            result.add(qualityMeasure.clone());
        }

        return result;
    }

    @Override
    public ClustEvalValue getQualityOfClusteringHelper(
            IClustering clustering, IClustering goldStandard, IDataConfig dataConfig, IRengine rEngine)
            throws InvalidDataSetFormatVersionException, IllegalArgumentException,
                   InterruptedException, RException, ROperationNotSupported, RNotAvailableException {
        return getQualityOfClustering(clustering, clustering, dataConfig);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // changed 05.12.2013
        return this.getClass().getSimpleName()
                .equals(obj.getClass().getSimpleName());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public ClusteringQualityMeasure clone() {
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

    @Override
    public void setParams(ClusteringEvaluationParameters params) {
        this.parameters = params;
    }

}
