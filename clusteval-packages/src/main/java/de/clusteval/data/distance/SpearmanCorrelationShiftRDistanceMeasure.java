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

import de.clusteval.api.data.IConversionInputToStandardConfiguration;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.ROperationNotSupported;
import de.clusteval.api.repository.IRepository;
import java.io.File;

/**
 * @author Christian Wiwie
 *
 */
public class SpearmanCorrelationShiftRDistanceMeasure extends DistanceMeasureR {

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public SpearmanCorrelationShiftRDistanceMeasure(IRepository repository,
            boolean register, long changeDate, File absPath)
            throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * The copy constructor for this measure.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public SpearmanCorrelationShiftRDistanceMeasure(
            final SpearmanCorrelationShiftRDistanceMeasure other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see utils.Distance#getDistance(double[], double[])
     */
    @Override
    public double getDistanceHelper(double[] point1, double[] point2,
            final IRengine rEngine) throws RException, ROperationNotSupported, InterruptedException {
        rEngine.assign("p1", point1);
        rEngine.assign("p2", point2);
        double result = rEngine.eval("cor(p1,p2,method='spearman')").asDouble();
        // convert to distance
        return 1 - (result + 1.0) / 2.0;
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
     * @see data.distance.DistanceMeasure#getDistances(double[][])
     */
    @Override
    public double[][] getDistancesHelper(
            IConversionInputToStandardConfiguration config, double[][] matrix,
            final IRengine rEngine, int firstRow, int lastRow)
            throws RException, ROperationNotSupported, InterruptedException {
        return rEngine.eval(String.format(
                "1 - (cor(cbind(matrix.t[,%d:%d]), cbind(matrix.t), method='spearman')+1)/2",
                firstRow, lastRow)).asDoubleMatrix();
    }
}
