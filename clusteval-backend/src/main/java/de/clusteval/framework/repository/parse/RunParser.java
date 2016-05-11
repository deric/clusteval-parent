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
import de.clusteval.api.data.DataSetConfigNotFoundException;
import de.clusteval.api.data.DataSetConfigurationException;
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
import de.clusteval.api.run.Run;
import de.clusteval.api.run.RunException;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.configuration.ConfigurationException;

/**
 *
 * @author deric
 */
class RunParser<T extends Run> extends RepositoryObjectParser<T> {

    // the members of the Run class
    protected IContext context;

    protected String mode;

    @Override
    public void parseFromFile(final File absPath)
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

        // by default we are in a clustering context
        if (getProps().containsKey("context")) {
            context = ContextFactory.parseFromString(repo, getProps().getString("context"));
        } else {
            context = ContextFactory.parseFromString(repo, "ClusteringContext");
        }

        mode = getProps().getString("mode", "clustering");
    }
}
