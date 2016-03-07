/*******************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.clusteval.cluster.quality;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.Test;

import ch.qos.logback.classic.Level;
import de.clusteval.cluster.Cluster;
import de.clusteval.cluster.ClusterItem;
import de.clusteval.cluster.Clustering;
import de.clusteval.cluster.ClusteringParseException;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.format.InvalidDataSetFormatVersionException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.goldstandard.GoldStandard;
import de.clusteval.data.goldstandard.format.UnknownGoldStandardFormatException;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.utils.AbstractClustEvalTest;
import de.clusteval.utils.RCalculationException;
import de.clusteval.utils.RNotAvailableException;

/**
 * @author Christian Wiwie
 * 
 */
public class TestFMeasureClusteringQualityMeasure extends AbstractClustEvalTest {

	static {
		ClustevalBackendServer.logLevel(Level.WARN);
	}

	@Test
	public void testSingleCluster() throws InstantiationException,
			IllegalAccessException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, NoRepositoryFoundException,
			RegisterException, NoSuchAlgorithmException,
			RNotAvailableException, RCalculationException,
			UnknownClusteringQualityMeasureException,
			UnknownGoldStandardFormatException, UnknownDataSetFormatException,
			InvalidDataSetFormatVersionException, IOException,
			InterruptedException {
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
						"TransClustFClusteringQualityMeasure",
						new ClusteringQualityMeasureParameters());
		double quality = measure.getQualityOfClustering(clustering,
				goldStandard, null).getValue();
		System.out.println(measure.getAlias() + " " + quality);

