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
package de.clusteval.framework.repository.parse;

import de.clusteval.api.data.DataConfig;
import de.clusteval.api.data.DataSetConfig;
import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
import de.clusteval.api.data.GoldStandardConfig;
import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.data.IGoldStandardConfig;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.run.IncompatibleParameterOptimizationMethodException;
import de.clusteval.api.run.RunException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.RunResultDataSetConfig;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import org.apache.commons.configuration.ConfigurationException;

/**
 *
 * @author deric
 */
class DataConfigParser extends RepositoryObjectParser<DataConfig> {

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.framework.repository.RepositoryObjectParser#parseFromFile
     * (java.io.File)
     */
    @Override
    public void parseFromFile(File absPath)
            throws NoRepositoryFoundException, ConfigurationException,
                   RunException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException, UnknownProviderException {
        super.parseFromFile(absPath);

        log.debug("Parsing data config \"" + absPath + "\"");

        try {
            getProps().setThrowExceptionOnMissing(true);

            String datasetConfigName = getProps().getString("datasetConfig");
            DataSetConfig dataSetConfig;
            if (repo instanceof RunResultRepository) {
                dataSetConfig = Parser.parseFromFile(RunResultDataSetConfig.class, new File(
                        FileUtils.buildPath(repo.getBasePath(IDataSetConfig.class), datasetConfigName + ".dsconfig")));
            } else {
                dataSetConfig = Parser.parseFromFile(DataSetConfig.class, new File(
                        FileUtils.buildPath(repo.getBasePath(IDataSetConfig.class), datasetConfigName + ".dsconfig")));
            }

            GoldStandardConfig goldStandardConfig = null;
            try {
                String gsConfigName = getProps().getString("goldstandardConfig");
                goldStandardConfig = Parser.parseFromFile(GoldStandardConfig.class, new File(
                        FileUtils.buildPath(repo.getBasePath(IGoldStandardConfig.class), gsConfigName + ".gsconfig")));
            } catch (NoSuchElementException e) {
                // No goldstandard config given
            }

            result = new DataConfig(repo, changeDate, absPath, dataSetConfig, goldStandardConfig);
            //result = repo.getRegisteredObject(result);
        } catch (NoSuchElementException e) {
            throw new DataConfigurationException(e);
        }
    }
}
