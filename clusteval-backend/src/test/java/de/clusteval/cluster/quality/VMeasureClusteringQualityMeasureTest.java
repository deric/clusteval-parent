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
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
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
import java.security.NoSuchAlgorithmException;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class VMeasureClusteringQualityMeasureTest extends AbstractClustEvalTest {

    @Test
    public void test()
            throws InstantiationException, IllegalAccessException,
                   RepositoryAlreadyExistsException, InvalidRepositoryException,
                   RepositoryConfigNotFoundException,
                   RepositoryConfigurationException, NoRepositoryFoundException,
                   RegisterException, NoSuchAlgorithmException,
                   RNotAvailableException, RCalculationException, InterruptedException, RException, UnknownProviderException {
        try {
            Clustering goldStandard = new Clustering(this.getRepository(),
                    System.currentTimeMillis(), new File(""));
            Cluster gsCluster1 = new Cluster("1");
            gsCluster1.add(new ClusterItem("square1"), 1.0f);
            gsCluster1.add(new ClusterItem("square2"), 1.0f);
            gsCluster1.add(new ClusterItem("square3"), 1.0f);
            gsCluster1.add(new ClusterItem("square4"), 1.0f);
            gsCluster1.add(new ClusterItem("square5"), 1.0f);
            goldStandard.addCluster(gsCluster1);

            Cluster gsCluster2 = new Cluster("2");
            gsCluster2.add(new ClusterItem("star1"), 1.0f);
            gsCluster2.add(new ClusterItem("star2"), 1.0f);
            gsCluster2.add(new ClusterItem("star3"), 1.0f);
            gsCluster2.add(new ClusterItem("star4"), 1.0f);
            gsCluster2.add(new ClusterItem("star5"), 1.0f);
            goldStandard.addCluster(gsCluster2);

            Cluster gsCluster3 = new Cluster("3");
            gsCluster3.add(new ClusterItem("circle1"), 1.0f);
            gsCluster3.add(new ClusterItem("circle2"), 1.0f);
            gsCluster3.add(new ClusterItem("circle3"), 1.0f);
            gsCluster3.add(new ClusterItem("circle4"), 1.0f);
            gsCluster3.add(new ClusterItem("circle5"), 1.0f);
            goldStandard.addCluster(gsCluster3);

            Clustering clustering = new Clustering(this.getRepository(),
                    System.currentTimeMillis(), new File(""));
            Cluster cluster1 = new Cluster("1");
            cluster1.add(new ClusterItem("square1"), 1.0f);
            cluster1.add(new ClusterItem("square2"), 1.0f);
            cluster1.add(new ClusterItem("square3"), 1.0f);
            cluster1.add(new ClusterItem("circle1"), 1.0f);
            cluster1.add(new ClusterItem("star1"), 1.0f);
            clustering.addCluster(cluster1);

            Cluster cluster2 = new Cluster("2");
            cluster2.add(new ClusterItem("star2"), 1.0f);
            cluster2.add(new ClusterItem("star3"), 1.0f);
            cluster2.add(new ClusterItem("star4"), 1.0f);
            cluster2.add(new ClusterItem("square4"), 1.0f);
            cluster2.add(new ClusterItem("circle2"), 1.0f);
            clustering.addCluster(cluster2);

            Cluster cluster3 = new Cluster("3");
            cluster3.add(new ClusterItem("circle3"), 1.0f);
            cluster3.add(new ClusterItem("circle4"), 1.0f);
            cluster3.add(new ClusterItem("circle5"), 1.0f);
            cluster3.add(new ClusterItem("square5"), 1.0f);
            cluster3.add(new ClusterItem("star5"), 1.0f);
            clustering.addCluster(cluster3);

            ClusteringEvaluation measure = ClusteringEvaluationFactory
                    .parseFromString(getRepository(),
                            "VMeasureClusteringQualityMeasure",
                            new ClusteringEvaluationParameters());
            double quality = measure.getQualityOfClustering(clustering,
                    goldStandard, null).getValue();
            System.out.println("V-Measure: " + quality);
            measure = ClusteringEvaluationFactory.parseFromString(getRepository(),
                    "TransClustFClusteringQualityMeasure",
                    new ClusteringEvaluationParameters());
            quality = measure.getQualityOfClustering(clustering, goldStandard,
                    null).getValue();
            System.out.println("F-Measure: " + quality);
        } catch (IllegalArgumentException | InvalidDataSetFormatException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() throws InstantiationException, IllegalAccessException,
                               RepositoryAlreadyExistsException, InvalidRepositoryException,
                               RepositoryConfigNotFoundException,
                               RepositoryConfigurationException, NoRepositoryFoundException,
                               RegisterException, NoSuchAlgorithmException,
                               RNotAvailableException, RCalculationException, InterruptedException, RException, UnknownProviderException {
        try {
            Clustering goldStandard = new Clustering(this.getRepository(),
                    System.currentTimeMillis(), new File(""));
            Cluster gsCluster1 = new Cluster("1");
            gsCluster1.add(new ClusterItem("square1"), 1.0f);
            gsCluster1.add(new ClusterItem("square2"), 1.0f);
            gsCluster1.add(new ClusterItem("square3"), 1.0f);
            gsCluster1.add(new ClusterItem("square4"), 1.0f);
            gsCluster1.add(new ClusterItem("square5"), 1.0f);
            goldStandard.addCluster(gsCluster1);

            Cluster gsCluster2 = new Cluster("2");
            gsCluster2.add(new ClusterItem("star1"), 1.0f);
            gsCluster2.add(new ClusterItem("star2"), 1.0f);
            gsCluster2.add(new ClusterItem("star3"), 1.0f);
            gsCluster2.add(new ClusterItem("star4"), 1.0f);
            gsCluster2.add(new ClusterItem("star5"), 1.0f);
            goldStandard.addCluster(gsCluster2);

            Cluster gsCluster3 = new Cluster("3");
            gsCluster3.add(new ClusterItem("circle1"), 1.0f);
            gsCluster3.add(new ClusterItem("circle2"), 1.0f);
            gsCluster3.add(new ClusterItem("circle3"), 1.0f);
            gsCluster3.add(new ClusterItem("circle4"), 1.0f);
            gsCluster3.add(new ClusterItem("circle5"), 1.0f);
            goldStandard.addCluster(gsCluster3);

            Clustering clustering = new Clustering(this.getRepository(),
                    System.currentTimeMillis(), new File(""));
            Cluster cluster1 = new Cluster("1");
            cluster1.add(new ClusterItem("square1"), 1.0f);
            cluster1.add(new ClusterItem("square2"), 1.0f);
            cluster1.add(new ClusterItem("square3"), 1.0f);
            cluster1.add(new ClusterItem("circle1"), 1.0f);
            cluster1.add(new ClusterItem("circle2"), 1.0f);
            clustering.addCluster(cluster1);

            Cluster cluster2 = new Cluster("2");
            cluster2.add(new ClusterItem("star1"), 1.0f);
            cluster2.add(new ClusterItem("star2"), 1.0f);
            cluster2.add(new ClusterItem("star3"), 1.0f);
            cluster2.add(new ClusterItem("square4"), 1.0f);
            cluster2.add(new ClusterItem("square5"), 1.0f);
            clustering.addCluster(cluster2);

            Cluster cluster3 = new Cluster("3");
            cluster3.add(new ClusterItem("circle3"), 1.0f);
            cluster3.add(new ClusterItem("circle4"), 1.0f);
            cluster3.add(new ClusterItem("circle5"), 1.0f);
            cluster3.add(new ClusterItem("star4"), 1.0f);
            cluster3.add(new ClusterItem("star5"), 1.0f);
            clustering.addCluster(cluster3);

            ClusteringEvaluation measure = ClusteringEvaluationFactory
                    .parseFromString(getRepository(),
                            "VMeasureClusteringQualityMeasure",
                            new ClusteringEvaluationParameters());
            double quality = measure.getQualityOfClustering(clustering,
                    goldStandard, null).getValue();
            System.out.println("V-Measure: " + quality);
            measure = ClusteringEvaluationFactory.parseFromString(getRepository(),
                    "TransClustFClusteringQualityMeasure",
                    new ClusteringEvaluationParameters());
            quality = measure.getQualityOfClustering(clustering, goldStandard,
                    null).getValue();
            System.out.println("F-Measure: " + quality);
        } catch (IllegalArgumentException | InvalidDataSetFormatException e) {
            e.printStackTrace();
        }
    }

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
                   RNotAvailableException, RCalculationException, InterruptedException, RException, UnknownProviderException {
        try {
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
                            "VMeasureClusteringQualityMeasure",
                            new ClusteringEvaluationParameters());
            double quality = measure.getQualityOfClustering(clustering,
                    goldStandard, null).getValue();
            System.out.println(measure.getName() + " " + quality);
        } catch (IllegalArgumentException | InvalidDataSetFormatException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSingleCluster2()
            throws InstantiationException,
                   IllegalAccessException, RepositoryAlreadyExistsException,
                   InvalidRepositoryException, RepositoryConfigNotFoundException,
                   RepositoryConfigurationException, NoRepositoryFoundException,
                   RegisterException, NoSuchAlgorithmException,
                   RNotAvailableException, RCalculationException, InterruptedException, RException, UnknownProviderException {
        try {
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
            clustering.addCluster(cluster1);

            Cluster cluster2 = new Cluster("2");
            cluster2.add(new ClusterItem("star7"), 1.0f);
            clustering.addCluster(cluster2);

            ClusteringEvaluation measure = ClusteringEvaluationFactory
                    .parseFromString(getRepository(),
                            "VMeasureClusteringQualityMeasure",
                            new ClusteringEvaluationParameters());
            double quality = measure.getQualityOfClustering(clustering,
                    goldStandard, null).getValue();
            System.out.println(measure.getName() + " " + quality);
        } catch (IllegalArgumentException | InvalidDataSetFormatException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSingletonCluster()
            throws InstantiationException,
                   IllegalAccessException, RepositoryAlreadyExistsException,
                   InvalidRepositoryException, RepositoryConfigNotFoundException,
                   RepositoryConfigurationException, NoRepositoryFoundException,
                   RegisterException, NoSuchAlgorithmException,
                   RNotAvailableException, RCalculationException, InterruptedException, RException, UnknownProviderException {
        try {
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
            clustering.addCluster(cluster1);

            Cluster cluster2 = new Cluster("2");
            cluster2.add(new ClusterItem("star7"), 1.0f);
            clustering.addCluster(cluster2);

            ClusteringEvaluation measure = ClusteringEvaluationFactory
                    .parseFromString(getRepository(),
                            "VMeasureClusteringQualityMeasure",
                            new ClusteringEvaluationParameters());
            double quality = measure.getQualityOfClustering(clustering,
                    goldStandard, null).getValue();
            System.out.println(measure.getName() + " " + quality);
        } catch (IllegalArgumentException | InvalidDataSetFormatException e) {
            e.printStackTrace();
        }
    }
}
