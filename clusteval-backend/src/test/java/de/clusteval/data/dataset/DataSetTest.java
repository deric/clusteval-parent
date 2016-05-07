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

import ch.qos.logback.classic.Level;
import de.clusteval.api.Matrix;
import de.clusteval.api.Precision;
import de.clusteval.api.data.DataConfig;
import de.clusteval.api.data.DataSetAttributeFilterer;
import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
import de.clusteval.api.data.DataSetFormatFactory;
import de.clusteval.api.data.DataSetTypeFactory;
import de.clusteval.api.data.DistanceMeasureFactory;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.data.InputToStd;
import de.clusteval.api.data.RelativeDataSet;
import de.clusteval.api.data.RelativeDataSetFormat;
import de.clusteval.api.data.StdToInput;
import de.clusteval.api.data.WEBSITE_VISIBILITY;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.exceptions.FormatConversionException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
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
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.api.run.IncompatibleParameterOptimizationMethodException;
import de.clusteval.api.run.RunException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.repository.db.StubSQLCommunicator;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.utils.AbstractClustEvalTest;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
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
public class DataSetTest extends AbstractClustEvalTest {

    public void testRegister()
            throws
            NoRepositoryFoundException, DataSetNotFoundException,
            DataSetConfigurationException, RegisterException,
            UnknownProviderException, NoDataSetException,
            GoldStandardNotFoundException, GoldStandardConfigurationException,
            DataSetConfigNotFoundException,
            GoldStandardConfigNotFoundException, DataConfigurationException,
            DataConfigNotFoundException, NumberFormatException,
            ConfigurationException,
            FileNotFoundException, UnknownParameterType, RunException,
            IncompatibleContextException, UnknownRunResultFormatException,
            InvalidOptimizationParameterException,
            UnknownProgramParameterException, UnknownProgramTypeException,
            UnknownRProgramException,
            IncompatibleDataSetConfigPreprocessorException,
            IncompatibleParameterOptimizationMethodException,
            UnknownParameterOptimizationMethodException,
            NoOptimizableProgramParameterException,
            UnknownRunResultPostprocessorException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile());

        assertEquals(this.repositoryObject, this.getRepository()
                .getRegisteredObject((IDataSet) this.repositoryObject));

        // adding a data set equal to another one already registered does
        // not register the second object.
        this.repositoryObject = new RelativeDataSet(
                (RelativeDataSet) this.repositoryObject);
        assertEquals(
                this.getRepository().getRegisteredObject(
                        (IDataSet) this.repositoryObject), this.repositoryObject);
        assertFalse(this.getRepository().getRegisteredObject(
                (IDataSet) this.repositoryObject) == this.repositoryObject);
    }

    /*
     * Registering a dataset of a runresult repository that is not present in
     * the parent repository should not be possible.
     *
     */
    @Test(expected = DataSetRegisterException.class)
    public void testRegisterRunResultRepositoryNotPresentInParent()
            throws FileNotFoundException, RepositoryAlreadyExistsException,
                   InvalidRepositoryException, RepositoryConfigurationException, NoRepositoryFoundException,
                   DataSetNotFoundException, DataSetConfigurationException,
                   RegisterException, UnknownProviderException, NoDataSetException,
                   NoSuchAlgorithmException, InterruptedException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   DataSetConfigNotFoundException, GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, NumberFormatException,
                   ConfigurationException, UnknownParameterType,
                   RunException, IncompatibleContextException,
                   UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException,
                   IncompatibleDataSetConfigPreprocessorException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException {
        try {
            Repository runResultRepository = new RunResultRepository(
                    new File(
                            "testCaseRepository/results/12_04_2012-14_05_42_tc_vs_DS1")
                    .getAbsolutePath(), getRepository());
            runResultRepository.setSQLCommunicator(new StubSQLCommunicator(
                    runResultRepository));
            runResultRepository.initialize();
            try {
                Parser.parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/results/12_04_2012-14_05_42_tc_vs_DS1/inputs/DS1/testCaseDataSetNotPresentInParent.txt")
                        .getAbsoluteFile());
            } finally {
                runResultRepository.terminateSupervisorThread();
            }
        } catch (DatabaseConnectException e) {
            // cannot happen
        }
    }

    public void testUnregister() throws
            NoRepositoryFoundException, DataSetNotFoundException,
            DataSetConfigurationException, RegisterException,
            UnknownProviderException, NoDataSetException,
            GoldStandardNotFoundException, GoldStandardConfigurationException,
            DataSetConfigNotFoundException,
            GoldStandardConfigNotFoundException, DataConfigurationException,
            DataConfigNotFoundException, NumberFormatException,
            ConfigurationException, FileNotFoundException, UnknownParameterType, RunException,
            IncompatibleContextException, UnknownRunResultFormatException,
            InvalidOptimizationParameterException,
            UnknownProgramParameterException, UnknownProgramTypeException,
            UnknownRProgramException,
            IncompatibleDataSetConfigPreprocessorException,
            IncompatibleParameterOptimizationMethodException,
            UnknownParameterOptimizationMethodException,
            NoOptimizableProgramParameterException,
            UnknownRunResultPostprocessorException {

        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile());

        assertEquals(this.repositoryObject, this.getRepository()
                .getRegisteredObject((IDataSet) this.repositoryObject));
        this.repositoryObject.unregister();
        // is not registered anymore
        assertTrue(this.getRepository().getRegisteredObject(
                (IDataSet) this.repositoryObject) == null);
    }

    @Test
    public void testParseFromFile() throws
            NoRepositoryFoundException, DataSetNotFoundException,
            DataSetConfigurationException, RegisterException,
            UnknownProviderException, NoDataSetException,
            GoldStandardNotFoundException, GoldStandardConfigurationException,
            DataSetConfigNotFoundException,
            GoldStandardConfigNotFoundException, DataConfigurationException,
            DataConfigNotFoundException, NumberFormatException,
            ConfigurationException, FileNotFoundException, UnknownParameterType,
            RunException, IncompatibleContextException, UnknownRunResultFormatException,
            InvalidOptimizationParameterException,
            UnknownProgramParameterException, UnknownProgramTypeException,
            UnknownRProgramException,
            IncompatibleDataSetConfigPreprocessorException,
            IncompatibleParameterOptimizationMethodException,
            UnknownParameterOptimizationMethodException,
            NoOptimizableProgramParameterException,
            UnknownRunResultPostprocessorException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile());
        assertEquals(
                new RelativeDataSet(
                        getRepository(),
                        false,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile().lastModified(),
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile(), "zachary",
                        (RelativeDataSetFormat) (DataSetFormatFactory.parseFromString(
                                getRepository(), "RowSimDataSetFormat")),
                        DataSetTypeFactory.parseFromString("PPIDataSetType"), WEBSITE_VISIBILITY.HIDE),
                this.repositoryObject);
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseFromNotExistingFile()
            throws NoRepositoryFoundException,
                   DataSetNotFoundException, DataSetNotFoundException,
                   DataSetConfigurationException, RegisterException,
                   UnknownProviderException, NoDataSetException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   DataSetConfigNotFoundException,
                   GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, NumberFormatException,
                   ConfigurationException, FileNotFoundException, UnknownParameterType,
                   RunException, IncompatibleContextException, UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException,
                   IncompatibleDataSetConfigPreprocessorException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities2.txt")
                        .getAbsoluteFile());
    }

    @Test
    public void testGetDataSetFormat() throws NoRepositoryFoundException,
                                              DataSetNotFoundException,
                                              DataSetConfigurationException, RegisterException,
                                              UnknownProviderException, NoDataSetException,
                                              GoldStandardNotFoundException, GoldStandardConfigurationException,
                                              DataSetConfigNotFoundException,
                                              GoldStandardConfigNotFoundException, DataConfigurationException,
                                              DataConfigNotFoundException, NumberFormatException,
                                              ConfigurationException, FileNotFoundException, UnknownParameterType,
                                              RunException, IncompatibleContextException, UnknownRunResultFormatException,
                                              InvalidOptimizationParameterException,
                                              UnknownProgramParameterException, UnknownProgramTypeException,
                                              UnknownRProgramException,
                                              IncompatibleDataSetConfigPreprocessorException,
                                              IncompatibleParameterOptimizationMethodException,
                                              UnknownParameterOptimizationMethodException,
                                              NoOptimizableProgramParameterException,
                                              UnknownRunResultPostprocessorException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile());
        IDataSetFormat dsFormat = ((IDataSet) this.repositoryObject)
                .getDataSetFormat();
        assertEquals(DataSetFormatFactory.parseFromString(getRepository(),
                "RowSimDataSetFormat"), dsFormat);
    }

    @Test
    public void testGetMajorName() throws NoRepositoryFoundException,
                                          DataSetNotFoundException,
                                          DataSetConfigurationException, RegisterException,
                                          UnknownProviderException, NoDataSetException,
                                          GoldStandardNotFoundException, GoldStandardConfigurationException,
                                          DataSetConfigNotFoundException,
                                          GoldStandardConfigNotFoundException, DataConfigurationException,
                                          DataConfigNotFoundException, NumberFormatException,
                                          ConfigurationException, FileNotFoundException, UnknownParameterType,
                                          RunException, IncompatibleContextException, UnknownRunResultFormatException,
                                          InvalidOptimizationParameterException,
                                          UnknownProgramParameterException, UnknownProgramTypeException,
                                          UnknownRProgramException,
                                          IncompatibleDataSetConfigPreprocessorException,
                                          IncompatibleParameterOptimizationMethodException,
                                          UnknownParameterOptimizationMethodException,
                                          NoOptimizableProgramParameterException,
                                          UnknownRunResultPostprocessorException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile());
        IDataSet casted = (IDataSet) this.repositoryObject;
        assertEquals("DS1", casted.getMajorName());
    }

    @Test
    public void testGetMinorName() throws NoRepositoryFoundException,
                                          DataSetNotFoundException,
                                          DataSetConfigurationException, RegisterException,
                                          UnknownProviderException, NoDataSetException,
                                          GoldStandardNotFoundException, GoldStandardConfigurationException,
                                          DataSetConfigNotFoundException,
                                          GoldStandardConfigNotFoundException, DataConfigurationException,
                                          DataConfigNotFoundException, NumberFormatException,
                                          ConfigurationException, FileNotFoundException, UnknownParameterType,
                                          RunException, IncompatibleContextException, UnknownRunResultFormatException,
                                          InvalidOptimizationParameterException,
                                          UnknownProgramParameterException, UnknownProgramTypeException,
                                          UnknownRProgramException,
                                          IncompatibleDataSetConfigPreprocessorException,
                                          IncompatibleParameterOptimizationMethodException,
                                          UnknownParameterOptimizationMethodException,
                                          NoOptimizableProgramParameterException,
                                          UnknownRunResultPostprocessorException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile());
        IDataSet casted = (IDataSet) this.repositoryObject;

        assertEquals(casted.getMinorName(), casted.getAbsolutePath()
                .substring(casted.getAbsolutePath().lastIndexOf("/") + 1));
    }

    @Test
    public void testGetFullName() throws NoRepositoryFoundException,
                                         DataSetNotFoundException,
                                         DataSetConfigurationException, RegisterException,
                                         UnknownProviderException, NoDataSetException,
                                         GoldStandardNotFoundException, GoldStandardConfigurationException,
                                         DataSetConfigNotFoundException,
                                         GoldStandardConfigNotFoundException, DataConfigurationException,
                                         DataConfigNotFoundException, NumberFormatException,
                                         ConfigurationException, FileNotFoundException, UnknownParameterType, RunException,
                                         IncompatibleContextException, UnknownRunResultFormatException,
                                         InvalidOptimizationParameterException,
                                         UnknownProgramParameterException, UnknownProgramTypeException,
                                         UnknownRProgramException,
                                         IncompatibleDataSetConfigPreprocessorException,
                                         IncompatibleParameterOptimizationMethodException,
                                         UnknownParameterOptimizationMethodException,
                                         NoOptimizableProgramParameterException,
                                         UnknownRunResultPostprocessorException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile());
        assertEquals("DS1/Zachary_karate_club_similarities.txt",
                ((IDataSet) this.repositoryObject).getFullName());
    }

    @Test
    public void testToString() throws NoRepositoryFoundException,
                                      DataSetNotFoundException,
                                      DataSetConfigurationException, RegisterException,
                                      UnknownProviderException, NoDataSetException,
                                      GoldStandardNotFoundException, GoldStandardConfigurationException,
                                      DataSetConfigNotFoundException,
                                      GoldStandardConfigNotFoundException, DataConfigurationException,
                                      DataConfigNotFoundException, NumberFormatException,
                                      ConfigurationException, FileNotFoundException, UnknownParameterType,
                                      RunException, IncompatibleContextException, UnknownRunResultFormatException,
                                      InvalidOptimizationParameterException,
                                      UnknownProgramParameterException, UnknownProgramTypeException,
                                      UnknownRProgramException,
                                      IncompatibleDataSetConfigPreprocessorException,
                                      IncompatibleParameterOptimizationMethodException,
                                      UnknownParameterOptimizationMethodException,
                                      NoOptimizableProgramParameterException,
                                      UnknownRunResultPostprocessorException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile());
        assertEquals("DS1/Zachary_karate_club_similarities.txt",
                ((IDataSet) this.repositoryObject).toString());
    }

    @Test
    public void testLoadIntoMemory()
            throws NoRepositoryFoundException,
                   FormatConversionException,
                   IOException, DataSetNotFoundException,
                   InvalidDataSetFormatException,
                   DataSetConfigurationException, RegisterException,
                   UnknownProviderException, NoDataSetException,
                   InstantiationException, IllegalAccessException,
                   RNotAvailableException, GoldStandardNotFoundException, GoldStandardConfigurationException,
                   DataSetConfigNotFoundException,
                   GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, NumberFormatException,
                   ConfigurationException, UnknownParameterType,
                   RunException, IncompatibleContextException,
                   UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException,
                   IncompatibleDataSetConfigPreprocessorException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException,
                   InterruptedException, RException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile());

        IDataSet standard = ((IDataSet) this.repositoryObject)
                .preprocessAndConvertTo(context,
                        DataSetFormatFactory.parseFromString(getRepository(),
                                "SimMatrixDataSetFormat"),
                        new InputToStd(
                                DistanceMeasureFactory.parseFromString(
                                        getRepository(),
                                        "EuclidianDistanceMeasure"),
                                Precision.DOUBLE,
                                new ArrayList<>(),
                                new ArrayList<>()),
                        new StdToInput());
        assertFalse(standard.isInMemory());
        standard.loadIntoMemory();
        assertTrue(standard.isInMemory());
    }

    @Test
    public void testGetSimilarityMatrix() throws NoRepositoryFoundException,
                                                 FormatConversionException,
                                                 IOException, DataSetNotFoundException,
                                                 InvalidDataSetFormatException,
                                                 DataSetConfigurationException, RegisterException,
                                                 UnknownProviderException, NoDataSetException,
                                                 InstantiationException, IllegalAccessException,
                                                 RNotAvailableException,
                                                 GoldStandardNotFoundException, GoldStandardConfigurationException,
                                                 DataSetConfigNotFoundException,
                                                 GoldStandardConfigNotFoundException, DataConfigurationException,
                                                 DataConfigNotFoundException, NumberFormatException,
                                                 ConfigurationException, UnknownParameterType,
                                                 RunException, IncompatibleContextException,
                                                 UnknownRunResultFormatException,
                                                 InvalidOptimizationParameterException,
                                                 UnknownProgramParameterException, UnknownProgramTypeException,
                                                 UnknownRProgramException,
                                                 IncompatibleDataSetConfigPreprocessorException,
                                                 IncompatibleParameterOptimizationMethodException,
                                                 UnknownParameterOptimizationMethodException,
                                                 NoOptimizableProgramParameterException,
                                                 UnknownRunResultPostprocessorException,
                                                 InterruptedException, RException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim")
                        .getAbsoluteFile());
        RelativeDataSet standard = (RelativeDataSet) ((IDataSet) this.repositoryObject)
                .preprocessAndConvertTo(context,
                        DataSetFormatFactory.parseFromString(getRepository(),
                                "SimMatrixDataSetFormat"),
                        new InputToStd(
                                DistanceMeasureFactory.parseFromString(
                                        getRepository(),
                                        "EuclidianDistanceMeasure"),
                                Precision.DOUBLE,
                                new ArrayList<>(),
                                new ArrayList<>()),
                        new StdToInput());
        standard.loadIntoMemory();
        Matrix simMatrix = standard.getDataSetContent();
        double[][] sims = new double[][]{new double[]{1.0, 0.6, 0.5},
        new double[]{0.6, 0.5, 0.1}, new double[]{0.5, 0.1, 0.8}};
        String[] ids = new String[]{"1", "2", "3"};
        SimilarityMatrix expected = new SimilarityMatrix(sims);
        expected.setIds(ids);
        assertEquals(expected, simMatrix);
    }

    @Test
    public void testUnloadFromMemory()
            throws NoRepositoryFoundException,
                   FormatConversionException,
                   IOException, DataSetNotFoundException,
                   InvalidDataSetFormatException,
                   DataSetConfigurationException, RegisterException,
                   UnknownProviderException, NoDataSetException,
                   InstantiationException, IllegalAccessException,
                   RNotAvailableException, GoldStandardNotFoundException, GoldStandardConfigurationException,
                   DataSetConfigNotFoundException, GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, NumberFormatException,
                   ConfigurationException, UnknownParameterType,
                   RunException, IncompatibleContextException,
                   UnknownRunResultFormatException,
                   InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException,
                   UnknownRProgramException,
                   IncompatibleDataSetConfigPreprocessorException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException,
                   InterruptedException, RException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/DS1/Zachary_karate_club_similarities.txt")
                        .getAbsoluteFile());
        IDataSet standard = ((IDataSet) this.repositoryObject)
                .preprocessAndConvertTo(context,
                        DataSetFormatFactory.parseFromString(getRepository(),
                                "SimMatrixDataSetFormat"),
                        new InputToStd(
                                DistanceMeasureFactory.parseFromString(
                                        getRepository(),
                                        "EuclidianDistanceMeasure"),
                                Precision.DOUBLE,
                                new ArrayList<>(),
                                new ArrayList<>()),
                        new StdToInput());
        standard.loadIntoMemory();
        assertTrue(standard.isInMemory());
        standard.unloadFromMemory();
        assertFalse(standard.isInMemory());
    }

    /**
     * Test method for
     * {@link data.dataset.DataSet#convertTo(data.dataset.format.DataSetFormat)}
     * . Only verify, that the conversion process is started correctly and the
     * result file is created in the end. verification of the conversion result
     * itself is not done here.
     *
     * @throws UnknownDataSetFormatException
     * @throws NoRepositoryFoundException
     * @throws IOException
     * @throws FormatConversionException
     * @throws DataSetNotFoundException
     * @throws InvalidDataSetFormatException
     * @throws DataSetConfigurationException
     * @throws RegisterException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws RNotAvailableException
     */
    @Test
    public void testConvertTo() throws NoRepositoryFoundException,
                                       FormatConversionException,
                                       IOException, DataSetNotFoundException,
                                       InvalidDataSetFormatException,
                                       DataSetConfigurationException, RegisterException,
                                       UnknownProviderException, NoDataSetException,
                                       InstantiationException, IllegalAccessException,
                                       IllegalArgumentException,
                                       SecurityException, InvocationTargetException,
                                       NoSuchMethodException, RNotAvailableException, InterruptedException, RException {
        /*
         * SimMatrixDataSetFormat.convertTo() is a special case
         */
        this.repositoryObject = this
                .getRepository()
                .getStaticObjectWithName(IDataSet.class,
                        "nora_cancer/all_expression_spearman.txt").clone();
        IDataSet newDataSet = ((IDataSet) this.repositoryObject)
                .preprocessAndConvertTo(context,
                        DataSetFormatFactory.parseFromString(getRepository(),
                                "SimMatrixDataSetFormat"),
                        new InputToStd(
                                DistanceMeasureFactory.parseFromString(
                                        getRepository(),
                                        "EuclidianDistanceMeasure"),
                                Precision.DOUBLE,
                                new ArrayList<>(),
                                new ArrayList<>()),
                        new StdToInput());
        assertEquals(this.repositoryObject.getAbsolutePath(),
                newDataSet.getAbsolutePath());
        /*
         * SimMatrixDataSetFormat.convertTo(APRowSimDataSetFormat)
         */
        this.repositoryObject = this
                .getRepository()
                .getStaticObjectWithName(IDataSet.class,
                        "nora_cancer/all_expression_spearman.txt").clone();
        newDataSet = ((IDataSet) this.repositoryObject).preprocessAndConvertTo(context,
                DataSetFormatFactory.parseFromString(getRepository(),
                        "APRowSimDataSetFormat"),
                new InputToStd(DistanceMeasureFactory
                        .parseFromString(getRepository(),
                                "EuclidianDistanceMeasure"),
                        Precision.DOUBLE,
                        new ArrayList<>(),
                        new ArrayList<>()),
                new StdToInput());

        /*
         * convertTo(SimMatrixDataSetFormat) is a special case
         */
        this.repositoryObject = this
                .getRepository()
                .getStaticObjectWithName(IDataSet.class,
                        "rowSimTest/rowSimTestFile.sim").clone();
        ((IDataSet) this.repositoryObject).preprocessAndConvertTo(context,
                DataSetFormatFactory.parseFromString(getRepository(),
                        "SimMatrixDataSetFormat"),
                new InputToStd(DistanceMeasureFactory
                        .parseFromString(getRepository(),
                                "EuclidianDistanceMeasure"),
                        Precision.DOUBLE,
                        new ArrayList<>(),
                        new ArrayList<>()),
                new StdToInput());
        assertTrue(new File(
                "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.SimMatrix")
                .getAbsoluteFile().exists());

        /*
         * Convert to a non standard format
         */
        this.repositoryObject = this
                .getRepository()
                .getStaticObjectWithName(IDataSet.class,
                        "rowSimTest/rowSimTestFile.sim").clone();
        ((IDataSet) this.repositoryObject).preprocessAndConvertTo(context,
                DataSetFormatFactory.parseFromString(getRepository(),
                        "APRowSimDataSetFormat"),
                new InputToStd(DistanceMeasureFactory
                        .parseFromString(getRepository(),
                                "EuclidianDistanceMeasure"),
                        Precision.DOUBLE,
                        new ArrayList<>(),
                        new ArrayList<>()),
                new StdToInput());
        assertTrue(new File(
                "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.APRowSim")
                .getAbsoluteFile().exists());
        assertTrue(new File(
                "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.APRowSim.map")
                .getAbsoluteFile().exists());

        new File(
                "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.SimMatrix")
                .getAbsoluteFile().deleteOnExit();
        new File(
                "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.APRowSim")
                .getAbsoluteFile().deleteOnExit();
        new File(
                "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.APRowSim.map")
                .getAbsoluteFile().deleteOnExit();
    }

    @Test(expected = FormatConversionException.class)
    public void testConvertToRelativeToAbsolute()
            throws NoRepositoryFoundException,
                   FormatConversionException, IOException, DataSetNotFoundException,
                   InvalidDataSetFormatException,
                   DataSetConfigurationException, RegisterException,
                   UnknownProviderException, NoDataSetException,
                   InstantiationException, IllegalAccessException,
                   IllegalArgumentException,
                   SecurityException, InvocationTargetException,
                   NoSuchMethodException, RNotAvailableException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   DataSetConfigNotFoundException,
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
                   InterruptedException, RException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/sfld/sfld_brown_et_al_amidohydrolases_protein_similarities_for_beh.txt")
                        .getAbsoluteFile());
        ((IDataSet) this.repositoryObject).preprocessAndConvertTo(context,
                DataSetFormatFactory.parseFromString(getRepository(),
                        "MatrixDataSetFormat"),
                new InputToStd(DistanceMeasureFactory
                        .parseFromString(getRepository(),
                                "EuclidianDistanceMeasure"),
                        Precision.DOUBLE,
                        new ArrayList<>(),
                        new ArrayList<>()),
                new StdToInput());
    }

    @Test
    public void testConvertToDirectly() throws NoRepositoryFoundException,
                                               IOException,
                                               DataSetNotFoundException, InvalidDataSetFormatException,
                                               DataSetConfigurationException, RegisterException,
                                               UnknownProviderException, NoDataSetException,
                                               InvalidParameterException, RNotAvailableException, GoldStandardNotFoundException,
                                               GoldStandardConfigurationException, DataSetConfigNotFoundException,
                                               GoldStandardConfigNotFoundException, DataConfigurationException,
                                               DataConfigNotFoundException, NumberFormatException,
                                               ConfigurationException, UnknownParameterType,
                                               RunException, IncompatibleContextException,
                                               UnknownRunResultFormatException,
                                               InvalidOptimizationParameterException,
                                               UnknownProgramParameterException, UnknownProgramTypeException,
                                               UnknownRProgramException,
                                               IncompatibleDataSetConfigPreprocessorException,
                                               IncompatibleParameterOptimizationMethodException,
                                               UnknownParameterOptimizationMethodException,
                                               NoOptimizableProgramParameterException,
                                               UnknownRunResultPostprocessorException,
                                               InterruptedException {

        File targetFile = new File(
                "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip.SimMatrix")
                .getAbsoluteFile();
        if (targetFile.exists()) {
            targetFile.delete();
        }

        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim")
                        .getAbsoluteFile());
        DataSetAttributeFilterer filterer = new DataSetAttributeFilterer(
                "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim");
        filterer.process();
        ((IDataSet) this.repositoryObject)
                .setAbsolutePath(new File(
                        "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip")
                        .getAbsoluteFile());
        ((IDataSet) this.repositoryObject).convertToStandardDirectly(context,
                new InputToStd(DistanceMeasureFactory
                        .parseFromString(getRepository(), "EuclidianDistanceMeasure"),
                        Precision.DOUBLE,
                        new ArrayList<>(),
                        new ArrayList<>()));
        assertTrue(targetFile.exists());
        targetFile.delete();
    }

    @Test
    public void testGetInStandardFormat() throws NoRepositoryFoundException,
                                                 IOException,
                                                 DataSetNotFoundException, InvalidDataSetFormatException,
                                                 DataSetConfigurationException, RegisterException,
                                                 UnknownProviderException, NoDataSetException,
                                                 InvalidParameterException,
                                                 RNotAvailableException, GoldStandardNotFoundException,
                                                 GoldStandardConfigurationException, DataSetConfigNotFoundException,
                                                 GoldStandardConfigNotFoundException, DataConfigurationException,
                                                 DataConfigNotFoundException, NumberFormatException,
                                                 ConfigurationException, UnknownParameterType,
                                                 RunException, IncompatibleContextException,
                                                 UnknownRunResultFormatException,
                                                 InvalidOptimizationParameterException,
                                                 UnknownProgramParameterException, UnknownProgramTypeException,
                                                 UnknownRProgramException,
                                                 IncompatibleDataSetConfigPreprocessorException,
                                                 IncompatibleParameterOptimizationMethodException,
                                                 UnknownParameterOptimizationMethodException,
                                                 NoOptimizableProgramParameterException,
                                                 UnknownRunResultPostprocessorException,
                                                 InterruptedException {

        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim")
                        .getAbsoluteFile());
        DataSetAttributeFilterer filterer = new DataSetAttributeFilterer(
                "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim");
        filterer.process();
        ((IDataSet) this.repositoryObject)
                .setAbsolutePath(new File(
                        "testCaseRepository/data/datasets/rowSimTest/rowSimTestFile.sim.strip")
                        .getAbsoluteFile());
        ((IDataSet) this.repositoryObject).convertToStandardDirectly(context,
                new InputToStd(DistanceMeasureFactory
                        .parseFromString(getRepository(),
                                "EuclidianDistanceMeasure"),
                        Precision.DOUBLE,
                        new ArrayList<>(),
                        new ArrayList<>()));
        IDataSet standard = ((IDataSet) this.repositoryObject).getInStandardFormat();
        assertEquals(DataSetFormatFactory.parseFromString(getRepository(),
                "SimMatrixDataSetFormat"), standard.getDataSetFormat());
    }

    @Test
    public void testConvertToAbsoluteToAbsolute()
            throws NoRepositoryFoundException,
                   FormatConversionException, IOException, DataSetNotFoundException,
                   InvalidDataSetFormatException,
                   DataSetConfigurationException, RegisterException,
                   UnknownProviderException, NoDataSetException,
                   InstantiationException, IllegalAccessException,
                   IllegalArgumentException,
                   SecurityException, InvocationTargetException,
                   NoSuchMethodException, RNotAvailableException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   DataSetConfigNotFoundException,
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
                   UnknownRunResultPostprocessorException, InterruptedException, RException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/bone_marrow_gene_expr/ALB_ALT_AML.1000genes.res.out2")
                        .getAbsoluteFile());
        IDataSet newDataSet = ((IDataSet) this.repositoryObject)
                .preprocessAndConvertTo(context,
                        DataSetFormatFactory.parseFromString(getRepository(),
                                "MatrixDataSetFormat"),
                        new InputToStd(
                                DistanceMeasureFactory.parseFromString(
                                        getRepository(),
                                        "EuclidianDistanceMeasure"),
                                Precision.DOUBLE,
                                new ArrayList<>(),
                                new ArrayList<>()),
                        new StdToInput());
        assertEquals("MatrixDataSetFormat", newDataSet
                .getDataSetFormat().getClass().getSimpleName());
        assertEquals(context.getStandardInputFormat().getClass()
                .getSimpleName(), newDataSet.getInStandardFormat()
                .getDataSetFormat().getClass().getSimpleName());
    }

    @Test
    public void testConvertToStandardToStandard()
            throws NoRepositoryFoundException,
                   FormatConversionException, IOException, DataSetNotFoundException,
                   InvalidDataSetFormatException,
                   DataSetConfigurationException, RegisterException,
                   UnknownProviderException, NoDataSetException,
                   InstantiationException, IllegalAccessException,
                   IllegalArgumentException,
                   SecurityException, InvocationTargetException,
                   NoSuchMethodException, RNotAvailableException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   DataSetConfigNotFoundException,
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
                   InterruptedException, RException {
        this.repositoryObject = Parser
                .parseFromFile(
                        IDataSet.class,
                        new File(
                                "testCaseRepository/data/datasets/bone_marrow_gene_expr/ALB_ALT_AML.1000genes.res.out2.SimMatrix")
                        .getAbsoluteFile());
        IDataSet newDataSet = ((IDataSet) this.repositoryObject)
                .preprocessAndConvertTo(context,
                        context.getStandardInputFormat(),
                        new InputToStd(
                                DistanceMeasureFactory.parseFromString(
                                        getRepository(),
                                        "EuclidianDistanceMeasure"),
                                Precision.DOUBLE,
                                new ArrayList<>(),
                                new ArrayList<>()),
                        new StdToInput());
        assertEquals(context.getStandardInputFormat().getClass()
                .getSimpleName(), newDataSet.getDataSetFormat().getClass()
                .getSimpleName());
        assertEquals(context.getStandardInputFormat().getClass()
                .getSimpleName(), newDataSet.getInStandardFormat()
                .getDataSetFormat().getClass().getSimpleName());
    }

    @Test
    public void testConvertMatrixToSimMatrix()
            throws RepositoryAlreadyExistsException, InvalidRepositoryException,
                   RepositoryConfigurationException, InvalidDataSetFormatException, RegisterException,
                   FormatConversionException, IOException, RNotAvailableException,
                   InterruptedException, RException, UnknownProviderException {
        ClustevalBackendServer.logLevel(Level.INFO);

        DataConfig dataConfig = getRepository().getStaticObjectWithName(
                DataConfig.class, "synthetic_cassini250");
        IDataSet ds = dataConfig.getDatasetConfig().getDataSet();
        IDataSetFormat internal = DataSetFormatFactory.parseFromString(getRepository(),
                "SimMatrixDataSetFormat");
        ds = ds.preprocessAndConvertTo(context,
                internal,
                new InputToStd(DistanceMeasureFactory
                        .parseFromString(getRepository(),
                                "EuclidianDistanceMeasure"),
                        Precision.DOUBLE,
                        new ArrayList<>(),
                        new ArrayList<>()),
                new StdToInput());
    }
}
