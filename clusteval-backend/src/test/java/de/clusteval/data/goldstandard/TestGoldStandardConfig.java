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
package de.clusteval.data.goldstandard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.Assert;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.InvalidOptimizationParameterException;
import de.clusteval.cluster.paramOptimization.UnknownParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.context.IncompatibleContextException;
import de.clusteval.context.UnknownContextException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.DataSetNotFoundException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.NoDataSetException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.dataset.type.UnknownDataSetTypeException;
import de.clusteval.data.distance.UnknownDistanceMeasureException;
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
public class TestGoldStandardConfig extends AbstractClustEvalTest {

	/**
	 * Test method for {@link data.goldstandard.GoldStandardConfig#register()}.
	 * 
	 * @throws GoldStandardNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws GoldStandardConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDataSetTypeException
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
	 * @throws NoDataSetException
	 * @throws DataSetConfigNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 */
	public void testRegister() throws GoldStandardConfigurationException,
			IOException, NoRepositoryFoundException,
			GoldStandardNotFoundException, GoldStandardConfigNotFoundException,
			RegisterException, UnknownDataSetFormatException,
			DataSetConfigurationException, DataSetNotFoundException,
			DataSetConfigNotFoundException, NoDataSetException,
			DataConfigurationException, DataConfigNotFoundException,
			NumberFormatException, ConfigurationException,
			UnknownContextException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		this.repositoryObject = Parser
				.parseFromFile(
						GoldStandardConfig.class,
						new File(
								"testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
								.getAbsoluteFile());
		Assert.assertEquals(
				this.repositoryObject,
				this.getRepository().getRegisteredObject(
						(GoldStandardConfig) this.repositoryObject));

		// adding a GoldStandardConfig equal to another one already registered
		// does
		// not register the second object.
		this.repositoryObject = new GoldStandardConfig(
				(GoldStandardConfig) this.repositoryObject);
		Assert.assertEquals(
				this.getRepository().getRegisteredObject(
						(GoldStandardConfig) this.repositoryObject),
				this.repositoryObject);
		Assert.assertFalse(this.getRepository().getRegisteredObject(
				(GoldStandardConfig) this.repositoryObject) == this.repositoryObject);
	}

	/**
	 * Test method for {@link data.goldstandard.GoldStandardConfig#unregister()}
	 * .
	 * 
	 * @throws GoldStandardNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws GoldStandardConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDataSetTypeException
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
	 * @throws NoDataSetException
	 * @throws DataSetConfigNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 */
	public void testUnregister() throws GoldStandardConfigurationException,
			IOException, NoRepositoryFoundException,
			GoldStandardNotFoundException, GoldStandardConfigNotFoundException,
			RegisterException, UnknownDataSetFormatException,
			DataSetConfigurationException, DataSetNotFoundException,
			DataSetConfigNotFoundException, NoDataSetException,
			DataConfigurationException, DataConfigNotFoundException,
			NumberFormatException, ConfigurationException,
			UnknownContextException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		this.repositoryObject = Parser
				.parseFromFile(
						GoldStandardConfig.class,
						new File(
								"testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
								.getAbsoluteFile());
		Assert.assertEquals(
				this.repositoryObject,
				this.getRepository().getRegisteredObject(
						(GoldStandardConfig) this.repositoryObject));
		this.repositoryObject.unregister();
		// is not registered anymore
		Assert.assertTrue(this.getRepository().getRegisteredObject(
				(GoldStandardConfig) this.repositoryObject) == null);
	}

