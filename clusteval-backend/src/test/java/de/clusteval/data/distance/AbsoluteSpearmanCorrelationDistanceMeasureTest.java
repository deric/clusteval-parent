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

import de.clusteval.api.data.DistanceMeasure;
import de.clusteval.api.IDistanceMeasure;
import de.clusteval.api.Precision;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.utils.AbstractClustEvalTest;
import de.clusteval.utils.ArraysExt;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class AbsoluteSpearmanCorrelationDistanceMeasureTest extends AbstractClustEvalTest {

    @Test
    public void test() throws UnknownDistanceMeasureException,
                              RNotAvailableException, InterruptedException {
        IDistanceMeasure measure = DistanceMeasure.parseFromString(
                getRepository(), "AbsoluteSpearmanCorrelationRDistanceMeasure");
        Assert.assertTrue(measure != null);

        ConversionInputToStandardConfiguration config = new ConversionInputToStandardConfiguration(
                measure, Precision.FLOAT,
                new ArrayList<>(),
                new ArrayList<>());

        double[][] matrix = new double[][]{new double[]{1, 2, 1},
        new double[]{4, 5, 6}, new double[]{7, 8, 9},
        new double[]{7, 6, 5}};

        double[][] result = measure.getDistances(config, matrix).toArray();

        Assert.assertArrayEquals(new double[][]{new double[]{0, 1, 1, 1},
        new double[]{1, 0, 0, 0}, new double[]{1, 0, 0, 0},
        new double[]{1, 0, 0, 0}}, result);

        ArraysExt.print(result);
    }
}
