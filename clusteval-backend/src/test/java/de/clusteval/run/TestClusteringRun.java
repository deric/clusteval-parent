/**
 * 
 */
package de.clusteval.run;

import java.io.File;
import java.io.IOException;

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
import de.clusteval.framework.repository.db.SQLConfig;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.program.NoOptimizableProgramParameterException;
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
public class TestClusteringRun extends AbstractClustEvalTest {

	/**
	 * @throws NumberFormatException
	 * @throws InterruptedException
	 * @throws RepositoryConfigurationException
	 * @throws RepositoryConfigNotFoundException
	 * @throws InvalidRepositoryException
	 * @throws RepositoryAlreadyExistsException
	 * @throws IncompatibleContextException
	 * @throws IncompatibleDataSetConfigPreprocessorException
	 * @throws UnknownDataPreprocessorException
	 * @throws UnknownDataSetTypeException
	 * @throws UnknownDistanceMeasureException
	 * @throws UnknownRProgramException
	 * @throws UnknownProgramTypeException
	 * @throws RunException
	 * @throws InvalidOptimizationParameterException
	 * @throws NoRepositoryFoundException
	 * @throws UnknownProgramParameterException
	 * @throws UnknownClusteringQualityMeasureException
	 * @throws UnknownRunResultFormatException
	 * @throws IOException
	 * @throws UnknownParameterType
	 * @throws UnknownContextException
	 * @throws RegisterException
	 * @throws ConfigurationException
	 * @throws DataConfigNotFoundException
	 * @throws DataConfigurationException
	 * @throws NoDataSetException
	 * @throws GoldStandardConfigNotFoundException
	 * @throws DataSetConfigNotFoundException
	 * @throws DataSetNotFoundException
	 * @throws DataSetConfigurationException
	 * @throws GoldStandardConfigurationException
	 * @throws GoldStandardNotFoundException
	 * @throws UnknownDataSetFormatException
	 * @throws UnknownRunDataStatisticException
	 * @throws UnknownRunStatisticException
	 * @throws UnknownDataStatisticException
	 * @throws NoOptimizableProgramParameterException
	 * @throws UnknownParameterOptimizationMethodException
	 * @throws IncompatibleParameterOptimizationMethodException
	 * @throws DatabaseConnectException
	 */
	@Test
	public void testNewParser() throws InterruptedException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException, RepositoryConfigurationException,
			UnknownDataSetFormatException, GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigurationException, DataSetNotFoundException, DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, NoDataSetException, DataConfigurationException,
			DataConfigNotFoundException, NumberFormatException, ConfigurationException, RegisterException,
			UnknownContextException, UnknownParameterType, IOException, UnknownRunResultFormatException,
			UnknownClusteringQualityMeasureException, UnknownProgramParameterException, NoRepositoryFoundException,
			InvalidOptimizationParameterException, RunException, UnknownProgramTypeException, UnknownRProgramException,
			UnknownDistanceMeasureException, UnknownDataSetTypeException, UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException, IncompatibleContextException,
			IncompatibleParameterOptimizationMethodException, UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException, UnknownDataStatisticException, UnknownRunStatisticException,
			UnknownRunDataStatisticException, UnknownRunResultPostprocessorException, UnknownDataRandomizerException,
			DatabaseConnectException {
		ClusteringRun run = Parser.parseFromFile(ClusteringRun.class,
				new File("testCaseRepository/runs/all_vs_DS1_clustering.run").getAbsoluteFile());

		getRepository().terminateSupervisorThread();

		de.clusteval.framework.repository.Repository.unregister(getRepository());

		Repository newRepo = new Repository(new File("testCaseRepository").getAbsolutePath(), null,
				new DefaultRepositoryConfig());
		newRepo.initialize();
		try {

			ClusteringRun run2 = Parser.parseFromFile(ClusteringRun.class,
					new File("testCaseRepository/runs/all_vs_DS1_clustering.run").getAbsoluteFile());

			Assert.assertEquals(run2.logFilePath, run.logFilePath);
			Assert.assertEquals(run2.runIdentString, run.runIdentString);
			Assert.assertEquals(run2.startTime, run.startTime);
			Assert.assertEquals(run2.progress, run.progress);
			Assert.assertEquals(run2.context, run.context);
			Assert.assertEquals(run2.results, run.results);
			Assert.assertEquals(run2.runnables, run.runnables);
			Assert.assertEquals(run2.dataConfigs, run.dataConfigs);
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
