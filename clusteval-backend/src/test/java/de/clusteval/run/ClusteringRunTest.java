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

import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
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
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.api.repository.RepositoryController;
import de.clusteval.api.run.IncompatibleParameterOptimizationMethodException;
import de.clusteval.api.run.RunException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.config.DefaultRepositoryConfig;
import de.clusteval.framework.repository.parse.Parser;
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

    @Test
    public void testNewParser()
            throws InterruptedException, RepositoryAlreadyExistsException,
                   InvalidRepositoryException, RepositoryConfigurationException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   DataSetConfigurationException, DataSetNotFoundException, DataSetConfigNotFoundException,
                   GoldStandardConfigNotFoundException, NoDataSetException, DataConfigurationException,
                   DataConfigNotFoundException, NumberFormatException, ConfigurationException, RegisterException,
                   UnknownParameterType, IOException, UnknownRunResultFormatException,
                   UnknownProgramParameterException, NoRepositoryFoundException,
                   InvalidOptimizationParameterException, RunException, UnknownProgramTypeException, UnknownRProgramException,
                   IncompatibleContextException,
                   IncompatibleParameterOptimizationMethodException, UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException,
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

            assertEquals(run2.getLogFilePath(), run.getLogFilePath());
            assertEquals(run2.getRunIdentificationString(), run.getRunIdentificationString());
            assertEquals(run2.getStartTime(), run.getStartTime());
            assertEquals(run2.getProgress(), run.getProgress());
            assertEquals(run2.getContext(), run.getContext());
            assertEquals(run2.getResults(), run.getResults());
            assertEquals(run2.getRunRunnables(), run.getRunRunnables());
            assertEquals(run2.getDataConfigs(), run.getDataConfigs());
            assertEquals(run2.getParameterValues(), run.getParameterValues());
            assertEquals(run2.getProgramConfigs(), run.getProgramConfigs());
            assertEquals(run2.getQualityMeasures(), run.getQualityMeasures());
            assertEquals(run2.getRunPairs(), run.getRunPairs());
            assertEquals(run2.getStatus(), run.getStatus());
        } finally {
            newRepo.terminateSupervisorThread();
        }
    }
}
