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
package de.clusteval.data.goldstandard;

import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
import de.clusteval.api.data.GoldStandard;
import de.clusteval.api.data.GoldStandardConfig;
import de.clusteval.api.data.IGoldStandard;
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
import org.apache.commons.configuration.ConfigurationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class GoldStandardConfigTest extends AbstractClustEvalTest {

    public void testRegister() throws GoldStandardConfigurationException,
                                      IOException, NoRepositoryFoundException,
                                      GoldStandardNotFoundException, GoldStandardConfigNotFoundException,
                                      RegisterException,
                                      DataSetConfigurationException, DataSetNotFoundException,
                                      DataSetConfigNotFoundException, NoDataSetException,
                                      DataConfigurationException, DataConfigNotFoundException,
                                      NumberFormatException, ConfigurationException,
                                      UnknownParameterType, RunException,
                                      IncompatibleContextException, UnknownRunResultFormatException,
                                      InvalidOptimizationParameterException,
                                      UnknownProgramParameterException, UnknownProgramTypeException,
                                      UnknownRProgramException,
                                      UnknownProviderException,
                                      IncompatibleParameterOptimizationMethodException,
                                      UnknownParameterOptimizationMethodException,
                                      NoOptimizableProgramParameterException,
                                      UnknownRunResultPostprocessorException {
        this.repositoryObject = Parser
                .parseFromFile(
                        GoldStandardConfig.class,
                        new File(
                                "testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
                        .getAbsoluteFile());
        assertEquals(
                this.repositoryObject,
                this.getRepository().getRegisteredObject(
                        (GoldStandardConfig) this.repositoryObject));

        // adding a GoldStandardConfig equal to another one already registered
        // does
        // not register the second object.
        this.repositoryObject = new GoldStandardConfig(
                (GoldStandardConfig) this.repositoryObject);
        assertEquals(
                this.getRepository().getRegisteredObject(
                        (GoldStandardConfig) this.repositoryObject),
                this.repositoryObject);
        assertFalse(this.getRepository().getRegisteredObject(
                (GoldStandardConfig) this.repositoryObject) == this.repositoryObject);
    }

    public void testUnregister() throws GoldStandardConfigurationException,
                                        IOException, NoRepositoryFoundException,
                                        GoldStandardNotFoundException, GoldStandardConfigNotFoundException,
                                        RegisterException,
                                        DataSetConfigurationException, DataSetNotFoundException,
                                        DataSetConfigNotFoundException, NoDataSetException,
                                        DataConfigurationException, DataConfigNotFoundException,
                                        NumberFormatException, ConfigurationException,
                                        UnknownParameterType, RunException,
                                        IncompatibleContextException, UnknownRunResultFormatException,
                                        InvalidOptimizationParameterException,
                                        UnknownProgramParameterException, UnknownProgramTypeException,
                                        UnknownRProgramException,
                                        UnknownProviderException,
                                        IncompatibleParameterOptimizationMethodException,
                                        UnknownParameterOptimizationMethodException,
                                        NoOptimizableProgramParameterException,
                                        UnknownRunResultPostprocessorException {
        this.repositoryObject = Parser
                .parseFromFile(
                        GoldStandardConfig.class,
                        new File(
                                "testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
                        .getAbsoluteFile());
        assertEquals(
                this.repositoryObject,
                this.getRepository().getRegisteredObject(
                        (GoldStandardConfig) this.repositoryObject));
        this.repositoryObject.unregister();
        // is not registered anymore
        assertTrue(this.getRepository().getRegisteredObject(
                (GoldStandardConfig) this.repositoryObject) == null);
    }

    @Test
    public void testNotifyRepositoryEvent()
            throws IOException,
                   NoRepositoryFoundException, GoldStandardNotFoundException,
                   GoldStandardConfigurationException,
                   GoldStandardConfigNotFoundException, RegisterException,
                   DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException,
                   NoDataSetException, DataConfigurationException,
                   DataConfigNotFoundException, NumberFormatException,
                   ConfigurationException, UnknownParameterType,
                   RunException, IncompatibleContextException,
                   UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException, UnknownProviderException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException {

        /*
         * REPLACE
         */

 /*
         * First check, whether listeners of goldstandardconfigs are notified
         * correctly when the goldstandardconfig is replaced
         */
        GoldStandardConfig gsConfig = Parser
                .parseFromFile(
                        GoldStandardConfig.class,
                        new File(
                                "testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
                        .getAbsoluteFile());
        StubRepositoryObject child = new StubRepositoryObject(getRepository(),
                false, System.currentTimeMillis(), new File(
                        "testCaseRepository/Bla"));
        gsConfig.addListener(child);

        GoldStandardConfig gsConfig2 = new GoldStandardConfig(gsConfig);

        gsConfig.notify(new RepositoryReplaceEvent(gsConfig, gsConfig2));
        assertTrue(child.notified);

        /*
         * Now check, whether goldstandard configs update their references
         * correctly, when their goldstandard is replaced
         */
        IGoldStandard gs = gsConfig.getGoldstandard();
        GoldStandard gs2 = new GoldStandard(gs);

        gsConfig.notify(new RepositoryReplaceEvent(gs, gs2));

        assertFalse(gsConfig.getGoldstandard() == gs);
        assertTrue(gsConfig.getGoldstandard() == gs2);

        /*
         * REMOVE
         */

 /*
         * First check, whether listeners of goldstandardconfigs are notified
         * correctly when the goldstandardconfig is replaced
         */
        child.notified = false;
        gsConfig.notify(new RepositoryRemoveEvent(gsConfig));
        assertTrue(child.notified);

        /*
         * Now check, whether goldstandard configs remove themselves when their
         * goldstandard is removed
         */
        // gsconfig has to be registered
        assertTrue(getRepository().getRegisteredObject(gsConfig) == gsConfig);

        gsConfig.notify(new RepositoryRemoveEvent(gs2));

        // not registered anymore
        assertTrue(getRepository().getRegisteredObject(gsConfig) == null);
    }

    @Test(expected = GoldStandardConfigurationException.class)
    public void testParseFromFileGoldStandardNameMissing()
            throws GoldStandardConfigurationException, IOException,
                   NoRepositoryFoundException, GoldStandardNotFoundException,
                   GoldStandardConfigNotFoundException, RegisterException,
                   DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException,
                   NoDataSetException, DataConfigurationException,
                   DataConfigNotFoundException, NumberFormatException,
                   ConfigurationException, UnknownParameterType,
                   RunException, IncompatibleContextException,
                   UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException, UnknownProviderException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException {
        // create empty file
        File f = new File(
                "testCaseRepository/data/goldstandards/configs/goldStandardConfigTest.gsconfig")
                .getAbsoluteFile();
        f.createNewFile();
        try {
            Parser.parseFromFile(GoldStandardConfig.class, f);
        } catch (GoldStandardConfigurationException e) {
            // Assert.assertEquals(
            // "'goldstandardName' doesn't map to an existing object",
            // e.getMessage());
            throw e;
        } finally {
            f.delete();
        }
    }

    @Test
    public void testParseFromFile() throws GoldStandardConfigurationException,
                                           IOException, NoRepositoryFoundException,
                                           GoldStandardNotFoundException, GoldStandardConfigNotFoundException,
                                           RegisterException,
                                           DataSetConfigurationException, DataSetNotFoundException,
                                           DataSetConfigNotFoundException, NoDataSetException,
                                           DataConfigurationException, DataConfigNotFoundException,
                                           NumberFormatException, ConfigurationException,
                                           UnknownParameterType, RunException,
                                           IncompatibleContextException, UnknownRunResultFormatException,
                                           InvalidOptimizationParameterException,
                                           UnknownProgramParameterException, UnknownProgramTypeException,
                                           UnknownRProgramException,
                                           UnknownProviderException,
                                           IncompatibleParameterOptimizationMethodException,
                                           UnknownParameterOptimizationMethodException,
                                           NoOptimizableProgramParameterException,
                                           UnknownRunResultPostprocessorException {
        GoldStandardConfig gsConfig = Parser
                .parseFromFile(
                        GoldStandardConfig.class,
                        new File(
                                "testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
                        .getAbsoluteFile());
        assertEquals(
                new GoldStandardConfig(
                        getRepository(),
                        new File(
                                "testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
                        .getAbsoluteFile().lastModified(),
                        new File(
                                "testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
                        .getAbsoluteFile(),
                        GoldStandard
                        .parseFromFile(new File(
                                "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                                .getAbsoluteFile())), gsConfig);
    }

    @Test(expected = GoldStandardConfigurationException.class)
    public void testParseFromFileGoldStandardFileMissing()
            throws GoldStandardConfigurationException, IOException,
                   NoRepositoryFoundException, GoldStandardNotFoundException,
                   GoldStandardConfigNotFoundException, RegisterException,
                   DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException,
                   NoDataSetException, DataConfigurationException,
                   DataConfigNotFoundException, NumberFormatException,
                   ConfigurationException, UnknownParameterType,
                   RunException, IncompatibleContextException,
                   UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException,
                   UnknownProviderException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException {

        File f = new File(
                "testCaseRepository/data/goldstandards/configs/goldStandardConfigTest2.gsconfig")
                .getAbsoluteFile();
        f.createNewFile();

        try {
            PrintWriter bw = new PrintWriter(new FileWriter(f));
            bw.println("goldstandardName = Test");
            bw.flush();
            bw.close();

            Parser.parseFromFile(GoldStandardConfig.class, f);
        } catch (GoldStandardConfigurationException e) {
            // Assert.assertEquals(
            // "'goldstandardFile' doesn't map to an existing object",
            // e.getMessage());
            throw e;
        } finally {
            f.delete();
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseFromNotExistingFile() throws IOException,
                                                      NoRepositoryFoundException, GoldStandardNotFoundException,
                                                      GoldStandardConfigurationException,
                                                      GoldStandardConfigNotFoundException, RegisterException,
                                                      DataSetConfigurationException,
                                                      DataSetNotFoundException, DataSetConfigNotFoundException,
                                                      NoDataSetException, DataConfigurationException,
                                                      DataConfigNotFoundException, NumberFormatException,
                                                      ConfigurationException, UnknownParameterType,
                                                      RunException, IncompatibleContextException,
                                                      UnknownRunResultFormatException,
                                                      InvalidOptimizationParameterException,
                                                      UnknownProgramParameterException, UnknownProgramTypeException,
                                                      UnknownRProgramException,
                                                      UnknownProviderException,
                                                      IncompatibleParameterOptimizationMethodException,
                                                      UnknownParameterOptimizationMethodException,
                                                      NoOptimizableProgramParameterException,
                                                      UnknownRunResultPostprocessorException {
        Parser.parseFromFile(
                GoldStandardConfig.class,
                new File(
                        "testCaseRepository/data/goldstandards/configs/DS1_12.gsconfig")
                .getAbsoluteFile());
    }

    @Test
    public void testGetGoldstandard()
            throws GoldStandardConfigurationException,
                   NoRepositoryFoundException, GoldStandardNotFoundException,
                   GoldStandardConfigNotFoundException, RegisterException,
                   DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException,
                   NoDataSetException, DataConfigurationException,
                   DataConfigNotFoundException, NumberFormatException,
                   ConfigurationException, FileNotFoundException, UnknownParameterType,
                   RunException, IncompatibleContextException, UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException,
                   UnknownProviderException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException {
        GoldStandardConfig gsConfig = Parser
                .parseFromFile(
                        GoldStandardConfig.class,
                        new File(
                                "testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
                        .getAbsoluteFile());
        IGoldStandard gs = gsConfig.getGoldstandard();
        IGoldStandard expected = GoldStandard
                .parseFromFile(new File(
                        "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                        .getAbsoluteFile());
        assertEquals(expected, gs);
    }

    @Test
    public void testToString() throws GoldStandardConfigurationException,
                                      IOException, NoRepositoryFoundException,
                                      GoldStandardNotFoundException, GoldStandardConfigNotFoundException,
                                      RegisterException,
                                      DataSetConfigurationException, DataSetNotFoundException,
                                      DataSetConfigNotFoundException, NoDataSetException,
                                      DataConfigurationException, DataConfigNotFoundException,
                                      NumberFormatException, ConfigurationException,
                                      UnknownParameterType, RunException, IncompatibleContextException, UnknownRunResultFormatException,
                                      InvalidOptimizationParameterException,
                                      UnknownProgramParameterException, UnknownProgramTypeException,
                                      UnknownRProgramException,
                                      UnknownProviderException,
                                      IncompatibleParameterOptimizationMethodException,
                                      UnknownParameterOptimizationMethodException,
                                      NoOptimizableProgramParameterException,
                                      UnknownRunResultPostprocessorException {
        GoldStandardConfig gsConfig = Parser.parseFromFile(GoldStandardConfig.class,
                new File(
                        "testCaseRepository/data/goldstandards/configs/DS1_1.gsconfig")
                .getAbsoluteFile());
        assertEquals("DS1_1", gsConfig.toString());

    }

}
