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
        this.basePath = basePath;
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

    public abstract <S extends T> boolean register(final S object)
            throws RegisterException;

    public abstract <S extends T> boolean unregister(final S object);
}
