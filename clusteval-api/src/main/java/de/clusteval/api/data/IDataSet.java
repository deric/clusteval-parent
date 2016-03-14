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
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.repository.IRepositoryObject;
import java.io.IOException;
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
     * @return This dataset in its original format.
     * @see #originalDataSet
     */
    IDataSet getOriginalDataSet();

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
    public boolean loadIntoMemory() throws IllegalArgumentException,
                                           IOException, InvalidDataSetFormatVersionException,
                                           UnknownDataSetFormatException;
}
