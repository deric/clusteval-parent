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
package de.clusteval.data.dataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import junit.framework.Assert;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.InvalidOptimizationParameterException;
import de.clusteval.cluster.paramOptimization.UnknownParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.context.IncompatibleContextException;
import de.clusteval.context.UnknownContextException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.data.dataset.format.ConversionStandardToInputConfiguration;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.dataset.type.UnknownDataSetTypeException;
import de.clusteval.data.distance.DistanceMeasure;
import de.clusteval.data.distance.UnknownDistanceMeasureException;
import de.clusteval.data.goldstandard.GoldStandardConfigNotFoundException;
import de.clusteval.data.goldstandard.GoldStandardConfigurationException;
import de.clusteval.data.goldstandard.GoldStandardNotFoundException;
import de.clusteval.data.preprocessing.DataPreprocessor;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.data.statistics.UnknownDataStatisticException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.RepositoryRemoveEvent;
import de.clusteval.framework.repository.RepositoryReplaceEvent;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.program.NoOptimizableProgramParameterException;
import de.clusteval.program.UnknownParameterType;
import de.clusteval.program.UnknownProgramParameterException;
import de.clusteval.program.UnknownProgramTypeException;
import de.clusteval.program.r.UnknownRProgramException;
import de.clusteval.run.RunException;
import de.clusteval.run.result.format.UnknownRunResultFormatException;
import de.clusteval.run.result.postprocessing.UnknownRunResultPostprocessorException;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.clusteval.utils.AbstractClustEvalTest;
import de.clusteval.utils.StubRepositoryObject;

/**
 * @author Christian Wiwie
 * 
 */
public class TestDataSetConfig extends AbstractClustEvalTest {

	/**
	 * Test method for {@link data.dataset.DataSetConfig#register()}.
	 * 
	 * @throws DataSetNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetConfigNotFoundException
	 * @throws UnknownDistanceMeasureException
	 * @throws RegisterException
	 * @throws UnknownDataPreprocessorException
	 * @throws NumberFormatException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws FileNotFoundException
	 * @throws UnknownContextException
	 * @throws ConfigurationException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	public void testRegister() throws DataSetConfigurationException,
			NoRepositoryFoundException, DataSetNotFoundException,
			UnknownDataSetFormatException, DataSetConfigNotFoundException,
			UnknownDistanceMeasureException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			NumberFormatException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, FileNotFoundException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSetConfig.class,
						new File(
								"testCaseRepository/data/datasets/configs/astral_1.dsconfig")
								.getAbsoluteFile());
		Assert.assertEquals(this.repositoryObject, this.getRepository()
				.getRegisteredObject((DataSetConfig) this.repositoryObject));

		// adding a DataSetConfig equal to another one already registered
		// does
		// not register the second object.
		this.repositoryObject = new DataSetConfig(
				(DataSetConfig) this.repositoryObject);
		Assert.assertEquals(
				this.getRepository().getRegisteredObject(
						(DataSetConfig) this.repositoryObject),
				this.repositoryObject);
		Assert.assertFalse(this.getRepository().getRegisteredObject(
				(DataSetConfig) this.repositoryObject) == this.repositoryObject);
	}

	/**
	 * Test method for {@link data.dataset.DataSetConfig#unregister()} .
	 * 
	 * @throws DataSetNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 * @throws UnknownDistanceMeasureException
	 * @throws RegisterException
	 * @throws UnknownDataPreprocessorException
	 * @throws NumberFormatException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws FileNotFoundException
	 * @throws UnknownContextException
	 * @throws ConfigurationException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	public void testUnregister() throws DataSetConfigurationException,
			NoRepositoryFoundException, DataSetNotFoundException,
			UnknownDataSetFormatException, DataSetConfigNotFoundException,
			UnknownDistanceMeasureException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			NumberFormatException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, FileNotFoundException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSetConfig.class,
						new File(
								"testCaseRepository/data/datasets/configs/astral_1.dsconfig")
								.getAbsoluteFile());
		Assert.assertEquals(this.repositoryObject, this.getRepository()
				.getRegisteredObject((DataSetConfig) this.repositoryObject));
		this.repositoryObject.unregister();
		// is not registered anymore
		Assert.assertTrue(this.getRepository().getRegisteredObject(
				(DataSetConfig) this.repositoryObject) == null);
	}

	/**
	 * Test method for
	 * {@link data.dataset.DataSetConfig#notify(utils.RepositoryEvent)} .
	 * 
	 * @throws DataSetNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetConfigNotFoundException
	 * @throws UnknownDistanceMeasureException
	 * @throws RegisterException
	 * @throws UnknownDataPreprocessorException
	 * @throws NumberFormatException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws FileNotFoundException
	 * @throws UnknownContextException
	 * @throws ConfigurationException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testNotifyRepositoryEvent() throws NoRepositoryFoundException,
			DataSetNotFoundException, DataSetConfigurationException,
			UnknownDataSetFormatException, DataSetConfigNotFoundException,
			UnknownDistanceMeasureException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			NumberFormatException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, FileNotFoundException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {

		/*
		 * REPLACE
		 */

