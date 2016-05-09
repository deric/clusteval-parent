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
package de.clusteval.data.dataset;

import de.clusteval.api.Precision;
import de.clusteval.api.data.DataSetConfig;
import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
import de.clusteval.api.data.DistanceMeasureFactory;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.InputToStd;
import de.clusteval.api.data.RelativeDataSet;
import de.clusteval.api.data.StdToInput;
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
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.RepositoryRemoveEvent;
import de.clusteval.api.repository.RepositoryReplaceEvent;
import de.clusteval.api.run.IncompatibleParameterOptimizationMethodException;
import de.clusteval.api.run.RunException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.utils.AbstractClustEvalTest;
import de.clusteval.utils.StubRepositoryObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.commons.configuration.ConfigurationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class DataSetConfigTest extends AbstractClustEvalTest {

    public void testRegister() throws DataSetConfigurationException,
                                      NoRepositoryFoundException, DataSetNotFoundException,
                                      DataSetConfigNotFoundException,
                                      RegisterException, NoDataSetException,
                                      NumberFormatException,
                                      GoldStandardNotFoundException, GoldStandardConfigurationException,
                                      GoldStandardConfigNotFoundException, DataConfigurationException,
                                      DataConfigNotFoundException, ConfigurationException,
                                      FileNotFoundException, UnknownParameterType,
                                      RunException, IncompatibleContextException,
                                      UnknownRunResultFormatException,
                                      InvalidOptimizationParameterException,
                                      UnknownProgramParameterException, UnknownProgramTypeException,
                                      UnknownRProgramException,
                                      IncompatibleParameterOptimizationMethodException,
                                      UnknownParameterOptimizationMethodException,
                                      NoOptimizableProgramParameterException,
                                      UnknownRunResultPostprocessorException,
                                      UnknownProviderException {
        this.repositoryObject = Parser
                .parseFromFile(
                        DataSetConfig.class,
                        new File(
                                "testCaseRepository/data/datasets/configs/astral_1.dsconfig")
                        .getAbsoluteFile());
        assertEquals(this.repositoryObject, this.getRepository()
                .getRegisteredObject((DataSetConfig) this.repositoryObject));

        // adding a DataSetConfig equal to another one already registered
        // does
        // not register the second object.
        this.repositoryObject = new DataSetConfig(
                (DataSetConfig) this.repositoryObject);
        assertEquals(
                this.getRepository().getRegisteredObject(
                        (DataSetConfig) this.repositoryObject),
                this.repositoryObject);
        assertFalse(this.getRepository().getRegisteredObject(
                (DataSetConfig) this.repositoryObject) == this.repositoryObject);
    }

    public void testUnregister() throws DataSetConfigurationException,
                                        NoRepositoryFoundException, DataSetNotFoundException,
                                        DataSetConfigNotFoundException,
                                        RegisterException, NoDataSetException,
                                        NumberFormatException,
                                        GoldStandardNotFoundException, GoldStandardConfigurationException,
                                        GoldStandardConfigNotFoundException, DataConfigurationException,
                                        DataConfigNotFoundException, ConfigurationException,
                                        FileNotFoundException, UnknownParameterType,
                                        RunException, IncompatibleContextException,
                                        UnknownRunResultFormatException,
                                        InvalidOptimizationParameterException,
                                        UnknownProgramParameterException, UnknownProgramTypeException,
                                        UnknownRProgramException,
                                        IncompatibleParameterOptimizationMethodException,
                                        UnknownParameterOptimizationMethodException,
                                        NoOptimizableProgramParameterException,
                                        UnknownRunResultPostprocessorException,
                                        UnknownProviderException {
        this.repositoryObject = Parser
                .parseFromFile(
                        DataSetConfig.class,
                        new File(
                                "testCaseRepository/data/datasets/configs/astral_1.dsconfig")
                        .getAbsoluteFile());
        assertEquals(this.repositoryObject, this.getRepository()
                .getRegisteredObject((DataSetConfig) this.repositoryObject));
        this.repositoryObject.unregister();
        // is not registered anymore
        assertTrue(this.getRepository().getRegisteredObject(
                (DataSetConfig) this.repositoryObject) == null);
    }

    @Test
    public void testNotifyRepositoryEvent()
            throws NoRepositoryFoundException,
                   DataSetNotFoundException, DataSetConfigurationException,
                   DataSetConfigNotFoundException,
                   RegisterException, UnknownProviderException, NoDataSetException,
                   NumberFormatException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, ConfigurationException,
                   FileNotFoundException, UnknownParameterType,
                   RunException, IncompatibleContextException,
                   UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException {

        /*
         * REPLACE
         */

 /*
         * First check, whether listeners of DataSetconfigs are notified
         * correctly when the DataSetconfig is replaced
         */
        DataSetConfig gsConfig = Parser
                .parseFromFile(
                        DataSetConfig.class,
                        new File(
                                "testCaseRepository/data/datasets/configs/astral_1.dsconfig")
                        .getAbsoluteFile());
        StubRepositoryObject child = new StubRepositoryObject(getRepository(),
                false, System.currentTimeMillis(), new File(
                        "testCaseRepository/Bla"));
        gsConfig.addListener(child);

        DataSetConfig gsConfig2 = new DataSetConfig(gsConfig);

        gsConfig.notify(new RepositoryReplaceEvent(gsConfig, gsConfig2));
        assertTrue(child.notified);

        /*
         * Now check, whether DataSet configs update their references correctly,
         * when their DataSet is replaced
         */
        RelativeDataSet gs = (RelativeDataSet) (gsConfig.getDataSet());
        RelativeDataSet gs2 = new RelativeDataSet(gs);

        gsConfig.notify(new RepositoryReplaceEvent(gs, gs2));

        assertFalse(gsConfig.getDataSet() == gs);
        assertTrue(gsConfig.getDataSet() == gs2);

        /*
         * REMOVE
         */

 /*
         * First check, whether listeners of DataSetconfigs are notified
         * correctly when the DataSetconfig is replaced
         */
        child.notified = false;
        gsConfig.notify(new RepositoryRemoveEvent(gsConfig));
        assertTrue(child.notified);

        /*
         * Now check, whether DataSet configs remove themselves when their
         * DataSet is removed
         */
        // gsconfig has to be registered
        assertTrue(getRepository().getRegisteredObject(gsConfig) == gsConfig);

        gsConfig.notify(new RepositoryRemoveEvent(gs2));

        // not registered anymore
        assertTrue(getRepository().getRegisteredObject(gsConfig) == null);
    }

    @Test(expected = DataSetConfigurationException.class)
    public void testParseFromFileDataSetNameMissing()
            throws DataSetConfigurationException, NoRepositoryFoundException,
                   DataSetNotFoundException, DataSetConfigNotFoundException,
                   RegisterException, UnknownProviderException, NoDataSetException,
                   NumberFormatException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
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
                   UnknownRunResultPostprocessorException, IOException {
        // create empty file
        File f = new File(
                "testCaseRepository/data/datasets/configs/testDataSetConfig.dsconfig")
                .getAbsoluteFile();
        f.createNewFile();
        Parser.parseFromFile(DataSetConfig.class, f);
        f.delete();
    }

    @Test
    public void testParseFromFile()
            throws DataSetConfigurationException,
                   NoRepositoryFoundException, DataSetNotFoundException,
                   DataSetConfigNotFoundException,
                   RegisterException, UnknownProviderException, NoDataSetException,
                   NumberFormatException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, ConfigurationException,
                   FileNotFoundException, UnknownParameterType,
                   RunException, IncompatibleContextException,
                   UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException {
        DataSetConfig gsConfig = Parser
                .parseFromFile(
                        DataSetConfig.class,
                        new File(
                                "testCaseRepository/data/datasets/configs/astral_1.dsconfig")
                        .getAbsoluteFile());
        assertEquals(new DataSetConfig(
                getRepository(),
                new File(
                        "testCaseRepository/data/datasets/configs/astral_1.dsconfig")
                .getAbsoluteFile().lastModified(),
                new File(
                        "testCaseRepository/data/datasets/configs/astral_1.dsconfig")
                .getAbsoluteFile(),
                Parser.parseFromFile(
                        IDataSet.class, new File(
                                "testCaseRepository/data/datasets/astral_1_161/blastResults.txt")
                        .getAbsoluteFile()),
                new InputToStd(
                        DistanceMeasureFactory.parseFromString(
                                getRepository(),
                                "EuclidianDistanceMeasure"),
                        Precision.DOUBLE,
                        new ArrayList<>(),
                        new ArrayList<>()),
                new StdToInput()), gsConfig);
    }

    @Test(expected = DataSetConfigurationException.class)
    public void testParseFromFileDataSetFileMissing()
            throws DataSetConfigurationException, NoRepositoryFoundException,
                   DataSetNotFoundException, DataSetConfigNotFoundException,
                   RegisterException, UnknownProviderException, NoDataSetException,
                   NumberFormatException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
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
                   UnknownRunResultPostprocessorException, IOException {

        File f = new File(
                "testCaseRepository/data/datasets/configs/testDataSetConfig2.dsconfig")
                .getAbsoluteFile();
        f.createNewFile();

        PrintWriter bw = new PrintWriter(new FileWriter(f));
        bw.println("datasetName = Test");
        bw.flush();
        bw.close();
        Parser.parseFromFile(DataSetConfig.class, f);
        f.delete();
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseFromNotExistingFile()
            throws NoRepositoryFoundException, DataSetNotFoundException,
                   DataSetConfigurationException, DataSetConfigNotFoundException,
                   RegisterException, UnknownProviderException, NoDataSetException,
                   NumberFormatException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, ConfigurationException,
                   FileNotFoundException, UnknownParameterType,
                   RunException, IncompatibleContextException,
                   UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException {
        Parser.parseFromFile(DataSetConfig.class, new File(
                "testCaseRepository/data/datasets/configs/DS1_12.gsconfig")
                .getAbsoluteFile());
    }

    @Test
    public void testGetDataSet()
            throws DataSetConfigurationException,
                   NoRepositoryFoundException, DataSetNotFoundException,
                   DataSetConfigNotFoundException,
                   RegisterException, UnknownProviderException, NoDataSetException,
                   NumberFormatException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, ConfigurationException,
                   FileNotFoundException, UnknownParameterType,
                   RunException, IncompatibleContextException,
                   UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException {
        DataSetConfig dsConfig = Parser
                .parseFromFile(
                        DataSetConfig.class,
                        new File(
                                "testCaseRepository/data/datasets/configs/astral_1.dsconfig")
                        .getAbsoluteFile());
        IDataSet ds = dsConfig.getDataSet();
        IDataSet expected = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/astral_1_161/blastResults.txt")
                        .getAbsoluteFile());
        assertEquals(expected, ds);
    }

    @Test
    public void testSetDataSet()
            throws DataSetConfigurationException,
                   NoRepositoryFoundException, DataSetNotFoundException,
                   DataSetConfigNotFoundException,
                   RegisterException,
                   UnknownProviderException, NoDataSetException,
                   NumberFormatException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, ConfigurationException,
                   FileNotFoundException, UnknownParameterType,
                   RunException, IncompatibleContextException,
                   UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException {
        DataSetConfig dsConfig = Parser
                .parseFromFile(
                        DataSetConfig.class,
                        new File(
                                "testCaseRepository/data/datasets/configs/astral_1.dsconfig")
                        .getAbsoluteFile());
        IDataSet ds = dsConfig.getDataSet();
        IDataSet expected = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/astral_1_161/blastResults.txt")
                        .getAbsoluteFile());
        assertEquals(expected, ds);

        IDataSet override = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile());
        dsConfig.setDataSet(override);
        assertEquals(override, dsConfig.getDataSet());
    }

    @Test
    public void testToString()
            throws DataSetConfigurationException,
                   NoRepositoryFoundException, DataSetNotFoundException,
                   DataSetConfigNotFoundException, RegisterException, UnknownProviderException, NoDataSetException,
                   NumberFormatException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, ConfigurationException,
                   FileNotFoundException, UnknownParameterType,
                   RunException, IncompatibleContextException,
                   UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException {
        DataSetConfig gsConfig = Parser
                .parseFromFile(
                        DataSetConfig.class,
                        new File(
                                "testCaseRepository/data/datasets/configs/astral_1.dsconfig")
                        .getAbsoluteFile());
        assertEquals("astral_1", gsConfig.toString());

    }

}
