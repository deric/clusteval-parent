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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import ch.qos.logback.classic.Level;
import de.clusteval.cluster.Cluster;
import de.clusteval.cluster.ClusterItem;
import de.clusteval.cluster.Clustering;
import de.clusteval.context.Context;
import de.clusteval.context.UnknownContextException;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetConfig;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.data.dataset.format.ConversionStandardToInputConfiguration;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.dataset.format.InvalidDataSetFormatVersionException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.distance.DistanceMeasure;
import de.clusteval.data.distance.UnknownDistanceMeasureException;
import de.clusteval.data.goldstandard.format.UnknownGoldStandardFormatException;
import de.clusteval.data.preprocessing.DataPreprocessor;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.utils.AbstractClustEvalTest;
import de.clusteval.utils.FormatConversionException;
import de.clusteval.utils.RCalculationException;
import de.clusteval.utils.RNotAvailableException;

/**
 * @author Christian Wiwie
 * 
 */
public class TestSilhouetteValueFuzzyRClusteringQualityMeasure
		extends
			AbstractClustEvalTest {

	static {
		ClustevalBackendServer.logLevel(Level.WARN);
	}

	@Test
	public void testSingleCrispCluster() throws InstantiationException,
			IllegalAccessException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, NoRepositoryFoundException,
			RegisterException, NoSuchAlgorithmException,
			RNotAvailableException, RCalculationException,
			UnknownClusteringQualityMeasureException, InterruptedException {
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
							"SilhouetteValueFuzzyRClusteringQualityMeasure",
							new ClusteringQualityMeasureParameters());
			double quality = measure.getQualityOfClustering(clustering, null,
					null).getValue();
			Assert.assertEquals(-1.0, quality, 0.0);
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

	@Test
	public void testTwoCrispClusters() throws InstantiationException,
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
							new ArrayList<DataPreprocessor>(),
							new ArrayList<DataPreprocessor>()),
					new ConversionStandardToInputConfiguration());
			ds.getInStandardFormat().loadIntoMemory();
			ClusteringQualityMeasure measure = ClusteringQualityMeasure
					.parseFromString(getRepository(),
							"SilhouetteValueFuzzyRClusteringQualityMeasure",
							new ClusteringQualityMeasureParameters());
			ClusteringQualityMeasure measureSil = ClusteringQualityMeasure
					.parseFromString(getRepository(),
							"SilhouetteValueRClusteringQualityMeasure",
							new ClusteringQualityMeasureParameters());
			double quality = measure.getQualityOfClustering(clustering, null,
					dc).getValue();
			double qualitySil = measureSil.getQualityOfClustering(clustering,
					null, dc).getValue();
			ds.getInStandardFormat().unloadFromMemory();
			Assert.assertEquals(qualitySil, quality, 0.0);
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

	@Test
	public void testTwoFuzzyClusters() throws InstantiationException,
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
			cluster1.add(new ClusterItem("id1"), 0.7f);
			cluster1.add(new ClusterItem("id2"), 0.5f);
			cluster1.add(new ClusterItem("id3"), 0.0f);
			clustering.addCluster(cluster1);

			Cluster cluster2 = new Cluster("2");
			cluster1.add(new ClusterItem("id1"), 0.3f);
			cluster1.add(new ClusterItem("id2"), 0.5f);
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
							new ArrayList<DataPreprocessor>(),
							new ArrayList<DataPreprocessor>()),
					new ConversionStandardToInputConfiguration());
			ds.getInStandardFormat().loadIntoMemory();
			ClusteringQualityMeasure measure = ClusteringQualityMeasure
					.parseFromString(getRepository(),
							"SilhouetteValueFuzzyRClusteringQualityMeasure",
							new ClusteringQualityMeasureParameters());
			double quality = measure.getQualityOfClustering(clustering, null,
					dc).getValue();
			ds.getInStandardFormat().unloadFromMemory();
			Assert.assertEquals(0.014446969720531404, quality, 0.0);
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

	@Test
	public void testTwoFuzzyAlphaZeroClusters() throws InstantiationException,
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
			cluster1.add(new ClusterItem("id1"), 0.7f);
			cluster1.add(new ClusterItem("id2"), 0.5f);
			cluster1.add(new ClusterItem("id3"), 0.0f);
			clustering.addCluster(cluster1);

			Cluster cluster2 = new Cluster("2");
			cluster1.add(new ClusterItem("id1"), 0.3f);
			cluster1.add(new ClusterItem("id2"), 0.5f);
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
							new ArrayList<DataPreprocessor>(),
							new ArrayList<DataPreprocessor>()),
					new ConversionStandardToInputConfiguration());
			ds.getInStandardFormat().loadIntoMemory();

			ClusteringQualityMeasureParameters params = new ClusteringQualityMeasureParameters();
			params.put("alpha", "0.0");

			ClusteringQualityMeasure measure = ClusteringQualityMeasure
					.parseFromString(getRepository(),
							"SilhouetteValueFuzzyRClusteringQualityMeasure",
							params);
			ClusteringQualityMeasure measureSil = ClusteringQualityMeasure
					.parseFromString(getRepository(),
							"SilhouetteValueRClusteringQualityMeasure",
							new ClusteringQualityMeasureParameters());
			double quality = measure.getQualityOfClustering(clustering, null,
					dc).getValue();
			double qualitySil = measureSil.getQualityOfClustering(clustering,
					null, dc).getValue();
			ds.getInStandardFormat().unloadFromMemory();
			Assert.assertEquals(qualitySil, quality, 0.0);
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
