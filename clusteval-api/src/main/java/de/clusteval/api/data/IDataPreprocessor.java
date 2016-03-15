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

import de.clusteval.api.repository.IRepositoryObject;
import java.util.Set;

/**
 *
 * @author deric
 */
public interface IDataPreprocessor extends IRepositoryObject {

    /**
     * This method is reponsible for preprocessing the passed data and creating
     * a new dataset object corresponding to the newly created preprocessed
     * dataset.
     *
     * @param dataSet
     *                The dataset to be preprocessed.
     * @return The preprocessed dataset.
     * @throws InterruptedException
     */
    IDataSet preprocess(final IDataSet dataSet) throws InterruptedException;

    IDataPreprocessor clone();

    /**
     * @return A set with simple names of all classes, this preprocessor is
     *         compatible to.
     */
    Set<String> getCompatibleDataSetFormats();
}
