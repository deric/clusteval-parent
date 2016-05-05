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
package de.clusteval.data.preprocessing;

import de.clusteval.api.data.DataPreprocessor;
import de.clusteval.api.data.DataPreprocessorFactory;
import de.clusteval.api.data.DataSetAttributeFilterer;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.exceptions.DataSetNotFoundException;
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
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.run.RunException;
import de.clusteval.utils.AbstractClustEvalTest;
import java.io.File;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class RangeNormalizationDataPreprocesserTest extends AbstractClustEvalTest {

    @Test
    public void test() throws RepositoryAlreadyExistsException,
                              InvalidRepositoryException, RepositoryConfigNotFoundException,
                              RepositoryConfigurationException, DataSetNotFoundException,
                              DataSetConfigurationException,
                              NoDataSetException, NumberFormatException, RegisterException,
                              NoRepositoryFoundException, UnknownProviderException,
                              IOException,
                              InterruptedException, GoldStandardNotFoundException,
                              GoldStandardConfigurationException, DataSetConfigNotFoundException,
                              GoldStandardConfigNotFoundException, DataConfigurationException,
                              DataConfigNotFoundException, ConfigurationException,
                              UnknownParameterType, RunException,
                              IncompatibleContextException, UnknownRunResultFormatException,
                              InvalidOptimizationParameterException,
                              UnknownProgramParameterException, UnknownProgramTypeException,
                              UnknownRProgramException,
                              IncompatibleDataSetConfigPreprocessorException,
                              IncompatibleParameterOptimizationMethodException,
                              UnknownParameterOptimizationMethodException,
                              NoOptimizableProgramParameterException,
                              UnknownRunResultPostprocessorException,
                              RException {

        File f = new File(
                "testCaseRepository/data/datasets/synthetic/cassini250");

        IDataSet ds = Parser.parseFromFile(IDataSet.class, f);

        DataSetAttributeFilterer filterer = new DataSetAttributeFilterer(
                f.getAbsolutePath());
        filterer.process();

        ds.setAbsolutePath(new File(
                "testCaseRepository/data/datasets/synthetic/cassini250.strip"));

        DataPreprocessor proc = DataPreprocessorFactory.parseFromString(
                this.getRepository(), "RangeNormalizationDataPreprocessor");

        IDataSet newDs = proc.preprocess(ds);
        assertTrue(new File(newDs.getAbsolutePath()).exists());
    }
}
