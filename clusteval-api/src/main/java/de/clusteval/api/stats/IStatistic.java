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
package de.clusteval.api.stats;

import de.clusteval.api.repository.IRepositoryObject;

/**
 *
 * @author deric
 */
public interface IStatistic extends IRepositoryObject {

    /**
     * Parses the values of a statistic from a string and stores them in the
     * local attributes of this object.
     *
     * @param contents The string to parse the values from.
     *
     */
    void parseFromString(final String contents);

    String toString();

}
