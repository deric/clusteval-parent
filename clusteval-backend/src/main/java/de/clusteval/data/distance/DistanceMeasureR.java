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

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;

import de.wiwie.wiutils.utils.SimilarityMatrix;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.framework.repository.MyRengine;
import de.clusteval.framework.repository.RException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.utils.RNotAvailableException;

/**
 * This type of distance measure uses the R framework.
 * 
 * @author Christian Wiwie
 * 
 */
public abstract class DistanceMeasureR extends DistanceMeasure {

	/**
	 * @param repository
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 */
	public DistanceMeasureR(Repository repository, boolean register,
			long changeDate, File absPath) throws RegisterException {
		super(repository, register, changeDate, absPath);
	}

	/**
	 * The copy constructor of this R distance measures.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public DistanceMeasureR(final DistanceMeasureR other)
			throws RegisterException {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.data.distance.DistanceMeasure#getDistance(double[],
	 * double[])
	 */
	@Override
	public final double getDistance(double[] point1, double[] point2)
			throws RNotAvailableException, InterruptedException {
		try {
			MyRengine rEngine = repository.getRengineForCurrentThread();
			try {
				try {
					return getDistanceHelper(point1, point2, rEngine);
				} catch (REXPMismatchException e) {
					// handle this type of exception as an REngineException
					throw new RException(rEngine, e.getMessage());
				}
			} catch (REngineException e) {
				this.log.warn("R-framework (" + this.getClass().getSimpleName()
						+ "): " + rEngine.getLastError());
				// TODO
				return -1.0;
			} finally {
				rEngine.clear();
			}
		} catch (RserveException e) {
			throw new RNotAvailableException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.data.distance.DistanceMeasure#getDistances(double[][])
	 */
	@Override
	public final SimilarityMatrix getDistances(
			ConversionInputToStandardConfiguration config, double[][] matrix)
			throws RNotAvailableException, InterruptedException {
		try {
			MyRengine rEngine = repository.getRengineForCurrentThread();
			try {
				this.log.debug("Transferring coordinates to R");
				rEngine.assign("matrix", matrix);
				rEngine.eval("matrix.t <- t(matrix)");
				try {
					SimilarityMatrix result = new SimilarityMatrix(null,
							matrix.length, matrix.length,
							config.getSimilarityPrecision(), this.isSymmetric());
					// calculate similarities package-wise (in each iteration
					// all
					// similarities of 1/100 of all objects, but at least 100
					this.log.debug("Calculating pairwise distances in R and transferring back to Java");
					int rowsPerInvocation = Math.max(matrix.length / 100, 100);
					for (int i = 0; i < matrix.length; i += rowsPerInvocation) {
						int firstRow = i + 1;
						int lastRow = Math.min(firstRow + rowsPerInvocation,
								matrix.length);
						double[][] vector = getDistancesHelper(config, matrix,
								rEngine, firstRow, lastRow);
						for (int x = 0; x < vector.length; x++)
							for (int y = 0; y < vector[x].length; y++)
								result.setSimilarity(i + x, y, vector[x][y]);
						// this.log.info(String.format("%d%%", i
						// / rowsPerInvocation + 1));
					}
					return result;
				} catch (REXPMismatchException e) {
					// handle this type of exception as an REngineException
					throw new RException(rEngine, e.getMessage());
				}
			} catch (REngineException e) {
				this.log.warn("R-framework (" + this.getClass().getSimpleName()
						+ "): " + rEngine.getLastError());
				// TODO
				return null;
			}
		} catch (RserveException e) {
			throw new RNotAvailableException(e.getMessage());
		}
	}

	protected abstract double getDistanceHelper(double[] point1,
			double[] point2, final MyRengine rEngine) throws REngineException,
			REXPMismatchException, InterruptedException;

	protected abstract double[][] getDistancesHelper(
			ConversionInputToStandardConfiguration config, double[][] matrix,
			final MyRengine rEngine, int firstRow, int lastRow)
			throws REngineException, REXPMismatchException,
			InterruptedException;
}
