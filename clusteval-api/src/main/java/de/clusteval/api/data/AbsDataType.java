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

import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.repository.RepositoryObject;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author deric
 */
public abstract class AbsDataType extends RepositoryObject implements IRepositoryObject, IDataSetType {

    public AbsDataType() {

    }

    /**
     * The copy constructor for dataset types.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public AbsDataType(final AbsDataType other) throws RegisterException {
        super(other);
    }

    @Override
    public final IDataSetType clone() {
        try {
            return (IDataSetType) getClass().getConstructor(getClass()).newInstance(this);
        } catch (IllegalArgumentException | SecurityException | InstantiationException |
                IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class " + getClass().getSimpleName() + " failed");
        return null;
    }

}
