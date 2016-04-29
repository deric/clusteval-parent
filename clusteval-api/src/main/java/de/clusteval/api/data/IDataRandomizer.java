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

import de.clusteval.api.Pair;
import de.clusteval.api.repository.IRepositoryObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author deric
 */
public interface IDataRandomizer extends IRepositoryObject {

    /**
     * @return A wrapper object keeping the options of your dataset generator.
     *         The options returned by this method are going to be used and interpreted
     *         in your subclass implementation in {@link #generateDataSet()} .
     */
    Options getOptions();

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
     * for {@link #randomize(String[])}. It provides the core of a dataset
     * generator by generating the dataset file and creating a {@link DataSet}
     * wrapper object for it.
     *
     * @throws InterruptedException
     *
     * @throws DataSetGenerationException If something goes wrong during the
     * generation process, this exception is thrown.
     */
    Pair<IDataSet, IGoldStandard> randomizeDataConfig() throws InterruptedException;

}
