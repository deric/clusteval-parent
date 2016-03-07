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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import junit.framework.Assert;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import de.wiwie.wiutils.utils.SimilarityMatrix;
import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import ch.qos.logback.classic.Level;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.InvalidOptimizationParameterException;
import de.clusteval.cluster.paramOptimization.UnknownParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.context.IncompatibleContextException;
import de.clusteval.context.UnknownContextException;
import de.clusteval.data.DataConfig;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSet.WEBSITE_VISIBILITY;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.data.dataset.format.ConversionStandardToInputConfiguration;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.dataset.format.InvalidDataSetFormatVersionException;
import de.clusteval.data.dataset.format.RelativeDataSetFormat;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.dataset.type.DataSetType;
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
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.db.DatabaseConnectException;
import de.clusteval.framework.repository.db.StubSQLCommunicator;
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
import de.clusteval.utils.FormatConversionException;
import de.clusteval.utils.RNotAvailableException;

/**
 * @author Christian Wiwie
 * 
 */
public class TestDataSet extends AbstractClustEvalTest {

	/**
	 * Test method for {@link data.dataset.DataSet#register()}.
	 * 
	 * @throws NoRepositoryFoundException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws UnknownDataSetTypeException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDistanceMeasureException
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
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 * @throws UnknownDataRandomizerException
	 */
	public void testRegister() throws UnknownDataSetFormatException,
			NoRepositoryFoundException, DataSetNotFoundException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			FileNotFoundException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile());

		Assert.assertEquals(this.repositoryObject, this.getRepository()
				.getRegisteredObject((DataSet) this.repositoryObject));

		// adding a data set equal to another one already registered does
		// not register the second object.
		this.repositoryObject = new RelativeDataSet(
				(RelativeDataSet) this.repositoryObject);
		Assert.assertEquals(
				this.getRepository().getRegisteredObject(
						(DataSet) this.repositoryObject), this.repositoryObject);
		Assert.assertFalse(this.getRepository().getRegisteredObject(
				(DataSet) this.repositoryObject) == this.repositoryObject);
	}

	/**
	 * Registering a dataset of a runresult repository that is not present in
	 * the parent repository should not be possible.
	 * 
	 * @throws NoRepositoryFoundException
	 * @throws RepositoryConfigurationException
	 * @throws RepositoryConfigNotFoundException
	 * @throws InvalidRepositoryException
	 * @throws RepositoryAlreadyExistsException
	 * @throws FileNotFoundException
	 * @throws RegisterException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetConfigurationException
	 * @throws DataSetNotFoundException
	 * @throws UnknownDataSetTypeException
	 * @throws NoSuchAlgorithmException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDistanceMeasureException
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
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test(expected = DataSetRegisterException.class)
	public void testRegisterRunResultRepositoryNotPresentInParent()
			throws FileNotFoundException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, NoRepositoryFoundException,
			DataSetNotFoundException, DataSetConfigurationException,
			UnknownDataSetFormatException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			NoSuchAlgorithmException, InterruptedException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		try {
			Repository runResultRepository = new RunResultRepository(
					new File(
							"testCaseRepository/results/12_04_2012-14_05_42_tc_vs_DS1")
							.getAbsolutePath(), getRepository());
			runResultRepository.setSQLCommunicator(new StubSQLCommunicator(
					runResultRepository));
			runResultRepository.initialize();
			try {
				Parser.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/results/12_04_2012-14_05_42_tc_vs_DS1/inputs/DS1/testCaseDataSetNotPresentInParent.txt")
								.getAbsoluteFile());
			} finally {
				runResultRepository.terminateSupervisorThread();
			}
		} catch (DatabaseConnectException e) {
			// cannot happen
		}
	}

	/**
	 * Test method for {@link data.dataset.DataSet#unregister()}.
	 * 
	 * @throws NoRepositoryFoundException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDistanceMeasureException
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
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	public void testUnregister() throws UnknownDataSetFormatException,
			NoRepositoryFoundException, DataSetNotFoundException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			FileNotFoundException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException {

		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile());

		Assert.assertEquals(this.repositoryObject, this.getRepository()
				.getRegisteredObject((DataSet) this.repositoryObject));
		this.repositoryObject.unregister();
		// is not registered anymore
		Assert.assertTrue(this.getRepository().getRegisteredObject(
				(DataSet) this.repositoryObject) == null);
	}

	/**
	 * Test method for
	 * {@link data.dataset.DataSet#parseFromFile(java.io.File, data.dataset.format.DataSetFormat)}
	 * .
	 * 
	 * @throws NoRepositoryFoundException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws UnknownDataSetTypeException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDistanceMeasureException
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
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testParseFromFile() throws UnknownDataSetFormatException,
			NoRepositoryFoundException, DataSetNotFoundException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			FileNotFoundException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile());
		Assert.assertEquals(
				new RelativeDataSet(
						getRepository(),
						false,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile().lastModified(),
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile(), "zachary",
						(RelativeDataSetFormat) (DataSetFormat.parseFromString(
								getRepository(), "RowSimDataSetFormat")),
						DataSetType.parseFromString(getRepository(),
								"PPIDataSetType"), WEBSITE_VISIBILITY.HIDE),
				this.repositoryObject);
	}

	/**
	 * @throws NoRepositoryFoundException
	 * @throws UnknownDataSetFormatException
	 * @throws FileNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDistanceMeasureException
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
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test(expected = FileNotFoundException.class)
	public void testParseFromNotExistingFile()
			throws UnknownDataSetFormatException, NoRepositoryFoundException,
			DataSetNotFoundException, DataSetNotFoundException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			FileNotFoundException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException, UnknownDataRandomizerException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities2.txt")
								.getAbsoluteFile());
	}

	/**
	 * Test method for {@link data.dataset.DataSet#getDataSetFormat()}.
	 * 
	 * @throws UnknownDataSetFormatException
	 * @throws NoRepositoryFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDistanceMeasureException
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
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testGetDataSetFormat() throws NoRepositoryFoundException,
			UnknownDataSetFormatException, DataSetNotFoundException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			FileNotFoundException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile());
		DataSetFormat dsFormat = ((DataSet) this.repositoryObject)
				.getDataSetFormat();
		Assert.assertEquals(DataSetFormat.parseFromString(getRepository(),
				"RowSimDataSetFormat"), dsFormat);
	}

	/**
	 * Test method for {@link data.dataset.DataSet#getMajorName()}.
	 * 
	 * @throws UnknownDataSetFormatException
	 * @throws NoRepositoryFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDistanceMeasureException
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
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testGetMajorName() throws NoRepositoryFoundException,
			UnknownDataSetFormatException, DataSetNotFoundException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			FileNotFoundException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile());
		DataSet casted = (DataSet) this.repositoryObject;
		Assert.assertEquals("DS1", casted.getMajorName());
	}

	/**
	 * Test method for {@link data.dataset.DataSet#getMinorName()}.
	 * 
	 * @throws NoRepositoryFoundException
	 * @throws UnknownDataSetFormatException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDistanceMeasureException
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
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testGetMinorName() throws NoRepositoryFoundException,
			UnknownDataSetFormatException, DataSetNotFoundException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			FileNotFoundException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile());
		DataSet casted = (DataSet) this.repositoryObject;

		Assert.assertEquals(casted.getMinorName(), casted.getAbsolutePath()
				.substring(casted.getAbsolutePath().lastIndexOf("/") + 1));
	}

	/**
	 * Test method for {@link data.dataset.DataSet#getFullName()}.
	 * 
	 * @throws UnknownDataSetFormatException
	 * @throws NoRepositoryFoundException
	 * @throws FileNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDistanceMeasureException
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
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testGetFullName() throws NoRepositoryFoundException,
			UnknownDataSetFormatException, DataSetNotFoundException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			FileNotFoundException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile());
		Assert.assertEquals("DS1/Zachary_karate_club_similarities.txt",
				((DataSet) this.repositoryObject).getFullName());
	}

	/**
	 * Test method for {@link data.dataset.DataSet#toString()}.
	 * 
	 * @throws UnknownDataSetFormatException
	 * @throws NoRepositoryFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDistanceMeasureException
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
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testToString() throws NoRepositoryFoundException,
			UnknownDataSetFormatException, DataSetNotFoundException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			FileNotFoundException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile());
		Assert.assertEquals("DS1/Zachary_karate_club_similarities.txt",
				((DataSet) this.repositoryObject).toString());
	}

	/**
	 * Test method for {@link data.dataset.DataSet#loadIntoMemory()}.
	 * 
	 * @throws UnknownDataSetFormatException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws FormatConversionException
	 * @throws DataSetNotFoundException
	 * @throws InvalidDataSetFormatVersionException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws UnknownDistanceMeasureException
	 * @throws RNotAvailableException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws ConfigurationException
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 * @throws UnknownContextException
	 * @throws InterruptedException
	 */
	@Test
	public void testLoadIntoMemory() throws NoRepositoryFoundException,
			UnknownDataSetFormatException, FormatConversionException,
			IOException, DataSetNotFoundException,
			InvalidDataSetFormatVersionException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			InstantiationException, IllegalAccessException,
			UnknownDistanceMeasureException, RNotAvailableException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
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
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException, InterruptedException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile());

		DataSet standard = ((DataSet) this.repositoryObject)
				.preprocessAndConvertTo(
						context,
						DataSetFormat.parseFromString(getRepository(),
								"SimMatrixDataSetFormat"),
						new ConversionInputToStandardConfiguration(
								DistanceMeasure.parseFromString(
										getRepository(),
										"EuclidianDistanceMeasure"),
								NUMBER_PRECISION.DOUBLE,
								new ArrayList<DataPreprocessor>(),
								new ArrayList<DataPreprocessor>()),
						new ConversionStandardToInputConfiguration());
		Assert.assertFalse(standard.isInMemory());
		standard.loadIntoMemory();
		Assert.assertTrue(standard.isInMemory());
	}

	/**
	 * Test method for {@link data.dataset.DataSet#getSimilarityMatrix()}.
	 * 
	 * @throws UnknownDataSetFormatException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws FormatConversionException
	 * @throws DataSetNotFoundException
	 * @throws InvalidDataSetFormatVersionException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws RNotAvailableException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws ConfigurationException
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 * @throws UnknownContextException
	 */
	@Test
	public void testGetSimilarityMatrix() throws NoRepositoryFoundException,
			UnknownDataSetFormatException, FormatConversionException,
			IOException, DataSetNotFoundException,
			InvalidDataSetFormatVersionException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			InstantiationException, IllegalAccessException,
			UnknownDistanceMeasureException, RNotAvailableException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
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
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException, InterruptedException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim")
								.getAbsoluteFile());
		RelativeDataSet standard = (RelativeDataSet) ((DataSet) this.repositoryObject)
				.preprocessAndConvertTo(
						context,
						DataSetFormat.parseFromString(getRepository(),
								"SimMatrixDataSetFormat"),
						new ConversionInputToStandardConfiguration(
								DistanceMeasure.parseFromString(
										getRepository(),
										"EuclidianDistanceMeasure"),
								NUMBER_PRECISION.DOUBLE,
								new ArrayList<DataPreprocessor>(),
								new ArrayList<DataPreprocessor>()),
						new ConversionStandardToInputConfiguration());
		standard.loadIntoMemory();
		SimilarityMatrix simMatrix = standard.getDataSetContent();
		double[][] sims = new double[][]{new double[]{1.0, 0.6, 0.5},
				new double[]{0.6, 0.5, 0.1}, new double[]{0.5, 0.1, 0.8}};
		String[] ids = new String[]{"1", "2", "3"};
		SimilarityMatrix expected = new SimilarityMatrix(sims);
		expected.setIds(ids);
		Assert.assertEquals(expected, simMatrix);
	}

	/**
	 * Test method for {@link data.dataset.DataSet#unloadFromMemory()}.
	 * 
	 * @throws UnknownDataSetFormatException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws FormatConversionException
	 * @throws DataSetNotFoundException
	 * @throws InvalidDataSetFormatVersionException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws RNotAvailableException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownProgramParameterException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws IncompatibleContextException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownParameterType
	 * @throws ConfigurationException
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 * @throws UnknownContextException
	 */
	@Test
	public void testUnloadFromMemory() throws NoRepositoryFoundException,
			UnknownDataSetFormatException, FormatConversionException,
			IOException, DataSetNotFoundException,
			InvalidDataSetFormatVersionException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			InstantiationException, IllegalAccessException,
			UnknownDistanceMeasureException, RNotAvailableException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
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
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException, InterruptedException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
								.getAbsoluteFile());
		DataSet standard = ((DataSet) this.repositoryObject)
				.preprocessAndConvertTo(
						context,
						DataSetFormat.parseFromString(getRepository(),
								"SimMatrixDataSetFormat"),
						new ConversionInputToStandardConfiguration(
								DistanceMeasure.parseFromString(
										getRepository(),
										"EuclidianDistanceMeasure"),
								NUMBER_PRECISION.DOUBLE,
								new ArrayList<DataPreprocessor>(),
								new ArrayList<DataPreprocessor>()),
						new ConversionStandardToInputConfiguration());
		standard.loadIntoMemory();
		Assert.assertTrue(standard.isInMemory());
		standard.unloadFromMemory();
		Assert.assertFalse(standard.isInMemory());
	}

	/**
	 * Test method for
	 * {@link data.dataset.DataSet#convertTo(data.dataset.format.DataSetFormat)}
	 * . Only verify, that the conversion process is started correctly and the
	 * result file is created in the end. verification of the conversion result
	 * itself is not done here.
	 * 
	 * @throws UnknownDataSetFormatException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws FormatConversionException
	 * @throws DataSetNotFoundException
	 * @throws InvalidDataSetFormatVersionException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws RNotAvailableException
	 * @throws UnknownContextException
	 */
	@Test
	public void testConvertTo() throws NoRepositoryFoundException,
			UnknownDataSetFormatException, FormatConversionException,
			IOException, DataSetNotFoundException,
			InvalidDataSetFormatVersionException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			InstantiationException, IllegalAccessException,
			UnknownDistanceMeasureException, IllegalArgumentException,
			SecurityException, InvocationTargetException,
			NoSuchMethodException, RNotAvailableException, InterruptedException {
		/*
		 * SimMatrixDataSetFormat.convertTo() is a special case
		 */
		this.repositoryObject = this
				.getRepository()
				.getStaticObjectWithName(DataSet.class,
						"nora_cancer/all_expression_spearman.txt").clone();
		DataSet newDataSet = ((DataSet) this.repositoryObject)
				.preprocessAndConvertTo(
						context,
						DataSetFormat.parseFromString(getRepository(),
								"SimMatrixDataSetFormat"),
						new ConversionInputToStandardConfiguration(
								DistanceMeasure.parseFromString(
										getRepository(),
										"EuclidianDistanceMeasure"),
								NUMBER_PRECISION.DOUBLE,
								new ArrayList<DataPreprocessor>(),
								new ArrayList<DataPreprocessor>()),
						new ConversionStandardToInputConfiguration());
		Assert.assertEquals(this.repositoryObject.getAbsolutePath(),
				newDataSet.getAbsolutePath());
		/*
		 * SimMatrixDataSetFormat.convertTo(APRowSimDataSetFormat)
		 */
		this.repositoryObject = this
				.getRepository()
				.getStaticObjectWithName(DataSet.class,
						"nora_cancer/all_expression_spearman.txt").clone();
		newDataSet = ((DataSet) this.repositoryObject).preprocessAndConvertTo(
				context,
				DataSetFormat.parseFromString(getRepository(),
						"APRowSimDataSetFormat"),
				new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());

		/*
		 * convertTo(SimMatrixDataSetFormat) is a special case
		 */
		this.repositoryObject = this
				.getRepository()
				.getStaticObjectWithName(DataSet.class,
						"rowSimTest/rowSimTestFile.sim").clone();
		((DataSet) this.repositoryObject).preprocessAndConvertTo(
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
		Assert.assertTrue(new File(
				"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.SimMatrix")
				.getAbsoluteFile().exists());

		/*
		 * Convert to a non standard format
		 */
		this.repositoryObject = this
				.getRepository()
				.getStaticObjectWithName(DataSet.class,
						"rowSimTest/rowSimTestFile.sim").clone();
		((DataSet) this.repositoryObject).preprocessAndConvertTo(
				context,
				DataSetFormat.parseFromString(getRepository(),
						"APRowSimDataSetFormat"),
				new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());
		Assert.assertTrue(new File(
				"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.APRowSim")
				.getAbsoluteFile().exists());
		Assert.assertTrue(new File(
				"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.APRowSim.map")
				.getAbsoluteFile().exists());

		new File(
				"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.SimMatrix")
				.getAbsoluteFile().deleteOnExit();
		new File(
				"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.APRowSim")
				.getAbsoluteFile().deleteOnExit();
		new File(
				"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.APRowSim.map")
				.getAbsoluteFile().deleteOnExit();
	}

	@Test(expected = FormatConversionException.class)
	public void testConvertToRelativeToAbsolute()
			throws NoRepositoryFoundException, UnknownDataSetFormatException,
			FormatConversionException, IOException, DataSetNotFoundException,
			InvalidDataSetFormatVersionException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			InstantiationException, IllegalAccessException,
			UnknownDistanceMeasureException, IllegalArgumentException,
			SecurityException, InvocationTargetException,
			NoSuchMethodException, RNotAvailableException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException, InterruptedException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/sfld/sfld_brown_et_al_amidohydrolases_protein_similarities_for_beh.txt")
								.getAbsoluteFile());
		((DataSet) this.repositoryObject).preprocessAndConvertTo(
				context,
				DataSetFormat.parseFromString(getRepository(),
						"MatrixDataSetFormat"),
				new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());
	}

	/**
	 * Test method for
	 * {@link data.dataset.DataSet#convertToDirectly(data.dataset.format.DataSetFormat)}
	 * .
	 * 
	 * @throws UnknownDataSetFormatException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws DataSetNotFoundException
	 * @throws InvalidDataSetFormatVersionException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws RNotAvailableException
	 * @throws InvalidParameterException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
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
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testConvertToDirectly() throws NoRepositoryFoundException,
			UnknownDataSetFormatException, IOException,
			DataSetNotFoundException, InvalidDataSetFormatVersionException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			UnknownDistanceMeasureException, InvalidParameterException,
			RNotAvailableException, GoldStandardNotFoundException,
			GoldStandardConfigurationException, DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
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
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException, InterruptedException {

		File targetFile = new File(
				"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.SimMatrix")
				.getAbsoluteFile();
		if (targetFile.exists())
			targetFile.delete();

		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim")
								.getAbsoluteFile());
		DataSetAttributeFilterer filterer = new DataSetAttributeFilterer(
				"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim");
		filterer.process();
		((DataSet) this.repositoryObject)
				.setAbsolutePath(new File(
						"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip")
						.getAbsoluteFile());
		((DataSet) this.repositoryObject).convertToStandardDirectly(
				context,
				new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()));
		Assert.assertTrue(targetFile.exists());

		targetFile.delete();
	}

	/**
	 * Test method for {@link data.dataset.DataSet#getInStandardFormat()}.
	 * 
	 * @throws UnknownDataSetFormatException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws DataSetNotFoundException
	 * @throws InvalidDataSetFormatVersionException
	 * @throws DataSetConfigurationException
	 * @throws DataSetConfigurationException
	 * @throws RegisterException
	 * @throws RNotAvailableException
	 * @throws InvalidParameterException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
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
	 * @throws NumberFormatException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 */
	@Test
	public void testGetInStandardFormat() throws NoRepositoryFoundException,
			UnknownDataSetFormatException, IOException,
			DataSetNotFoundException, InvalidDataSetFormatVersionException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			UnknownDistanceMeasureException, InvalidParameterException,
			RNotAvailableException, GoldStandardNotFoundException,
			GoldStandardConfigurationException, DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
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
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException, InterruptedException {

		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim")
								.getAbsoluteFile());
		DataSetAttributeFilterer filterer = new DataSetAttributeFilterer(
				"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim");
		filterer.process();
		((DataSet) this.repositoryObject)
				.setAbsolutePath(new File(
						"testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip")
						.getAbsoluteFile());
		((DataSet) this.repositoryObject).convertToStandardDirectly(
				context,
				new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()));
		DataSet standard = ((DataSet) this.repositoryObject)
				.getInStandardFormat();
		Assert.assertEquals(DataSetFormat.parseFromString(getRepository(),
				"SimMatrixDataSetFormat"), standard.getDataSetFormat());
	}

	@Test
	public void testConvertToAbsoluteToAbsolute()
			throws NoRepositoryFoundException, UnknownDataSetFormatException,
			FormatConversionException, IOException, DataSetNotFoundException,
			InvalidDataSetFormatVersionException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			InstantiationException, IllegalAccessException,
			UnknownDistanceMeasureException, IllegalArgumentException,
			SecurityException, InvocationTargetException,
			NoSuchMethodException, RNotAvailableException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException, InterruptedException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/bone_marrow_gene_expr/ALB_ALT_AML.1000genes.res.out2")
								.getAbsoluteFile());
		DataSet newDataSet = ((DataSet) this.repositoryObject)
				.preprocessAndConvertTo(
						context,
						DataSetFormat.parseFromString(getRepository(),
								"MatrixDataSetFormat"),
						new ConversionInputToStandardConfiguration(
								DistanceMeasure.parseFromString(
										getRepository(),
										"EuclidianDistanceMeasure"),
								NUMBER_PRECISION.DOUBLE,
								new ArrayList<DataPreprocessor>(),
								new ArrayList<DataPreprocessor>()),
						new ConversionStandardToInputConfiguration());
		Assert.assertEquals("MatrixDataSetFormat", newDataSet
				.getDataSetFormat().getClass().getSimpleName());
		Assert.assertEquals(context.getStandardInputFormat().getClass()
				.getSimpleName(), newDataSet.thisInStandardFormat
				.getDataSetFormat().getClass().getSimpleName());
	}

	@Test
	public void testConvertToStandardToStandard()
			throws NoRepositoryFoundException, UnknownDataSetFormatException,
			FormatConversionException, IOException, DataSetNotFoundException,
			InvalidDataSetFormatVersionException,
			DataSetConfigurationException, RegisterException,
			UnknownDataSetTypeException, NoDataSetException,
			InstantiationException, IllegalAccessException,
			UnknownDistanceMeasureException, IllegalArgumentException,
			SecurityException, InvocationTargetException,
			NoSuchMethodException, RNotAvailableException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, DataConfigurationException,
			DataConfigNotFoundException, ConfigurationException,
			UnknownContextException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownDataRandomizerException, InterruptedException {
		this.repositoryObject = Parser
				.parseFromFile(
						DataSet.class,
						new File(
								"testCaseRepository/data/datasets/bone_marrow_gene_expr/ALB_ALT_AML.1000genes.res.out2.SimMatrix")
								.getAbsoluteFile());
		DataSet newDataSet = ((DataSet) this.repositoryObject)
				.preprocessAndConvertTo(
						context,
						context.getStandardInputFormat(),
						new ConversionInputToStandardConfiguration(
								DistanceMeasure.parseFromString(
										getRepository(),
										"EuclidianDistanceMeasure"),
								NUMBER_PRECISION.DOUBLE,
								new ArrayList<DataPreprocessor>(),
								new ArrayList<DataPreprocessor>()),
						new ConversionStandardToInputConfiguration());
		Assert.assertEquals(context.getStandardInputFormat().getClass()
				.getSimpleName(), newDataSet.getDataSetFormat().getClass()
				.getSimpleName());
		Assert.assertEquals(context.getStandardInputFormat().getClass()
				.getSimpleName(), newDataSet.thisInStandardFormat
				.getDataSetFormat().getClass().getSimpleName());
	}

	@Test
	public void testConvertMatrixToSimMatrix()
			throws RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, UnknownDataSetFormatException,
			InvalidDataSetFormatVersionException, RegisterException,
			FormatConversionException, IOException,
			UnknownDistanceMeasureException, RNotAvailableException,
			InterruptedException {
		ClustevalBackendServer.logLevel(Level.INFO);

		DataConfig dataConfig = getRepository().getStaticObjectWithName(
				DataConfig.class, "synthetic_cassini250");
		DataSet ds = dataConfig.getDatasetConfig().getDataSet();
		DataSetFormat internal = DataSetFormat.parseFromString(getRepository(),
				"SimMatrixDataSetFormat");
		ds = ds.preprocessAndConvertTo(
				context,
				internal,
				new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());
	}
}
