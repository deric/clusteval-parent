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
package de.clusteval.api.opt;

import de.clusteval.api.exceptions.InternalAttributeException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepositoryObject;

/**
 *
 * @author deric
 */
public interface IParameterOptimizationMethod extends IRepositoryObject {

    /**
     * This method purely determines and calculates the next parameter set that
     * follows from the current state of the method.
     *
     * <p>
     * If the force parameter set is given != null, this parameter set is forced
     * to be evaluated. This scenario is used during resumption of an older run,
     * where the parameter sets are already fixed and we want to feed them to
     * this method together with their results exactly as they were performed
     * last time.
     *
     * <p>
     * This is a helper-method for {@link #next()} and
     * {@link #next(ParameterSet)}.
     *
     * <p>
     * It might happen that this method puts the calling thread to sleep, in
     * case the next parameter set requires older parameter sets to finish
     * first.
     *
     * <p>
     * It might happen that this method puts the calling thread to sleep, in
     * case the next parameter set requires older parameter sets to finish
     * first.
     *
     * @param forcedParameterSet If this parameter is set != null, this
     *                           parameter set is forced to be evaluated in the next iteration.
     * @return The next parameter set.
     * @throws InternalAttributeException
     * @throws RegisterException
     * @throws NoParameterSetFoundException This exception is thrown, if no
     * parameter set was found that was not already evaluated before.
     * @throws InterruptedException
     * @throws ParameterSetAlreadyEvaluatedException
     */
    ParameterSet getNextParameterSet(ParameterSet forcedParameterSet)
            throws InternalAttributeException, RegisterException,
                   NoParameterSetFoundException, InterruptedException,
                   ParameterSetAlreadyEvaluatedException, UnknownProviderException;

    /**
     * @return True, if there are more iterations together with parameter sets
     *         that have to be evaluated.
     */
    boolean hasNext();

}
