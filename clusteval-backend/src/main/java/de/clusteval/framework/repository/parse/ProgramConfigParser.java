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

import de.clusteval.api.ContextFactory;
import de.clusteval.api.IContext;
import de.clusteval.api.data.DataSetFormatFactory;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.api.run.RunResultFormatFactory;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.program.Program;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.program.StandaloneProgram;
import de.clusteval.program.r.RProgram;
import de.clusteval.program.r.RProgramConfig;
import de.clusteval.run.RunException;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;

/**
 *
 * @author deric
 */
class ProgramConfigParser extends RepositoryObjectParser<ProgramConfig> {

    /**
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.framework.repository.RepositoryObjectParser#parseFromFile
     * (java.io.File)
     */
    @Override
    public void parseFromFile(File absPath)
            throws NoRepositoryFoundException, ConfigurationException, RunException,
                   UnknownDataSetFormatException, FileNotFoundException, RegisterException, UnknownParameterType,
                   IncompatibleContextException, UnknownRunResultFormatException, InvalidOptimizationParameterException,
                   UnknownProgramParameterException, UnknownProgramTypeException, UnknownRProgramException,
                   GoldStandardNotFoundException, GoldStandardConfigurationException, DataSetConfigurationException,
                   DataSetNotFoundException, DataSetConfigNotFoundException, GoldStandardConfigNotFoundException,
                   NoDataSetException, DataConfigurationException, DataConfigNotFoundException, NumberFormatException,
                   UnknownDataPreprocessorException,
                   IncompatibleDataSetConfigPreprocessorException, IncompatibleParameterOptimizationMethodException,
                   UnknownParameterOptimizationMethodException, NoOptimizableProgramParameterException,
                   UnknownRunResultPostprocessorException, UnknownProviderException {
        super.parseFromFile(absPath);

        log.debug("Parsing program config \"" + absPath + "\"");
        getProps().setThrowExceptionOnMissing(true);

        IContext context;
        // by default we are in a clustering context
        if (getProps().containsKey("context")) {
            context = ContextFactory.parseFromString(repo, getProps().getString("context"));
        } else {
            context = ContextFactory.parseFromString(repo, "ClusteringContext");
        }

        /**
         * Added 07.08.2012 Type of programconfig is either standalone or R
         */
        String type;
        if (getProps().containsKey("type")) {
            type = getProps().getString("type");
        } else {
            // Default
            type = "standalone";
        }

        Program programP = null;
        // initialize compatible dataset formats
        String[] compatibleDataSetFormatsStr;

        IRunResultFormat runresultFormat;
        Set<IDataSetFormat> compatibleDataSetFormats;
        boolean expectsNormalizedDataSet = false;
        if (type.equals("standalone")) {
            String program = FileUtils.buildPath(repo.getBasePath(Program.class), getProps().getString("program"));

            File programFile = new File(program);
            if (!(programFile).exists()) {
                throw new FileNotFoundException(
                        "The given program executable does not exist: " + programFile.getAbsolutePath());
            }

            changeDate = programFile.lastModified();

            String outputFormat = getProps().getString("outputFormat");

            compatibleDataSetFormatsStr = getProps().getStringArray("compatibleDataSetFormats");

            compatibleDataSetFormats = DataSetFormatFactory.parseFromString(repo, compatibleDataSetFormatsStr);

            if (getProps().containsKey("expectsNormalizedDataSet")) {
                expectsNormalizedDataSet = getProps().getBoolean("expectsNormalizedDataSet");
            } else {
                expectsNormalizedDataSet = false;
            }

            for (IDataSetFormat format : compatibleDataSetFormats) {
                format.setNormalized(expectsNormalizedDataSet);
            }

            runresultFormat = RunResultFormatFactory.parseFromString(repo, outputFormat);

            String alias = getProps().getString("alias");

            Map<String, String> envVars = new HashMap<>();
            Iterator<String> vars = getProps().getSection("envVars").getKeys();
            while (vars.hasNext()) {
                String var = vars.next();
                envVars.put(var, getProps().getSection("envVars").getString(var));
            }

            programP = new StandaloneProgram(repo, context, true, changeDate, programFile, alias, envVars);
        } else if (repo.isClassRegistered(RProgram.class, "de.clusteval.program.r." + type)) {
            programP = RProgram.parseFromString(repo, type);

            RProgram rProgram = (RProgram) programP;
            compatibleDataSetFormats = rProgram.getCompatibleDataSetFormats();
            runresultFormat = rProgram.getRunResultFormat();
        } else {
            throw new UnknownProgramTypeException("The type " + type + " is unknown.");
        }

        List<String> paras = Arrays.asList(getProps().getStringArray("parameters"));

        List<IProgramParameter<?>> params = new ArrayList<>();
        List<IProgramParameter<?>> optimizableParameters = new ArrayList<>();

        changeDate = absPath.lastModified();

        // check whether there are parameter-sections for parameters, that are
        // not listed in the parameters-list
        Set<String> sections = getProps().getSections();
        sections.removeAll(paras);
        sections.remove("envVars");
        sections.remove(null);
        sections.remove("invocationFormat");

        if (sections.size() > 0) {
            throw new UnknownProgramParameterException("There are parameter-sections " + sections + " in ProgramConfig "
                    + absPath.getName() + " for undefined parameters. Please add them to the parameter-list.");
        }

        int maxExecutionTimeMinutes = -1;
        if (getProps().containsKey("maxExecutionTimeMinutes")) {
            maxExecutionTimeMinutes = getProps().getInt("maxExecutionTimeMinutes");
        }

        if (type.equals("standalone")) {
            String invocationFormat = getProps().getSection("invocationFormat").getString("invocationFormat");
            String invocationFormatWithoutGoldStandard = null;
            String invocationFormatParameterOptimization = null;
            String invocationFormatParameterOptimizationWithoutGoldStandard = null;

            if (getProps().getSection("invocationFormat").containsKey("invocationFormatWithoutGoldStandard")) {
                invocationFormatWithoutGoldStandard = getProps().getSection("invocationFormat")
                        .getString("invocationFormatWithoutGoldStandard");
            } else {
                invocationFormatWithoutGoldStandard = invocationFormat;
            }

            if (getProps().getSection("invocationFormat").containsKey("invocationFormatParameterOptimization")) {
                invocationFormatParameterOptimization = getProps().getSection("invocationFormat")
                        .getString("invocationFormatParameterOptimization");
            }

            if (getProps().getSection("invocationFormat")
                    .containsKey("invocationFormatParameterOptimizationWithoutGoldStandard")) {
                invocationFormatParameterOptimizationWithoutGoldStandard = getProps().getSection("invocationFormat")
                        .getString("invocationFormatParameterOptimizationWithoutGoldStandard");
            } else {
                invocationFormatParameterOptimizationWithoutGoldStandard = invocationFormatParameterOptimization;
            }

            result = new ProgramConfig(repo, true, changeDate, absPath, programP, runresultFormat,
                    compatibleDataSetFormats, invocationFormat, invocationFormatWithoutGoldStandard,
                    invocationFormatParameterOptimization, invocationFormatParameterOptimizationWithoutGoldStandard,
                    params, optimizableParameters, expectsNormalizedDataSet, maxExecutionTimeMinutes);
        } // RProgram
        else {
            result = new RProgramConfig(repo, true, changeDate, absPath, programP, runresultFormat,
                    compatibleDataSetFormats, params, optimizableParameters, expectsNormalizedDataSet,
                    maxExecutionTimeMinutes);
        }

        // // add parameter objects for input (i), executable (e), output (o)
        // // and goldstandard (gs)
        // params.add(new StringProgramParameter(repo, false, result, "i",
        // "Input", null, null));
        // params.add(new StringProgramParameter(repo, false, result, "e",
        // "Executable", null, null));
        // params.add(new StringProgramParameter(repo, false, result, "o",
        // "Output", null, null));
        // params.add(new StringProgramParameter(repo, false, result, "q",
        // "Quality", null, null));
        // params.add(new StringProgramParameter(repo, false, result, "gs",
        // "Goldstandard", null, null));

        /*
         * Get the optimization parameters (parameters, that can be optimized
         * for this program in parameter_optimization runmode
         */
        String[] optimizableParams = getProps().getStringArray("optimizationParameters");

        // iterate over all parameters
        for (String pa : paras) {

            // skip the empty string
            if (pa.equals("")) {
                continue;
            }

            final Map<String, String> paramValues = new HashMap<>();
            paramValues.put("name", pa);

            ProgramParameter<?> param = ProgramParameter.parseFromConfiguration(result, pa, getProps().getSection(pa));
            params.add(param);

            /*
             * Check if this parameter is declared as an optimizable parameter
             */
            boolean optimizable = false;
            for (String optPa : optimizableParams) {
                if (optPa.equals(pa)) {
                    optimizable = true;
                    break;
                }
            }

            if (optimizable) {
                /*
                 * Check if min and max values are given for this parameter,
                 * which is necessary for optimizing it
                 */
                if (!(param.isMinValueSet() || !param.isMaxValueSet()) && !param.isOptionsSet()) {
                    throw new InvalidOptimizationParameterException("The parameter " + param
                            + " cannot be used as an optimization parameter, because its min and max values are not set.");
                }
                optimizableParameters.add(param);
            }
        }

        result = repo.getRegisteredObject(result);
    }
}
