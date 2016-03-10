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

import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import java.io.File;

/**
 *
 * @author deric
 */
public interface IRepository {

    /**
     * Register a new repository.
     *
     * @param repository The new repository to register.
     * @return The old repository, if the new repository replaced an old one
     * with equal root path. Null otherwise.
     * @throws RepositoryAlreadyExistsException
     * @throws InvalidRepositoryException
     */
    IRepository register(IRepository repository) throws RepositoryAlreadyExistsException, InvalidRepositoryException;

    /**
     *
     * @return The parent repository of this repository, or null if this
     * repository has no parent.
     */
    IRepository getParent();

    /**
     * @return The absolute path to the root of this repository.
     */
    public String getBasePath();

    String getBasePath(final Class<? extends IRepositoryObject> c);

    /**
     * @return The absolute path to the directory, where for a certain runresult
     * (identified by its unique run identifier) all log files are stored.
     */
    public String getLogBasePath();

    /**
     * This method looks up and returns (if it exists) the repository object
     * that belongs to the passed absolute path.
     *
     * @param absFilePath The absolute path for which we want to find the
     * repository object.
     * @return The repository object which has the given absolute path.
     */
    IRepositoryObject getRegisteredObject(final File absFilePath);

    /**
     * @return The configuration of this repository.
     */
    IRepositoryConfig getRepositoryConfig();

    <T extends IRepositoryObject> T getStaticObjectWithName(final Class<T> c, final String name);

    //<T extends IRepositoryObject> Collection<T> getCollectionStaticEntities(final Class<T> c);
    <T extends IRepositoryObject> T getCollectionStaticEntities(final Class<T> c);

    <T extends IRepositoryObject> boolean isClassRegistered(final Class<T> c);

    <T extends IRepositoryObject> boolean isClassRegistered(final Class<T> base, final String classSimpleName);

    <T extends IRepositoryObject> boolean isClassRegistered(final String classFullName);

    <T extends IRepositoryObject> boolean registerClass(final Class<T> c);

    <T extends IRepositoryObject> boolean unregisterClass(final Class<T> c);

    int getCurrentDataSetFormatVersion(final String formatClass) throws UnknownDataSetFormatException;

    IRengine getRengineForCurrentThread() throws RException;

    public String getAnalysisResultsBasePath();

    public String getClusterResultsBasePath();

    boolean updateStatusOfRun(final IRun run, final String newStatus);
}
