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

import de.clusteval.api.Precision;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.program.RegisterException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 *
 * @author deric
 */
public interface IDataSetFormatParser extends IRepositoryObject {

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
     * Signals that an I/O exception has occurred.
     * @throws InvalidDataSetFormatVersionException
     * @throws RegisterException
     * @throws UnknownDataSetFormatException
     * @throws RNotAvailableException
     * @throws InterruptedException
     * @throws InvalidParameterException
     */
    IDataSet convertToStandardFormat(IDataSet dataSet, IConversionInputToStandardConfiguration config)
            throws IOException,
                   InvalidDataSetFormatVersionException, RegisterException,
                   UnknownDataSetFormatException, RNotAvailableException,
                   InvalidParameterException, InterruptedException;

    /**
     * @param dataSet
     *                  The dataset to be parsed.
     * @param precision
     *                  The precision with which to store the similarities in memory.
     * @return A wrapper object containing the contents of the dataset
     * @throws IllegalArgumentException
     * @throws InvalidDataSetFormatVersionException
     * @throws IOException
     */
    Object parse(IDataSet dataSet, Precision precision) throws IOException, InvalidDataSetFormatVersionException;

    void writeToFileHelper(IDataSet dataSet, BufferedWriter writer) throws IOException;

    /**
     * @param dataSet
     *                   The dataset to be written to the filesystem.
     * @param withHeader
     *                   Whether to write the header into the dataset file.
     * @return True, if the dataset has been written to filesystem successfully.
     */
    boolean writeToFile(final IDataSet dataSet, final boolean withHeader);

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
     * Signals that an I/O exception has occurred.
     * @throws InvalidDataSetFormatVersionException
     * @throws RegisterException
     * @throws UnknownDataSetFormatException
     */
    IDataSet convertToThisFormat(IDataSet dataSet, IDataSetFormat dataSetFormat, IConversionConfiguration config)
            throws IOException, InvalidDataSetFormatVersionException,
                   RegisterException, UnknownDataSetFormatException;

}
