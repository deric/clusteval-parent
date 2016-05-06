/** *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ***************************************************************************** */
package de.clusteval.run;

import de.clusteval.api.run.RunException;
import de.clusteval.api.opt.ParameterOptimizationRun;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.RepositoryController;
import de.clusteval.api.run.IncompatibleParameterOptimizationMethodException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.config.DefaultRepositoryConfig;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.utils.AbstractClustEvalTest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class ParameterOptimizationRunTest extends AbstractClustEvalTest {

    @Test
    public void test() throws GoldStandardNotFoundException, GoldStandardConfigurationException,
                              DataSetConfigurationException, DataSetNotFoundException,
                              DataSetConfigNotFoundException,
                              GoldStandardConfigNotFoundException, NoDataSetException,
                              DataConfigurationException, DataConfigNotFoundException,
                              NumberFormatException, ConfigurationException, RegisterException,
                              UnknownParameterType, IOException,
                              UnknownRunResultFormatException,
                              UnknownParameterOptimizationMethodException,
                              NoOptimizableProgramParameterException,
                              UnknownProgramParameterException, NoRepositoryFoundException,
                              InvalidOptimizationParameterException, RunException,
                              UnknownProgramTypeException, UnknownRProgramException,
                              IncompatibleParameterOptimizationMethodException,
                              UnknownProviderException,
                              IncompatibleDataSetConfigPreprocessorException,
                              IncompatibleContextException,
                              UnknownRunResultPostprocessorException {
        ParameterOptimizationRun run = Parser.parseFromFile(
                ParameterOptimizationRun.class, new File(
                        "testCaseRepository/runs/testTwiceTheParam.run")
                .getAbsoluteFile());
        List<IProgramParameter<?>> paramList = new ArrayList<>();

        paramList.add(run.programConfigs.get(0).getParameterForName("T"));

        List<List<IProgramParameter<?>>> expected = new ArrayList<>();
        expected.add(paramList);
        assertEquals(expected, run.optimizationParameters);
    }

    @Test
    public void testNewParser() throws
            GoldStandardNotFoundException, GoldStandardConfigurationException,
            DataSetConfigurationException, DataSetNotFoundException,
            DataSetConfigNotFoundException,
            GoldStandardConfigNotFoundException, NoDataSetException,
            NumberFormatException, DataConfigurationException,
            DataConfigNotFoundException, RegisterException,
            ConfigurationException,
            UnknownParameterType,
            IncompatibleParameterOptimizationMethodException,
            UnknownParameterOptimizationMethodException, RunException,
            NoOptimizableProgramParameterException,
            UnknownProgramParameterException, UnknownRunResultFormatException,
            NoRepositoryFoundException, InvalidOptimizationParameterException,
            UnknownProgramTypeException, UnknownRProgramException,
            IncompatibleContextException,
            UnknownProviderException,
            IncompatibleDataSetConfigPreprocessorException, IOException,
            InterruptedException, RepositoryAlreadyExistsException,
            InvalidRepositoryException, RepositoryConfigNotFoundException,
            RepositoryConfigurationException,
            UnknownRunResultPostprocessorException,
            DatabaseConnectException {
        ParameterOptimizationRun run = Parser.parseFromFile(
                ParameterOptimizationRun.class, new File(
                        "testCaseRepository/runs/baechler2003.run")
                .getAbsoluteFile());

        getRepository().terminateSupervisorThread();

        RepositoryController.getInstance().unregister(getRepository());

        Repository newRepo = new Repository(
                new File("testCaseRepository").getAbsolutePath(), null, new DefaultRepositoryConfig());
        newRepo.initialize();
        try {

            ParameterOptimizationRun run2 = Parser.parseFromFile(
                    ParameterOptimizationRun.class, new File(
                            "testCaseRepository/runs/baechler2003.run")
                    .getAbsoluteFile());

            assertEquals(run2.logFilePath, run.logFilePath);
            assertEquals(run2.runIdentString, run.runIdentString);
            assertEquals(run2.startTime, run.startTime);
            assertEquals(run2.progress, run.progress);
            assertEquals(run2.context, run.context);
            assertEquals(run2.results, run.results);
            assertEquals(run2.runnables, run.runnables);
            assertEquals(run2.dataConfigs, run.dataConfigs);
            assertEquals(run2.optimizationMethods,
                    run.optimizationMethods);
            assertEquals(run2.optimizationParameters,
                    run.optimizationParameters);
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
