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
package de.clusteval.data.randomizer;

import de.clusteval.api.Pair;
import de.clusteval.api.data.AbstractDataSetProvider;
import de.clusteval.api.data.DataConfig;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataRandomizer;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IGoldStandard;
import de.clusteval.api.exceptions.RepositoryObjectDumpException;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RLibraryInferior;
import de.clusteval.api.repository.IRepository;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.openide.util.Exceptions;

/**
 * <p>
 * {@code
 *
 * A data randomizer MyDataRandomizer can be added to ClustEval by
 *
 * 1. extending this class with your own class MyDataRandomizer. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your runresult format.
 *
 *   * :java:ref:`DataRandomizer(Repository, boolean, long, File)`: The constructor of your class. This constructor has to be implemented and public, otherwise the framework will not be able to load your runresult format.
 *   * :java:ref:`DataRandomizer(DataRandomizer)`: The copy constructor of your class taking another instance of your class. This constructor has to be implemented and public.
 *   * :java:ref:`getOptions()`: This method returns an :java:ref:`Options` object that encapsulates all parameters that this randomizer has. These can be set by the user in the client.
 *   * :java:ref:`handleOptions(CommandLine)`: This method handles the values that the user set for the parameters specified in :java:ref:`getOptions()`.
 *   * :java:ref:`getDataSetFileNamePostFix()`: This method makes sure, that randomized data sets of the same data configuration do not end up with the same file name and overwrite each other. A good advice is to integrate the randomizer parameter values or a timestamp.
 *   * :java:ref:`randomizeDataConfig()`: This is the core of your randomizer; In this method the #dataConfig attribute is randomized and a distorted data set and gold standard is returned.
 *
 * 2. Creating a jar file named MyDataRandomizer.jar containing the MyDataRandomizer.class compiled on your machine in the correct folder structure corresponding to the packages:
 *
 *   * de/clusteval/data/randomizer/MyDataRandomizer.class
 *
 * 3. Putting the MyDataRandomizer.jar into the corresponding folder of the repository:
 *
 *   * <REPOSITORY ROOT>/supp/randomizers
 *   * The backend server will recognize and try to load the new class automatically the next time, the :java:ref:`DataRandomizerFinderThread` checks the filesystem.
 *
 * }
 *
 * @author Christian Wiwie
 *
 */
public abstract class DataRandomizer extends AbstractDataSetProvider implements RLibraryInferior, IDataRandomizer {

    /**
     * This attribute holds the name of the data configuration to randomize.
     */
    protected DataConfig dataConfig;

    protected String uniqueId;

    protected boolean onlySimulate;

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public DataRandomizer(IRepository repository, boolean register, long changeDate, File absPath)
            throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * The copy constructor of dataset randomizer.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public DataRandomizer(DataRandomizer other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.RepositoryObject#clone()
     */
    @Override
    public DataRandomizer clone() {
        try {
            return this.getClass().getConstructor(this.getClass()).newInstance(this);
        } catch (IllegalArgumentException | SecurityException |
                InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class " + this.getClass().getSimpleName() + " failed");
        return null;
    }

    /**
     * @return A wrapper object keeping all options of your dataset generator
     *         together with the default options of all dataset generators. The options
     *         returned by this method are going to be used and interpreted in your
     *         subclass implementation in {@link #randomizeDataConfig()} .
     */
    public Options getAllOptions() {
        // options of actual generator implementation
        Options options = this.getOptions();

        // default options of all generators
        this.addDefaultOptions(options);
        return options;
    }

    public IDataConfig randomize(final String[] cliArguments) throws DataRandomizeException {
        return randomize(cliArguments, false);
    }

    /**
     * This method has to be invoked with command line arguments for this
     * generator. Valid arguments are defined by the options returned by
     * {@link #getOptions()}.
     *
     * @param cliArguments
     * @return The generated {@link DataSet}.
     * @throws DataRandomizeException This exception is thrown, if the passed
     *                                arguments are not valid, or parsing of the written data set/ gold
     *                                standard or config files fails.
     * @throws DataRandomizeException
     */
    // TODO: remove onlySimulate attribute
    public IDataConfig randomize(final String[] cliArguments, final boolean onlySimulate) throws DataRandomizeException {
        try {
            this.onlySimulate = onlySimulate;
            CommandLineParser parser = new PosixParser();

            Options options = this.getAllOptions();

            CommandLine cmd = parser.parse(options, cliArguments);

            // get data config with the specified name
            String absFilePath = FileUtils.buildPath(this.repository.getBasePath(IDataConfig.class),
                    cmd.getOptionValue("dataConfig") + ".dataconfig");
            this.dataConfig = (DataConfig) this.repository.getRegisteredObject(new File(absFilePath));

            this.uniqueId = cmd.getOptionValue("uniqueId");

            this.handleOptions(cmd);

            Pair<IDataSet, IGoldStandard> newObjects = randomizeDataConfig();

            IDataConfig dataConfig = this.writeConfigFiles(newObjects.getFirst(), newObjects.getSecond(), this.uniqueId
                    + "_" + this.dataConfig.getGoldstandardConfig().toString() + getDataSetFileNamePostFix());

            return dataConfig;
        } catch (ParseException | InterruptedException | RException |
                RepositoryObjectDumpException | RegisterException | UnknownDistanceMeasureException e) {
            throw new DataRandomizeException(e);
        }
    }

    protected abstract String getDataSetFileNamePostFix();

    /**
     * Adds the default options of dataset generators to the given Options
     * attribute
     *
     * @param options The existing Options attribute, holding already the
     *                options of the actual generator implementation.
     */
    private void addDefaultOptions(final Options options) {
        OptionBuilder.withArgName("dataConfig");
        OptionBuilder.isRequired();
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The name of the data configuration to randomize");
        Option option = OptionBuilder.create("dataConfig");
        options.addOption(option);

        OptionBuilder.withArgName("uniqueId");
        OptionBuilder.isRequired();
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("A unique id (infix) for the generated files.");
        option = OptionBuilder.create("uniqueId");
        options.addOption(option);
    }

    /**
     * Parses a dataconfig randomizer from string.
     *
     * @param repository     the repository
     * @param dataRandomizer The simple name of the dataset randomizer class.
     * @return the clustering quality measure
     * @throws UnknownDataRandomizerException
     */
    public static DataRandomizer parseFromString(final IRepository repository, String dataRandomizer)
            throws UnknownDataRandomizerException {

        Class<? extends DataRandomizer> c = repository.getRegisteredClass(DataRandomizer.class,
                "de.clusteval.data.randomizer." + dataRandomizer);
        try {
            DataRandomizer generator = c.getConstructor(IRepository.class, boolean.class, long.class, File.class)
                    .newInstance(repository, false, System.currentTimeMillis(), new File(dataRandomizer));
            return generator;

        } catch (InstantiationException | IllegalAccessException |
                IllegalArgumentException | SecurityException |
                InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Exceptions.printStackTrace(e);
        }
        throw new UnknownDataRandomizerException("\"" + dataRandomizer + "\" is not a known data randomizer.");
    }
}
