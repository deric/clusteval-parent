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
/**
 *
 */
package de.clusteval.cluster.quality;

import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.cluster.Clustering;
import de.clusteval.context.Context;
import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetConfig;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.data.dataset.format.ConversionStandardToInputConfiguration;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.distance.DistanceMeasure;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.utils.AbstractClustEvalTest;
import de.clusteval.api.exceptions.FormatConversionException;
import de.clusteval.api.r.RCalculationException;
import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class DunnIndexRClusteringQualityMeasureTest extends AbstractClustEvalTest {

    @Test
    public void test() throws InstantiationException, IllegalAccessException,
                              RepositoryAlreadyExistsException, InvalidRepositoryException,
                              RepositoryConfigNotFoundException,
                              RepositoryConfigurationException, NoRepositoryFoundException,
                              RegisterException, NoSuchAlgorithmException,
                              FormatConversionException, UnknownDistanceMeasureException,
                              UnknownContextException, RNotAvailableException,
                              RCalculationException, InterruptedException {
        try {

            Context context = Context.parseFromString(getRepository(), "ClusteringContext");
            Clustering clustering = new Clustering(this.getRepository(),
                    System.currentTimeMillis(), new File(""));
            Cluster cluster1 = new Cluster("1");
            cluster1.add(new ClusterItem("id1"), 1.0f);
            cluster1.add(new ClusterItem("id2"), 1.0f);
            clustering.addCluster(cluster1);

            Cluster cluster2 = new Cluster("2");
            cluster2.add(new ClusterItem("id3"), 1.0f);
            clustering.addCluster(cluster2);

            DataConfig dc = this.getRepository().getStaticObjectWithName(
                    DataConfig.class, "dunnIndexMatrixTest");
            DataSetConfig dsc = dc.getDatasetConfig();
            DataSet ds = dsc.getDataSet();
            ds.preprocessAndConvertTo(
                    context,
                    DataSetFormat.parseFromString(this.getRepository(),
                            "SimMatrixDataSetFormat"),
                    new ConversionInputToStandardConfiguration(DistanceMeasure
                            .parseFromString(getRepository(),
                                    "EuclidianDistanceMeasure"),
                            NUMBER_PRECISION.DOUBLE,
                            new ArrayList<>(),
                            new ArrayList<>()),
                    new ConversionStandardToInputConfiguration());
            ds.getInStandardFormat().loadIntoMemory();
            ClusteringQualityMeasure measure = ClusteringQualityMeasure
                    .parseFromString(getRepository(),
                            "DunnIndexRClusteringQualityMeasure",
                            new ClusteringQualityMeasureParameters());
            double quality = measure.getQualityOfClustering(clustering, null, dc).getValue();
            ds.getInStandardFormat().unloadFromMemory();
            System.out.println("Dunn Index: " + quality);
            assertEquals(2.0326861833688232, quality, DELTA);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnknownGoldStandardFormatException | UnknownDataSetFormatException | IllegalArgumentException | InvalidDataSetFormatVersionException | UnknownClusteringQualityMeasureException | IOException e) {
            e.printStackTrace();
        }

    }
}
