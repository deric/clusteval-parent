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
package de.clusteval.api.r;

import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.run.IRunResultFormat;
import java.util.Set;

/**
 *
 * @author deric
 */
public interface IRProgram extends IRepositoryObject {

    /**
     * @return The format of the invocation line of this RProgram.
     */
    String getInvocationFormat();

    /**
     * @return A set containing dataset formats, which this r program can take
     *         as input.
     * @throws UnknownDataSetFormatException
     */
    Set<IDataSetFormat> getCompatibleDataSetFormats() throws UnknownDataSetFormatException;

    /**
     * @return The runresult formats, the results of this r program will be
     *         generated in.
     * @throws UnknownRunResultFormatException
     */
    IRunResultFormat getRunResultFormat() throws UnknownRunResultFormatException;

    IRProgram clone();
}
