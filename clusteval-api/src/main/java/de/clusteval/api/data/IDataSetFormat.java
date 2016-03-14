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

import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.RNotAvailableException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.repository.RegisterException;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 *
 * @author deric
 */
public interface IDataSetFormat extends IRepositoryObject {

    Object parse(final IDataSet dataSet, SimilarityMatrix.NUMBER_PRECISION precision)
            throws IllegalArgumentException, IOException, InvalidDataSetFormatVersionException;

    /**
     * @param dataSet
     *                   The dataset to be written to the filesystem.
     * @param withHeader
     *                   Whether to write the header into the dataset file.
     * @return True, if the dataset has been written to filesystem successfully.
     */
    boolean writeToFile(final IDataSet dataSet, final boolean withHeader);

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
    IDataSet convertToStandardFormat(IDataSet dataSet, IConversionInputToStandardConfiguration config) throws IOException,
                                                                                                              InvalidDataSetFormatVersionException, RegisterException,
                                                                                                              UnknownDataSetFormatException, RNotAvailableException,
                                                                                                              InvalidParameterException, InterruptedException;

    /**
     *
     * @return cloned object
     */
    IDataSetFormat clone();

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
    boolean copyDataSetTo(final IDataSet dataSet, final File copyDestination, final boolean overwrite);

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
    public boolean copyDataSetToFolder(final IDataSet dataSet, final File copyFolderDestination, final boolean overwrite);

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
    public boolean moveDataSetTo(final IDataSet dataSet, final File moveDestination, final boolean overwrite);

    /**
     * @return The version number of the dataset format.
     */
    public int getVersion();

    /**
     * @param normalized
     *                   Whether this dataset is normalized.
     */
    void setNormalized(final boolean normalized);

    /**
     * @return Whether this dataset is normalized.
     */
    boolean getNormalized();
}
