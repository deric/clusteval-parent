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
package de.clusteval.cluster.quality;

import ch.qos.logback.classic.Level;
import de.clusteval.api.ContextFactory;
import de.clusteval.api.IContext;
import de.clusteval.api.Precision;
import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.ClusteringEvaluationParameters;
import de.clusteval.api.data.DataConfig;
import de.clusteval.api.data.DataSetFormatFactory;
import de.clusteval.api.data.DistanceMeasureFactory;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.data.InputToStd;
import de.clusteval.api.data.StdToInput;
import de.clusteval.api.exceptions.FormatConversionException;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RCalculationException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.cluster.Clustering;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.utils.AbstractClustEvalTest;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 * @author Christian Wiwie
 *
 */
public class SilhouetteValueGlobalRClusteringQualityMeasureTest extends
        AbstractClustEvalTest {

    static {
        ClustevalBackendServer.logLevel(Level.WARN);
    }

    @Test
    public void testSingleCluster()
            throws InstantiationException,
                   IllegalAccessException, RepositoryAlreadyExistsException,
                   InvalidRepositoryException, RepositoryConfigNotFoundException,
                   RepositoryConfigurationException, NoRepositoryFoundException,
                   RegisterException, NoSuchAlgorithmException,
                   RNotAvailableException, RCalculationException,
                   UnknownClusteringQualityMeasureException, InterruptedException, RException {
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
                            "SilhouetteValueGlobalRClusteringQualityMeasure",
                            new ClusteringEvaluationParameters());
            double quality = measure.getQualityOfClustering(clustering, null,
                    null).getValue();
            assertEquals(-1.0, quality, DELTA);
        } catch (IllegalArgumentException | InvalidDataSetFormatVersionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTwoClusters()
            throws InstantiationException,
                   IllegalAccessException, RepositoryAlreadyExistsException,
                   InvalidRepositoryException, RepositoryConfigNotFoundException,
                   RepositoryConfigurationException, NoRepositoryFoundException,
                   RegisterException, NoSuchAlgorithmException,
                   RNotAvailableException, RCalculationException,
                   UnknownClusteringQualityMeasureException, FormatConversionException,
                   InterruptedException, RException, UnknownDataSetFormatException, UnknownProviderException {
        try {

            IContext context = ContextFactory.parseFromString(getRepository(),
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
            ds.preprocessAndConvertTo(context,
                    DataSetFormatFactory.parseFromString("SimMatrixDataSetFormat"),
                    new InputToStd(DistanceMeasureFactory
                            .parseFromString(getRepository(),
                                    "EuclidianDistanceMeasure"),
                            Precision.DOUBLE,
                            new ArrayList<>(),
                            new ArrayList<>()),
                    new StdToInput());
            ds.getInStandardFormat().loadIntoMemory();
            ClusteringQualityMeasure measure = ClusteringQualityMeasure
                    .parseFromString(getRepository(),
                            "SilhouetteValueGlobalRClusteringQualityMeasure",
                            new ClusteringEvaluationParameters());
            double quality = measure.getQualityOfClustering(clustering, null,
                    dc).getValue();
            ds.getInStandardFormat().unloadFromMemory();
            assertEquals(0.3346755237978247, quality, DELTA);
        } catch (IllegalArgumentException | InvalidDataSetFormatVersionException |
                IOException e) {
            Exceptions.printStackTrace(e);
        }
    }
}
