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
package de.clusteval.framework.repository;

import de.clusteval.api.exceptions.RepositoryObjectDumpException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.program.RegisterException;
import java.io.File;

/**
 * @author Christian Wiwie
 *
 */
public abstract class DumpableRepositoryObject extends RepositoryObject {

    /**
     * Instantiates a new dumpable repository object.
     *
     * @param repository The repository this object is registered in.
     * @param register   Whether this object should be registered implicitely in
     *                   the repository or if the user wants to register manually later.
     * @param changeDate The changedate of this object can be used for
     *                   identification and equality checks of objects.
     * @param absPath    The absolute path of this object is used for
     *                   identification and equality checks of objects.
     * @throws RegisterException
     */
    public DumpableRepositoryObject(final IRepository repository,
            final boolean register, final long changeDate, final File absPath)
            throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * Instantiates a new dumpable repository object.
     *
     * <p>
     * This is a convenience constructor which implicitely registers the new
     * object in its repository.
     *
     * @param repository The repository this object is registered in.
     * @param changeDate The changedate of this object can be used for
     *                   identification and equality checks of objects.
     * @param absPath    The absolute path of this object is used for
     *                   identification and equality checks of objects.
     * @throws RegisterException
     */
    public DumpableRepositoryObject(final IRepository repository,
            final long changeDate, final File absPath) throws RegisterException {
        this(repository, true, changeDate, absPath);
    }

    /**
     * The copy constructor for dumpable repository objects.
     *
     * <p>
     * Cloned repository objects are never registered at the repository.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public DumpableRepositoryObject(final IRepositoryObject other)
            throws RegisterException {
        this(other.getRepository(), false, other.getChangeDate(), new File(
                other.getAbsolutePath()));
    }

    /**
     * @throws RepositoryObjectDumpException
     */
    public final void dumpToFile() throws RepositoryObjectDumpException {
        this.dumpToFileHelper();
        this.changeDate = this.absPath.lastModified();
    }

    protected abstract void dumpToFileHelper()
            throws RepositoryObjectDumpException;

}
