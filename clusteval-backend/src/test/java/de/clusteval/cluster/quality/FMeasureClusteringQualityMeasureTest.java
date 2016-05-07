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
import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.ClusteringEvaluationFactory;
import de.clusteval.api.cluster.ClusteringEvaluationParameters;
import de.clusteval.api.data.DataConfig;
import de.clusteval.api.data.IGoldStandard;
import de.clusteval.api.exceptions.ClusteringParseException;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RCalculationException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.cluster.Clustering;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.utils.AbstractClustEvalTest;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class FMeasureClusteringQualityMeasureTest extends AbstractClustEvalTest {

    static {
        ClustevalBackendServer.logLevel(Level.WARN);
    }

    @Test
    public void testSingleCluster()
            throws InstantiationException, IllegalAccessException, RepositoryAlreadyExistsException,
                   InvalidRepositoryException, RepositoryConfigurationException, NoRepositoryFoundException,
                   RegisterException, NoSuchAlgorithmException,
                   RNotAvailableException, RCalculationException,
                   UnknownGoldStandardFormatException,
                   InvalidDataSetFormatException, IOException,
                   InterruptedException, IllegalArgumentException, RException, UnknownProviderException {
        ClustevalBackendServer.logLevel(Level.WARN);
        Clustering goldStandard = new Clustering(this.getRepository(),
                System.currentTimeMillis(), new File(""));
        Cluster gsCluster1 = new Cluster("1");
        gsCluster1.add(new ClusterItem("square1"), 1.0f);
        goldStandard.addCluster(gsCluster1);

        Cluster gsCluster2 = new Cluster("2");
        gsCluster2.add(new ClusterItem("star1"), 1.0f);
        gsCluster2.add(new ClusterItem("star2"), 1.0f);
        gsCluster2.add(new ClusterItem("star3"), 1.0f);
        gsCluster2.add(new ClusterItem("star4"), 1.0f);
        gsCluster2.add(new ClusterItem("star5"), 1.0f);
        gsCluster2.add(new ClusterItem("star6"), 1.0f);
        goldStandard.addCluster(gsCluster2);

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

        ClusteringEvaluation measure = ClusteringEvaluationFactory
                .parseFromString(getRepository(),
                        "TransClustFClusteringQualityMeasure",
                        new ClusteringEvaluationParameters());
        double quality = measure.getQualityOfClustering(clustering,
                goldStandard, null).getValue();
        System.out.println(measure.getName() + " " + quality);

        measure = ClusteringEvaluationFactory.parseFromString(getRepository(),
                "TransClustF2ClusteringQualityMeasure",
                new ClusteringEvaluationParameters());
        quality = measure
                .getQualityOfClustering(clustering, goldStandard, null)
                .getValue();
        System.out.println(measure.getName() + " " + quality);
    }

    @Test
    public void testSingleCluster2() throws InstantiationException, IllegalAccessException, RepositoryAlreadyExistsException,
                                            InvalidRepositoryException, RepositoryConfigurationException, NoRepositoryFoundException,
                                            RegisterException, NoSuchAlgorithmException,
                                            RNotAvailableException, RCalculationException,
                                            UnknownGoldStandardFormatException,
                                            InvalidDataSetFormatException, IOException,
                                            InterruptedException, IllegalArgumentException, RException, UnknownProviderException {
        ClustevalBackendServer.logLevel(Level.WARN);
        Clustering goldStandard = new Clustering(this.getRepository(),
                System.currentTimeMillis(), new File(""));
        Cluster gsCluster1 = new Cluster("1");
        gsCluster1.add(new ClusterItem("square1"), 1.0f);
        goldStandard.addCluster(gsCluster1);

        Cluster gsCluster2 = new Cluster("2");
        gsCluster2.add(new ClusterItem("star1"), 1.0f);
        gsCluster2.add(new ClusterItem("star2"), 1.0f);
        gsCluster2.add(new ClusterItem("star3"), 1.0f);
        gsCluster2.add(new ClusterItem("star4"), 1.0f);
        gsCluster2.add(new ClusterItem("star5"), 1.0f);
        gsCluster2.add(new ClusterItem("star6"), 1.0f);
        gsCluster2.add(new ClusterItem("star7"), 1.0f);
        goldStandard.addCluster(gsCluster2);

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
        cluster1.add(new ClusterItem("star7"), 1.0f);
        clustering.addCluster(cluster1);

        ClusteringEvaluation measure = ClusteringEvaluationFactory
                .parseFromString(getRepository(),
                        "TransClustFClusteringQualityMeasure",
                        new ClusteringEvaluationParameters());
        double quality = measure.getQualityOfClustering(clustering,
                goldStandard, null).getValue();
        assertEquals(0.8444444444444444, quality, DELTA);
        System.out.println(measure.getName() + " " + quality);

        measure = ClusteringEvaluationFactory.parseFromString(getRepository(),
                "TransClustF2ClusteringQualityMeasure",
                new ClusteringEvaluationParameters());
        quality = measure
                .getQualityOfClustering(clustering, goldStandard, null)
                .getValue();
        assertEquals(0.9027777777777778, quality, DELTA);
        System.out.println(measure.getName() + " " + quality);
    }

    @Test
    public void testAdditionalElementsInGs() throws InstantiationException,
                                                    IllegalAccessException, RepositoryAlreadyExistsException,
                                                    InvalidRepositoryException,
                                                    RepositoryConfigurationException, NoRepositoryFoundException,
                                                    RegisterException, NoSuchAlgorithmException,
                                                    RNotAvailableException, RCalculationException,
                                                    UnknownGoldStandardFormatException,
                                                    InvalidDataSetFormatException, IOException,
                                                    InterruptedException, IllegalArgumentException, RException, UnknownProviderException {
        ClustevalBackendServer.logLevel(Level.WARN);
        Clustering goldStandard = new Clustering(this.getRepository(),
                System.currentTimeMillis(), new File(""));
        Cluster gsCluster1 = new Cluster("1");
        gsCluster1.add(new ClusterItem("square1"), 1.0f);
        goldStandard.addCluster(gsCluster1);

        Cluster gsCluster2 = new Cluster("2");
        gsCluster2.add(new ClusterItem("star1"), 1.0f);
        gsCluster2.add(new ClusterItem("star2"), 1.0f);
        gsCluster2.add(new ClusterItem("star3"), 1.0f);
        gsCluster2.add(new ClusterItem("star4"), 1.0f);
        gsCluster2.add(new ClusterItem("star5"), 1.0f);
        gsCluster2.add(new ClusterItem("star6"), 1.0f);
        gsCluster2.add(new ClusterItem("star7"), 1.0f);
        // additional element that is not contained in clustering should be
        // removed and not affect result
        gsCluster2.add(new ClusterItem("star8"), 1.0f);
        goldStandard.addCluster(gsCluster2);

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
        cluster1.add(new ClusterItem("star7"), 1.0f);
        clustering.addCluster(cluster1);

        ClusteringEvaluation measure = ClusteringEvaluationFactory
                .parseFromString(getRepository(),
                        "TransClustFClusteringQualityMeasure",
                        new ClusteringEvaluationParameters());
        double quality = measure.getQualityOfClustering(clustering,
                goldStandard, null).getValue();
        assertEquals(0.8444444444444444, quality, DELTA);
        System.out.println(measure.getName() + " " + quality);

        measure = ClusteringEvaluationFactory.parseFromString(getRepository(),
                "TransClustF2ClusteringQualityMeasure",
                new ClusteringEvaluationParameters());
        quality = measure
                .getQualityOfClustering(clustering, goldStandard, null)
                .getValue();
        assertEquals(0.9027777777777778, quality, DELTA);
        System.out.println(measure.getName() + " " + quality);
    }

    @Test
    public void testAdditionalElementsInClustering()
            throws InstantiationException, IllegalAccessException,
                   RepositoryAlreadyExistsException, InvalidRepositoryException,
                   RepositoryConfigurationException, NoRepositoryFoundException,
                   RegisterException, NoSuchAlgorithmException,
                   RNotAvailableException, RCalculationException,
                   UnknownGoldStandardFormatException,
                   InvalidDataSetFormatException, IOException,
                   InterruptedException, IllegalArgumentException, RException, UnknownProviderException {
        ClustevalBackendServer.logLevel(Level.WARN);
        Clustering goldStandard = new Clustering(this.getRepository(),
                System.currentTimeMillis(), new File(""));
        Cluster gsCluster1 = new Cluster("1");
        gsCluster1.add(new ClusterItem("square1"), 1.0f);
        goldStandard.addCluster(gsCluster1);

        Cluster gsCluster2 = new Cluster("2");
        gsCluster2.add(new ClusterItem("star1"), 1.0f);
        gsCluster2.add(new ClusterItem("star2"), 1.0f);
        gsCluster2.add(new ClusterItem("star3"), 1.0f);
        gsCluster2.add(new ClusterItem("star4"), 1.0f);
        gsCluster2.add(new ClusterItem("star5"), 1.0f);
        gsCluster2.add(new ClusterItem("star6"), 1.0f);
        gsCluster2.add(new ClusterItem("star7"), 1.0f);
        goldStandard.addCluster(gsCluster2);

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
        cluster1.add(new ClusterItem("star7"), 1.0f);
        // additional element that is not contained in goldstandard should be
        // removed and not affect result
        cluster1.add(new ClusterItem("star8"), 1.0f);
        clustering.addCluster(cluster1);

        ClusteringEvaluation measure = ClusteringEvaluationFactory
                .parseFromString(getRepository(),
                        "TransClustFClusteringQualityMeasure",
                        new ClusteringEvaluationParameters());
        double quality = measure.getQualityOfClustering(clustering,
                goldStandard, null).getValue();
        assertEquals(0.8444444444444444, quality, DELTA);
        System.out.println(measure.getName() + " " + quality);

        measure = ClusteringEvaluationFactory.parseFromString(getRepository(),
                "TransClustF2ClusteringQualityMeasure",
                new ClusteringEvaluationParameters());
        quality = measure
                .getQualityOfClustering(clustering, goldStandard, null)
                .getValue();
        assertEquals(0.9027777777777778, quality, DELTA);
        System.out.println(measure.getName() + " " + quality);
    }

    // TODO: find another clustering file
    //@Test
    public void testOverlappingClusters()
            throws InstantiationException, IllegalAccessException, RepositoryAlreadyExistsException,
                   InvalidRepositoryException,
                   RepositoryConfigurationException, NoRepositoryFoundException,
                   RegisterException, NoSuchAlgorithmException,
                   RNotAvailableException, RCalculationException,
                   UnknownGoldStandardFormatException,
                   InvalidDataSetFormatException, IOException,
                   ClusteringParseException, InterruptedException, IllegalArgumentException, RException, UnknownProviderException {
        ClustevalBackendServer.logLevel(Level.WARN);

        DataConfig dataConfig = this.getRepository().getStaticObjectWithName(
                DataConfig.class, "astral_40");

        IGoldStandard goldStandard = dataConfig.getGoldstandardConfig().getGoldstandard();

        Clustering clustering = Clustering
                .parseFromFile(
                        this.getRepository(),
                        new File(
                                "/home/wiwiec/git/clusteval/clusteval/testCaseRepository/results/01_30_2013-21_31_25_tc_vs_DS1/clusters/clusterONE_astral_40.1.results.conv"),
                        false).getSecond();

        clustering.loadIntoMemory();

        ClusteringEvaluation measure = ClusteringEvaluationFactory
                .parseFromString(getRepository(),
                        "TransClustFClusteringQualityMeasure",
                        new ClusteringEvaluationParameters());
        double quality = measure.getQualityOfClustering(clustering,
                goldStandard.getClustering(), dataConfig).getValue();
        System.out.println(measure.getName() + " " + quality);
    }

    @Test
    public void testTwoClusters() throws InstantiationException,
                                         IllegalAccessException, RepositoryAlreadyExistsException,
                                         InvalidRepositoryException,
                                         RepositoryConfigurationException, NoRepositoryFoundException,
                                         RegisterException, NoSuchAlgorithmException,
                                         RNotAvailableException, RCalculationException,
                                         UnknownGoldStandardFormatException,
                                         InvalidDataSetFormatException, IOException,
                                         InterruptedException, IllegalArgumentException, RException, UnknownProviderException {
        ClustevalBackendServer.logLevel(Level.WARN);
        Clustering goldStandard = new Clustering(this.getRepository(),
                System.currentTimeMillis(), new File(""));
        Cluster gsCluster1 = new Cluster("1");
        gsCluster1.add(new ClusterItem("square1"), 1.0f);
        gsCluster1.add(new ClusterItem("star1"), 1.0f);
        gsCluster1.add(new ClusterItem("star2"), 1.0f);
        goldStandard.addCluster(gsCluster1);

        Cluster gsCluster2 = new Cluster("2");
        gsCluster2.add(new ClusterItem("star3"), 1.0f);
        gsCluster2.add(new ClusterItem("star4"), 1.0f);
        gsCluster2.add(new ClusterItem("star5"), 1.0f);
        gsCluster2.add(new ClusterItem("star6"), 1.0f);
        gsCluster2.add(new ClusterItem("star7"), 1.0f);
        goldStandard.addCluster(gsCluster2);

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
        cluster1.add(new ClusterItem("star7"), 1.0f);
        clustering.addCluster(cluster1);

        ClusteringEvaluation measure = ClusteringEvaluationFactory
                .parseFromString(getRepository(),
                        "TransClustFClusteringQualityMeasure",
                        new ClusteringEvaluationParameters());
        double quality = measure.getQualityOfClustering(clustering,
                goldStandard, null).getValue();
        System.out.println(measure.getName() + " " + quality);

        measure = ClusteringEvaluationFactory.parseFromString(getRepository(),
                "TransClustF2ClusteringQualityMeasure",
                new ClusteringEvaluationParameters());
        quality = measure
                .getQualityOfClustering(clustering, goldStandard, null)
                .getValue();
        System.out.println(measure.getName() + " " + quality);
    }

}
