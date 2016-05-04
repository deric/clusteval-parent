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
package de.clusteval.data.distance;

import de.clusteval.api.IDistanceMeasure;
import de.clusteval.api.Precision;
import de.clusteval.api.data.DistanceMeasureFactory;
import de.clusteval.api.data.InputToStd;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.r.RNotAvailableException;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;

/**
 *
 * @author deric
 */
public class EuclidianDistanceMeasureTest extends AbstractTest {

    public EuclidianDistanceMeasureTest() {

    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // @Test
    public void test() throws RNotAvailableException, InterruptedException, UnknownProviderException {
        IDistanceMeasure measure = DistanceMeasureFactory.parseFromString("euclidean");

        assertTrue(measure != null);

        InputToStd config = new InputToStd(
                measure, Precision.FLOAT,
                new ArrayList<>(),
                new ArrayList<>());

        double[][] matrix = new double[][]{new double[]{1, 2, 3},
        new double[]{4, 5, 6}, new double[]{7, 8, 9}};

        System.out.println(Arrays.toString(measure.getDistances(config, matrix).toArray()));
    }

}