		/*
		 * First check, whether listeners of DataSetconfigs are notified
		 * correctly when the DataSetconfig is replaced
		 */
		DataSetConfig gsConfig = Parser
				.parseFromFile(
						DataSetConfig.class,
						new File(
								"testCaseRepository/data/datasets/configs/astral_1.dsconfig")
								.getAbsoluteFile());
		StubRepositoryObject child = new StubRepositoryObject(getRepository(),
				false, System.currentTimeMillis(), new File(
						"testCaseRepository/Bla"));
		gsConfig.addListener(child);

		DataSetConfig gsConfig2 = new DataSetConfig(gsConfig);

		gsConfig.notify(new RepositoryReplaceEvent(gsConfig, gsConfig2));
		Assert.assertTrue(child.notified);

		/*
		 * Now check, whether DataSet configs update their references correctly,
		 * when their DataSet is replaced
		 */
		RelativeDataSet gs = (RelativeDataSet) (gsConfig.getDataSet());
		RelativeDataSet gs2 = new RelativeDataSet(gs);

		gsConfig.notify(new RepositoryReplaceEvent(gs, gs2));

		Assert.assertFalse(gsConfig.getDataSet() == gs);
		Assert.assertTrue(gsConfig.getDataSet() == gs2);

		/*
		 * REMOVE
		 */

		/*
		 * First check, whether listeners of DataSetconfigs are notified
		 * correctly when the DataSetconfig is replaced
		 */
		child.notified = false;
		gsConfig.notify(new RepositoryRemoveEvent(gsConfig));
		Assert.assertTrue(child.notified);

		/*
		 * Now check, whether DataSet configs remove themselves when their
		 * DataSet is removed
		 */
		// gsconfig has to be registered
		Assert.assertTrue(getRepository().getRegisteredObject(gsConfig) == gsConfig);

		gsConfig.notify(new RepositoryRemoveEvent(gs2));

