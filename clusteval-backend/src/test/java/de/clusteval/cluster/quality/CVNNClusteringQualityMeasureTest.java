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

import ch.qos.logback.classic.Level;
import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.exceptions.FormatConversionException;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
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
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.utils.AbstractClustEvalTest;
import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

/**
 * TODO: recover CVNN measure
 *
 * @author Christian Wiwie
 *
 */
public class CVNNClusteringQualityMeasureTest extends AbstractClustEvalTest {

    static {
        ClustevalBackendServer.logLevel(Level.WARN);
    }

    //@Test
    public void testSingleCluster() throws InstantiationException,
                                           IllegalAccessException, RepositoryAlreadyExistsException,
                                           InvalidRepositoryException, RepositoryConfigNotFoundException,
                                           RepositoryConfigurationException, NoRepositoryFoundException,
                                           RegisterException, NoSuchAlgorithmException,
                                           RNotAvailableException, RCalculationException,
                                           UnknownClusteringQualityMeasureException,
                                           FormatConversionException, UnknownDistanceMeasureException,
                                           InterruptedException {
        try {
            Clustering clustering = new Clustering(this.getRepository(),
                    System.currentTimeMillis(), new File(""));
            Cluster cluster1 = new Cluster("1");
            cluster1.add(new ClusterItem("square1"), 1.0f);
            cluster1.add(new ClusterItem("star1"), 1.0f);
            cluster1.add(new ClusterItem("star2"), 1.0f);
            cluster1.add(new ClusterItem("star3"), 1.0f);
            cluster1.add(new ClusterItem("star4"), 1.0f);
            cluster1.add(new ClusterItem("star5"), 1.0f);
            cluster1.add(new ClusterItem("star6"), 1.0f);
            clustering.addCluster(cluster1);

            ClusteringQualityMeasure measure = ClusteringQualityMeasure
                    .parseFromString(getRepository(),
                            "CVNNClusteringQualityMeasure",
                            new ClusteringQualityMeasureParameters());

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

            double quality = measure.getQualityOfClustering(clustering, null,
                    dc).getValue();
            assertEquals(quality, 0.0, 1e-9);
            System.out.println(quality);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnknownGoldStandardFormatException | UnknownDataSetFormatException | IllegalArgumentException | InvalidDataSetFormatVersionException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //@Test
    public void testTwoClusters() throws InstantiationException,
                                         IllegalAccessException, RepositoryAlreadyExistsException,
                                         InvalidRepositoryException, RepositoryConfigNotFoundException,
                                         RepositoryConfigurationException, NoRepositoryFoundException,
                                         RegisterException, NoSuchAlgorithmException,
                                         RNotAvailableException, RCalculationException,
                                         UnknownClusteringQualityMeasureException,
                                         FormatConversionException, UnknownDistanceMeasureException,
                                         UnknownContextException, InterruptedException {
        try {

            Context context = Context.parseFromString(getRepository(),
                    "ClusteringContext");
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
                            "CVNNClusteringQualityMeasure",
                            new ClusteringQualityMeasureParameters());
            double quality = measure.getQualityOfClustering(clustering, null,
                    dc).getValue();
            ds.getInStandardFormat().unloadFromMemory();
            System.out.println(quality);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnknownGoldStandardFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnknownDataSetFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidDataSetFormatVersionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
