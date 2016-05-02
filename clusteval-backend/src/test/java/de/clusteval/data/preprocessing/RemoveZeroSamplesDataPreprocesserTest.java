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

import de.clusteval.api.data.IDataPreprocessor;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.stats.UnknownDataStatisticException;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetAttributeFilterer;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.type.UnknownDataSetTypeException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.run.RunException;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
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
    public void test() throws RepositoryAlreadyExistsException,
                              InvalidRepositoryException, RepositoryConfigNotFoundException,
                              RepositoryConfigurationException, DataSetNotFoundException,
                              UnknownDataSetFormatException, DataSetConfigurationException,
                              NoDataSetException, NumberFormatException, RegisterException,
                              NoRepositoryFoundException, UnknownDataSetTypeException,
                              UnknownDataPreprocessorException, IOException,
                              InterruptedException, GoldStandardNotFoundException,
                              GoldStandardConfigurationException, DataSetConfigNotFoundException,
                              GoldStandardConfigNotFoundException, DataConfigurationException,
                              DataConfigNotFoundException, ConfigurationException,
                              UnknownContextException, UnknownParameterType,
                              UnknownClusteringQualityMeasureException, RunException,
                              IncompatibleContextException, UnknownRunResultFormatException,
                              InvalidOptimizationParameterException,
                              UnknownProgramParameterException, UnknownProgramTypeException,
                              UnknownRProgramException, UnknownDistanceMeasureException,
                              IncompatibleDataSetConfigPreprocessorException,
                              IncompatibleParameterOptimizationMethodException,
                              UnknownParameterOptimizationMethodException,
                              NoOptimizableProgramParameterException,
                              UnknownDataStatisticException, UnknownRunStatisticException,
                              UnknownRunDataStatisticException,
                              UnknownRunResultPostprocessorException,
                              UnknownDataRandomizerException, RException {

        File f = new File(
                "testCaseRepository/data/datasets/sfld/sfld_brown_et_al_amidohydrolases_protein_similarities_for_beh.txt.SimMatrix");

        DataSet ds = Parser.parseFromFile(DataSet.class, f);

        DataSetAttributeFilterer filterer = new DataSetAttributeFilterer(
                f.getAbsolutePath());
        filterer.process();

        ds.setAbsolutePath(new File(
                "testCaseRepository/data/datasets/sfld/sfld_brown_et_al_amidohydrolases_protein_similarities_for_beh.txt.SimMatrix.strip"));

        IDataPreprocessor proc = DataPreprocessor.parseFromString(
                this.getRepository(), "RemoveZeroSamplesDataPreprocessor");

        IDataSet newDs = proc.preprocess(ds);
        Assert.assertTrue(new File(newDs.getAbsolutePath()).exists());
    }
}
