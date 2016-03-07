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
package de.clusteval.run.result.postprocessing;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

import de.wiwie.wiutils.utils.Pair;
import de.clusteval.cluster.Cluster;
import de.clusteval.cluster.ClusterItem;
import de.clusteval.cluster.Clustering;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.InvalidOptimizationParameterException;
import de.clusteval.cluster.paramOptimization.UnknownParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.context.IncompatibleContextException;
import de.clusteval.context.UnknownContextException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetAttributeFilterer;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.DataSetNotFoundException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.NoDataSetException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.dataset.type.UnknownDataSetTypeException;
import de.clusteval.data.distance.UnknownDistanceMeasureException;
import de.clusteval.data.goldstandard.GoldStandardConfigNotFoundException;
import de.clusteval.data.goldstandard.GoldStandardConfigurationException;
import de.clusteval.data.goldstandard.GoldStandardNotFoundException;
import de.clusteval.data.preprocessing.DataPreprocessor;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.statistics.UnknownDataStatisticException;
import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.program.NoOptimizableProgramParameterException;
import de.clusteval.program.ParameterSet;
import de.clusteval.program.UnknownParameterType;
import de.clusteval.program.UnknownProgramParameterException;
import de.clusteval.program.UnknownProgramTypeException;
import de.clusteval.program.r.UnknownRProgramException;
import de.clusteval.run.RunException;
import de.clusteval.run.result.format.UnknownRunResultFormatException;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.clusteval.utils.AbstractClustEvalTest;

/**
 * @author Christian Wiwie
 * 
 */
public class TestFuzzyCoefficientThresholdRunResultPostprocessor
		extends
			AbstractClustEvalTest {

	@Test
	public void test() throws RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, DataSetNotFoundException,
			UnknownDataSetFormatException, DataSetConfigurationException,
			NoDataSetException, NumberFormatException, RegisterException,
			NoRepositoryFoundException, UnknownDataSetTypeException,
			UnknownDataPreprocessorException, IOException,
			InterruptedException, GoldStandardNotFoundException,
			GoldStandardConfigurationException, DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException {

		Clustering clustering = new Clustering(this.getRepository(),
				System.currentTimeMillis(), new File(""));
		Cluster cluster1 = new Cluster("1");
		cluster1.add(new ClusterItem("id1"), 0.7f);
		cluster1.add(new ClusterItem("id2"), 0.5f);
		cluster1.add(new ClusterItem("id3"), 0.0f);
		clustering.addCluster(cluster1);

		Cluster cluster2 = new Cluster("2");
		cluster2.add(new ClusterItem("id1"), 0.3f);
		cluster2.add(new ClusterItem("id2"), 0.5f);
		cluster2.add(new ClusterItem("id3"), 1.0f);
		clustering.addCluster(cluster2);

		Clustering expected = new Clustering(this.getRepository(),
				System.currentTimeMillis(), new File(""));
		Cluster expectedCluster1 = new Cluster("1");
		expectedCluster1.add(new ClusterItem("id1"), 1.0f);
		expectedCluster1.add(new ClusterItem("id2"), 0.5f);
		expected.addCluster(expectedCluster1);

		Cluster expectedCluster2 = new Cluster("2");
		expectedCluster2.add(new ClusterItem("id2"), 0.5f);
		expectedCluster2.add(new ClusterItem("id3"), 1.0f);
		expected.addCluster(expectedCluster2);

		RunResultPostprocessorParameters params = new RunResultPostprocessorParameters();
		params.put("threshold", "0.5");

		RunResultPostprocessor proc = RunResultPostprocessor.parseFromString(
				this.getRepository(),
				"FuzzyCoefficientThresholdRunResultPostprocessor", params);

		Clustering postprocessed = proc.postprocess(clustering);
		Assert.assertEquals(expected, postprocessed);
	}

	// TODO: choose another file and add asserts
	//@Test
	public void test2() throws IOException,
			UnknownRunResultPostprocessorException {
		Clustering clustering = Clustering
				.parseFromFile(
						null,
						new File(
								"testCaseRepository/results/04_01_2015-10_19_51_martin_spearman_nonabsolute_fanny_50cl_threshold/clusters/Fanny_Clustering_3membexp_50cl_martin_spearman.5.results.conv")
								.getAbsoluteFile(), false).getSecond();

		RunResultPostprocessorParameters params = new RunResultPostprocessorParameters();
		params.put("threshold", "0.5");

		RunResultPostprocessor proc = RunResultPostprocessor.parseFromString(
				this.getRepository(),
				"FuzzyCoefficientThresholdRunResultPostprocessor", params);

		Clustering postprocessed = proc.postprocess(clustering);
		System.out.println(postprocessed);

	}
}
