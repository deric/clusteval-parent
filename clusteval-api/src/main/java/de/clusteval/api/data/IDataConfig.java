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

/**
 * A data configuration encapsulates options and settings for all kinds of data
 * (dataset and goldstandard). During the execution of a run, when programs are
 * applied to datasets and goldstandards, settings are required that control the
 * behaviour of how this data has to be handled.
 *
 * @author deric
 */
public interface IDataConfig extends IRepositoryObject {

    /**
     * The name of a data configuration is the filename of the corresponding
     * file on the filesystem, without the file extension.
     *
     * @return The name of this data configuration.
     */
    String getName();

    /**
     * Use this method to check, whether this DataConfig has a goldstandard
     * configuration or not. Some clustering quality measures do not require a
     * goldstandard to evaluate a clustering (see
     * {@link ClusteringQualityMeasure#requiresGoldstandard()}).
     *
     * @return True, if this data configuration has a goldstandard, false
     *         otherwise.
     */
    boolean hasGoldStandardConfig();

    /**
     *
     * @return If during the execution of a run the dataset has been converted
     *         to a different format, this method returns the converted dataset
     *         configuration. If no conversion has been performed, this method returns
     *         the original dataset configuration.
     */
    IDataSetConfig getDatasetConfig();

    IGoldStandardConfig getGoldstandardConfig();

}
