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
package de.clusteval.data.dataset;

import de.clusteval.api.IContext;
import de.clusteval.api.Precision;
import de.clusteval.api.data.IConversionConfiguration;
import de.clusteval.api.data.IConversionInputToStandardConfiguration;
import de.clusteval.api.data.IDataPreprocessor;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.data.IDataSetType;
import de.clusteval.api.data.WEBSITE_VISIBILITY;
import de.clusteval.api.exceptions.FormatConversionException;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.repository.RepositoryEvent;
import de.clusteval.api.repository.RepositoryMoveEvent;
import de.clusteval.api.repository.RepositoryRemoveEvent;
import de.clusteval.api.repository.RepositoryReplaceEvent;
import de.clusteval.api.data.AbsoluteDataSetFormat;
import de.clusteval.data.dataset.format.DataSetFormatParser;
import de.clusteval.api.data.RelativeDataSetFormat;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.api.repository.RepositoryObject;
import de.clusteval.utils.NamedDoubleAttribute;
import de.clusteval.utils.NamedIntegerAttribute;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

/**
 * A wrapper class for a dataset on the filesystem.
 *
 * <p>
 * A dataset corresponds to and is parsed from a file on the filesystem in the
 * corresponding folder of the repository (see {@link Repository#basePath} and
 * {@link DataSetFinder}).
 *
 * <p>
 * When a program should be applied to a certain dataset during a run, the
 * dataset is not directly used by the run but instead a {@link DataSetConfig}
 * is referenced, which encapsulates a dataset together with some settings.
 *
 * @author Christian Wiwie
 *
 */
public abstract class DataSet extends RepositoryObject implements IDataSet, IRepositoryObject {

    /**
     * Every data set needs an alias, that is used to represent the data set as
     * a short string, for example on the website.
     */
    protected String alias;

    /**
     * The format of this dataset. The format for a dataset is required by the
     * framework in order for it to be able to convert it to the internal
     * standard format.
     *
     * <p>
     * When a dataset is used during a run, it is first converted from its
     * original dataset format to the internal standard format. Then it is
     * converted to the input format required by the clustering method.
     */
    protected IDataSetFormat datasetFormat;

    /**
     * The type of the dataset is used to categorize the datasets.
     */
    protected IDataSetType datasetType;

    /**
     * When a dataset is used during a run, it first is converted to the
     * internal standard format and afterwards into the format required by the
     * clustering method. This attribute holds the version of this dataset in
     * the standard format.
     */
    protected IDataSet thisInStandardFormat;

    /**
     * When a dataset is used during a run, it first is converted to the
     * internal standard format and afterwards into the format required by the
     * clustering method. This attribute holds the original unconverted dataset.
     */
    protected IDataSet originalDataSet;

    /**
     * The checksum of a dataset is used to check a dataset for changes and to
     * check two datasets for equality.
     */
    protected long checksum;

    protected WEBSITE_VISIBILITY websiteVisibility;

