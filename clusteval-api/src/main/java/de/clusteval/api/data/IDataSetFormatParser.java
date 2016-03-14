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
     *                                              Signals that an I/O exception has occurred.
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

}
