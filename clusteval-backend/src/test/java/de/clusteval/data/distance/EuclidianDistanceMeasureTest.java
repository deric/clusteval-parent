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
import de.clusteval.api.Matrix;
import de.clusteval.api.Precision;
import de.clusteval.api.data.DistanceMeasureFactory;
import de.clusteval.api.data.InputToStd;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.utils.AbstractClustEvalTest;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class EuclidianDistanceMeasureTest extends AbstractClustEvalTest {

    @Test
    public void test() throws RNotAvailableException, InterruptedException, UnknownProviderException {
        IDistanceMeasure measure = DistanceMeasureFactory.parseFromString(
                getRepository(), "EuclidianDistanceMeasure");
        assertTrue(measure != null);

        InputToStd config = new InputToStd(
                measure, Precision.FLOAT,
                new ArrayList<>(),
                new ArrayList<>());

        double[][] matrix = new double[][]{new double[]{1, 2, 3},
        new double[]{4, 5, 6}, new double[]{7, 8, 9}};
        Matrix res = measure.getDistances(config, matrix);
        System.out.println("res: " + res.toString());
        assertEquals(0.0, res.getSimilarity(1, 1), DELTA);
        assertEquals(5.196152210235596, res.getSimilarity(0, 1), DELTA);
        assertEquals(10.392304420471191, res.getSimilarity(0, 2), DELTA);
    }
}
