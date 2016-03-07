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
package de.clusteval.run;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.data.DataConfig;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.run.result.postprocessing.RunResultPostprocessor;
import de.clusteval.utils.AbstractClustEvalTest;

/**
 * @author Christian Wiwie
 * 
 */
public class TestRun extends AbstractClustEvalTest {

	@Test
	public void testRun() throws RegisterException {
		/*
		 * Ensure that a run is registered in the constructor
		 */
		Run run = new ClusteringRun(this.getRepository(), context,
				System.currentTimeMillis(), new File("test"),
				new ArrayList<ProgramConfig>(), new ArrayList<DataConfig>(),
				new ArrayList<ClusteringQualityMeasure>(),
				new ArrayList<Map<ProgramParameter<?>, String>>(),
				new ArrayList<RunResultPostprocessor>(),
				new HashMap<String, Integer>());
		Assert.assertTrue(run == this.getRepository().getRegisteredObject(run));
	}
}
