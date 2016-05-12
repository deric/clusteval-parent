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
package de.clusteval.data.dataset;

import de.clusteval.api.data.DataSetConfig;
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
import de.clusteval.api.run.RunException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.utils.AbstractClustEvalTest;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.configuration.ConfigurationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DataSetRegistrationTest extends AbstractClustEvalTest {

    @Test
    public void testRegister() throws DataSetConfigurationException,
                                      NoRepositoryFoundException, DataSetNotFoundException,
                                      DataSetConfigNotFoundException,
                                      RegisterException, NoDataSetException,
                                      NumberFormatException,
                                      GoldStandardNotFoundException, GoldStandardConfigurationException,
                                      GoldStandardConfigNotFoundException,
                                      ConfigurationException,
                                      FileNotFoundException, UnknownParameterType,
                                      RunException, IncompatibleContextException,
                                      UnknownRunResultFormatException,
                                      InvalidOptimizationParameterException,
                                      UnknownProgramParameterException, UnknownProgramTypeException,
                                      UnknownRProgramException,
                                      IncompatibleParameterOptimizationMethodException,
                                      UnknownParameterOptimizationMethodException,
                                      NoOptimizableProgramParameterException,
                                      UnknownRunResultPostprocessorException,
                                      UnknownProviderException {
        this.repositoryObject = Parser
                .parseFromFile(
                        DataSetConfig.class,
                        new File(
                                "testCaseRepository/data/datasets/configs/astral_1.dsconfig")
                        .getAbsoluteFile());
        assertEquals(this.repositoryObject, this.getRepository()
                .getRegisteredObject((DataSetConfig) this.repositoryObject));

        // adding a DataSetConfig equal to another one already registered
        // does
        // not register the second object.
        this.repositoryObject = new DataSetConfig(
                (DataSetConfig) this.repositoryObject);
        assertEquals(
                this.getRepository().getRegisteredObject(
                        (DataSetConfig) this.repositoryObject),
                this.repositoryObject);
        assertFalse(this.getRepository().getRegisteredObject(
                (DataSetConfig) this.repositoryObject) == this.repositoryObject);
    }

}
