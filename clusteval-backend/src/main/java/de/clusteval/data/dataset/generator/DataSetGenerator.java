/**
 * *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 *****************************************************************************
 */
package de.clusteval.data.dataset.generator;

import de.clusteval.api.data.AbsoluteDataSet;
import de.clusteval.api.data.AbsoluteDataSetFormat;
import de.clusteval.api.data.AbstractDataSetProvider;
import de.clusteval.api.data.DataSetFormatFactory;
import de.clusteval.api.data.DataSetTypeFactory;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetGenerator;
import de.clusteval.api.data.IGoldStandard;
import de.clusteval.api.data.WEBSITE_VISIBILITY;
import de.clusteval.api.exceptions.DataSetGenerationException;
import de.clusteval.api.exceptions.GoldStandardGenerationException;
import de.clusteval.api.exceptions.RepositoryObjectDumpException;
import de.clusteval.api.exceptions.UnknownDataSetGeneratorException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RLibraryInferior;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryObject;
import de.clusteval.utils.FileUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * <p>
 * {@code
 *
 * A data set generator MyDataSetGenerator can be added to ClustEval by
 *
 * 1. extending this class with your own class MyDataSetGenerator. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your runresult format.
 *
 *   * :java:ref:`DataSetGenerator(IRepository, boolean, long, File)`: The constructor of your class. This constructor has to be implemented and public, otherwise the framework will not be able to load your runresult format.
 *   * :java:ref:`DataSetGenerator(DataSetGenerator)`: The copy constructor of your class taking another instance of your class. This constructor has to be implemented and public.
 *   * :java:ref:`generateDataSet()`: This method generates the data set, writes it to the file system and returns a DataSet wrapper object.
 *   * :java:ref:`generatesGoldStandard()`: Returns true, if this generator generates a gold standard together with each generated data set.
 *   * :java:ref:`generateGoldStandard()`: If :java:ref:`generatesGoldStandard()` returns true, this method generates a gold standard for the generated data set, writes it to the file system and returns a GoldStandard wrapper object.
 *   * :java:ref:`getOptions()`: This method returns an :java:ref:`Options` object that encapsulates all parameters that this generator has. These can be set by the user in the client.
 *   * :java:ref:`handleOptions(CommandLine)`: This method handles the values that the user set for the parameters specified in :java:ref:`getOptions()`.
 *
 * 2. Creating a jar file named MyDataSetGenerator.jar containing the MyDataSetGenerator.class compiled on your machine in the correct folder structure corresponding to the packages:
 *
 *   * de/clusteval/data/dataset/generator/MyDataSetGenerator.class
 *
 * 3. Putting the MyDataSetGenerator.jar into the corresponding folder of the repository:
 *
 *   * <REPOSITORY ROOT>/supp/generators
 *   * The backend server will recognize and try to load the new class automatically the next time, the :java:ref:`DataSetGeneratorFinderThread` checks the filesystem.
 *
 * }
 *
 * @author Christian Wiwie
 *
 */
public abstract class DataSetGenerator extends AbstractDataSetProvider implements RLibraryInferior, IDataSetGenerator {

    /**
     * This attribute corresponds to the name of the folder located in
     * {@link IRepository#getDataSetBasePath()}, in which the dataset (and
     * goldstandard) will be stored.
     */
    private String folderName;

    /**
     * This attribute corresponds to the name of the dataset file, in which the
     * generated dataset (and optionally goldstandard) will be stored within the
     * {@link #folderName}.
     */
    private String fileName;

    /**
     * The alias of the data set that is to be generated.
     */
    private String alias;

    /**
     * Temp variable to hold the generated data set.
     */
    protected double[][] coords;

    /**
     * Temp variable for the goldstandard classes.
     */
    protected int[] classes;

    protected Logger LOG;

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public DataSetGenerator(IRepository repository, boolean register, long changeDate, File absPath)
            throws RegisterException {
        super(repository, register, changeDate, absPath);
        LOG = LoggerFactory.getLogger(DataSetGenerator.class.getName());
    }

