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
package de.clusteval.api.program;

import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.run.IRunResultFormat;
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
     *         false otherwise.
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

    /**
     * This method returns the invocation line format for non
     * parameter-optimization runs.
     *
     * @param withoutGoldStandard This boolean indicates, whether this method
     *                            returns the invocation format for the case with- or without goldstandard.
     *
     * @return The invocation line format
     */
    String getInvocationFormat(boolean withoutGoldStandard);

    /**
     *
     * @return The list of parameters of the encapsulated program.
     * @see #params
     */
    List<IProgramParameter<?>> getParams();

    /**
     *
     * @return The encapsulated program.
     * @see #program
     */
    IProgram getProgram();

    /**
     *
     * @return The compatible dataset input formats of the encapsulated program.
     * @see #compatibleDataSetFormats
     */
    List<IDataSetFormat> getCompatibleDataSetFormats();

    /**
     *
     * @return The output format of the encapsulated program.
     * @see #outputFormat
     */
    IRunResultFormat getOutputFormat();

    /**
     * @return The name of the program configuration is the name of the file
     *         without extension.
     */
    String getName();

    /**
     * TODO: merge this and {@link #getParamWithId(String)}
     *
     * @param name the name
     * @return the parameter for name
     */
    IProgramParameter<?> getParameterForName(final String name);

}
