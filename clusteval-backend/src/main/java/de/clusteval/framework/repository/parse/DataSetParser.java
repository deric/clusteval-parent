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

import de.clusteval.api.data.DataSetTypeFactory;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.data.IDataSetType;
import de.clusteval.api.data.WEBSITE_VISIBILITY;
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
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.stats.UnknownDataStatisticException;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.AbsoluteDataSet;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetAttributeParser;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.RelativeDataSet;
import de.clusteval.data.dataset.format.AbsoluteDataSetFormat;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.dataset.format.RelativeDataSetFormat;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.run.RunException;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
class DataSetParser extends RepositoryObjectParser<DataSet> {

    public DataSetParser() {
        this.loadConfigFile = false;
    }

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
                   UnknownRunResultPostprocessorException, UnknownDataRandomizerException, UnknownProviderException {
        super.parseFromFile(absPath);

        try {
            Map<String, String> attributeValues = extractDataSetAttributes(absPath);

            if (attributeValues.isEmpty()) {
                throw new NoDataSetException("The file " + absPath + " does not contain a dataset header.");
            }

            String alias;
            if (attributeValues.containsKey("alias")) {
                alias = attributeValues.get("alias");
            } else {
                throw new DataSetConfigurationException("No alias specified for data set " + absPath.getAbsolutePath());
            }
            // check whether the alias is already taken by another dataset ->
            // throw exception
            Collection<? extends IDataSet> dataSets;
            if (repo instanceof RunResultRepository) {
                dataSets = repo.getParent().getCollectionStaticEntities(IDataSet.class);
            } else {
                dataSets = repo.getCollectionStaticEntities(IDataSet.class);
            }

            for (IDataSet ds : dataSets) {
                if (!(repo instanceof RunResultRepository) && !(ds.getAbsolutePath().equals(absPath.getAbsolutePath()))
                        && ds.getAlias().equals(alias)) {
                    throw new DataSetConfigurationException("The alias (" + alias + ") of the data set "
                            + absPath.getAbsolutePath() + " is already taken by the data set " + ds.getAbsolutePath());
                }
            }

            IDataSetFormat dsFormat;
            if (attributeValues.containsKey("dataSetFormat")) {
                if (attributeValues.containsKey("dataSetFormatVersion")) {
                    dsFormat = DataSetFormat.parseFromString(repo, attributeValues.get("dataSetFormat"),
                            Integer.parseInt(attributeValues.get("dataSetFormatVersion")));
                } else {
                    dsFormat = DataSetFormat.parseFromString(repo, attributeValues.get("datasetFormat"));
                }
            } else {
                throw new DataSetConfigurationException("No format specified for dataset " + absPath.getAbsolutePath());
            }

            IDataSetType dsType;
            if (attributeValues.containsKey("dataSetType")) {
                dsType = DataSetTypeFactory.parseFromString(attributeValues.get("dataSetType"));
            } else {
                throw new DataSetConfigurationException("No type specified for dataset " + absPath.getAbsolutePath());
            }

            WEBSITE_VISIBILITY websiteVisibility = WEBSITE_VISIBILITY.HIDE;
            String vis = attributeValues.containsKey("websiteVisibility")
                         ? attributeValues.get("websiteVisibility")
                         : "hide";
            switch (vis) {
                case "hide":
                    websiteVisibility = WEBSITE_VISIBILITY.HIDE;
                    break;
                case "show_always":
                    websiteVisibility = WEBSITE_VISIBILITY.SHOW_ALWAYS;
                    break;
                case "show_optional":
                    websiteVisibility = WEBSITE_VISIBILITY.SHOW_OPTIONAL;
                    break;
                default:
                    break;
            }

            final long changeDate = absPath.lastModified();

            LoggerFactory.getLogger(DataSet.class).debug("Parsing dataset \"" + absPath + "\"");

            /*
             * Either the format is absolute or relative
             */
            if (RelativeDataSetFormat.class.isAssignableFrom(dsFormat.getClass())) {
                result = new RelativeDataSet(repo, true, changeDate, absPath, alias, (RelativeDataSetFormat) dsFormat,
                        dsType, websiteVisibility);
            } else {
                result = new AbsoluteDataSet(repo, true, changeDate, absPath, alias, (AbsoluteDataSetFormat) dsFormat,
                        dsType, websiteVisibility);
            }
            result = repo.getRegisteredObject(result);
            LoggerFactory.getLogger(DataSet.class).debug("Dataset parsed");
        } catch (IOException e) {
            throw new UnknownDataSetFormatException(e);
        }
    }

    /**
     * This method parses the header of a dataset file. A header is required for
     * a dataset file to be recognized by the framework as a valid dataset file.
     * If the file does not contain any header lines, it is ignored by the
     * framework. A header line is of the form '// attribute = value'. The
     * header should contain several lines:
     *
     * <p>
     * The type of the dataset, e.g. '// dataSetType =
     * GeneExpressionDataSetType'
     * <p>
     * The format of the dataset, e.g. '// dataSetFormat = RowSimDataSetFormat'
     * <p>
     * The version of the dataset format, e.g. '// dataSetFormatVersion = 1'
     *
     * @param absPath
     * @return
     * @throws IOException
     */
    protected static Map<String, String> extractDataSetAttributes(final File absPath) throws IOException {
        DataSetAttributeParser attributeParser = new DataSetAttributeParser(absPath.getAbsolutePath());
        attributeParser.process();
        Map<String, String> attributeValues = attributeParser.getAttributeValues();
        return attributeValues;
    }
}
