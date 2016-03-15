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

import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.run.IRunResultFormat;
import java.util.Set;

/**
 *
 * @author deric
 */
public interface IContext extends IRepositoryObject {

    /**
     * Contexts have a unique name.
     *
     * @return The name of this context
     */
    String getName();

    /**
     * @return A set with all simple names of classes this context requires.
     */
    Set<String> getRequiredJavaClassFullNames();

    /**
     *
     * @return The standard input format connected to this context. Every
     *         context has its own standard format, which is used during execution of
     *         runs.
     */
    IDataSetFormat getStandardInputFormat();

    /**
     *
     * @return The standard output format connected to this context. Every
     *         context has its own standard format, which is used during execution of
     *         runs.
     */
    IRunResultFormat getStandardOutputFormat();

    IContext clone();

}
