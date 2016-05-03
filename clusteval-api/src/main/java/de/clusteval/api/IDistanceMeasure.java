/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.api;

import de.clusteval.api.data.IConversionInputToStandardConfiguration;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.r.ROperationNotSupported;
import de.clusteval.api.repository.IRepositoryObject;

/**
 *
 * @author deric
 */
public interface IDistanceMeasure extends IRepositoryObject {

    /**
     * A unique identifier for distance measure
     *
     * @return commonly used name
     */
    String getName();

    /**
     * @param point1 A point with double valued coordinates.
     * @param point2 A point with double valued coordinates.
     * @return Distance between point1 and point2.
     * @throws RNotAvailableException
     * @throws InterruptedException
     */
    double getDistance(double[] point1, double[] point2) throws RNotAvailableException, InterruptedException;

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
    boolean supportsMatrix();

    /**
     * This method indicates whether the similarity s(x,y)==s(y,x).
     *
     * @return True, if this distance measure is symmetric, false otherwise.
     */
    boolean isSymmetric();

    /**
     * This method calculates all pairwise distances between the rows of a
     * matrix.
     *
     * @param config
     * @param matrix A matrix containing samples in each row and features in the
     *               columns.
     * @return Matrix containing all pairwise distances of rows of the matrix
     * @throws RNotAvailableException
     * @throws InterruptedException
     */
    Matrix getDistances(IConversionInputToStandardConfiguration config, double[][] matrix)
            throws RNotAvailableException, InterruptedException;

    IDistanceMeasure clone();

    double getDistanceHelper(double[] point1, double[] point2, final IRengine rEngine)
            throws RException, ROperationNotSupported, InterruptedException;

    double[][] getDistancesHelper(IConversionInputToStandardConfiguration config, double[][] matrix,
            final IRengine rEngine, int firstRow, int lastRow)
            throws RException, ROperationNotSupported, InterruptedException;
}
