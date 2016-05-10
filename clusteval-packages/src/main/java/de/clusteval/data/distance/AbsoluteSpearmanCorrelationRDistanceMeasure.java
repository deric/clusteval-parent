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
import de.clusteval.api.data.InputToStd;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.ROperationNotSupported;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Christian Wiwie
 *
 */
@ServiceProvider(service = IDistanceMeasure.class)
public class AbsoluteSpearmanCorrelationRDistanceMeasure extends DistanceMeasureR {

    public static final String NAME = "absolute spearman correlation R";

    public AbsoluteSpearmanCorrelationRDistanceMeasure() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * The copy constructor for this measure.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public AbsoluteSpearmanCorrelationRDistanceMeasure(
            final AbsoluteSpearmanCorrelationRDistanceMeasure other)
            throws RegisterException {
        super(other);
    }

    @Override
    public double getDistanceHelper(double[] point1, double[] point2,
            final IRengine rEngine) throws RException, ROperationNotSupported, InterruptedException {
        rEngine.assign("p1", point1);
        rEngine.assign("p2", point2);
        double result = rEngine.eval("cor(p1,p2,method='spearman')").asDouble();
        // convert to distance
        return 1.0 - Math.abs(result);
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

    @Override
    public boolean isSymmetric() {
        return true;
    }

    @Override
    public double[][] getDistancesHelper(InputToStd config, double[][] matrix,
            final IRengine rEngine, int firstRow, int lastRow)
            throws RException, ROperationNotSupported, InterruptedException {
        return rEngine
                .eval(String
                        .format("1-abs(cor(cbind(matrix.t[,%d:%d]), cbind(matrix.t), method='spearman'))",
                                firstRow, lastRow)).asDoubleMatrix();
    }
}
