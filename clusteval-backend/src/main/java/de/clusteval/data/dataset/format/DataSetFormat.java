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
package de.clusteval.data.dataset.format;

import de.clusteval.api.Precision;
import de.clusteval.api.data.DataSetFormatFactory;
import de.clusteval.api.data.IConversionConfiguration;
import de.clusteval.api.data.IConversionInputToStandardConfiguration;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.data.IDataSetFormatParser;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.framework.repository.RepositoryObject;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Datasets can have different formats. For all kinds of operations the
 * framework needs to know which format a dataset has and how it can be
 * converted to an understandable (standard) format.
 *
 * <p>
 * Every dataset format comes together with a parser class (see
 * {@link DataSetFormatParser}).
 *
 * <p>
 * {@code
 *
 * A data set format MyDataSetFormat can be added to ClustEval by
 *
 * 1. extending the class de.clusteval.data.dataset.format.DataSetFormat with your own class MyDataSetFormat. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your dataset format.
 *
 *   * :java:ref:`DataSetFormat(IRepository, boolean, long, File, int)`: The constructor of your dataset format class. This constructor has to be implemented and public, otherwise the framework will not be able to load your dataset format.
 *   * :java:ref:`DataSetFormat(DataSetFormat)`: The copy constructor of your class taking another instance of your class. This constructor has to be implemented and public.
 *
 * 2. extending the class de.clusteval.data.dataset.format.DataSetFormatParser with your own class MyDataSetFormatParser. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your class.
 *
 *   * :java:ref:`convertToStandardFormat(DataSet, ConversionInputToStandardConfiguration)`: This method converts the given dataset to the standard input format of the framework using the given conversion configuration. This assumes, that the passed dataset has this format.
 *   * :java:ref:`convertToThisFormat(DataSet, DataSetFormat, ConversionConfiguration)`: This method converts the given dataset to the given input format using the conversion configuration.
 *   * :java:ref:`parse(DataSet)`: This method parses the given dataset and returns an object, wrapping the contents of the dataset (e.g. an instance of SimilarityMatrix or DataMatrix ).
 *
 * 3. Creating a jar file named MyDataSetFormat.jar containing the MyDataSetFormat.class and MyDataSetFormatParser.class compiled on your machine in the correct folder structure corresponding to the packages:
 *
 *   * de/clusteval/data/dataset/format/MyDataSetFormat.class
 *   * de/clusteval/data/dataset/format/MyDataSetFormatParser.class
 *
 * 4. Putting the MyDataSetFormat.jar into the dataset formats folder of the repository:
 *
 *   * <REPOSITORY ROOT>/supp/formats/dataset
 *   * The backend server will recognize and try to load the new dataset format automatically the next time, the :java:ref:`DataSetFormatFinderThread` checks the filesystem.
 *
 * }
 *
 *
 * @author Christian Wiwie
 *
 */
public abstract class DataSetFormat extends RepositoryObject implements IDataSetFormat {

    protected static final Logger LOG = LoggerFactory.getLogger(DataSetFormat.class);

    public DataSetFormat() {

    }

    /**
     * This method returns a deep copy of the given list of dataset formats,
     * i.e. the objects of the list are also cloned.
     *
     * @param dataSetFormats
     *                       The list of dataset formats to clone.
     * @return The cloned list of dataset formats.
     */
    public static List<IDataSetFormat> cloneDataSetFormats(final List<IDataSetFormat> dataSetFormats) {
        List<IDataSetFormat> result = new ArrayList<>();

        for (IDataSetFormat dataSetFormat : dataSetFormats) {
            result.add(dataSetFormat.clone());
        }

        return result;
    }

    /**
     * A boolean indicating, whether the dataset format is normalized.
     */
    private boolean normalized;

    /**
     * The version number of the dataset format.
     *
     * <p>
     * This is used for compatibility reasons to ensure, that if at some point a
     * format specification changes, the framework can recognize this.
     */
    private int version;

    /**
     * This method parses a dataset format from the given string, containing a
     * dataset format class name and a given dataset format version.
     *
     * @param repository
     *                      The repository where to look up the dataset format class.
     * @param datasetFormat
     *                      The dataset format class name as string.
     * @param formatVersion
     *                      The version of the dataset format.
     * @return The parsed dataset format.
     * @throws UnknownDataSetFormatException
     */
    public static IDataSetFormat parseFromString(final IRepository repository,
            String datasetFormat, final int formatVersion) throws UnknownDataSetFormatException, UnknownProviderException {

        DataSetFormatFactory factory = DataSetFormatFactory.getInstance();
        if (factory.hasProvider(datasetFormat)) {
            IDataSetFormat format = factory.getProvider(datasetFormat);

            format.init(repository, System.currentTimeMillis(), new File(datasetFormat));
            return format;
        }

        throw new UnknownDataSetFormatException("\"" + datasetFormat + "\" "
                + "is not a known dataset format. Supported formats are "
                + factory.getAll().toString());
    }

