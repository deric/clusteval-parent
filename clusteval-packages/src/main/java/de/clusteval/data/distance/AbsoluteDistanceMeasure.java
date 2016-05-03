/** *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ***************************************************************************** */
package de.clusteval.data.distance;

import de.clusteval.api.IDistanceMeasure;
import de.clusteval.api.data.IConversionInputToStandardConfiguration;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.r.ROperationNotSupported;
import de.clusteval.utils.ArraysExt;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"proxy"})
@ServiceProvider(service = IDistanceMeasure.class)
public class AbsoluteDistanceMeasure extends DistanceMeasureR {

    public static final String NAME = "absolute distance";

    @Override
    public String getName() {
        return NAME;
    }

    public AbsoluteDistanceMeasure() {
        super();
    }

    /**
     * The copy constructor for this measure.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public AbsoluteDistanceMeasure(final AbsoluteDistanceMeasure other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.distance.DistanceMeasure#supportsMatrix()
     */
    @Override
    public boolean supportsMatrix() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.data.distance.DistanceMeasure#isSymmetric()
     */
    @Override
    public boolean isSymmetric() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.distance.DistanceMeasureR#getDistanceHelper(double[],
     * double[], de.clusteval.framework.repository.MyRengine)
     */
    @Override
    public double getDistanceHelper(double[] point1, double[] point2,
            IRengine rEngine) throws RException, ROperationNotSupported, InterruptedException {
        return ArraysExt.sum(ArraysExt.abs(ArraysExt.subtract(point1, point2)));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.distance.DistanceMeasureR#getDistancesHelper(de.clusteval
     * .data.dataset.format.ConversionInputToStandardConfiguration, double[][],
     * de.clusteval.framework.repository.MyRengine, int)
     */
    @Override
    public double[][] getDistancesHelper(
            IConversionInputToStandardConfiguration config, double[][] matrix,
            IRengine rEngine, int firstRow, int lastRow)
            throws RException, ROperationNotSupported, InterruptedException {
        return rEngine
                .eval(String
                        .format("proxy::dist(rbind(matrix[%d:%d,]), rbind(matrix), method='Manhattan')",
                                firstRow, lastRow)).asDoubleMatrix();
    }
}
