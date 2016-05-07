/** *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ***************************************************************************** */
/**
 *
 */
package de.clusteval.api.program;

import de.clusteval.api.program.ProgramParameter;
import de.clusteval.api.program.ProgramConfig;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.exceptions.InternalAttributeException;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import javax.script.ScriptException;

/**
 * A type of program parameter that only holds string values.
 *
 * @author Christian Wiwie
 *
 */
public class StringProgramParameter extends ProgramParameter<String> {

    /**
     * Parse a string program parameter from strings.
     *
     * @param programConfig
     *                      The program configuration defining this parameter.
     * @param name
     *                      The name of the parameter.
     * @param desc
     *                      The description of the parameter.
     * @param options
     *                      The possible values of the parameter.
     * @param def
     *                      The default value of the parameter.
     * @return The parsed string program parameter.
     * @throws RegisterException
     */
    public static StringProgramParameter parseFromStrings(
            final ProgramConfig programConfig, final String name,
            final String desc, final String[] options, final String def)
            throws RegisterException {
        final IRepository repo = programConfig.getRepository();

        StringProgramParameter result = new StringProgramParameter(repo, false,
                programConfig, name, desc, options, def);

        StringProgramParameter registeredResult = programConfig.getRepository().getRegisteredObject(result);

        // if our new object has not been found in the repository, we register
        // it
        if (registeredResult == null) {
            result.register();
            return result;
        }
        return registeredResult;
    }

    /**
     * The constructor of string program parameters.
     *
     * @param repository
     *                      The repository to register the new parameter.
     * @param register
     *                      Whether to register the new parameter.
     *
     * @param programConfig
     *                      The program configuration defining this parameter.
     * @param name
     *                      The name of the parameter.
     * @param desc
     *                      The description of the parameter.
     * @param options
     *                      The possible values of this parameter.
     * @param def
     *                      The default value of the parameter.
     * @throws RegisterException
     */
    public StringProgramParameter(final IRepository repository,
            final boolean register, final ProgramConfig programConfig,
            final String name, final String desc, String[] options, String def)
            throws RegisterException {
        super(repository, register, programConfig, name, desc, "", "", options,
                def);
    }

    /**
     * The copy constructor of string program parameters.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public StringProgramParameter(final StringProgramParameter other)
            throws RegisterException {
        super(other);

    }

    /*
     * (non-Javadoc)
     *
     * @see program.ProgramParameter#clone(program.ProgramParameter)
     */
    @Override
    public StringProgramParameter clone() {
        try {
            return new StringProgramParameter(this);
        } catch (RegisterException e) {
            // should not occur
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see program.ProgramParameter#isMinValueSet()
     */
    @Override
    public boolean isMinValueSet() {
        return !this.minValue.equals("");
    }

    /*
     * (non-Javadoc)
     *
     * @see program.ProgramParameter#isMaxValueSet()
     */
    @Override
    public boolean isMaxValueSet() {
        return !this.maxValue.equals("");
    }

    /*
     * (non-Javadoc)
     *
     * @see program.ProgramParameter#evaluateMinValue()
     */
    @Override
    public String evaluateMinValue(final IDataConfig dataConfig,
            final IProgramConfig programConfig)
            throws InternalAttributeException {

        /*
         * Parse minValue
         */
        String newMinValue = this.repository.evaluateInternalAttributes(
                minValue, dataConfig, programConfig);

        try {
            newMinValue = this.repository.evaluateJavaScript(newMinValue);
        } catch (ScriptException e) {
            throw new InternalAttributeException("The expression '" + minValue
                    + "' for parameter attribute " + this.programConfig + "/"
                    + this.name + "/minValue is invalid");
        }

        return newMinValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see program.ProgramParameter#evaluateMaxValue()
     */
    @Override
    public String evaluateMaxValue(final IDataConfig dataConfig, final IProgramConfig programConfig)
            throws InternalAttributeException {

        /*
         * Parse maxValue
         */
        String newMaxValue = this.repository.evaluateInternalAttributes(
                maxValue, dataConfig, programConfig);

        try {
            newMaxValue = this.repository.evaluateJavaScript(newMaxValue);
        } catch (ScriptException e) {
            throw new InternalAttributeException("The expression '" + maxValue
                    + "' for parameter attribute " + this.programConfig + "/"
                    + this.name + "/maxValue is invalid");
        }

        return newMaxValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see program.ProgramParameter#evaluateDefaultValue()
     */
    @Override
    public String evaluateDefaultValue(final IDataConfig dataConfig, final IProgramConfig programConfig)
            throws InternalAttributeException {

        /*
         * Parse default
         */
        String newDefaultValue = this.repository.evaluateInternalAttributes(
                def, dataConfig, programConfig);

        return newDefaultValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.program.ProgramParameter#evaluateOptions(de.clusteval.data
     * .DataConfig, de.clusteval.program.ProgramConfig)
     */
    @Override
    public String[] evaluateOptions(IDataConfig dataConfig, IProgramConfig programConfig) throws InternalAttributeException {
        /*
         * Parse options
         */
        String[] newOptions = new String[this.options.length];
        for (int i = 0; i < this.options.length; i++) {
            newOptions[i] = this.repository.evaluateInternalAttributes(
                    options[i], dataConfig, programConfig);
            try {
                newOptions[i] = this.repository
                        .evaluateJavaScript(newOptions[i]);
            } catch (ScriptException e) {
                throw new InternalAttributeException("The expression '"
                        + newOptions[i] + "' for parameter attribute "
                        + this.programConfig + "/" + this.name
                        + "/options is invalid");
            }
        }

        return newOptions;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.program.ProgramParameter#isOptionsSet()
     */
    @Override
    public boolean isOptionsSet() {
        return !(this.options.length == 0);
    }
}