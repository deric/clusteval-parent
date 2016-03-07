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
package de.clusteval.data.distance;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import de.wiwie.wiutils.utils.SimilarityMatrix;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryObject;
import de.clusteval.program.r.RLibraryInferior;
import de.clusteval.utils.RNotAvailableException;

/**
 * 
 * {@code 
 * 
 * 
 * A distance measure MyDistanceMeasure can be added to ClustEval by
 * 
 * 1. extending this class with your own class MyDistanceMeasure . You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your distance measure: 
 *
 *   * :java:ref:`DistanceMeasure(Repository, boolean, long, File)` : The constructor for your distance measure. This constructor has to be implemented and public, otherwise the framework will not be able to load your distance measure.
 *   * :java:ref:`DistanceMeasure(MyDistanceMeasure)` : The copy constructor for your distance measure. This constructor has to be implemented and public, otherwise the framework will not be able to load your distance measure.
 *   * :java:ref:`getDistance(double[],double[])` : This method is the core of your distance measure. It returns the distance of the two points specified by the absolute coordinates in the two double arrays. 
 *   * :java:ref:`supportsMatrix()` : This method indicates, whether your distance measure can calculate distances of a whole set of point-pairs, i.e. your distance measure implements the method getDistances(double[][]).
 *   * :java:ref:`getDistances(double[][])` : The absolute coordinates of the points are stored row-wise in the given matrix and distances are calculated between every pair of rows. Position [i][j] of the returned double[][] matrix contains the distance between the i-th and j-th row of the input matrix.
 * 
 * 2. Creating a jar file named MyDistanceMeasure.jar containing the MyDistanceMeasure.class compiled on your machine in the correct folder structure corresponding to the packages:
 * 
 *   * de/clusteval/data/distance/MyDistanceMeasure.class
 *   
 * 3. Putting the MyDistanceMeasure.jar into the distance measure folder of the repository:
 * 
 *   * <REPOSITORY ROOT>/supp/distanceMeasures 
 *   
 * The backend server will recognize and try to load the new distance measure au- tomatically the
 * 
 * }
 * 
 * @author Christian Wiwie
 * 
 */
public abstract class DistanceMeasure extends RepositoryObject
		implements
			RLibraryInferior {

	/**
	 * @param repository
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 */
	public DistanceMeasure(Repository repository, boolean register,
			long changeDate, File absPath) throws RegisterException {
		super(repository, register, changeDate, absPath);
	}

	/**
	 * The copy constructor of this distance measures.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public DistanceMeasure(final DistanceMeasure other)
			throws RegisterException {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public final DistanceMeasure clone() {
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

	/**
	 * Parses the from string.
	 * 
	 * @param repository
	 *            the repository
	 * @param distanceMeasure
	 *            the distance measure
	 * @return the distance measure
	 * @throws UnknownDistanceMeasureException
	 */
	public static DistanceMeasure parseFromString(final Repository repository,
			String distanceMeasure) throws UnknownDistanceMeasureException {
		Class<? extends DistanceMeasure> c = repository.getRegisteredClass(
				DistanceMeasure.class, "de.clusteval.data.distance."
						+ distanceMeasure);
		try {
			DistanceMeasure measure = c.getConstructor(Repository.class,
					boolean.class, long.class, File.class).newInstance(
					repository, false, System.currentTimeMillis(),
					new File(distanceMeasure));

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
		throw new UnknownDistanceMeasureException("\"" + distanceMeasure
				+ "\" is not a known distance measure.");
	}

	/**
	 * @param point1
	 *            A point with double valued coordinates.
	 * @param point2
	 *            A point with double valued coordinates.
	 * @return Distance between point1 and point2.
	 * @throws RNotAvailableException
	 * @throws InterruptedException
	 */
	public abstract double getDistance(double[] point1, double[] point2)
			throws RNotAvailableException, InterruptedException;

	/**
	 * This method indicates, whether a distance measure supports the bulk
	 * calculation of all pairwise distances of rows of a matrix with rows of a
	 * second matrix. Overwrite it in your subclass and return the appropriate
	 * boolean value. If your subclass supports matrices you also have to
	 * overwrite {@link #getDistances(double[][])} with a correct
	 * implementation.
	 * 
	 * @return True, if this distance measure supports bulk distance calculation
	 *         of matrices.
	 */
	public abstract boolean supportsMatrix();

	/**
	 * This method indicates whether the similarity s(x,y)==s(y,x).
	 * 
	 * @return True, if this distance measure is symmetric, false otherwise.
	 */
	public abstract boolean isSymmetric();

	/**
	 * This method calculates all pairwise distances between the rows of a
	 * matrix.
	 * 
	 * @param matrix
	 *            A matrix containing samples in each row and features in the
	 *            columns.
	 * @return Matrix containing all pairwise distances of rows of the matrix
	 * @throws RNotAvailableException
	 * @throws InterruptedException
	 */
	public abstract SimilarityMatrix getDistances(
			ConversionInputToStandardConfiguration config, double[][] matrix)
			throws RNotAvailableException, InterruptedException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return this.getClass().equals(obj.getClass());
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

}