	/**
	 * Test method for
	 * {@link data.goldstandard.GoldStandardConfig#notify(utils.RepositoryEvent)}
	 * .
	 * 
	 * @throws GoldStandardNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws GoldStandardConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDataSetTypeException
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
	 * @throws NoDataSetException
	 * @throws DataSetConfigNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 */
	@Test
	public void testNotifyRepositoryEvent() throws IOException,
			NoRepositoryFoundException, GoldStandardNotFoundException,
			GoldStandardConfigurationException,
			GoldStandardConfigNotFoundException, RegisterException,
			UnknownDataSetFormatException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException,
			NoDataSetException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
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
		 * First check, whether listeners of goldstandardconfigs are notified
		 * correctly when the goldstandardconfig is replaced
		 */
		GoldStandardConfig gsConfig = Parser
				.parseFromFile(
						GoldStandardConfig.class,
						new File(
								"testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
								.getAbsoluteFile());
		StubRepositoryObject child = new StubRepositoryObject(getRepository(),
				false, System.currentTimeMillis(), new File(
						"testCaseRepository/Bla"));
		gsConfig.addListener(child);

		GoldStandardConfig gsConfig2 = new GoldStandardConfig(gsConfig);

		gsConfig.notify(new RepositoryReplaceEvent(gsConfig, gsConfig2));
		Assert.assertTrue(child.notified);

		/*
		 * Now check, whether goldstandard configs update their references
		 * correctly, when their goldstandard is replaced
		 */
		GoldStandard gs = gsConfig.getGoldstandard();
		GoldStandard gs2 = new GoldStandard(gs);

		gsConfig.notify(new RepositoryReplaceEvent(gs, gs2));

		Assert.assertFalse(gsConfig.getGoldstandard() == gs);
		Assert.assertTrue(gsConfig.getGoldstandard() == gs2);

		/*
		 * REMOVE
		 */

		/*
		 * First check, whether listeners of goldstandardconfigs are notified
		 * correctly when the goldstandardconfig is replaced
		 */
		child.notified = false;
		gsConfig.notify(new RepositoryRemoveEvent(gsConfig));
		Assert.assertTrue(child.notified);

		/*
		 * Now check, whether goldstandard configs remove themselves when their
		 * goldstandard is removed
		 */
		// gsconfig has to be registered
		Assert.assertTrue(getRepository().getRegisteredObject(gsConfig) == gsConfig);

		gsConfig.notify(new RepositoryRemoveEvent(gs2));

		// not registered anymore
		Assert.assertTrue(getRepository().getRegisteredObject(gsConfig) == null);
	}

