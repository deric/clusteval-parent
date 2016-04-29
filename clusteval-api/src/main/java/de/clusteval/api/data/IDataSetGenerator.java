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
package de.clusteval.api.data;

import de.clusteval.api.exceptions.DataSetGenerationException;
import de.clusteval.api.exceptions.GoldStandardGenerationException;
import de.clusteval.api.repository.IRepositoryObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author deric
 */
public interface IDataSetGenerator extends IRepositoryObject {

    /**
     * @return A wrapper object keeping the options of your dataset generator.
     *         The options returned by this method are going to be used and interpreted
     *         in your subclass implementation in {@link #generateDataSet()} .
     */
    Options getOptions();

    /**
     * If your dataset generator also creates a goldstandard for the generated
     * dataset, this method has to return true.
     *
     * <p>
     * If a goldstandard is to be created, it is going to be stored under the
     * same {@link #folderName} and with the same {@link #fileName} as the
     * dataset, but within the goldstandard directory of the repository.
     *
     * @return A boolean indicating, whether your dataset generator also
     *         generates a corresponding goldstandard for the created dataset.
     */
    boolean generatesGoldStandard();

    /**
     * This method is responsible for interpreting the arguments passed to this
     * generator call and to initialize possibly needed member variables.
     *
     * <p>
     * If you want to react to certain options in your implementation of
     * {@link #generateDataSet()}, initialize member variables in this method.
     *
     * @param cmd A wrapper object for the arguments passed to this generator.
     * @throws ParseException
     */
    void handleOptions(final CommandLine cmd) throws ParseException;

    /**
     * This method needs to be implemented in subclasses and is a helper method
     * for {@link #generate(String[])}. It provides the core of a dataset
     * generator by generating the dataset and storing it in the coords
     * attribute.
     *
     * @throws DataSetGenerationException If something goes wrong during the
     * generation process, this exception is thrown.
     * @throws InterruptedException
     */
    void generateDataSet() throws DataSetGenerationException, InterruptedException;

    /**
     * This method needs to be implemented in subclasses and is a helper method
     * for {@link #generate(String[])}. It provides the functionality to
     * generate the goldstandard file and creating a {@link GoldStandard}
     * wrapper object for it.
     *
     * @return A {@link GoldStandard} wrapper object for the generated
     *         goldstandard file.
     * @throws GoldStandardGenerationException If something goes wrong during
     * the generation process, this exception is thrown.
     */
    IGoldStandard generateGoldStandard() throws GoldStandardGenerationException;

}
