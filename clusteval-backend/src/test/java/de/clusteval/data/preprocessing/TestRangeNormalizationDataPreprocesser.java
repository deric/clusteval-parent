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
package de.clusteval.data.preprocessing;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.InvalidOptimizationParameterException;
import de.clusteval.cluster.paramOptimization.UnknownParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.context.IncompatibleContextException;
import de.clusteval.context.UnknownContextException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetAttributeFilterer;
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
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.data.statistics.UnknownDataStatisticException;
import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
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

/**
 * @author Christian Wiwie
 * 
 */
public class TestRangeNormalizationDataPreprocesser
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
			UnknownDataRandomizerException {

		File f = new File(
				"testCaseRepository/data/datasets/synthetic/cassini250");

		DataSet ds = Parser.parseFromFile(DataSet.class, f);

		DataSetAttributeFilterer filterer = new DataSetAttributeFilterer(
				f.getAbsolutePath());
		filterer.process();

		ds.setAbsolutePath(new File(
				"testCaseRepository/data/datasets/synthetic/cassini250.strip"));

		DataPreprocessor proc = DataPreprocessor.parseFromString(
				this.getRepository(), "RangeNormalizationDataPreprocessor");

		DataSet newDs = proc.preprocess(ds);
		Assert.assertTrue(new File(newDs.getAbsolutePath()).exists());
	}
}