	/**
	 * Test method for
	 * {@link data.goldstandard.GoldStandardConfig#parseFromFile(java.io.File)}.
	 * 
	 * @throws GoldStandardNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws GoldStandardConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDataSetTypeException
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
	 * @throws NoDataSetException
	 * @throws DataSetConfigNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 */
	@Test(expected = GoldStandardConfigurationException.class)
	public void testParseFromFileGoldStandardNameMissing()
			throws GoldStandardConfigurationException, IOException,
			NoRepositoryFoundException, GoldStandardNotFoundException,
			GoldStandardConfigNotFoundException, RegisterException,
			UnknownDataSetFormatException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException,
			NoDataSetException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		// create empty file
		File f = new File(
				"testCaseRepository/data/goldstandards/configs/goldStandardConfigTest.gsconfig")
				.getAbsoluteFile();
		f.createNewFile();
		try {
			Parser.parseFromFile(GoldStandardConfig.class, f);
		} catch (GoldStandardConfigurationException e) {
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
	 * {@link data.goldstandard.GoldStandardConfig#parseFromFile(java.io.File)}.
	 * 
	 * @throws GoldStandardNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws GoldStandardConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDataSetTypeException
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
	 * @throws NoDataSetException
	 * @throws DataSetConfigNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 */
	@Test
	public void testParseFromFile() throws GoldStandardConfigurationException,
			IOException, NoRepositoryFoundException,
			GoldStandardNotFoundException, GoldStandardConfigNotFoundException,
			RegisterException, UnknownDataSetFormatException,
			DataSetConfigurationException, DataSetNotFoundException,
			DataSetConfigNotFoundException, NoDataSetException,
			DataConfigurationException, DataConfigNotFoundException,
			NumberFormatException, ConfigurationException,
			UnknownContextException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		GoldStandardConfig gsConfig = Parser
				.parseFromFile(
						GoldStandardConfig.class,
						new File(
								"testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
								.getAbsoluteFile());
		Assert.assertEquals(
				new GoldStandardConfig(
						getRepository(),
						new File(
								"testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
								.getAbsoluteFile().lastModified(),
						new File(
								"testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
								.getAbsoluteFile(),
						GoldStandard
								.parseFromFile(new File(
										"testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
										.getAbsoluteFile())), gsConfig);
	}

	/**
	 * Test method for
	 * {@link data.goldstandard.GoldStandardConfig#parseFromFile(java.io.File)}.
	 * 
	 * @throws GoldStandardNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws GoldStandardConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDataSetTypeException
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
	 * @throws NoDataSetException
	 * @throws DataSetConfigNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 */
	@Test(expected = GoldStandardConfigurationException.class)
	public void testParseFromFileGoldStandardFileMissing()
			throws GoldStandardConfigurationException, IOException,
			NoRepositoryFoundException, GoldStandardNotFoundException,
			GoldStandardConfigNotFoundException, RegisterException,
			UnknownDataSetFormatException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException,
			NoDataSetException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {

		File f = new File(
				"testCaseRepository/data/goldstandards/configs/goldStandardConfigTest2.gsconfig")
				.getAbsoluteFile();
		f.createNewFile();

		try {
			PrintWriter bw = new PrintWriter(new FileWriter(f));
			bw.println("goldstandardName = Test");
			bw.flush();
			bw.close();

			Parser.parseFromFile(GoldStandardConfig.class, f);
		} catch (GoldStandardConfigurationException e) {
			// Assert.assertEquals(
			// "'goldstandardFile' doesn't map to an existing object",
			// e.getMessage());
			throw e;
		} finally {
			f.delete();
		}
	}

	/**
	 * @throws IOException
	 * @throws NoRepositoryFoundException
	 * @throws GoldStandardNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDataSetTypeException
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
	 * @throws NoDataSetException
	 * @throws DataSetConfigNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 */
	@Test(expected = FileNotFoundException.class)
	public void testParseFromNotExistingFile() throws IOException,
			NoRepositoryFoundException, GoldStandardNotFoundException,
			GoldStandardConfigurationException,
			GoldStandardConfigNotFoundException, RegisterException,
			UnknownDataSetFormatException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException,
			NoDataSetException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			UnknownParameterType, UnknownClusteringQualityMeasureException,
			RunException, IncompatibleContextException,
			UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		Parser.parseFromFile(
				GoldStandardConfig.class,
				new File(
						"testCaseRepository/data/goldstandards/configs/DS1_12.gsconfig")
						.getAbsoluteFile());
	}

	/**
	 * Test method for
	 * {@link data.goldstandard.GoldStandardConfig#getGoldstandard()}.
	 * 
	 * @throws GoldStandardNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDataSetTypeException
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
	 * @throws NoDataSetException
	 * @throws DataSetConfigNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 */
	@Test
	public void testGetGoldstandard()
			throws GoldStandardConfigurationException,
			NoRepositoryFoundException, GoldStandardNotFoundException,
			GoldStandardConfigNotFoundException, RegisterException,
			UnknownDataSetFormatException, DataSetConfigurationException,
			DataSetNotFoundException, DataSetConfigNotFoundException,
			NoDataSetException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException,
			ConfigurationException, UnknownContextException,
			FileNotFoundException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		GoldStandardConfig gsConfig = Parser
				.parseFromFile(
						GoldStandardConfig.class,
						new File(
								"testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
								.getAbsoluteFile());
		GoldStandard gs = gsConfig.getGoldstandard();
		GoldStandard expected = GoldStandard
				.parseFromFile(new File(
						"testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
						.getAbsoluteFile());
		Assert.assertEquals(expected, gs);
	}

	/**
	 * Test method for {@link data.goldstandard.GoldStandardConfig#toString()}.
	 * 
	 * @throws GoldStandardNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws IOException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws RegisterException
	 * @throws UnknownRunDataStatisticException
	 *             , UnknownRunResultPostprocessorException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDataSetTypeException
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
	 * @throws NoDataSetException
	 * @throws DataSetConfigNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws UnknownDataSetFormatException
	 */
	@Test
	public void testToString() throws GoldStandardConfigurationException,
			IOException, NoRepositoryFoundException,
			GoldStandardNotFoundException, GoldStandardConfigNotFoundException,
			RegisterException, UnknownDataSetFormatException,
			DataSetConfigurationException, DataSetNotFoundException,
			DataSetConfigNotFoundException, NoDataSetException,
			DataConfigurationException, DataConfigNotFoundException,
			NumberFormatException, ConfigurationException,
			UnknownContextException, UnknownParameterType,
			UnknownClusteringQualityMeasureException, RunException,
			IncompatibleContextException, UnknownRunResultFormatException,
			InvalidOptimizationParameterException,
			UnknownProgramParameterException, UnknownProgramTypeException,
			UnknownRProgramException, UnknownDistanceMeasureException,
			UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		GoldStandardConfig gsConfig = Parser
				.parseFromFile(
						GoldStandardConfig.class,
						new File(
								"testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
								.getAbsoluteFile());
		Assert.assertEquals("DS1_1", gsConfig.toString());

	}

}
