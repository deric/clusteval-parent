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

import de.clusteval.api.exceptions.RepositoryObjectDumpException;
import de.clusteval.api.repository.IRepositoryObject;

/**
 *
 * A dataset configuration encapsulates options and settings for a dataset.
 * During the execution of a run, when programs are applied to datasets,
 * settings are required that control the behaviour of how the dataset has to be
 * handled.
 *
 * @author deric
 */
public interface IDataSetConfig extends IRepositoryObject {

    /**
     * @return The dataset, this configuration belongs to.
     */
    public IDataSet getDataSet();

    public IDataSetConfig clone();

    /**
     * @throws RepositoryObjectDumpException
     */
    void dumpToFile() throws RepositoryObjectDumpException;

    /**
     * @return The configuration for conversion from the original input format
     *         to the standard format.
     * @see #configInputToStandard
     */
    IConversionInputToStandardConfiguration getConversionInputToStandardConfiguration();

    /**
     * @param dataset The new dataset
     */
    void setDataSet(IDataSet dataset);

    IConversionStandardToInputConfiguration getConversionStandardToInputConfiguration();

}
