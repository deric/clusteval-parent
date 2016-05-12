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

import de.clusteval.api.IDistanceMeasure;
import de.clusteval.api.Precision;
import de.clusteval.api.data.DataPreprocessor;
import de.clusteval.api.data.DataPreprocessorFactory;
import de.clusteval.api.data.DataSetConfig;
import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
import de.clusteval.api.data.DistanceMeasureFactory;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetConfig;
import de.clusteval.api.data.InputToStd;
import de.clusteval.api.data.StdToInput;
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
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.configuration.ConfigurationException;

/**
 *
 * @author deric
 */
class DataSetConfigParser extends RepositoryObjectParser<DataSetConfig> {

    protected String datasetName;
    protected String datasetFile;

    protected IDataSet dataSet;
    protected InputToStd configInputToStandard;
    protected StdToInput configStandardToInput;

    @Override
    public void parseFromFile(File absPath)
            throws NoRepositoryFoundException, ConfigurationException, RunException,
                   FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, NumberFormatException,
                   IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException, UnknownProviderException {
        super.parseFromFile(absPath);

        log.debug("Parsing dataset config \"" + absPath + "\"");

        try {
            getProps().setThrowExceptionOnMissing(true);

            datasetName = getProps().getString("datasetName");
            datasetFile = getProps().getString("datasetFile");

            IDistanceMeasure distanceMeasure;
            if (getProps().containsKey("distanceMeasureAbsoluteToRelative")) {
                distanceMeasure = DistanceMeasureFactory.parseFromString(repo,
                        getProps().getString("distanceMeasureAbsoluteToRelative"));
            } else {
                distanceMeasure = DistanceMeasureFactory.parseFromString(repo, "EuclidianDistanceMeasure");
            }

            Precision similarityPrecision = Precision.DOUBLE;
            if (getProps().containsKey("similarityPrecision")) {
                String val = getProps().getString("similarityPrecision");
                switch (val) {
                    case "double":
                        similarityPrecision = Precision.DOUBLE;
                        break;
                    case "float":
                        similarityPrecision = Precision.FLOAT;
                        break;
                    case "short":
                        similarityPrecision = Precision.SHORT;
                        break;
                    default:
                        break;
                }
            }

            dataSet = this.getDataSet();

            // added 12.04.2013
            List<DataPreprocessor> preprocessorBeforeDistance;
            if (getProps().containsKey("preprocessorBeforeDistance")) {
                preprocessorBeforeDistance = DataPreprocessorFactory.parseFromString(repo,
                        getProps().getStringArray("preprocessorBeforeDistance"));

                for (DataPreprocessor proc : preprocessorBeforeDistance) {
                    if (!proc.getCompatibleDataSetFormats()
                            .contains(dataSet.getDataSetFormat().getClass().getSimpleName())) {
                        throw new DataSetConfigurationException("The data preprocessor "
                                + proc.getClass().getSimpleName() + " cannot be applied to a dataset with format "
                                + dataSet.getDataSetFormat().getClass().getSimpleName());
                    }
                }
            } else {
                preprocessorBeforeDistance = new ArrayList<>();
            }

            List<DataPreprocessor> preprocessorAfterDistance;
            if (getProps().containsKey("preprocessorAfterDistance")) {
                preprocessorAfterDistance = DataPreprocessorFactory.parseFromString(repo,
                        getProps().getStringArray("preprocessorAfterDistance"));
            } else {
                preprocessorAfterDistance = new ArrayList<>();
            }

            configInputToStandard = new InputToStd(distanceMeasure, similarityPrecision,
                    preprocessorBeforeDistance, preprocessorAfterDistance);
            configStandardToInput = new StdToInput();

            result = new DataSetConfig(repo, changeDate, absPath, dataSet, configInputToStandard,
                    configStandardToInput);
            boolean b = result.register();
            log.debug("trying to register. success? " + b + ", " + result.getAbsolutePath());
            IDataSetConfig conf = repo.getRegisteredObject(result);
            log.debug("result " + conf);
        } catch (NoSuchElementException e) {
            throw new DataSetConfigurationException(e);
        }
    }

    protected IDataSet getDataSet()
            throws DataSetNotFoundException, DataSetConfigurationException,
                   NoDataSetException, NumberFormatException, RegisterException, NoRepositoryFoundException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   ConfigurationException, FileNotFoundException,
                   UnknownParameterType, RunException, IncompatibleContextException,
                   UnknownRunResultFormatException, InvalidOptimizationParameterException, UnknownProgramParameterException,
                   UnknownProgramTypeException, UnknownRProgramException,
                   IncompatibleParameterOptimizationMethodException, UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException, UnknownRunResultPostprocessorException, UnknownProviderException {
        if (repo instanceof RunResultRepository) {
            return repo.findByName(IDataSet.class, datasetName + "/" + datasetFile);
        }
        return Parser.parseFromFile(IDataSet.class,
                new File(FileUtils.buildPath(repo.getBasePath(IDataSet.class), datasetName, datasetFile)));
    }
}
