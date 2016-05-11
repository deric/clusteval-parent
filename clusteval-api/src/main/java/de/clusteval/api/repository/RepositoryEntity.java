/*
 * Copyright (C) 2013-2016 Christian Wiwie
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

import de.clusteval.api.program.RegisterException;
import java.io.File;
import org.slf4j.LoggerFactory;

public abstract class RepositoryEntity<T extends IRepositoryObject> {

    protected IRepository repository;

    /**
     * A boolean attribute indicating whether the datasets have been initialized
     * by the {@link DataSetFinderThread}.
     */
    private boolean initialized;

    /**
     * The absolute path to the directory within this repository, where all
     * datasets are stored.
     */
    protected String basePath;

    protected boolean printOnRegister = true;

    public RepositoryEntity(final IRepository repository, final String basePath) {
        super();
        this.repository = repository;
        this.initialized = false;
        setBasePath(basePath);
    }

    public void setInitialized() {
        this.initialized = true;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public String getBasePath() {
        return this.basePath;
    }

    /**
     * Basepath must be a directory
     *
     * @param path
     */
    public final void setBasePath(String path) {
        if (path == null) {
            LoggerFactory.getLogger(RepositoryEntity.class.getName()).warn("base path is null");
            return;
        }
        File f = new File(path);
        if (!f.isDirectory()) {
            basePath = f.getParent();
        } else {
            basePath = path;
        }
    }

    public abstract <S extends T> boolean register(final S object) throws RegisterException;

    public abstract <S extends T> boolean unregister(final S object);
}
