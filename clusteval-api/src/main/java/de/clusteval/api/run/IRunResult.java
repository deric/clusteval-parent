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

import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.InvalidConfigurationFileException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.RunResultParseException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.repository.RepositoryConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;

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

    String getIdentifier();

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

    IRunResult parseFromRunResultFolder(final IRepository parentRepository, final File runResultFolder)
            throws
            RepositoryAlreadyExistsException, InvalidRepositoryException,
            GoldStandardConfigurationException, DataSetConfigurationException, DataSetNotFoundException,
            DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
            IOException, UnknownRunResultFormatException,
            InvalidConfigurationFileException,
            UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
            UnknownProgramParameterException, NoRepositoryFoundException, GoldStandardNotFoundException,
            InvalidOptimizationParameterException, RunException,
            UnknownProgramTypeException, UnknownRProgramException,
            IncompatibleParameterOptimizationMethodException,
            UnknownGoldStandardFormatException, RepositoryConfigurationException, ConfigurationException, RegisterException,
            NumberFormatException, NoDataSetException,
            RunResultParseException,
            IncompatibleContextException, UnknownParameterType, InterruptedException,
            UnknownRunResultPostprocessorException,
            FileNotFoundException, UnknownProviderException;

}