    /**
     * The copy constructor of dataset generators.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public DataSetGenerator(DataSetGenerator other) throws RegisterException {
        super(other);
        LOG = LoggerFactory.getLogger(DataSetGenerator.class.getName());
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.RepositoryObject#clone()
     */
    @Override
    public RepositoryObject clone() {
        try {
            return this.getClass().getConstructor(this.getClass()).newInstance(this);
        } catch (IllegalArgumentException | SecurityException | InstantiationException |
                 IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class " + this.getClass().getSimpleName() + " failed");
        return null;
    }

    /**
     * @return A wrapper object keeping all options of your dataset generator
     *         together with the default options of all dataset generators. The options
     *         returned by this method are going to be used and interpreted in your
     *         subclass implementation in {@link #generateDataSet()} .
     */
    public Options getAllOptions() {
        // options of actual generator implementation
        Options options = this.getOptions();

        // default options of all generators
        this.addDefaultOptions(options);
        return options;
    }

    /**
     * This method has to be invoked with command line arguments for this
     * generator. Valid arguments are defined by the options returned by
     * {@link #getOptions()}.
     *
     * @param cliArguments
     * @return The generated {@link DataSet}.
     * @throws ParseException This exception is thrown, if the passed arguments
     * are not valid.
     * @throws DataSetGenerationException
     * @throws GoldStandardGenerationException
     * @throws InterruptedException
     * @throws UnknownDistanceMeasureException
     * @throws RegisterException
     * @throws RepositoryObjectDumpException
     */
    public IDataSet generate(final String[] cliArguments)
            throws ParseException, DataSetGenerationException, GoldStandardGenerationException, InterruptedException,
                   RepositoryObjectDumpException, RegisterException, UnknownProviderException {
        CommandLineParser parser = new PosixParser();

        Options options = this.getAllOptions();

        CommandLine cmd = parser.parse(options, cliArguments);

        this.folderName = cmd.getOptionValue("folderName");
        this.fileName = cmd.getOptionValue("fileName");
        this.alias = cmd.getOptionValue("alias");

        this.handleOptions(cmd);

        // Ensure, that the dataset target file does not exist yet
        File targetFile = new File(
                FileUtils.buildPath(this.repository.getBasePath(IDataSet.class), this.folderName, this.fileName));

        if (targetFile.exists()) {
            throw new ParseException("A dataset with the given name does already exist!");
        }
        targetFile.getParentFile().mkdirs();

        generateDataSet();

        IDataSet dataSet = null;

        try {
            // create the target file
            File dataSetFile = new File(FileUtils.buildPath(this.repository.getBasePath(IDataSet.class),
                    this.getFolderName(), this.getFileName()));

            dataSet = writeCoordsToFile(dataSetFile);
        } catch (IOException | RegisterException e) {
            throw new DataSetGenerationException("The dataset could not be generated!");
        }

        IGoldStandard gs = null;

        if (this.generatesGoldStandard()) {
            // Ensure, that the goldstandard target file does not exist yet
            targetFile = new File(FileUtils.buildPath(this.repository.getBasePath(IGoldStandard.class), this.folderName,
                    this.fileName));

            if (targetFile.exists()) {
                throw new ParseException("A goldstandard with the given name does already exist!");
            }
            targetFile.getParentFile().mkdirs();

            gs = generateGoldStandard();
        }

        IDataConfig dataConfig = this.writeConfigFiles(dataSet, gs, fileName);

        return dataSet;
    }

    /**
     * Adds the default options of dataset generators to the given Options
     * attribute
     *
     * @param options The existing Options attribute, holding already the
     *                options of the actual generator implementation.
     */
    private void addDefaultOptions(final Options options) {
        OptionBuilder.withArgName("folderName");
        OptionBuilder.isRequired();
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The name of the folder to store this dataset in.");
        Option option = OptionBuilder.create("folderName");
        options.addOption(option);

        OptionBuilder.withArgName("fileName");
        OptionBuilder.isRequired();
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The name of the dataset file to generate.");
        option = OptionBuilder.create("fileName");
        options.addOption(option);

        OptionBuilder.withArgName("alias");
        OptionBuilder.isRequired();
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The alias of the data set.");
        option = OptionBuilder.create("alias");
        options.addOption(option);
    }

    protected String getFileName() {
        return this.fileName;
    }

    protected String getFolderName() {
        return this.folderName;
    }

    protected String getAlias() {
        return this.alias;
    }

    /**
     * Parses a dataset generator from string.
     *
     * @param repository       the repository
     * @param dataSetGenerator The simple name of the dataset generator class.
     * @return the clustering quality measure
     * @throws UnknownDataSetGeneratorException
     */
    public static DataSetGenerator parseFromString(final IRepository repository, String dataSetGenerator)
            throws UnknownDataSetGeneratorException {

        Class<? extends DataSetGenerator> c = repository.getRegisteredClass(DataSetGenerator.class,
                "de.clusteval.data.dataset.generator." + dataSetGenerator);
        try {
            DataSetGenerator generator = c.getConstructor(IRepository.class, boolean.class, long.class, File.class)
                    .newInstance(repository, false, System.currentTimeMillis(), new File(dataSetGenerator));
            return generator;

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                 SecurityException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

        }
        throw new UnknownDataSetGeneratorException("\"" + dataSetGenerator + "\" is not a known dataset generator.");
    }

    /**
     * @param dataSetFile
     * @return
     * @throws IOException
     * @throws RegisterException
     * @throws UnknownProviderException
     *
     */
    protected IDataSet writeCoordsToFile(final File dataSetFile)
            throws IOException, RegisterException, UnknownProviderException {
        // writer header
        try ( // dataset file
                BufferedWriter writer = new BufferedWriter(new FileWriter(dataSetFile))) {
            // writer header
            writer.append("// alias = " + getAlias());
            writer.newLine();
            writer.append("// dataSetFormat = MatrixDataSetFormat");
            writer.newLine();
            writer.append("// dataSetType = SyntheticDataSetType");
            writer.newLine();
            writer.append("// dataSetFormatVersion = 1");
            writer.newLine();
            for (int row = 0; row < coords.length; row++) {
                StringBuilder sb = new StringBuilder();
                sb.append((row + 1));
                sb.append("\t");
                for (int i = 0; i < coords[row].length; i++) {
                    sb.append(coords[row][i]).append("\t");
                }
                sb.deleteCharAt(sb.length() - 1);
                writer.append(sb.toString());
                writer.newLine();
            }
        }

        return new AbsoluteDataSet(this.repository, true, dataSetFile.lastModified(), dataSetFile, getAlias(),
                (AbsoluteDataSetFormat) DataSetFormatFactory.parseFromString(repository, "MatrixDataSetFormat"),
                DataSetTypeFactory.parseFromString("SyntheticDataSetType"), WEBSITE_VISIBILITY.HIDE);

    }
}
