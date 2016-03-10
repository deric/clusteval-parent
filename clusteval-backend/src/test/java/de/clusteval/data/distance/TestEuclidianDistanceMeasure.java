/**
 *
 */
package de.clusteval.data.distance;

import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.utils.AbstractClustEvalTest;
import de.clusteval.api.exceptions.RNotAvailableException;
import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class TestEuclidianDistanceMeasure extends AbstractClustEvalTest {

    @Test
    public void test() throws UnknownDistanceMeasureException,
                              RNotAvailableException, InterruptedException {
        DistanceMeasure measure = DistanceMeasure.parseFromString(
                getRepository(), "EuclidianDistanceMeasure");
        assertTrue(measure != null);

        ConversionInputToStandardConfiguration config = new ConversionInputToStandardConfiguration(
                measure, NUMBER_PRECISION.FLOAT,
                new ArrayList<>(),
                new ArrayList<>());

        double[][] matrix = new double[][]{new double[]{1, 2, 3},
        new double[]{4, 5, 6}, new double[]{7, 8, 9}};

        System.out.println(Arrays.toString(measure.getDistances(config, matrix).toArray()));
    }
}
