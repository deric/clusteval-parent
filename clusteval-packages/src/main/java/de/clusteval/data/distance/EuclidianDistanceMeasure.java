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
import de.clusteval.api.data.IConversionInputToStandardConfiguration;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.r.ROperationNotSupported;
import java.security.InvalidParameterException;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"proxy"})
@ServiceProvider(service = IDistanceMeasure.class)
public class EuclidianDistanceMeasure extends DistanceMeasureR {

    public static final String NAME = "euclidean";

    public EuclidianDistanceMeasure() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * The copy constructor for this measure.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public EuclidianDistanceMeasure(final EuclidianDistanceMeasure other) throws RegisterException {
        super(other);
    }

    @Override
    public boolean supportsMatrix() {
        return true;
    }

    @Override
    public boolean isSymmetric() {
        return true;
    }

    @Override
    public double getDistanceHelper(double[] point1, double[] point2, IRengine rEngine)
            throws RException, ROperationNotSupported, InterruptedException {
        double result = 0.0;
        if (point1.length != point2.length) {
            throw new InvalidParameterException(
                    "The dimensions of the points need to be the same.");
        }
        for (int i = 0; i < point1.length; i++) {
            result += Math.pow(point1[i] - point2[i], 2.0);
        }
        result = Math.sqrt(result);
        return result;
    }

    @Override
    public double[][] getDistancesHelper(
            IConversionInputToStandardConfiguration config, double[][] matrix,
            IRengine rEngine, int firstRow, int lastRow)
            throws RException, ROperationNotSupported, InterruptedException {
        return rEngine.eval(String.format(
                "proxy::dist(rbind(matrix[%d:%d,]), rbind(matrix), method='Euclidean')",
                firstRow, lastRow)).asDoubleMatrix();
    }
}