    /**
     * Instantiates a new dataset object.
     *
     * @param repository        The repository this dataset should be registered at.
     * @param register          Whether this dataset should be registered in the
     *                          repository.
     * @param changeDate        The change date of this dataset is used for equality
     *                          checks.
     * @param absPath           The absolute path of this dataset.
     * @param alias             A short alias name for this data set.
     * @param dsFormat          The format of this dataset.
     * @param dsType            The type of this dataset
     * @param websiteVisibility
     * @throws RegisterException
     */
    public DataSet(final IRepository repository, final boolean register,
            final long changeDate, final File absPath, final String alias,
            final IDataSetFormat dsFormat, final IDataSetType dsType,
            final WEBSITE_VISIBILITY websiteVisibility)
            throws RegisterException {
        super(repository, false, changeDate, absPath);

        this.alias = alias;
        this.datasetFormat = dsFormat;
        this.datasetType = dsType;

        this.originalDataSet = this;

        this.checksum = absPath.length();

        this.websiteVisibility = websiteVisibility;

        createAndRegisterInternalAttributes();

        if (register) {
            if (this.register()) {
                // added 21.03.2013: register dataset format here: only if the
                // dataset has been registered
                this.datasetFormat.register();
                this.datasetFormat.addListener(this);
                // added 21.03.2013: register dataset type here: only if the
                // dataset has been registered
                this.datasetType.register();
                this.datasetType.addListener(this);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.framework.repository.RepositoryObject#setAbsolutePath(java
     * .io.File)
     */
    @Override
    public void setAbsolutePath(File absFilePath) {
        super.setAbsolutePath(absFilePath);

        try {
            createAndRegisterInternalAttributes();
        } catch (RegisterException e) {
        }
    }

    @SuppressWarnings("unused")
    private void createAndRegisterInternalAttributes() throws RegisterException {
        new NamedDoubleAttribute(this.repository, this.getAbsolutePath()
                + ":meanSimilarity", new Double(Float.NEGATIVE_INFINITY));
        new NamedDoubleAttribute(this.repository, this.getAbsolutePath()
                + ":minSimilarity", new Double(Float.NEGATIVE_INFINITY));
        new NamedDoubleAttribute(this.repository, this.getAbsolutePath()
                + ":maxSimilarity", new Double(Float.NEGATIVE_INFINITY));
        new NamedIntegerAttribute(this.repository, this.getAbsolutePath()
                + ":numberOfElements", new Integer(Integer.MIN_VALUE));
    }

    /**
     * Copy constructor for the DataSet class.
     *
     * @param dataset the dataset to be cloned
     * @throws RegisterException
     */
    public DataSet(DataSet dataset) throws RegisterException {
        super(dataset);

        this.datasetFormat = dataset.datasetFormat.clone();
        this.datasetType = dataset.datasetType.clone();

        this.checksum = absPath.length();

        this.websiteVisibility = dataset.websiteVisibility;

        if (dataset.originalDataSet != null
                && dataset.originalDataSet != dataset) {
            this.originalDataSet = dataset.originalDataSet.clone();
        } else {
            this.originalDataSet = dataset.originalDataSet;
        }
    }

    @Override
    public abstract DataSet clone();

    /**
     * Gets the dataset format.
     *
     * @see DataSet#datasetFormat
     * @return The dataset format
     */
    public IDataSetFormat getDataSetFormat() {
        return datasetFormat;
    }

    /**
     * @return The type of this dataset.
     * @see #datasetType
     */
    public IDataSetType getDataSetType() {
        return datasetType;
    }

    /**
     * Gets the major name of this dataset. The major name corresponds to the
     * folder the dataset resides in in the filesystem.
     *
     * @return The major name
     */
    public String getMajorName() {
        return absPath.getParentFile().getName();
    }

    /**
     * Gets the minor name of this dataset. The minor name corresponds to the
     * name of the file of this dataset.
     *
     * @return The minor name
     */
    public String getMinorName() {
        return absPath.getName();
    }

    /**
     * Gets the full name of this dataset. The full name consists of the minor
     * and the major name, separated by a slash: MAJOR/MINOR
     *
     * @return The full name
     */
    public String getFullName() {
        return getMajorName() + "/" + getMinorName();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getFullName();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.RepositoryObject#copyTo(java.io.File, boolean)
     */
    @Override
    public boolean copyTo(File copyDestination, final boolean overwrite) {
        return copyTo(copyDestination, overwrite, false);
    }

    @Override
    public boolean copyTo(File copyDestination, final boolean overwrite, final boolean updateAbsolutePath) {
        boolean result = false;
        if (!copyDestination.exists() || overwrite) {
            result = this.datasetFormat.copyDataSetTo(this, copyDestination,
                    overwrite);
        }
        if (copyDestination.exists() && updateAbsolutePath) {
            this.setAbsolutePath(copyDestination);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.RepositoryObject#copyToFolder(java.io.File, boolean)
     */
    @Override
    public boolean copyToFolder(File copyFolderDestination, final boolean overwrite) {
        return this.datasetFormat.copyDataSetToFolder(this, copyFolderDestination, overwrite);
    }

    /**
     * Load this dataset into memory. When this method is invoked, it parses the
     * dataset file on the filesystem using the
     * {@link DataSetFormatParser#parse(DataSet)} method corresponding to the
     * dataset format of this dataset. Then the contents of the dataset is
     * stored in a member variable. Depending on whether this dataset is
     * relative or absolute, this member variable varies: For absolute datasets
     * the data is stored in {@link AbsoluteDataSet#dataMatrix}, for relative
     * datasets in {@link RelativeDataSet#similarities}
     *
     * @return true, if successful
     * @throws UnknownDataSetFormatException
     * @throws InvalidDataSetFormatVersionException
     * @throws IOException
     * @throws IllegalArgumentException
     */
    @Override
    public boolean loadIntoMemory() throws IllegalArgumentException,
                                           IOException, InvalidDataSetFormatVersionException,
                                           UnknownDataSetFormatException {
        return this.loadIntoMemory(Precision.DOUBLE);
    }

    /**
     * Load this dataset into memory. When this method is invoked, it parses the
     * dataset file on the filesystem using the
     * {@link DataSetFormatParser#parse(DataSet)} method corresponding to the
     * dataset format of this dataset. Then the contents of the dataset is
     * stored in a member variable. Depending on whether this dataset is
     * relative or absolute, this member variable varies: For absolute datasets
     * the data is stored in {@link AbsoluteDataSet#dataMatrix}, for relative
     * datasets in {@link RelativeDataSet#similarities}
     *
     * @param precision
     * @return true, if successful
     * @throws UnknownDataSetFormatException
     * @throws InvalidDataSetFormatVersionException
     * @throws IOException
     * @throws IllegalArgumentException
     */
    @Override
    public abstract boolean loadIntoMemory(Precision precision)
            throws UnknownDataSetFormatException, IllegalArgumentException,
                   IOException, InvalidDataSetFormatVersionException;

    /**
     * This method writes the contents of this dataset to the file denoted by
     * the {@link #getAbsolutePath()}.
     *
     * @param withHeader Whether to write a header into the dataset file.
     *
     * @return True, if the writing was succesfull
     */
    public boolean writeToFile(final boolean withHeader) {
        if (!isInMemory()) {
            return false;
        }
        return this.getDataSetFormat().writeToFile(this, withHeader);
    }

    /**
     * This method converts this dataset to a target format:
     * <p>
     * First this dataset is converted to a internal standard format (depending
     * on the type of the Run). Then it is converted to the target format.
     *
     * @param context
     * @param targetFormat          This is the format, the dataset is expected to be in
     *                              after the conversion process. After the dataset is converted to the
     *                              internal format, it is converted to the target format.
     * @param configInputToStandard This is the configuration that is used
     *                              during the conversion from the original format to the internal standard
     *                              format.
     * @param configStandardToInput This is the configuration that is used
     *                              during the conversion from the internal standard format to the target
     *                              format.
     * @return The dataset in the target format.
     * @throws FormatConversionException
     * @throws IOException
     * @throws InvalidDataSetFormatVersionException
     * @throws RegisterException
     * @throws RNotAvailableException
     * @throws InterruptedException
     */
    @Override
    public IDataSet preprocessAndConvertTo(final IContext context,
            final IDataSetFormat targetFormat,
            final IConversionInputToStandardConfiguration configInputToStandard,
            final IConversionConfiguration configStandardToInput)
            throws FormatConversionException, IOException,
                   InvalidDataSetFormatVersionException, RegisterException,
                   RNotAvailableException, InterruptedException, RException, UnknownProviderException {

        // only one conversion process at a time
        File sourceFile = ClustevalBackendServer.getCommonFile(new File(this
                .getAbsolutePath()));
        synchronized (sourceFile) {
            IDataSet result = null;
            IDataSetFormat sourceFormat = this.getDataSetFormat();

            // check if we can convert from source to target format
            // right now no conversion from relative to absolute possible
            if (sourceFormat instanceof RelativeDataSetFormat
                    && targetFormat instanceof AbsoluteDataSetFormat) {
                throw new FormatConversionException(
                        "No conversion from relative to absolute dataset format possible.");
            }

            // check, whether dataset format parsers are registered.
            if ((!sourceFormat.equals(context.getStandardInputFormat()) && !this
                    .getRepository().isRegisteredForDataSetFormat(sourceFormat.getClass()))
                    || (!targetFormat.equals(context.getStandardInputFormat()) && !this
                    .getRepository().isRegisteredForDataSetFormat(targetFormat.getClass()))) {
                throw new FormatConversionException(
                        "No conversion from "
                        + sourceFormat
                        + " to "
                        + targetFormat
                        + " via internal standard format "
                        + context.getStandardInputFormat()
                        + " possible, because of missing dataset format parsers.");
            }

            // 13.04.2013: update the original dataset of the dataset to itself
            this.originalDataSet = this;
            this.log.debug("Apply preprocessors");
            // 13.04.2013: apply all data preprocessors before distance
            // conversion
            IDataSet preprocessed = this;
            List<IDataPreprocessor> preprocessors = configInputToStandard
                    .getPreprocessorsBeforeDistance();
            for (IDataPreprocessor proc : preprocessors) {
                preprocessed = proc.preprocess(preprocessed);
            }

            // convert the input format to the standard format
            try {
                IDataSetFormat standardFormat = context.getStandardInputFormat();
                // DataSetFormat.parseFromString(
                // repository, context.getStandardInputFormat().getClass()
                // .getSimpleName());
                standardFormat.setNormalized(targetFormat.getNormalized());

                this.log.debug(String.format(
                        "Convert input to standard format %s",
                        standardFormat.toString()));

                // Remove dataset attributes from file and write the result to
                // dataSet.getAbsolutePath() + ".strip"
                final File strippedFilePath = new File(this.getAbsolutePath()
                        + ".strip");
                if (!strippedFilePath.exists()) {
                    DataSetAttributeFilterer filterer = new DataSetAttributeFilterer(
                            this.getAbsolutePath());
                    filterer.process();
                }
                this.setAbsolutePath(strippedFilePath);

                try {
                    result = preprocessed.convertToStandardDirectly(context,
                            configInputToStandard);
                } catch (IOException | InvalidDataSetFormatVersionException |
                        RegisterException | UnknownDataSetFormatException |
                        InvalidParameterException | RNotAvailableException | InterruptedException e) {
                    e.printStackTrace();
                    // throw e;
                }

                // 13.04.2013: apply all data preprocessors after distance
                // conversion
                this.log.debug("Apply preprocessors");
                preprocessors = configInputToStandard.getPreprocessorsAfterDistance();
                for (IDataPreprocessor proc : preprocessors) {
                    result = proc.preprocess(result);
                }

                this.log.debug(String.format(
                        "Convert to input format of method %s",
                        targetFormat.toString()));
                /*
                 * This temporary dataset is now in our standard format. Store
                 * it and convert it to the target format.
                 */
                try {
                    result = result.convertStandardToDirectly(context,
                            targetFormat, configStandardToInput);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result;
            } catch (UnknownDataSetFormatException e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }

    /**
     * This method is a helper method to convert a dataset in a internal
     * standard format to a target format directly, that means using one
     * conversion step.
     *
     * @param context
     * @param targetFormat          This is the format, the dataset is expected to be in
     *                              after the conversion process. After the dataset is converted to the
     *                              internal format, it is converted to the target format.
     * @param configStandardToInput This is the configuration that is used
     *                              during the conversion from the internal standard format to the target
     *                              format.
     * @return The dataset in the target format.
     * @throws IOException                          Signals that an I/O exception has occurred.
     * @throws InvalidDataSetFormatVersionException
     * @throws RegisterException
     * @throws UnknownDataSetFormatException
     * @throws FormatConversionException
     */
    public IDataSet convertStandardToDirectly(final IContext context,
            final IDataSetFormat targetFormat,
            final IConversionConfiguration configStandardToInput)
            throws IOException, InvalidDataSetFormatVersionException,
                   RegisterException, UnknownDataSetFormatException,
                   FormatConversionException {

        IDataSet result = null;

        // if the target format is equal to the standard format, we simply
        // return the old dataset
        IDataSetFormat standardFormat = context.getStandardInputFormat();
        if (targetFormat.equals(standardFormat)) {
            result = this;
        } // if the target format is an absolute format, then the conversion is
        // only possible, if the original data set was an absolute data set.
        // Then we return the original data set.
        else if (this.getDataSetFormat() instanceof RelativeDataSetFormat
                && targetFormat instanceof AbsoluteDataSetFormat) {
            if (this.getOriginalDataSet().getDataSetFormat() instanceof AbsoluteDataSetFormat) {
                // changed 18.03.2014: returning a new dataset object instead of
                // the originalDataSet with changed path here
                this.originalDataSet.copyTo(
                        new File(this.originalDataSet.getAbsolutePath()
                                + ".conv"), false, false);
                IDataSet tmp = this.originalDataSet.clone();
                tmp.setAbsolutePath(new File(this.originalDataSet
                        .getAbsolutePath() + ".conv"));
                tmp.setOriginal(originalDataSet);
                result = tmp;
            } else {
                throw new FormatConversionException(
                        "No conversion from relative to absolute dataset format possible.");
            }
        } else {
            result = targetFormat.convertToThisFormat(this, targetFormat, configStandardToInput);
        }
        result.setStandard(this);
        return result;
    }

    /**
     * This method is a helper method to convert a dataset in its original
     * format to a internal standard format directly, that means using one
     * conversion step.
     *
     * @param context
     * @param dsFormat              This is the format, the dataset is expected to be in
     *                              after the conversion process. After the dataset is converted to the
     *                              internal format, it is converted to the target format.
     * @param configInputToStandard This is the configuration that is used
     *                              during the conversion from the original format to the internal standard
     *                              format.
     * @return The dataset in the target format.
     * @throws IOException                          Signals that an I/O exception has occurred.
     * @throws InvalidDataSetFormatVersionException
     * @throws RegisterException
     * @throws UnknownDataSetFormatException
     * @throws RNotAvailableException
     * @throws InvalidParameterException
     * @throws InterruptedException
     */
    @Override
    public IDataSet convertToStandardDirectly(final IContext context,
            final IConversionInputToStandardConfiguration configInputToStandard)
            throws IOException, InvalidDataSetFormatVersionException,
                   RegisterException, UnknownDataSetFormatException,
                   InvalidParameterException, RNotAvailableException,
                   InterruptedException, UnknownProviderException {
        IDataSet result;

        // if the source format is equal to the standard format, we simply
        // return the old dataset
        IDataSetFormat standardFormat = context.getStandardInputFormat();
        if (this.getDataSetFormat().equals(standardFormat)) {
            result = this;
        } else {
            result = this.getDataSetFormat().convertToStandardFormat(this, configInputToStandard);
        }
        result.setStandard(result);
        this.thisInStandardFormat = result;
        result.setOriginal(originalDataSet);
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.RepositoryObject#notify(utils.RepositoryEvent)
     */
    @Override
    public void notify(RepositoryEvent e) throws RegisterException {
        if (e instanceof RepositoryReplaceEvent) {
            RepositoryReplaceEvent event = (RepositoryReplaceEvent) e;
            if (event.getOld().equals(this)) {
                super.notify(event);
            }
        } else if (e instanceof RepositoryRemoveEvent) {
            RepositoryRemoveEvent event = (RepositoryRemoveEvent) e;
            if (event.getRemovedObject().equals(this)) {
                super.notify(event);
            } else if (event.getRemovedObject().equals(this.datasetFormat)) {
                event.getRemovedObject().removeListener(this);
                this.log.info("DataSet " + this
                        + ": Removed, because DataSetFormat " + datasetFormat
                        + " has changed.");
                RepositoryRemoveEvent newEvent = new RepositoryRemoveEvent(this);
                this.unregister();
                this.notify(newEvent);
            } else if (event.getRemovedObject().equals(this.datasetType)) {
                event.getRemovedObject().removeListener(this);
                this.log.info("DataSet " + this
                        + ": Removed, because DataSetType " + datasetType
                        + " has changed.");
                RepositoryRemoveEvent newEvent = new RepositoryRemoveEvent(this);
                this.unregister();
                this.notify(newEvent);
            }
        } else if (e instanceof RepositoryMoveEvent) {
            RepositoryMoveEvent event = (RepositoryMoveEvent) e;
            if (event.getObject().equals(this)) {
                super.notify(event);
            }
        }
    }

    /**
     * Move this dataset to a new location.
     *
     * <p>
     * If the overwrite parameter is true and the target file exists already, it
     * is overwritten.
     *
     * @param targetFile A file object wrapping the absolute path of the
     *                   destination this dataset should be moved to.
     * @param overwrite  A boolean indicating, whether to overwrite a possibly
     *                   existing target file.
     * @return True, if this dataset has been moved successfully.
     */
    @Override
    public boolean moveTo(final File targetFile, final boolean overwrite) {
        if (!targetFile.exists() || overwrite) {
            boolean result = this.datasetFormat.moveDataSetTo(this, targetFile,
                    overwrite);
            if (targetFile.exists()) {
                this.absPath = targetFile;
                try {
                    this.notify(new RepositoryMoveEvent(this));
                } catch (RegisterException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
        if (targetFile.exists()) {
            this.absPath = targetFile;
        }
        return false;
    }

    /**
     * @return The alias of this data set.
     * @see #alias
     */
    public String getAlias() {
        return this.alias;
    }

    /**
     * @return This dataset in the internal standard format.
     * @see #thisInStandardFormat
     */
    public IDataSet getInStandardFormat() {
        return this.thisInStandardFormat;
    }

    @Override
    public void setStandard(IDataSet result) {
        this.thisInStandardFormat = result;
    }

    /**
     * @return This dataset in its original format.
     * @see #originalDataSet
     */
    @Override
    public IDataSet getOriginalDataSet() {
        return this.originalDataSet;
    }

    public void setOriginal(IDataSet dataset) {
        this.originalDataSet = dataset;
    }

    /**
     * @return Checksum of this dataset
     * @see #checksum
     */
    public long getChecksum() {
        return this.checksum;
    }

    /**
     * @return The object ids contained in the dataset.
     */
    public abstract List<String> getIds();

    @Override
    public WEBSITE_VISIBILITY getWebsiteVisibility() {
        return this.websiteVisibility;
    }

    /**
     * This method parses the header of a dataset file. A header is required for
     * a dataset file to be recognized by the framework as a valid dataset file.
     * If the file does not contain any header lines, it is ignored by the
     * framework. A header line is of the form '// attribute = value'. The
     * header should contain several lines:
     *
     * <p>
     * The type of the dataset, e.g. '// dataSetType =
     * GeneExpressionDataSetType'
     * <p>
     * The format of the dataset, e.g. '// dataSetFormat = RowSimDataSetFormat'
     * <p>
     * The version of the dataset format, e.g. '// dataSetFormatVersion = 1'
     *
     * @param absPath
     * @return
     * @throws IOException
     */
    @Deprecated
    protected static Map<String, String> extractDataSetAttributes(
            final File absPath) throws IOException {
        DataSetAttributeParser attributeParser = new DataSetAttributeParser(
                absPath.getAbsolutePath());
        attributeParser.process();
        Map<String, String> attributeValues = attributeParser
                .getAttributeValues();
        return attributeValues;
    }
}