    /**
     * This method parses a dataset format from the given string, containing a
     * dataset format class name.
     *
     * @param repository
     *                      The repository where to look up the dataset format class.
     * @param datasetFormat
     *                      The dataset format class name as string.
     * @return The parsed dataset format.
     * @throws UnknownDataSetFormatException
     */
    public static IDataSetFormat parseFromString(final IRepository repository,
            String datasetFormat) throws UnknownDataSetFormatException, UnknownProviderException {
        return parseFromString(repository, datasetFormat,
                repository.getCurrentDataSetFormatVersion(datasetFormat));
    }

    /**
     * This method parses several dataset formats from a string array.
     *
     * <p>
     * This is a convenience method for
     * {@link #parseFromString(IRepository, String)}.
     *
     * @param repo
     *                       the repo
     * @param datasetFormats
     *                       the dataset formats
     * @return the list
     * @throws UnknownDataSetFormatException
     *                                       the unknown data set format exception
     * @throws de.clusteval.api.factory.UnknownProviderException
     */
    public static List<IDataSetFormat> parseFromString(final IRepository repo,
            String[] datasetFormats) throws UnknownDataSetFormatException, UnknownProviderException {
        List<IDataSetFormat> result = new LinkedList<>();
        for (String dsFormat : datasetFormats) {
            result.add(parseFromString(repo, dsFormat));
        }
        return result;
    }

    /**
     * @param dataSet
     *                  The dataset to be parsed.
     * @param precision
     * @return A wrapper object containing the contents of the dataset
     * @throws IllegalArgumentException
     * @throws InvalidDataSetFormatVersionException
     * @throws IOException
     */
    public Object parse(final IDataSet dataSet, Precision precision)
            throws IllegalArgumentException, IOException,
                   InvalidDataSetFormatVersionException {
        final IDataSetFormatParser parser = getDataSetFormatParser();
        if (parser == null) {
            throw new IllegalArgumentException("Operation only supported for the standard dataset format");
        }
        return parser.parse(dataSet, precision);
    }

    /**
     * @param dataSet
     *                   The dataset to be written to the filesystem.
     * @param withHeader
     *                   Whether to write the header into the dataset file.
     * @return True, if the dataset has been written to filesystem successfully.
     */
    public boolean writeToFile(final IDataSet dataSet, final boolean withHeader) {
        final IDataSetFormatParser parser = getDataSetFormatParser();
        if (parser == null) {
            throw new IllegalArgumentException(
                    "Operation only supported for the standard dataset format");
        }
        return parser.writeToFile(dataSet, withHeader);
    }

    /**
     * Convert the given dataset with this dataset format and the given version
     * using the passed configuration.
     *
     * <p>
     * This method validates, that the passed dataset has the correct format and
     * that the version of the format is supported.
     *
     * @param dataSet
     *                The dataset to convert to the standard format.
     * @param config
     *                The configuration to use to convert the passed dataset.
     * @return The converted dataset.
     * @throws IOException
     *                                              Signals that an I/O exception has occurred.
     * @throws InvalidDataSetFormatVersionException
     * @throws RegisterException
     * @throws UnknownDataSetFormatException
     * @throws RNotAvailableException
     * @throws InterruptedException
     * @throws InvalidParameterException
     */
    @Override
    public final IDataSet convertToStandardFormat(IDataSet dataSet,
            IConversionInputToStandardConfiguration config)
            throws IOException,
                   InvalidDataSetFormatVersionException, RegisterException,
                   UnknownDataSetFormatException, RNotAvailableException,
                   InvalidParameterException, InterruptedException {
        final IDataSetFormatParser parser = getDataSetFormatParser();
        if (parser == null) {
            throw new IllegalArgumentException(
                    "Operation only supported for the standard dataset format");
        }
        return parser.convertToStandardFormat(dataSet, config);
    }

