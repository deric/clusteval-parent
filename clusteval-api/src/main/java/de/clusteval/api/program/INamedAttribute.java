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
package de.clusteval.api.program;

import de.clusteval.api.repository.IRepositoryObject;

/**
 *
 * @author deric
 * @param <T>
 */
public interface INamedAttribute<T> extends IRepositoryObject {

    T cloneValue(final T value);

    /**
     * @return The name of this attribute.
     */
    String getName();

    /**
     * @return The value of this attribute.
     */
    T getValue();

    /**
     * @param value The new value of this attribute.
     */
    void setValue(final T value);

}
