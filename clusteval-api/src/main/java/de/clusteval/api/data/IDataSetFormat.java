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
import de.clusteval.api.repository.IRepositoryObject;
import de.wiwie.wiutils.utils.SimilarityMatrix;
import java.io.IOException;

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
}