		// not registered anymore
		Assert.assertTrue(getRepository().getRegisteredObject(gsConfig) == null);
	}

	/**
	 * Test method for
	 * {@link data.dataset.DataSetConfig#parseFromFile(java.io.File)}.
	 * 
	 * @throws DataSetNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetConfigNotFoundException
	 * @throws UnknownDistanceMeasureException
	 * @throws RegisterException
	 * @throws UnknownDataPreprocessorException
	 * @throws NumberFormatException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws UnknownContextException
	 * @throws ConfigurationException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 * @throws IOException
	 * @throws UnknownDataRandomizerException
	 */
	@Test(expected = DataSetConfigurationException.class)
	public void testParseFromFileDataSetNameMissing()
			throws DataSetConfigurationException, NoRepositoryFoundException,
			DataSetNotFoundException, UnknownDataSetFormatException,
			DataSetConfigNotFoundException, UnknownDistanceMeasureException,
			RegisterException, UnknownDataSetTypeException, NoDataSetException,
			NumberFormatException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, IOException,
			UnknownDataRandomizerException {
		// create empty file
		File f = new File(
				"testCaseRepository/data/datasets/configs/testDataSetConfig.dsconfig")
				.getAbsoluteFile();
		f.createNewFile();
		try {
			Parser.parseFromFile(DataSetConfig.class, f);
		} catch (DataSetConfigurationException e) {
			// Assert.assertEquals(
			// "'goldstandardName' doesn't map to an existing object",
			// e.getMessage());
			throw e;
		} finally {
			f.delete();
		}
	}

	/**
	 * Test method for
	 * {@link data.dataset.DataSetConfig#parseFromFile(java.io.File)}.
	 * 
	 * @throws DataSetNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetConfigNotFoundException
	 * @throws UnknownDistanceMeasureException
	 * @throws RegisterException
	 * @throws UnknownDataPreprocessorException
	 * @throws NumberFormatException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws FileNotFoundException
	 * @throws UnknownContextException
	 * @throws ConfigurationException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testParseFromFile() throws DataSetConfigurationException,
			NoRepositoryFoundException, DataSetNotFoundException,
			UnknownDataSetFormatException, DataSetConfigNotFoundException,
			UnknownDistanceMeasureException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			NumberFormatException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, FileNotFoundException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		DataSetConfig gsConfig = Parser
				.parseFromFile(
						DataSetConfig.class,
						new File(
								"testCaseRepository/data/datasets/configs/astral_1.dsconfig")
								.getAbsoluteFile());
		Assert.assertEquals(
				new DataSetConfig(
						getRepository(),
						new File(
								"testCaseRepository/data/datasets/configs/astral_1.dsconfig")
								.getAbsoluteFile().lastModified(),
						new File(
								"testCaseRepository/data/datasets/configs/astral_1.dsconfig")
								.getAbsoluteFile(),
						Parser.parseFromFile(
								DataSet.class,
								new File(
										"testCaseRepository/data/datasets/astral_1_161/blastResults.txt")
										.getAbsoluteFile()),
						new ConversionInputToStandardConfiguration(
								DistanceMeasure.parseFromString(
										getRepository(),
										"EuclidianDistanceMeasure"),
								NUMBER_PRECISION.DOUBLE,
								new ArrayList<DataPreprocessor>(),
								new ArrayList<DataPreprocessor>()),
						new ConversionStandardToInputConfiguration()), gsConfig);
	}

	/**
	 * Test method for
	 * {@link data.dataset.DataSetConfig#parseFromFile(java.io.File)}.
	 * 
	 * @throws DataSetNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetConfigNotFoundException
	 * @throws UnknownDistanceMeasureException
	 * @throws RegisterException
	 * @throws UnknownDataPreprocessorException
	 * @throws NumberFormatException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws UnknownContextException
	 * @throws ConfigurationException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 * @throws IOException
	 */
	@Test(expected = DataSetConfigurationException.class)
	public void testParseFromFileDataSetFileMissing()
			throws DataSetConfigurationException, NoRepositoryFoundException,
			DataSetNotFoundException, UnknownDataSetFormatException,
			DataSetConfigNotFoundException, UnknownDistanceMeasureException,
			RegisterException, UnknownDataSetTypeException, NoDataSetException,
			NumberFormatException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException, IOException,
			UnknownDataRandomizerException {

		File f = new File(
				"testCaseRepository/data/datasets/configs/testDataSetConfig2.dsconfig")
				.getAbsoluteFile();
		f.createNewFile();

		try {
			PrintWriter bw = new PrintWriter(new FileWriter(f));
			bw.println("datasetName = Test");
			bw.flush();
			bw.close();

			Parser.parseFromFile(DataSetConfig.class, f);
		} catch (DataSetConfigurationException e) {
			// Assert.assertEquals(
			// "'goldstandardFile' doesn't map to an existing object",
			// e.getMessage());
			throw e;
		} finally {
			f.delete();
		}
	}

	/**
	 * @throws NoRepositoryFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetConfigNotFoundException
	 * @throws UnknownDistanceMeasureException
	 * @throws RegisterException
	 * @throws UnknownDataPreprocessorException
	 * @throws NumberFormatException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws FileNotFoundException
	 * @throws UnknownContextException
	 * @throws ConfigurationException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test(expected = FileNotFoundException.class)
	public void testParseFromNotExistingFile()
			throws NoRepositoryFoundException, DataSetNotFoundException,
			DataSetConfigurationException, UnknownDataSetFormatException,
			DataSetConfigNotFoundException, UnknownDistanceMeasureException,
			RegisterException, UnknownDataSetTypeException, NoDataSetException,
			NumberFormatException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, FileNotFoundException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		Parser.parseFromFile(DataSetConfig.class, new File(
				"testCaseRepository/data/datasets/configs/DS1_12.gsconfig")
				.getAbsoluteFile());
	}

	/**
	 * Test method for {@link data.dataset.DataSetConfig#getDataSet()}.
	 * 
	 * @throws DataSetNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetConfigNotFoundException
	 * @throws UnknownDistanceMeasureException
	 * @throws RegisterException
	 * @throws UnknownDataPreprocessorException
	 * @throws NumberFormatException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws FileNotFoundException
	 * @throws UnknownContextException
	 * @throws ConfigurationException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testGetDataSet() throws DataSetConfigurationException,
			NoRepositoryFoundException, DataSetNotFoundException,
			UnknownDataSetFormatException, DataSetConfigNotFoundException,
			UnknownDistanceMeasureException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			NumberFormatException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, FileNotFoundException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		DataSetConfig dsConfig = Parser
				.parseFromFile(
						DataSetConfig.class,
						new File(
								"testCaseRepository/data/datasets/configs/astral_1.dsconfig")
								.getAbsoluteFile());
		DataSet ds = dsConfig.getDataSet();
		DataSet expected = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/astral_1_161/blastResults.txt")
								.getAbsoluteFile());
		Assert.assertEquals(expected, ds);
	}

	/**
	 * Test method for
	 * {@link data.dataset.DataSetConfig#setDataSet(data.dataset.DataSet)} .
	 * 
	 * @throws DataSetNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetConfigNotFoundException
	 * @throws UnknownDistanceMeasureException
	 * @throws RegisterException
	 * @throws UnknownDataSetTypeException
	 * @throws UnknownDataPreprocessorException
	 * @throws NumberFormatException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws FileNotFoundException
	 * @throws UnknownContextException
	 * @throws ConfigurationException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testSetDataSet() throws DataSetConfigurationException,
			NoRepositoryFoundException, DataSetNotFoundException,
			UnknownDataSetFormatException, DataSetConfigNotFoundException,
			UnknownDistanceMeasureException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			NumberFormatException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, FileNotFoundException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		DataSetConfig dsConfig = Parser
				.parseFromFile(
						DataSetConfig.class,
						new File(
								"testCaseRepository/data/datasets/configs/astral_1.dsconfig")
								.getAbsoluteFile());
		DataSet ds = dsConfig.getDataSet();
		DataSet expected = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/astral_1_161/blastResults.txt")
								.getAbsoluteFile());
		Assert.assertEquals(expected, ds);

		DataSet override = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile());
		dsConfig.setDataSet(override);
		Assert.assertEquals(override, dsConfig.getDataSet());
	}

	/**
	 * Test method for {@link data.dataset.DataSetConfig#toString()}.
	 * 
	 * @throws DataSetNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetConfigNotFoundException
	 * @throws UnknownDistanceMeasureException
	 * @throws RegisterException
	 * @throws UnknownDataPreprocessorException
	 * @throws NumberFormatException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws FileNotFoundException
	 * @throws UnknownContextException
	 * @throws ConfigurationException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testToString() throws DataSetConfigurationException,
			NoRepositoryFoundException, DataSetNotFoundException,
			UnknownDataSetFormatException, DataSetConfigNotFoundException,
			UnknownDistanceMeasureException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			NumberFormatException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, FileNotFoundException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		DataSetConfig gsConfig = Parser
				.parseFromFile(
						DataSetConfig.class,
						new File(
								"testCaseRepository/data/datasets/configs/astral_1.dsconfig")
								.getAbsoluteFile());
		Assert.assertEquals("astral_1", gsConfig.toString());

	}

}
