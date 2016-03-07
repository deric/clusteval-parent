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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import de.clusteval.cluster.Clustering;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.format.InvalidDataSetFormatVersionException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.goldstandard.format.UnknownGoldStandardFormatException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryObject;
import de.clusteval.program.r.RLibraryInferior;
import de.clusteval.utils.RCalculationException;
import de.clusteval.utils.RNotAvailableException;

/**
 * A clustering quality measure is used to assess the quality of a
 * {@link Clustering} by invoking
 * {@link #getQualityOfClustering(Clustering, Clustering, DataConfig)}.
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
 * {@code
 * 
 * A clustering quality measure MyClusteringQualityMeasure can be added to ClustEval by
 * 
 * 1. extending the class :java:ref:`ClusteringQualityMeasure` with your own class MyClusteringQualityMeasure. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your clustering quality measure.
 * 
 *   * public :java:ref:`ClusteringQualityMeasure(Repository, boolean, long, File, ClusteringQualityMeasureParameters)` : The constructor for your distance measure. This constructor has to be implemented and public.
 *   * public :java:ref:`ClusteringQualityMeasure(MyClusteringQualityMeasure)` : The copy constructor for your distance measure. This constructor has to be implemented and public.
 *   * public :java:ref:`getAlias()` : This method returns a readable alias for this clustering quality measure which is used e.g. on the website.
 *   * public :java:ref:`getMinimum()` : Returns the minimal value this measure can calculate.
 *   * public :java:ref:`getMaximum()` : Returns the maximal value this measure can calculate.
 *   * public :java:ref:`requiresGoldStandard()` : Indicates, whether this clustering quality measure requires a goldstandard to assess the quality of a given clustering.
 *   * public :java:ref:`getQualityOfClustering(Clustering)` : This method is the core of your clustering quality measure. It assesses and returns the quality of the given clustering.
 *   * public :java:ref:`isBetterThanHelper(ClusteringQualityMeasureValue)` : This method is used by sorting algorithms of the framework to compare clustering quality measure results and find the optimal parameter sets.
 * 
 * 2. Creating a jar file named MyClusteringQualityMeasure.jar containing the MyClusteringQualityMeasure class compiled on your machine in the correct folder structure corresponding to the packages:
 * 
 *   * de/clusteval/cluster/quality/MyClusteringQualityMeasure.class
 * 
 * 3. Putting the MyClusteringQualityMeasure.jar into the clustering quality measure folder of the repository:
 * 
 *   * <REPOSITORY ROOT>/supp/clustering/qualityMeasures
 *   * The backend server will recognize and try to load the new clustering quality measure automatically the next time, the ClusteringQualityMeasureFinderThread checks the filesystem.
 *
 * }
 * 
 * @author Christian Wiwie
 */
