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
package de.clusteval.program.r;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import ch.qos.logback.classic.Level;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.program.ProgramConfig;
import de.clusteval.run.result.format.RunResultFormat;
import de.clusteval.utils.AbstractClustEvalTest;

/**
 * @author Christian Wiwie
 * 
 */
public class TestRProgramConfig extends AbstractClustEvalTest {

	@Test
	public void testKMeansCompatibleDataSetFormats()
			throws FileNotFoundException, RepositoryAlreadyExistsException, InvalidRepositoryException,
			RepositoryConfigNotFoundException, RepositoryConfigurationException, InterruptedException {

		ProgramConfig programConfig = this.getRepository().getStaticObjectWithName(ProgramConfig.class,
				"KMeans_Clustering");
		List<DataSetFormat> dataSetFormats = programConfig.getCompatibleDataSetFormats();
		Assert.assertEquals(1, dataSetFormats.size());
		DataSetFormat format = dataSetFormats.get(0);
		Assert.assertEquals("MatrixDataSetFormat", format.getClass().getSimpleName());
	}

	@Test
	public void testKMeansRunResultFormat()
			throws FileNotFoundException, RepositoryAlreadyExistsException, InvalidRepositoryException,
			RepositoryConfigNotFoundException, RepositoryConfigurationException, InterruptedException {

		ProgramConfig programConfig = this.getRepository().getStaticObjectWithName(ProgramConfig.class,
				"KMeans_Clustering");
		RunResultFormat format = programConfig.getOutputFormat();
		Assert.assertEquals("TabSeparatedRunResultFormat", format.getClass().getSimpleName());
	}
}
