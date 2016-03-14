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

import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.repository.IRepositoryObject;

/**
 *
 * @author deric
 */
public interface IGoldStandard extends IRepositoryObject {

    boolean isInMemory();

    boolean loadIntoMemory() throws UnknownGoldStandardFormatException;

    /**
     * This method returns a reference to the clustering object representing the
     * contents of the goldstandard file.
     *
     * <p>
     * If this is not already the case, the contents of the file are parsed by
     * invoking {@link #loadIntoMemory()}.
     *
     * @return The clustering object representing the goldstandard.
     * @throws UnknownGoldStandardFormatException the unknown gold standard
     *                                            format exception
     */
    IClustering getClustering() throws UnknownGoldStandardFormatException;

    /**
     *
     * @return cloned object
     */
    IGoldStandard clone();

    /**
     * Gets the major name of this goldstandard. The major name corresponds to
     * the folder the goldstandard resides in in the filesystem.
     *
     * @return The major name
     */
    String getMajorName();

    /**
     * Gets the minor name of this goldstandard. The minor name corresponds to
     * the name of the file of this goldstandard.
     *
     * @return The minor name
     */
    public String getMinorName();

}