public abstract class ClusteringQualityMeasure extends RepositoryObject
		implements
			RLibraryInferior {

	protected ClusteringQualityMeasureParameters parameters;

	/**
	 * 
	 * @param repo 
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @param parameters
	 * @throws RegisterException
	 */
	public ClusteringQualityMeasure(final Repository repo,
			final boolean register, final long changeDate, final File absPath,
			final ClusteringQualityMeasureParameters parameters)
			throws RegisterException {
		super(repo, false, changeDate, absPath);

		this.parameters = parameters;

		if (register)
			this.register();
	}

	/**
	 * The copy constructor of clustering quality measures.
	 * 
	 * @param other
	 *            The quality measure to clone.
	 * @throws RegisterException
	 */
	public ClusteringQualityMeasure(final ClusteringQualityMeasure other)
			throws RegisterException {
		super(other);
		this.parameters = other.parameters.clone();
	}

	/**
	 * Gets the quality of clustering.
	 * 
	 * @param clustering
	 *            the clustering
	 * @param goldStandard
	 *            The expected goldstandard.
	 * @param dataConfig
	 *            the data config
	 * @return the quality of clustering
	 * @throws UnknownGoldStandardFormatException
	 *             the unknown gold standard format exception
	 * @throws UnknownDataSetFormatException
	 * @throws InvalidDataSetFormatVersionException
	 * @throws IOException
	 * @throws RNotAvailableException
	 * @throws RCalculationException
	 * @throws InterruptedException
	 */
	public abstract ClusteringQualityMeasureValue getQualityOfClustering(
			Clustering clustering, Clustering goldStandard,
			DataConfig dataConfig) throws UnknownGoldStandardFormatException,
			UnknownDataSetFormatException, IOException,
			InvalidDataSetFormatVersionException, RNotAvailableException,
			RCalculationException, InterruptedException;

	/**
	 * This method has to be implemented in subclasses to indiciate, whether a
	 * quality measure supports validating fuzzy clusterings.
	 * 
	 * @return True, if this measure supports fuzzy clusterings, false
	 *         otherwise.
	 */
	public abstract boolean supportsFuzzyClusterings();

	/**
	 * This is a helper method for cloning a list of clustering quality
	 * measures.
	 * 
	 * @param qualityMeasures
	 *            The quality measures to be cloned.
	 * @return A list containining cloned objects of the given quality measures.
	 */
	public static List<ClusteringQualityMeasure> cloneQualityMeasures(
			final List<ClusteringQualityMeasure> qualityMeasures) {
		List<ClusteringQualityMeasure> result = new ArrayList<ClusteringQualityMeasure>();

		for (ClusteringQualityMeasure qualityMeasure : qualityMeasures)
			result.add(qualityMeasure.clone());

		return result;
	}

	/**
	 * Parses the from string.
	 * 
	 * @param repository
	 *            the repository
	 * @param qualityMeasure
	 *            the quality measure
	 * @param parameters
	 * @return the clustering quality measure
	 * @throws UnknownClusteringQualityMeasureException
	 *             the unknown clustering quality measure exception
	 */
	public static ClusteringQualityMeasure parseFromString(
			final Repository repository, String qualityMeasure,
			ClusteringQualityMeasureParameters parameters)
			throws UnknownClusteringQualityMeasureException {

		Class<? extends ClusteringQualityMeasure> c = repository
				.getRegisteredClass(ClusteringQualityMeasure.class,
						"de.clusteval.cluster.quality." + qualityMeasure);
		try {
			ClusteringQualityMeasure measure = c.getConstructor(
					Repository.class, boolean.class, long.class, File.class,
					ClusteringQualityMeasureParameters.class).newInstance(
					repository, false, System.currentTimeMillis(),
					new File(qualityMeasure), parameters);

			return measure;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {

		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		throw new UnknownClusteringQualityMeasureException("\""
				+ qualityMeasure
				+ "\" is not a known clustering quality measure.");
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
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
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
	 * @param quality1
	 *            The first quality value.
	 * @param quality2
	 *            The second quality value.
	 * @return True, if quality1 is better than quality2
	 */
	public final boolean isBetterThan(ClusteringQualityMeasureValue quality1,
			ClusteringQualityMeasureValue quality2) {
		if (!quality1.isTerminated)
			return false;
		if (!quality2.isTerminated)
			return true;
		// 06.05.2014: if this quality is NaN, the new one is always considered
		// better
		if (Double.isNaN(quality2.getValue()))
			return true;
		// 06.05.2014: if the new quality is NaN, the old one is considered
		// better (if it is not NaN)
		if (Double.isNaN(quality1.getValue()))
			return false;
		return isBetterThanHelper(quality1, quality2);
	}

	protected abstract boolean isBetterThanHelper(
			ClusteringQualityMeasureValue quality1,
			ClusteringQualityMeasureValue quality2);

	/**
	 * @return The minimal value of the range of possible values of this quality
	 *         measure.
	 */
	public abstract double getMinimum();

	/**
	 * @return The maximal value of the range of possible values of this quality
	 *         measure.
	 */
	public abstract double getMaximum();

	/**
	 * Override this method to indicate, whether the quality measure of your
	 * subclass needs a goldstandard to be able to be computed.
	 * 
	 * @return True, if this clustering quality measure requires a goldstandard
	 *         to be able to assess the quality of a clustering.
	 */
	public abstract boolean requiresGoldstandard();

	/**
	 * This alias is used whenever this clustering quality measure is visually
	 * represented and a readable name is needed.
	 * 
	 * @return The alias of this clustering quality measure.
	 */
	public abstract String getAlias();
}
