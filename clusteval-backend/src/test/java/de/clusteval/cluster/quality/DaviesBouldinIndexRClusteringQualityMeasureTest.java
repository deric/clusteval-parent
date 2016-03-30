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
package de.clusteval.cluster.quality;

import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.exceptions.FormatConversionException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RCalculationException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.cluster.Clustering;
import de.clusteval.context.Context;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.data.dataset.format.ConversionStandardToInputConfiguration;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.distance.DistanceMeasure;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.utils.AbstractClustEvalTest;
import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class DaviesBouldinIndexRClusteringQualityMeasureTest extends AbstractClustEvalTest {

    private static final double DELTA = 1e-9;

    @Test
    public void test() throws InstantiationException, IllegalAccessException,
                              RepositoryAlreadyExistsException, InvalidRepositoryException,
                              RepositoryConfigNotFoundException,
                              RepositoryConfigurationException, NoRepositoryFoundException,
                              RegisterException, NoSuchAlgorithmException,
                              FormatConversionException, UnknownDistanceMeasureException,
                              UnknownContextException, RNotAvailableException,
                              RCalculationException {
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
            IDataSetConfig dsc = dc.getDatasetConfig();
            IDataSet ds = dsc.getDataSet();
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
                            "DaviesBouldinIndexRClusteringQualityMeasure",
                            new ClusteringQualityMeasureParameters());
            double quality = measure.getQualityOfClustering(clustering, null,
                    dc).getValue();
            ds.getInStandardFormat().unloadFromMemory();
            System.out.println("Davies Bouldin Index: " + quality);
            assertEquals(0.49195985498493144, quality, DELTA);
        } catch (Exception e) {
            assertTrue(false);
        }

    }
}