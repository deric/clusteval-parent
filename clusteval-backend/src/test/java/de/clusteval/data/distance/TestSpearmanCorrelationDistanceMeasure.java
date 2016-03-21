/**
 *
 */
package de.clusteval.data.distance;

import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.utils.AbstractClustEvalTest;
import de.clusteval.api.r.RNotAvailableException;
import de.wiwie.wiutils.utils.ArraysExt;
import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import java.util.ArrayList;
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class TestSpearmanCorrelationDistanceMeasure extends AbstractClustEvalTest {

    @Test
    public void test() throws UnknownDistanceMeasureException,
                              RNotAvailableException, InterruptedException {
        DistanceMeasure measure = DistanceMeasure.parseFromString(
                getRepository(), "SpearmanCorrelationRDistanceMeasure");
        Assert.assertTrue(measure != null);

        ConversionInputToStandardConfiguration config = new ConversionInputToStandardConfiguration(
                measure, NUMBER_PRECISION.FLOAT,
                new ArrayList<>(),
                new ArrayList<>());

        double[][] matrix = new double[][]{new double[]{1, 2, 1},
        new double[]{4, 5, 6}, new double[]{7, 8, 9},
        new double[]{7, 6, 5}};

        double[][] result = measure.getDistances(config, matrix).toArray();

        ArraysExt.print(result);

        assertArrayEquals(new double[][]{new double[]{0, 0.5, 0.5, 0.5},
        new double[]{0.5, 0, 0, 1}, new double[]{0.5, 0, 0, 1},
        new double[]{0.5, 1, 1, 0}}, result);
    }
}
