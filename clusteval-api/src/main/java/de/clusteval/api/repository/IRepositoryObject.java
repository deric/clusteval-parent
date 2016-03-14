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
package de.clusteval.api.repository;

import java.io.File;

/**
 *
 * @author deric
 */
public interface IRepositoryObject {

    /**
     * Any subclass needs to implement this method. It will be responsible to
     * register a new object of the subclass at the repository.
     *
     * @return true, if successful
     * @throws RegisterException An exception is thrown if something goes wrong
     *                           during the registering process, that might be interesting to handle
     *                           individually.
     */
    boolean register() throws RegisterException;

    /**
     * Any subclass needs to implement this method. It will be responsible to
     * unregister an object of the subclass from the repository.
     *
     * @return true, if successful
     */
    boolean unregister();

    IRepository getRepository();

    /**
     * @return The absolute path of this repository object.
     */
    String getAbsolutePath();

    /**
     * @param absFilePath The new absolute file path.
     * @see #absPath
     */
    void setAbsolutePath(final File absFilePath);

    boolean addListener(final RepositoryListener listener);

    /**
     * Remove a listener from this objects listener.
     *
     * @param listener The listener to remove.
     * @return True, if the listener was removed successfully
     */
    boolean removeListener(final RepositoryListener listener);

    long getChangeDate();

    void notify(RepositoryEvent e) throws RegisterException;

}
