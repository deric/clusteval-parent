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

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.IProgram;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.run.IRunResultFormat;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author deric
 */
public interface IRProgram extends IProgram {

    /**
     *
     * @return unique program identifier
     */
    String getName();

    /**
     * @return The format of the invocation line of this RProgram.
     */
    String getInvocationFormat();

    /**
     * @return A set containing dataset formats, which this r program can take
     *         as input.
     * @throws UnknownProviderException
     */
    Set<IDataSetFormat> getCompatibleDataSetFormats() throws UnknownProviderException;

    /**
     * @return The runresult formats, the results of this r program will be
     *         generated in.
     * @throws UnknownProviderException
     */
    IRunResultFormat getRunResultFormat() throws UnknownProviderException;

    IRProgram clone();

    void setEngine(IRengine engine);

    IRengine getEngine();

    /**
     * This method is required to initialize the attributes
     * {@link #dataSetContent}, {@link #ids} and all other attributes of the
     * data, which are needed in
     * {@link #doExec(DataConfig, ProgramConfig, String[], Map, Map)}.
     *
     * @param dataConfig
     * @return
     */
    Object extractDataSetContent(IDataConfig dataConfig);

    void beforeExec(IDataConfig dataConfig, IProgramConfig programConfig, String[] invocationLine,
            Map<String, String> effectiveParams, Map<String, String> internalParams) throws RException,
                                                                                            RLibraryNotLoadedException, RNotAvailableException,
                                                                                            InterruptedException;

    void doExec(IDataConfig dataConfig, IProgramConfig programConfig,
            final String[] invocationLine, Map<String, String> effectiveParams,
            Map<String, String> internalParams) throws InterruptedException, RException;

    void afterExec(IDataConfig dataConfig,
            IProgramConfig programConfig, String[] invocationLine,
            Map<String, String> effectiveParams,
            Map<String, String> internalParams) throws RException, IOException, InterruptedException, ROperationNotSupported;

    /**
     * This method extracts the results after executing
     * {@link #doExec(DataConfig, ProgramConfig, String[], Map, Map)}. By
     * default the result is stored in the R variable "result".
     *
     * @return A two dimensional float array, containing fuzzy coefficients for
     *         each object and cluster. Rows correspond to objects and columns
     *         correspond to clusters. The order of objects is the same as in
     *         {@link #ids}.
     * @throws de.clusteval.api.r.RException
     * @throws de.clusteval.api.r.ROperationNotSupported
     * @throws InterruptedException
     */
    float[][] getFuzzyCoeffMatrixFromExecResult()
            throws RException, ROperationNotSupported, InterruptedException;
}
