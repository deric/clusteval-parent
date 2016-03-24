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
package de.clusteval.utils.plot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.rosuda.REngine.REngineException;

import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.InvalidOptimizationParameterException;
import de.clusteval.cluster.paramOptimization.UnknownParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.context.Context;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.data.DataConfig;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetConfig;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.data.dataset.format.ConversionStandardToInputConfiguration;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.data.dataset.type.UnknownDataSetTypeException;
import de.clusteval.data.distance.DistanceMeasure;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.data.preprocessing.DataPreprocessor;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.data.statistics.UnknownDataStatisticException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.run.RunException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.clusteval.utils.AbstractClustEvalTest;
import de.clusteval.api.exceptions.FormatConversionException;
import de.clusteval.api.r.RNotAvailableException;

/**
 * @author Christian Wiwie
 * 
 */
public class TestPlotter extends AbstractClustEvalTest {

	@Test
	public void testIsoMDS() throws RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, UnknownDataSetFormatException,
			RegisterException, UnknownDataSetTypeException,
			UnknownDistanceMeasureException,
			InvalidDataSetFormatVersionException, IllegalArgumentException,
			IOException, REngineException, FormatConversionException,
			DataSetNotFoundException, DataSetConfigurationException,
			NoDataSetException, NoRepositoryFoundException,
			UnknownContextException, RNotAvailableException,
			InterruptedException, GoldStandardNotFoundException,
			GoldStandardConfigurationException, DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		Context context = Context.parseFromString(getRepository(),
				"ClusteringContext");

		DataSet ds = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/results/04_07_2013-14_41_00_paper_run_synthetic/inputs/TransClust_2_synthetic_cassini250/synthetic/cassini250"));

		File targetFile = new File(
				"testCaseRepository/results/04_07_2013-14_41_00_paper_run_synthetic/inputs/TransClust_2_synthetic_cassini250/synthetic/cassini250.strip.isoMDS");
		if (targetFile.exists())
			targetFile.delete();

		ds = ds.preprocessAndConvertTo(
				context,
				DataSetFormat.parseFromString(getRepository(),
						"SimMatrixDataSetFormat"),
				new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());

		DataSetConfig dsc = new DataSetConfig(
				getRepository(),
				System.currentTimeMillis(),
				new File(
						"testCaseRepository/results/04_07_2013-14_41_00_paper_run_synthetic/configs/synthetic_cassini250.dsconfig"),
				ds, new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());

		DataConfig dc = new DataConfig(
				getRepository(),
				System.currentTimeMillis(),
				new File(
						"testCaseRepository/results/04_07_2013-14_41_00_paper_run_synthetic/configs/synthetic_cassini250.dataconfig"),
				dsc, null);

		Plotter.assessAndWriteIsoMDSCoordinates(dc);
		Assert.assertTrue(targetFile.exists());
	}

	@Test
	public void testPCA() throws RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, UnknownDataSetFormatException,
			RegisterException, UnknownDataSetTypeException,
			UnknownDistanceMeasureException,
			InvalidDataSetFormatVersionException, IllegalArgumentException,
			IOException, REngineException, FormatConversionException,
			DataSetNotFoundException, DataSetConfigurationException,
			NoDataSetException, NoRepositoryFoundException,
			UnknownContextException, RNotAvailableException,
			InterruptedException, GoldStandardNotFoundException,
			GoldStandardConfigurationException, DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		Context context = Context.parseFromString(getRepository(),
				"ClusteringContext");

		DataSet ds = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/results/04_07_2013-14_41_00_paper_run_synthetic/inputs/TransClust_2_synthetic_cassini250/synthetic/cassini250"));

		File targetFile = new File(
				"testCaseRepository/results/04_07_2013-14_41_00_paper_run_synthetic/inputs/TransClust_2_synthetic_cassini250/synthetic/cassini250.strip.PCA");
		if (targetFile.exists())
			targetFile.delete();

		ds = ds.preprocessAndConvertTo(
				context,
				DataSetFormat.parseFromString(getRepository(),
						"SimMatrixDataSetFormat"),
				new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());

		DataSetConfig dsc = new DataSetConfig(
				getRepository(),
				System.currentTimeMillis(),
				new File(
						"testCaseRepository/results/04_07_2013-14_41_00_paper_run_synthetic/configs/synthetic_cassini250.dsconfig"),
				ds, new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());

		DataConfig dc = new DataConfig(
				getRepository(),
				System.currentTimeMillis(),
				new File(
						"testCaseRepository/results/04_07_2013-14_41_00_paper_run_synthetic/configs/synthetic_cassini250.dataconfig"),
				dsc, null);

		Plotter.assessAndWritePCACoordinates(dc);
		Assert.assertTrue(targetFile.exists());
	}
}
