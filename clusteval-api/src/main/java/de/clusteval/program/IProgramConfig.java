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
package de.clusteval.program;

import de.clusteval.api.repository.IRepositoryObject;
import java.util.List;

/**
 * A program configuration encapsulates a program together with options and
 * settings.
 *
 * @author deric
 */
public interface IProgramConfig extends IRepositoryObject {

    /**
     * @return True, if the encapsulated program requires normalized input data,
     * false otherwise.
     * @see #expectsNormalizedDataSet
     */
    boolean expectsNormalizedDataSet();

    int getMaxExecutionTimeMinutes();

    void setMaxExecutionTimeMinutes(final int maxExecutionTimeMinutes);

    /**
     *
     * @return The list of optimizable parameters of the encapsulated program.
     * @see #optimizableParameters
     */
    List<IProgramParameter<?>> getOptimizableParams();

}
