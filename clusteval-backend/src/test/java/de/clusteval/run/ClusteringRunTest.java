/**
 * *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 *****************************************************************************
 */
package de.clusteval.run;

import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.stats.UnknownDataStatisticException;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.api.repository.RepositoryController;
import de.clusteval.framework.repository.config.DefaultRepositoryConfig;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.clusteval.utils.AbstractClustEvalTest;
import java.io.File;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class ClusteringRunTest extends AbstractClustEvalTest {

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
    public void testNewParser()
            throws InterruptedException, RepositoryAlreadyExistsException,
                   InvalidRepositoryException, RepositoryConfigNotFoundException, RepositoryConfigurationException,
                   UnknownDataSetFormatException, GoldStandardNotFoundException, GoldStandardConfigurationException,
                   DataSetConfigurationException, DataSetNotFoundException, DataSetConfigNotFoundException,
                   GoldStandardConfigNotFoundException, NoDataSetException, DataConfigurationException,
                   DataConfigNotFoundException, NumberFormatException, ConfigurationException, RegisterException,
                   UnknownParameterType, IOException, UnknownRunResultFormatException,
                   UnknownClusteringQualityMeasureException, UnknownProgramParameterException, NoRepositoryFoundException,
                   InvalidOptimizationParameterException, RunException, UnknownProgramTypeException, UnknownRProgramException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleContextException,
                   IncompatibleParameterOptimizationMethodException, UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException, UnknownDataStatisticException, UnknownRunStatisticException,
                   UnknownRunDataStatisticException, UnknownRunResultPostprocessorException, UnknownDataRandomizerException,
                   DatabaseConnectException, UnknownProviderException {
        ClusteringRun run = Parser.parseFromFile(ClusteringRun.class,
                new File("testCaseRepository/runs/all_vs_DS1_clustering.run").getAbsoluteFile());

        getRepository().terminateSupervisorThread();

        RepositoryController.getInstance().unregister(getRepository());

        Repository newRepo = new Repository(new File("testCaseRepository").getAbsolutePath(), null,
                new DefaultRepositoryConfig());
        newRepo.initialize();
        try {

            ClusteringRun run2 = Parser.parseFromFile(ClusteringRun.class,
                    new File("testCaseRepository/runs/all_vs_DS1_clustering.run").getAbsoluteFile());

            assertEquals(run2.logFilePath, run.logFilePath);
            assertEquals(run2.runIdentString, run.runIdentString);
            assertEquals(run2.startTime, run.startTime);
            assertEquals(run2.progress, run.progress);
            assertEquals(run2.context, run.context);
            assertEquals(run2.results, run.results);
            assertEquals(run2.runnables, run.runnables);
            assertEquals(run2.dataConfigs, run.dataConfigs);
            assertEquals(run2.parameterValues, run.parameterValues);
            assertEquals(run2.programConfigs, run.programConfigs);
            assertEquals(run2.qualityMeasures, run.qualityMeasures);
            assertEquals(run2.runPairs, run.runPairs);
            assertEquals(run2.status, run.status);
        } finally {
            newRepo.terminateSupervisorThread();
        }
    }
}
