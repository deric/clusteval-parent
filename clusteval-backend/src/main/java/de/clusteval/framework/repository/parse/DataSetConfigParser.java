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
import de.clusteval.api.data.IDataPreprocessor;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.stats.UnknownDataStatisticException;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetConfig;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.data.dataset.format.ConversionStandardToInputConfiguration;
import de.clusteval.data.distance.DistanceMeasure;
import de.clusteval.data.preprocessing.DataPreprocessor;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.run.RunException;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
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

    protected DataSet dataSet;
    protected ConversionInputToStandardConfiguration configInputToStandard;
    protected ConversionStandardToInputConfiguration configStandardToInput;

    /**
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.framework.repository.RepositoryObjectParser#parseFromFile
     * (java.io.File)
     */
    @Override
    public void parseFromFile(File absPath)
            throws NoRepositoryFoundException, ConfigurationException,
                   UnknownContextException, UnknownClusteringQualityMeasureException, RunException,
                   UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   UnknownDistanceMeasureException, UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownDataStatisticException, UnknownRunStatisticException, UnknownRunDataStatisticException,
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
        super.parseFromFile(absPath);

        log.debug("Parsing dataset config \"" + absPath + "\"");

        try {
            getProps().setThrowExceptionOnMissing(true);

            datasetName = getProps().getString("datasetName");
            datasetFile = getProps().getString("datasetFile");

            IDistanceMeasure distanceMeasure;
            if (getProps().containsKey("distanceMeasureAbsoluteToRelative")) {
                distanceMeasure = DistanceMeasure.parseFromString(repo,
                        getProps().getString("distanceMeasureAbsoluteToRelative"));
            } else {
                distanceMeasure = DistanceMeasure.parseFromString(repo, "EuclidianDistanceMeasure");
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
            List<IDataPreprocessor> preprocessorBeforeDistance;
            if (getProps().containsKey("preprocessorBeforeDistance")) {
                preprocessorBeforeDistance = DataPreprocessor.parseFromString(repo,
                        getProps().getStringArray("preprocessorBeforeDistance"));

                for (IDataPreprocessor proc : preprocessorBeforeDistance) {
                    if (!proc.getCompatibleDataSetFormats()
                            .contains(dataSet.getDataSetFormat().getClass().getSimpleName())) {
                        throw new IncompatibleDataSetConfigPreprocessorException("The data preprocessor "
                                + proc.getClass().getSimpleName() + " cannot be applied to a dataset with format "
                                + dataSet.getDataSetFormat().getClass().getSimpleName());
                    }
                }
            } else {
                preprocessorBeforeDistance = new ArrayList<>();
            }

            List<IDataPreprocessor> preprocessorAfterDistance;
            if (getProps().containsKey("preprocessorAfterDistance")) {
                preprocessorAfterDistance = DataPreprocessor.parseFromString(repo,
                        getProps().getStringArray("preprocessorAfterDistance"));
            } else {
                preprocessorAfterDistance = new ArrayList<>();
            }

            configInputToStandard = new ConversionInputToStandardConfiguration(distanceMeasure, similarityPrecision,
                    preprocessorBeforeDistance, preprocessorAfterDistance);
            configStandardToInput = new ConversionStandardToInputConfiguration();

            result = new DataSetConfig(repo, changeDate, absPath, dataSet, configInputToStandard,
                    configStandardToInput);
            result = repo.getRegisteredObject(result);
        } catch (NoSuchElementException e) {
            throw new DataSetConfigurationException(e);
        }
    }

    protected DataSet getDataSet()
            throws DataSetNotFoundException, UnknownDataSetFormatException, DataSetConfigurationException,
                   NoDataSetException, NumberFormatException, RegisterException, NoRepositoryFoundException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException,
                   DataSetConfigNotFoundException, GoldStandardConfigNotFoundException, DataConfigurationException,
                   DataConfigNotFoundException, ConfigurationException, UnknownContextException, FileNotFoundException,
                   UnknownParameterType, UnknownClusteringQualityMeasureException, RunException, IncompatibleContextException,
                   UnknownRunResultFormatException, InvalidOptimizationParameterException, UnknownProgramParameterException,
                   UnknownProgramTypeException, UnknownRProgramException, UnknownDistanceMeasureException,
                   UnknownDataPreprocessorException, IncompatibleDataSetConfigPreprocessorException,
                   IncompatibleParameterOptimizationMethodException, UnknownParameterOptimizationMethodException,
                   NoOptimizableProgramParameterException, UnknownDataStatisticException, UnknownRunStatisticException,
                   UnknownRunDataStatisticException, UnknownRunResultPostprocessorException, UnknownDataRandomizerException {
        if (repo instanceof RunResultRepository) {
            return repo.getStaticObjectWithName(DataSet.class, datasetName + "/" + datasetFile);
        }
        return Parser.parseFromFile(DataSet.class,
                new File(FileUtils.buildPath(repo.getBasePath(DataSet.class), datasetName, datasetFile)));
    }
}
