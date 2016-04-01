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
package de.clusteval.api.run;

import de.clusteval.api.exceptions.RunResultParseException;
import de.clusteval.api.repository.IRepositoryObject;

/**
 *
 * @author deric
 */
public interface IRunResult extends IRepositoryObject {

    /**
     * Name of result format
     *
     * @return unique identifier
     */
    String getName();

    IRunResult clone();

    /**
     * Checks, whether this run result is currently held in memory.
     *
     * @return True, if this run result is currently held in memory. False
     *         otherwise.
     */
    boolean isInMemory();

    /**
     * This method loads the contents of this run result into the memory by
     * parsing the files on the filesystem.
     *
     * <p>
     * The run result might consume a lot of memory afterwards. Only invoke this
     * method, if you really need access to the run results contents and
     * afterwards free the contents by invoking {@link #unloadFromMemory()}.
     *
     * @throws RunResultParseException
     */
    void loadIntoMemory() throws RunResultParseException;

    /**
     * This method unloads the contents of this run result from the memory and
     * releases the reserved memory. This can be helpful especially for large
     * parameter optimization run results.
     */
    void unloadFromMemory();

}
