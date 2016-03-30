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
package de.clusteval.api.program;

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.InternalAttributeException;
import de.clusteval.api.repository.IRepositoryObject;

/**
 *
 * @author deric
 * @param <T>
 */
public interface IProgramParameter<T> extends IRepositoryObject {

    /**
     * @return The minimal value of this parameter.
     */
    String getMinValue();

    IProgramParameter<T> clone();

    /**
     * This method evaluates the string representation of the minimal value
     * {@link #minValue} to a value corresponding to the dynamic type of this
     * object, e.g. in case this parameter is a double parameter, it is
     * evaluated to a double value.
     *
     * <p>
     * The method requires a data and program configuration, since the string
     * representation can contain a placeholder of a internal variable which is
     * replaced by looking it up during runtime. $(meanSimilarity) for example
     * is evaluated by looking into the data and calculating the mean similarity
     * of the input.
     *
     * @param dataConfig
     *                      The data configuration which might be needed to evaluate
     *                      certain placeholder variables.
     * @param programConfig
     *                      The program configuration which might be needed to evaluate
     *                      certain placeholder variables.
     * @return The evaluated value of the {@link #minValue} variable.
     * @throws InternalAttributeException
     */
    T evaluateMinValue(final IDataConfig dataConfig,
            final IProgramConfig programConfig)
            throws InternalAttributeException;

    /**
     * This method checks, whether the {@link #minValue} variable has been set
     * to a correct not-null value.
     *
     * @return True, if the variable has been set correctly, false otherwise.
     */
    boolean isMinValueSet();

    String getName();

    /**
     * This method evaluates the string representation of the default value
     * {@link #def} to a value corresponding to the dynamic type of this object,
     * e.g. in case this parameter is a double parameter, it is evaluated to a
     * double value.
     *
     * <p>
     * The method requires a data and program configuration, since the string
     * representation can contain a placeholder of a internal variable which is
     * replaced by looking it up during runtime. $(meanSimilarity) for example
     * is evaluated by looking into the data and calculating the mean similarity
     * of the input.
     *
     * @param dataConfig
     *                      The data configuration which might be needed to evaluate
     *                      certain placeholder variables.
     * @param programConfig
     *                      The program configuration which might be needed to evaluate
     *                      certain placeholder variables.
     * @return The evaluated value of the {@link #def} variable.
     * @throws InternalAttributeException
     */
    T evaluateDefaultValue(final IDataConfig dataConfig, final IProgramConfig programConfig)
            throws InternalAttributeException;

    /**
     * This method evaluates the string representation of the maximal value
     * {@link #maxValue} to a value corresponding to the dynamic type of this
     * object, e.g. in case this parameter is a double parameter, it is
     * evaluated to a double value.
     *
     * <p>
     * The method requires a data and program configuration, since the string
     * representation can contain a placeholder of a internal variable which is
     * replaced by looking it up during runtime. $(meanSimilarity) for example
     * is evaluated by looking into the data and calculating the mean similarity
     * of the input.
     *
     * @param dataConfig
     *                      The data configuration which might be needed to evaluate
     *                      certain placeholder variables.
     * @param programConfig
     *                      The program configuration which might be needed to evaluate
     *                      certain placeholder variables.
     * @return The evaluated value of the {@link #maxValue} variable.
     * @throws InternalAttributeException
     */
    T evaluateMaxValue(final IDataConfig dataConfig, final IProgramConfig programConfig)
            throws InternalAttributeException;

    /**
     * This method checks, whether the {@link #maxValue} variable has been set
     * to a correct not-null value.
     *
     * @return True, if the variable has been set correctly, false otherwise.
     */
    boolean isMaxValueSet();

    /**
     * This method evaluates the string representation of the options
     * {@link #options} to a value corresponding to the dynamic type of this
     * object, e.g. in case this parameter is a double parameter, it is
     * evaluated to a double value.
     *
     * <p>
     * The method requires a data and program configuration, since the string
     * representation can contain a placeholder of a internal variable which is
     * replaced by looking it up during runtime. $(meanSimilarity) for example
     * is evaluated by looking into the data and calculating the mean similarity
     * of the input.
     *
     * @param dataConfig
     *                      The data configuration which might be needed to evaluate
     *                      certain placeholder variables.
     * @param programConfig
     *                      The program configuration which might be needed to evaluate
     *                      certain placeholder variables.
     * @return The evaluated value of the {@link #minValue} variable.
     * @throws InternalAttributeException
     */
    T[] evaluateOptions(IDataConfig dataConfig, IProgramConfig programConfig) throws InternalAttributeException;

    /**
     * This method checks, whether the {@link #options} variable has been set to
     * a correct not-null value.
     *
     * @return True, if the variable has been set correctly, false otherwise.
     */
    boolean isOptionsSet();

    /**
     * @return The maximal value of this parameter.
     */
    String getMaxValue();

    /**
     * @return The possible values of this parameter.
     */
    String[] getOptions();

}
