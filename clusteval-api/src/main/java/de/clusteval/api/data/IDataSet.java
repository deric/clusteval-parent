/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.api.data;

import de.clusteval.api.IContext;
import de.clusteval.api.Precision;
import de.clusteval.api.exceptions.FormatConversionException;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.repository.IRepositoryObject;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

/**
 *
 * @author deric
 */
public interface IDataSet extends IRepositoryObject {

    String getAlias();

    /**
     * @return This dataset in the internal standard format.
     * @see #thisInStandardFormat
     */
    IDataSet getInStandardFormat();

    /**
     * Set dataset in standard format
     *
     * @param result
     */
    void setStandard(IDataSet result);

    /**
     * @return This dataset in its original format.
     * @see #originalDataSet
     */
    IDataSet getOriginalDataSet();

    void setOriginal(IDataSet dataset);

    /**
     * Compute dataset checksum
     *
     * @return
     */
    long getChecksum();

    /**
     * @return The object IDs contained in the dataset.
     */
    List<String> getIds();

    IDataSetFormat getDataSetFormat();

    /**
     * Checks whether this dataset is loaded into the memory.
     *
     * @return true, if is in memory
     */
    boolean isInMemory();

    /*
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
    boolean loadIntoMemory() throws IllegalArgumentException, IOException, InvalidDataSetFormatVersionException,
                                    UnknownDataSetFormatException;

    IDataSetType getDataSetType();

    IDataSet clone();

    /**
     * Gets the major name of this dataset. The major name corresponds to the
     * folder the dataset resides in in the filesystem.
     *
     * @return The major name
     */
    String getMajorName();

    /**
     * Gets the minor name of this dataset. The minor name corresponds to the
     * name of the file of this dataset.
     *
     * @return The minor name
     */
    String getMinorName();

    /**
     * Gets the full name of this dataset. The full name consists of the minor
     * and the major name, separated by a slash: MAJOR/MINOR
     *
     * @return The full name
     */
    String getFullName();

    WEBSITE_VISIBILITY getWebsiteVisibility();

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
    IDataSet convertToStandardDirectly(final IContext context, final IConversionInputToStandardConfiguration configInputToStandard)
            throws IOException, InvalidDataSetFormatVersionException,
                   RegisterException, UnknownDataSetFormatException,
                   InvalidParameterException, RNotAvailableException,
                   InterruptedException;

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
    IDataSet convertStandardToDirectly(final IContext context,
            final IDataSetFormat targetFormat,
            final IConversionConfiguration configStandardToInput)
            throws IOException, InvalidDataSetFormatVersionException,
                   RegisterException, UnknownDataSetFormatException,
                   FormatConversionException;

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
    public IDataSet preprocessAndConvertTo(final IContext context,
            final IDataSetFormat targetFormat,
            final IConversionInputToStandardConfiguration configInputToStandard,
            final IConversionConfiguration configStandardToInput)
            throws FormatConversionException, IOException,
                   InvalidDataSetFormatVersionException, RegisterException,
                   RNotAvailableException, InterruptedException, RException;

    /**
     * This method does not load the content of the dataset into memory, it just
     * assumes that it has been loaded before and returns the reference.
     *
     * @return The content of this dataset.
     */
    Object getDataSetContent();

    /**
     * This method sets the content of this dataset in memory to a new object.
     * Contents on file system are not refreshed.
     *
     * @param newContent The new content of this dataset.
     * @return True, if the content of this dataset has been updated to the new
     *         object.
     */
    boolean setDataSetContent(Object newContent);

    /**
     * Unload the contents of this dataset from memory.
     *
     * @return true, if successful
     */
    boolean unloadFromMemory();

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
    boolean loadIntoMemory(Precision precision)
            throws UnknownDataSetFormatException, IllegalArgumentException,
                   IOException, InvalidDataSetFormatVersionException;
}
