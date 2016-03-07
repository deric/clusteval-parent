/**
 * 
 */
package de.clusteval.run;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import de.clusteval.data.goldstandard.GoldStandardConfigNotFoundException;
import de.clusteval.data.goldstandard.GoldStandardConfigurationException;
import de.clusteval.data.goldstandard.GoldStandardNotFoundException;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.data.statistics.UnknownDataStatisticException;
import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.config.DefaultRepositoryConfig;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.db.DatabaseConnectException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.program.NoOptimizableProgramParameterException;
import de.clusteval.program.ProgramParameter;
import de.clusteval.program.UnknownParameterType;
import de.clusteval.program.UnknownProgramParameterException;
import de.clusteval.program.UnknownProgramTypeException;
import de.clusteval.program.r.UnknownRProgramException;
import de.clusteval.run.result.format.UnknownRunResultFormatException;
import de.clusteval.run.result.postprocessing.UnknownRunResultPostprocessorException;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.clusteval.utils.AbstractClustEvalTest;

/**
 * @author Christian Wiwie
 * 
 */
public class TestParameterOptimizationRun extends AbstractClustEvalTest {

	@Test
	public void test() throws UnknownDataSetFormatException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigurationException, DataSetNotFoundException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, NoDataSetException,
			DataConfigurationException, DataConfigNotFoundException,
			NumberFormatException, ConfigurationException, RegisterException,
			UnknownContextException, UnknownParameterType, IOException,
			UnknownRunResultFormatException,
			UnknownClusteringQualityMeasureException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownProgramParameterException, NoRepositoryFoundException,
			InvalidOptimizationParameterException, RunException,
			UnknownProgramTypeException, UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleContextException, UnknownDataStatisticException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException {
		ParameterOptimizationRun run = Parser.parseFromFile(
				ParameterOptimizationRun.class, new File(
						"testCaseRepository/runs/testTwiceTheParam.run")
						.getAbsoluteFile());
		List<ProgramParameter<?>> paramList = new ArrayList<ProgramParameter<?>>();

		paramList.add(run.programConfigs.get(0).getParameterForName("T"));

		List<List<ProgramParameter<?>>> expected = new ArrayList<List<ProgramParameter<?>>>();
		expected.add(paramList);
		Assert.assertEquals(expected, run.optimizationParameters);
	}

	/**
	 * @throws UnknownDataSetFormatException
	 * @throws GoldStandardNotFoundException
	 * @throws GoldStandardConfigurationException
	 * @throws DataSetConfigurationException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws NoDataSetException
	 * @throws NumberFormatException
	 * @throws DataConfigurationException
	 * @throws DataConfigNotFoundException
	 * @throws RegisterException
	 * @throws ConfigurationException
	 * @throws UnknownContextException
	 * @throws UnknownParameterType
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws RunException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownProgramParameterException
	 * @throws UnknownRunResultFormatException
	 * @throws NoRepositoryFoundException
	 * @throws InvalidOptimizationParameterException
	 * @throws UnknownProgramTypeException
	 * @throws UnknownRProgramException
	 * @throws IncompatibleContextException
	 * @throws UnknownDistanceMeasureException
	 * @throws UnknownDataSetTypeException
	 * @throws UnknownDataPreprocessorException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws RepositoryConfigurationException
	 * @throws RepositoryConfigNotFoundException
	 * @throws InvalidRepositoryException
	 * @throws RepositoryAlreadyExistsException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws DatabaseConnectException
	 */
	@Test
	public void testNewParser() throws UnknownDataSetFormatException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigurationException, DataSetNotFoundException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, NoDataSetException,
			NumberFormatException, DataConfigurationException,
			DataConfigNotFoundException, RegisterException,
			ConfigurationException, UnknownContextException,
			UnknownParameterType,
			IncompatibleParameterOptimizationMethodException,
			UnknownParameterOptimizationMethodException, RunException,
			UnknownClusteringQualityMeasureException,
			NoOptimizableProgramParameterException,
			UnknownProgramParameterException, UnknownRunResultFormatException,
			NoRepositoryFoundException, InvalidOptimizationParameterException,
			UnknownProgramTypeException, UnknownRProgramException,
			IncompatibleContextException, UnknownDistanceMeasureException,
			UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IOException,
			InterruptedException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, UnknownDataStatisticException,
			UnknownRunStatisticException, UnknownRunDataStatisticException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException, DatabaseConnectException {
		ParameterOptimizationRun run = Parser.parseFromFile(
				ParameterOptimizationRun.class, new File(
						"testCaseRepository/runs/baechler2003.run")
						.getAbsoluteFile());

		getRepository().terminateSupervisorThread();

		de.clusteval.framework.repository.Repository
				.unregister(getRepository());

		Repository newRepo = new Repository(
				new File("testCaseRepository").getAbsolutePath(), null, new DefaultRepositoryConfig());
		newRepo.initialize();
		try {

			ParameterOptimizationRun run2 = Parser.parseFromFile(
					ParameterOptimizationRun.class, new File(
							"testCaseRepository/runs/baechler2003.run")
							.getAbsoluteFile());

			Assert.assertEquals(run2.logFilePath, run.logFilePath);
			Assert.assertEquals(run2.runIdentString, run.runIdentString);
			Assert.assertEquals(run2.startTime, run.startTime);
			Assert.assertEquals(run2.progress, run.progress);
			Assert.assertEquals(run2.context, run.context);
			Assert.assertEquals(run2.results, run.results);
			Assert.assertEquals(run2.runnables, run.runnables);
			Assert.assertEquals(run2.dataConfigs, run.dataConfigs);
			Assert.assertEquals(run2.optimizationMethods,
					run.optimizationMethods);
			Assert.assertEquals(run2.optimizationParameters,
					run.optimizationParameters);
			Assert.assertEquals(run2.parameterValues, run.parameterValues);
			Assert.assertEquals(run2.programConfigs, run.programConfigs);
			Assert.assertEquals(run2.qualityMeasures, run.qualityMeasures);
			Assert.assertEquals(run2.runPairs, run.runPairs);
			Assert.assertEquals(run2.status, run.status);
		} finally {
			newRepo.terminateSupervisorThread();
		}
	}
}
