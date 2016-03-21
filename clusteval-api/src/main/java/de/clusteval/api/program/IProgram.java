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

import de.clusteval.api.IContext;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RLibraryNotLoadedException;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.r.ROperationNotSupported;
import de.clusteval.api.repository.IRepositoryObject;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author deric
 */
public interface IProgram extends IRepositoryObject {

    /**
     * Gets the absolute path of the executable.
     *
     * @return the executable
     */
    String getExecutable();

    /**
     * This method executes this program on the data defined in the data
     * configuration.
     *
     * <p>
     * The complete invocation line is also passed. It is taken from the program
     * configuration used by the run. All parameter placeholders contained in
     * this invocation line are already replaced by their actual values.
     *
     * <p>
     * Additionally all parameter values are passed in the two map parameters.
     *
     * @param dataConfig
     *                        This configuration encapsulates the data, this program should
     *                        be applied to.
     * @param programConfig
     *                        This parameter contains some additional configuration for this
     *                        program.
     * @param invocationLine
     *                        This is the complete invocation line, were all parameter
     *                        placeholders are already replaced by their actual values.
     * @param effectiveParams
     *                        This map contains only the program parameters defined in the
     *                        program configuration together with their actual values.
     * @param internalParams
     *                        This map contains parameters, that are not program specific,
     *                        but related and necessary for the execution of the program,
     *                        e.g. the path to the output or log files created by the
     *                        program.
     * @return A Process object which can be used to get the status of or to
     *         control the execution of this program.
     * @throws IOException
     * @throws RNotAvailableException
     * @throws RLibraryNotLoadedException
     * @throws de.clusteval.api.r.RException
     * @throws de.clusteval.api.r.ROperationNotSupported
     * @throws InterruptedException
     */
    Process exec(final IDataConfig dataConfig,
            final IProgramConfig programConfig, final String[] invocationLine,
            final Map<String, String> effectiveParams,
            final Map<String, String> internalParams) throws IOException,
                                                             RNotAvailableException, RLibraryNotLoadedException,
                                                             RException, ROperationNotSupported, InterruptedException;

    /**
     * @return The context of this program. A run can only perform this program,
     *         if it has the same context.
     * @throws UnknownContextException
     */
    IContext getContext() throws UnknownContextException;

    IProgram clone();
}
