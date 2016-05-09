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
import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
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
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.api.run.IncompatibleParameterOptimizationMethodException;
import de.clusteval.api.run.RunException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.utils.AbstractClustEvalTest;
import java.io.File;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class RemoveZeroSamplesDataPreprocesserTest
        extends
        AbstractClustEvalTest {

    @Test
    public void test()
            throws RepositoryAlreadyExistsException,
                   InvalidRepositoryException, RepositoryConfigurationException, DataSetNotFoundException,
                   DataSetConfigurationException, NoDataSetException, NumberFormatException, RegisterException,
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
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException,
                   RException {

        File f = new File(
                "testCaseRepository/data/datasets/sfld/sfld_brown_et_al_amidohydrolases_protein_similarities_for_beh.txt.SimMatrix");

        IDataSet ds = Parser.parseFromFile(IDataSet.class, f);

        DataSetAttributeFilterer filterer = new DataSetAttributeFilterer(
                f.getAbsolutePath());
        filterer.process();

        ds.setAbsolutePath(new File(
                "testCaseRepository/data/datasets/sfld/sfld_brown_et_al_amidohydrolases_protein_similarities_for_beh.txt.SimMatrix.strip"));

        DataPreprocessor proc = DataPreprocessorFactory.parseFromString(
                this.getRepository(), "RemoveZeroSamplesDataPreprocessor");

        IDataSet newDs = proc.preprocess(ds);
        Assert.assertTrue(new File(newDs.getAbsolutePath()).exists());
    }
}
