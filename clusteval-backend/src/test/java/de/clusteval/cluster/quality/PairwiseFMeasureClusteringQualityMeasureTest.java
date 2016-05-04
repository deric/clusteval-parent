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

import de.clusteval.api.cluster.ClusteringQualityMeasure;
import de.clusteval.api.cluster.ClusteringEvaluationParameters;
import ch.qos.logback.classic.Level;
import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RCalculationException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RException;
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
public class PairwiseFMeasureClusteringQualityMeasureTest
        extends
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
                   RNotAvailableException, RCalculationException, InterruptedException, RException {
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

            ClusteringQualityMeasure measure = ClusteringQualityMeasure
                    .parseFromString(getRepository(),
                            "PairwiseF2ClusteringQualityMeasure",
                            new ClusteringEvaluationParameters());
            double quality = measure.getQualityOfClustering(clustering,
                    goldStandard, null).getValue();
            System.out.println(measure.getAlias() + " " + quality);
        } catch (IllegalArgumentException | InvalidDataSetFormatVersionException |
                 UnknownClusteringQualityMeasureException e) {
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
                   RNotAvailableException, RCalculationException, InterruptedException, RException {
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
            cluster1.add(new ClusterItem("star7"), 1.0f);
            clustering.addCluster(cluster1);

            ClusteringQualityMeasure measure = ClusteringQualityMeasure
                    .parseFromString(getRepository(),
                            "PairwiseF2ClusteringQualityMeasure",
                            new ClusteringEvaluationParameters());
            double quality = measure.getQualityOfClustering(clustering,
                    goldStandard, null).getValue();
            System.out.println(measure.getAlias() + " " + quality);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidDataSetFormatVersionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnknownClusteringQualityMeasureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
