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
package de.clusteval.data.distance;

import de.clusteval.api.IDistanceMeasure;
import de.clusteval.api.data.DistanceMeasure;
import de.clusteval.api.data.InputToStd;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RNotAvailableException;
import de.wiwie.wiutils.utils.SimilarityMatrix;

/**
 * This type of distance measure uses the R framework.
 *
 * @author Christian Wiwie
 *
 */
public abstract class DistanceMeasureR extends DistanceMeasure implements IDistanceMeasure {

    public DistanceMeasureR() {
        super();
    }

    /**
     * The copy constructor of this R distance measures.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public DistanceMeasureR(final DistanceMeasureR other)
            throws RegisterException {
        super(other);
    }

    @Override
    public final double getDistance(double[] point1, double[] point2)
            throws RNotAvailableException, InterruptedException {
        try {
            IRengine rEngine = repository.getRengineForCurrentThread();
            try {
                return getDistanceHelper(point1, point2, rEngine);
            } catch (RException e) {
                this.log.warn("R-framework (" + this.getClass().getSimpleName()
                        + "): " + rEngine.getLastError());
                //TODO: wtf?
                return -1.0;
            } finally {
                rEngine.clear();
            }
        } catch (RException e) {
            throw new RNotAvailableException(e.getMessage());
        }
    }

    @Override
    public final SimilarityMatrix getDistances(InputToStd config, double[][] matrix)
            throws RNotAvailableException, InterruptedException {
        try {
            IRengine rEngine = repository.getRengineForCurrentThread();
            try {
                this.log.debug("Transferring coordinates to R");
                rEngine.assign("matrix", matrix);
                rEngine.eval("matrix.t <- t(matrix)");

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
                    for (int x = 0; x < vector.length; x++) {
                        for (int y = 0; y < vector[x].length; y++) {
                            result.setSimilarity(i + x, y, vector[x][y]);
                        }
                    }
                    // this.log.info(String.format("%d%%", i
                    // / rowsPerInvocation + 1));
                }
                return result;

            } catch (RException e) {
                this.log.warn("R-framework (" + this.getClass().getSimpleName()
                        + "): " + rEngine.getLastError());
                // TODO
                return null;
            }
        } catch (RException ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

}
