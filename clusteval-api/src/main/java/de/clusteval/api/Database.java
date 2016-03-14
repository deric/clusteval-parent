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
package de.clusteval.api;

import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.repository.IRepositoryObject;

/**
 *
 * @author deric
 */
public interface Database {

    boolean register(final IRepositoryObject object, final boolean updateOnly);

    boolean unregister(final IRepositoryObject object);

    boolean register(final Class<? extends IRepositoryObject> c);

    boolean unregister(final Class<? extends IRepositoryObject> c);

    /**
     * Initializes the database: 1) establishes a connection 2) tells the
     * database to delete this repository and all corresponding entries
     * (cascading) and recreate a new and empty repository
     *
     * @throws DatabaseConnectException
     */
    void initDB() throws DatabaseConnectException;

    public void commitDB();

}
