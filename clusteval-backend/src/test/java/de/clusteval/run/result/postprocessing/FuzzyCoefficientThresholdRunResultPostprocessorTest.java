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
package de.clusteval.run.result.postprocessing;

import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
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
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.api.run.IncompatibleParameterOptimizationMethodException;
import de.clusteval.api.run.RunException;
import de.clusteval.api.run.result.RunResultPostprocessor;
import de.clusteval.api.run.result.RunResultPostprocessorParameters;
import de.clusteval.cluster.Clustering;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
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
public class FuzzyCoefficientThresholdRunResultPostprocessorTest extends AbstractClustEvalTest {

    @Test
    public void test() throws RepositoryAlreadyExistsException,
                              InvalidRepositoryException,
                              RepositoryConfigurationException, DataSetNotFoundException,
                              DataSetConfigurationException,
                              NoDataSetException, NumberFormatException, RegisterException,
                              NoRepositoryFoundException, IOException,
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
                              UnknownRunResultPostprocessorException {

        Clustering clustering = new Clustering(this.getRepository(),
                System.currentTimeMillis(), new File(""));
        Cluster cluster1 = new Cluster("1");
        cluster1.add(new ClusterItem("id1"), 0.7f);
        cluster1.add(new ClusterItem("id2"), 0.5f);
        cluster1.add(new ClusterItem("id3"), 0.0f);
        clustering.addCluster(cluster1);

        Cluster cluster2 = new Cluster("2");
        cluster2.add(new ClusterItem("id1"), 0.3f);
        cluster2.add(new ClusterItem("id2"), 0.5f);
        cluster2.add(new ClusterItem("id3"), 1.0f);
        clustering.addCluster(cluster2);

        Clustering expected = new Clustering(this.getRepository(),
                System.currentTimeMillis(), new File(""));
        Cluster expectedCluster1 = new Cluster("1");
        expectedCluster1.add(new ClusterItem("id1"), 1.0f);
        expectedCluster1.add(new ClusterItem("id2"), 0.5f);
        expected.addCluster(expectedCluster1);

        Cluster expectedCluster2 = new Cluster("2");
        expectedCluster2.add(new ClusterItem("id2"), 0.5f);
        expectedCluster2.add(new ClusterItem("id3"), 1.0f);
        expected.addCluster(expectedCluster2);

        RunResultPostprocessorParameters params = new RunResultPostprocessorParameters();
        params.put("threshold", "0.5");

        RunResultPostprocessor proc = RunResultPostprocessor.parseFromString(
                this.getRepository(),
                "FuzzyCoefficientThresholdRunResultPostprocessor", params);

        IClustering postprocessed = proc.postprocess(clustering);
        assertEquals(expected, postprocessed);
    }

    // TODO: choose another file and add asserts
    //@Test
    public void test2() throws IOException,
                               UnknownRunResultPostprocessorException {
        Clustering clustering = Clustering
                .parseFromFile(
                        null,
                        new File(
                                "testCaseRepository/results/04_01_2015-10_19_51_martin_spearman_nonabsolute_fanny_50cl_threshold/clusters/Fanny_Clustering_3membexp_50cl_martin_spearman.5.results.conv")
                        .getAbsoluteFile(), false).getSecond();

        RunResultPostprocessorParameters params = new RunResultPostprocessorParameters();
        params.put("threshold", "0.5");

        RunResultPostprocessor proc = RunResultPostprocessor.parseFromString(
                this.getRepository(),
                "FuzzyCoefficientThresholdRunResultPostprocessor", params);

        IClustering postprocessed = proc.postprocess(clustering);
        System.out.println(postprocessed);

    }
}