		measure = ClusteringQualityMeasure.parseFromString(getRepository(),
				"TransClustF2ClusteringQualityMeasure",
				new ClusteringQualityMeasureParameters());
		quality = measure
				.getQualityOfClustering(clustering, goldStandard, null)
				.getValue();
		System.out.println(measure.getAlias() + " " + quality);
	}

	@Test
	public void testSingleCluster2() throws InstantiationException,
			IllegalAccessException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, NoRepositoryFoundException,
			RegisterException, NoSuchAlgorithmException,
			RNotAvailableException, RCalculationException,
			UnknownClusteringQualityMeasureException,
			UnknownGoldStandardFormatException, UnknownDataSetFormatException,
			InvalidDataSetFormatVersionException, IOException,
			InterruptedException {
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
						"TransClustFClusteringQualityMeasure",
						new ClusteringQualityMeasureParameters());
		double quality = measure.getQualityOfClustering(clustering,
				goldStandard, null).getValue();
		Assert.assertEquals(0.8444444444444444, quality);
		System.out.println(measure.getAlias() + " " + quality);

		measure = ClusteringQualityMeasure.parseFromString(getRepository(),
				"TransClustF2ClusteringQualityMeasure",
				new ClusteringQualityMeasureParameters());
		quality = measure
				.getQualityOfClustering(clustering, goldStandard, null)
				.getValue();
		Assert.assertEquals(0.9027777777777778, quality);
		System.out.println(measure.getAlias() + " " + quality);
	}

	@Test
	public void testAdditionalElementsInGs() throws InstantiationException,
			IllegalAccessException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, NoRepositoryFoundException,
			RegisterException, NoSuchAlgorithmException,
			RNotAvailableException, RCalculationException,
			UnknownClusteringQualityMeasureException,
			UnknownGoldStandardFormatException, UnknownDataSetFormatException,
			InvalidDataSetFormatVersionException, IOException,
			InterruptedException {
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

		ClusteringQualityMeasure measure = ClusteringQualityMeasure
				.parseFromString(getRepository(),
						"TransClustFClusteringQualityMeasure",
						new ClusteringQualityMeasureParameters());
		double quality = measure.getQualityOfClustering(clustering,
				goldStandard, null).getValue();
		Assert.assertEquals(0.8444444444444444, quality);
		System.out.println(measure.getAlias() + " " + quality);

		measure = ClusteringQualityMeasure.parseFromString(getRepository(),
				"TransClustF2ClusteringQualityMeasure",
				new ClusteringQualityMeasureParameters());
		quality = measure
				.getQualityOfClustering(clustering, goldStandard, null)
				.getValue();
		Assert.assertEquals(0.9027777777777778, quality);
		System.out.println(measure.getAlias() + " " + quality);
	}

	@Test
	public void testAdditionalElementsInClustering()
			throws InstantiationException, IllegalAccessException,
			RepositoryAlreadyExistsException, InvalidRepositoryException,
			RepositoryConfigNotFoundException,
			RepositoryConfigurationException, NoRepositoryFoundException,
			RegisterException, NoSuchAlgorithmException,
			RNotAvailableException, RCalculationException,
			UnknownClusteringQualityMeasureException,
			UnknownGoldStandardFormatException, UnknownDataSetFormatException,
			InvalidDataSetFormatVersionException, IOException,
			InterruptedException {
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

		ClusteringQualityMeasure measure = ClusteringQualityMeasure
				.parseFromString(getRepository(),
						"TransClustFClusteringQualityMeasure",
						new ClusteringQualityMeasureParameters());
		double quality = measure.getQualityOfClustering(clustering,
				goldStandard, null).getValue();
		Assert.assertEquals(0.8444444444444444, quality);
		System.out.println(measure.getAlias() + " " + quality);

		measure = ClusteringQualityMeasure.parseFromString(getRepository(),
				"TransClustF2ClusteringQualityMeasure",
				new ClusteringQualityMeasureParameters());
		quality = measure
				.getQualityOfClustering(clustering, goldStandard, null)
				.getValue();
		Assert.assertEquals(0.9027777777777778, quality);
		System.out.println(measure.getAlias() + " " + quality);
	}

	// TODO: find another clustering file
	//@Test
	public void testOverlappingClusters() throws InstantiationException,
			IllegalAccessException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, NoRepositoryFoundException,
			RegisterException, NoSuchAlgorithmException,
			RNotAvailableException, RCalculationException,
			UnknownClusteringQualityMeasureException,
			UnknownGoldStandardFormatException, UnknownDataSetFormatException,
			InvalidDataSetFormatVersionException, IOException,
			ClusteringParseException, InterruptedException {
		ClustevalBackendServer.logLevel(Level.WARN);

		DataConfig dataConfig = this.getRepository().getStaticObjectWithName(
				DataConfig.class, "astral_40");

		GoldStandard goldStandard = dataConfig.getGoldstandardConfig()
				.getGoldstandard();

		Clustering clustering = Clustering
				.parseFromFile(
						this.getRepository(),
						new File(
								"/home/wiwiec/git/clusteval/clusteval/testCaseRepository/results/01_30_2013-21_31_25_tc_vs_DS1/clusters/clusterONE_astral_40.1.results.conv"),
						false).getSecond();

		clustering.loadIntoMemory();

		ClusteringQualityMeasure measure = ClusteringQualityMeasure
				.parseFromString(getRepository(),
						"TransClustFClusteringQualityMeasure",
						new ClusteringQualityMeasureParameters());
		double quality = measure.getQualityOfClustering(clustering,
				goldStandard.getClustering(), dataConfig).getValue();
		System.out.println(measure.getAlias() + " " + quality);
	}

	@Test
	public void testTwoClusters() throws InstantiationException,
			IllegalAccessException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, NoRepositoryFoundException,
			RegisterException, NoSuchAlgorithmException,
			RNotAvailableException, RCalculationException,
			UnknownClusteringQualityMeasureException,
			UnknownGoldStandardFormatException, UnknownDataSetFormatException,
			InvalidDataSetFormatVersionException, IOException,
			InterruptedException {
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
	
		ClusteringQualityMeasure measure = ClusteringQualityMeasure
				.parseFromString(getRepository(),
						"TransClustFClusteringQualityMeasure",
						new ClusteringQualityMeasureParameters());
		double quality = measure.getQualityOfClustering(clustering,
				goldStandard, null).getValue();
		System.out.println(measure.getAlias() + " " + quality);
	
		measure = ClusteringQualityMeasure.parseFromString(getRepository(),
				"TransClustF2ClusteringQualityMeasure",
				new ClusteringQualityMeasureParameters());
		quality = measure
				.getQualityOfClustering(clustering, goldStandard, null)
				.getValue();
		System.out.println(measure.getAlias() + " " + quality);
	}

}