    /**
     * Convert the given dataset to the given dataset format (this format) using
     * the passed configuration.
     *
     * <p>
     * The passed dataset format object has to be of this class and is used only
     * for its version and normalize attributes.
     *
     * <p>
     * This method validates, that the passed dataset format to convert the
     * dataset to is correct and that the version of the format is supported.
     *
     * @param dataSet
     *                      The dataset to convert to the standard format.
     * @param dataSetFormat
     *                      The dataset format to convert the dataset to.
     * @param config
     *                      The configuration to use to convert the passed dataset.
     * @return The converted dataset.
     * @throws IOException
     *                                              Signals that an I/O exception has occurred.
     * @throws InvalidDataSetFormatVersionException
     * @throws RegisterException
     * @throws UnknownDataSetFormatException
     */
    public final IDataSet convertToThisFormat(IDataSet dataSet, IDataSetFormat dataSetFormat, IConversionConfiguration config)
            throws IOException, InvalidDataSetFormatVersionException,
                   RegisterException, UnknownDataSetFormatException {
        final IDataSetFormatParser parser = getDataSetFormatParser();
        if (parser == null) {
            throw new IllegalArgumentException(
                    "Operation only supported for the standard dataset format");
        }
        return parser.convertToThisFormat(dataSet, dataSetFormat, config);
    }

    /**
     * Instantiates a new dataset format with the given version.
     *
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     *
     * @param version    The version of the dataset format.
     *
     * @throws RegisterException
     */
    public DataSetFormat(final IRepository repo, final boolean register,
            final long changeDate, final File absPath, final int version)
            throws RegisterException {
        super(repo, false, changeDate, absPath);
        this.version = version;
        this.log = LoggerFactory.getLogger(this.getClass());

        if (register) {
            this.register();
        }
    }

    /**
     * The copy constructor of dataset formats.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public DataSetFormat(final DataSetFormat other) throws RegisterException {
        super(other);

        this.normalized = other.normalized;
        this.version = other.version;
    }

    /**
     * @param normalized
     *                   Whether this dataset is normalized.
     */
    public void setNormalized(final boolean normalized) {
        this.normalized = normalized;
    }

    /**
     * @return Whether this dataset is normalized.
     */
    public boolean getNormalized() {
        return this.normalized;
    }

    /**
     * @return The version number of the dataset format.
     */
    @Override
    public int getVersion() {
        return this.version;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DataSetFormat)) {
            return false;
        }

        DataSetFormat other = (DataSetFormat) o;
        return (other.getClass().getSimpleName()
                .equals(this.getClass().getSimpleName())
                && this.normalized == other.normalized && this.getVersion() == other
                .getVersion());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public final DataSetFormat clone() {
        try {
            return this.getClass().getConstructor(this.getClass())
                    .newInstance(this);
        } catch (IllegalArgumentException | SecurityException | InstantiationException |
                IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class "
                + this.getClass().getSimpleName() + " failed");
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (this.getClass().toString() + this.normalized + this
                .getVersion()).hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":v" + this.getVersion();
    }

    /**
     * This method copies the given dataset to the given target file, assuming
     * that the format of the dataset is this dataset format.
     *
     * @param dataSet
     *                        The dataset to copy to the target file destination.
     * @param copyDestination
     *                        The target file to which to copy the given dataset.
     * @param overwrite
     *                        Whether to overwrite the possibly already existing target
     *                        file.
     * @return True, if the copy operation was successful.
     */
    @Override
    public boolean copyDataSetTo(final IDataSet dataSet, final File copyDestination, final boolean overwrite) {
        try {
            if (!copyDestination.exists() || overwrite) {
                org.apache.commons.io.FileUtils.copyFile(
                        new File(dataSet.getAbsolutePath()), copyDestination);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * This method copies the given dataset to the given target file, assuming
     * that the format of the dataset is this dataset format.
     *
     * @param dataSet
     *                        The dataset to copy to the target file destination.
     * @param moveDestination
     *                        The target file to which to copy the given dataset.
     * @param overwrite
     *                        Whether to overwrite the possibly already existing target
     *                        file.
     * @return True, if the copy operation was successful.
     */
    @Override
    public boolean moveDataSetTo(final IDataSet dataSet, final File moveDestination, final boolean overwrite) {
        try {
            if (!moveDestination.exists() || overwrite) {
                org.apache.commons.io.FileUtils.moveFile(
                        new File(dataSet.getAbsolutePath()), moveDestination);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * This method copies the given dataset into the given target folder,
     * assuming that the format of the dataset is this dataset format.
     *
     * @param dataSet
     *                              The dataset to copy to the target file destination.
     * @param copyFolderDestination
     *                              The target folder to which into copy the given dataset.
     * @param overwrite
     *                              Whether to overwrite the possibly already existing target
     *                              file.
     * @return True, if the copy operation was successful.
     */
    @Override
    public boolean copyDataSetToFolder(final IDataSet dataSet, final File copyFolderDestination, final boolean overwrite) {
        try {
            File targetFile = new File(FileUtils.buildPath(
                    copyFolderDestination.getAbsolutePath(),
                    new File(dataSet.getAbsolutePath()).getName()));
            if (!targetFile.exists() || overwrite) {
                org.apache.commons.io.FileUtils.copyFile(
                        new File(dataSet.getAbsolutePath()), targetFile);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
