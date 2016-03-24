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
package de.clusteval.program;

import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.IProgramParameter;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.framework.repository.RepositoryObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.SubnodeConfiguration;

/**
 * An object of this class corresponds to a parameter of a program.
 *
 * <p>
 * Therefore a program parameter has a reference to a program configuration in
 * which it was defined.
 *
 * <p>
 * A program parameter has a certain {@link #name} unique for the program
 * configuration, a {@link #description}, a minimal value {@link #minValue}, a
 * maximal value {@link #maxValue} and a default value {@link #def}.
 *
 * @author Christian Wiwie
 * @param <T>
 *
 */
public abstract class ProgramParameter<T> extends RepositoryObject implements IProgramParameter<T> {

    /**
     * A helper method for cloning a list of parameters.
     *
     * @param programParameters
     *                          The optimization parameters to clone.
     * @return The list of cloned optimization parameters.
     */
    public static List<List<IProgramParameter<?>>> cloneParameterListList(
            List<List<IProgramParameter<?>>> programParameters) {
        List<List<IProgramParameter<?>>> result = new ArrayList<>();

        for (List<IProgramParameter<?>> list : programParameters) {
            List<IProgramParameter<?>> copyList = new ArrayList<>();

            for (IProgramParameter<?> param : list) {
                copyList.add(param.clone());
            }

            result.add(copyList);
        }

        return result;
    }

    /**
     * A helper method for cloning a list of parameters.
     *
     * @param programParameters
     *                          The optimization parameters to clone.
     * @return The list of cloned optimization parameters.
     */
    public static List<IProgramParameter<?>> cloneParameterList(
            List<IProgramParameter<?>> programParameters) {
        List<IProgramParameter<?>> result = new ArrayList<>();

        for (IProgramParameter<?> param : programParameters) {
            result.add(param.clone());
        }

        return result;
    }

    /**
     * The program configuration which defined this parameter.
     */
    protected ProgramConfig programConfig;

    /**
     * The name of this parameter has to be unique for the program configuration
     * and program.
     */
    protected String name;

    /**
     * A program parameter can have a description.
     */
    protected String description;

    /**
     * The minimal value this parameter can be set to. The attribute holds a
     * string, because the value of this variable can hold a placeholder which
     * is replaced dynamically by the framework, e.g $(meanSimilarity).
     */
    protected String minValue;

    /**
     * The maximal value this parameter can be set to. The attribute holds a
     * string, because the value of this variable can hold a placeholder which
     * is replaced dynamically by the framework, e.g $(meanSimilarity).
     */
    protected String maxValue;

    /**
     * The possible values this parameter can be set to. The attribute holds a
     * string[], because the values of this variable can hold placeholders which
     * are replaced dynamically by the framework, e.g $(meanSimilarity).
     */
    protected String[] options;

    /**
     * The default value of this parameter. The attribute holds a string,
     * because the value of this variable can hold a placeholder which is
     * replaced dynamically by the framework, e.g $(meanSimilarity).
     */
    protected String def;

    /**
     * Instantiates a new program parameter.
     *
     * <p>
     * The absolute path of a program parameter is defined as the absolute path
     * of its program configuration concatenated with its name, separated by a
     * colon.
     *
     * @param repository
     *                      The repository at which to register this program parameter.
     * @param register
     *                      A boolean indicating, whether to register this object at a
     *                      repository.
     *
     * @param programConfig
     *                      The program configuration which defined this parameter.
     * @param name
     *                      The name of this parameter has to be unique for the program
     *                      configuration and program.
     * @param desc
     *                      The name of this parameter has to be unique for the program
     *                      configuration and program.
     * @param minValue
     *                      The minimal value this parameter can be set to (see
     *                      {@link #minValue}).
     * @param maxValue
     *                      The maximal value this parameter can be set to (see
     *                      {@link #maxValue}).
     * @param options
     *                      The possible values of this parameter.
     * @param def
     *                      The default value of this parameter (see {@link #def}).
     * @throws RegisterException
     */
    public ProgramParameter(final IRepository repository,
            final boolean register, final ProgramConfig programConfig,
            final String name, final String desc, final String minValue,
            final String maxValue, final String[] options, final String def)
            throws RegisterException {
        super(repository, false, System.currentTimeMillis(), new File(
                programConfig.getAbsolutePath() + ":" + name));

        this.programConfig = programConfig;
        this.name = name;
        this.description = desc;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.options = options;
        this.def = def;

        if (register) {
            this.register();
        }
    }

    /**
     * The copy constructor of program parameters.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public ProgramParameter(final ProgramParameter<T> other)
            throws RegisterException {
        super(other);

        // do not clone, because it is upstream
        this.programConfig = other.programConfig;
        this.name = other.name;
        this.description = other.description;
        this.minValue = other.minValue;
        this.maxValue = other.maxValue;
        this.options = other.options;
        this.def = other.def;

    }

    @Override
    public abstract ProgramParameter<T> clone();

    /**
     * Sets the minimal value.
     *
     * @param minValue
     *                 The new minimal value.
     */
    public void setMinValue(final String minValue) {
        this.minValue = minValue;
    }

    /**
     * @return The minimal value of this parameter.
     */
    public String getMinValue() {
        return this.minValue;
    }

    /**
     * Sets the maximal value.
     *
     * @param maxValue
     *                 The new maximal value
     */
    public void setMaxValue(final String maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * @return The maximal value of this parameter.
     */
    public String getMaxValue() {
        return this.maxValue;
    }

    /**
     * Sets the minimal value.
     *
     * @param options
     *                The possible values of this parameter.
     */
    public void setOptions(final String[] options) {
        this.options = options;
    }

    /**
     * @return The possible values of this parameter.
     */
    public String[] getOptions() {
        return this.options;
    }

    /**
     * @return The name of this parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The program configuration which defined this parameter.
     */
    public IProgramConfig getProgramConfig() {
        return this.programConfig;
    }

    // changed 12.07.2012
    @Override
    public String toString() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (!(this.getClass().equals(o.getClass()))) {
            return false;
        }
        ProgramParameter<?> other = (ProgramParameter<?>) o;
        return super.equals(o)
                && this.programConfig.equals(other.programConfig)
                && this.name.equals(other.name)
                && ((this.getDefault() == null && other.getDefault() == null) || (this
                .getDefault() != null && other.getDefault() != null && this
                .getDefault().equals(other.getDefault())))
                && ((this.getMinValue() == null && other.getMinValue() == null) || (this
                .getMinValue() != null && other.getMinValue() != null && this
                .getMinValue().equals(other.getMinValue())))
                && ((this.getMaxValue() == null && other.getMaxValue() == null) || (this
                .getMaxValue() != null && other.getMaxValue() != null && this
                .getMaxValue().equals(other.getMaxValue())));
    }

    /**
     * @return The default value of this parameter.
     */
    public String getDefault() {
        return this.def;
    }

    /**
     * @return The description of this parameter.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the default value of this parameter.
     *
     * @param def
     *            The new default value of this parameter.
     */
    public void setDefault(String def) {
        this.def = def;
    }

    /**
     * Parses a program parameter from a section of a configuration file.
     *
     * <p>
     * This method only delegates depending on the type of the program parameter
     * to the methods
     * {@link DoubleProgramParameter#parseFromStrings(ProgramConfig, String, String, String, String, String)},
     * {@link IntegerProgramParameter#parseFromStrings(ProgramConfig, String, String, String, String, String)}
     * and
     * {@link StringProgramParameter#parseFromStrings(ProgramConfig, String, String, String, String, String)}.
     *
     * @param programConfig
     *                      The program configuration in which the program parameter has
     *                      been defined.
     * @param name
     *                      The name of the program parameter.
     * @param config
     *                      The section of the configuration, which contains the
     *                      information about this parameter.
     * @return The parsed program parameter.
     * @throws RegisterException
     * @throws UnknownParameterType
     */
    public static ProgramParameter<?> parseFromConfiguration(
            final ProgramConfig programConfig, final String name,
            final SubnodeConfiguration config) throws RegisterException,
                                                      UnknownParameterType {
        Map<String, String> paramValues = new HashMap<>();
        paramValues.put("name", name);

        Iterator<String> itSubParams = config.getKeys();
        while (itSubParams.hasNext()) {
            final String subParam = itSubParams.next();
            final String value = config.getString(subParam);

            paramValues.put(subParam, value);
        }
        ParameterType type = ProgramParameter.parseTypeFromString(paramValues
                .get("type"));

        String na = paramValues.get("name");
        String description = paramValues.get("desc");
        String def = paramValues.get("def");

        // 23.05.2014: added support for options for float and integer
        // parameters. if options are given, we set minValue and maxValue to the
        // empty string.
        ProgramParameter<?> param = null;
        String[] options = config.getStringArray("options");
        String minValue = paramValues.get("minValue");
        String maxValue = paramValues.get("maxValue");
        if (config.containsKey("options")) {
            minValue = "";
            maxValue = "";
        }
        switch (type) {
            case FLOAT:
                param = DoubleProgramParameter.parseFromStrings(programConfig, na,
                        description, minValue, maxValue, options, def);
                break;
            case INTEGER:
                param = IntegerProgramParameter.parseFromStrings(programConfig, na,
                        description, minValue, maxValue, options, def);
                break;
            case STRING:
                param = StringProgramParameter.parseFromStrings(programConfig, na,
                        description, options, def);
                break;
            default:
                break;
        }
        return param;
    }

    /**
     * Parses a parameter type from a string. There are different strings
     * representing the types of parameters:
     *
     * <p>
     * <ul>
     * <li><b>0</b>: A String parameter holding only string values.</li>
     * <li><b>1</b>: An integer parameter holding integer values.</li>
     * <li><b>2</b>: A double parameter holding double values.</li>
     * </ul>
     *
     * <p>
     * A helper method for
     * {@link #parseFromConfiguration(ProgramConfig, String, SubnodeConfiguration)}.
     *
     * @param value
     *              A string indicating a number corresponding to a parameter
     *              type.
     * @return the parameter type
     * @throws UnknownParameterType
     */
    private static ParameterType parseTypeFromString(String value)
            throws UnknownParameterType {

        // String
        if (value.equals("0")) {
            return ParameterType.STRING;
        } // Integer
        else if (value.equals("1")) {
            return ParameterType.INTEGER;
        } // Float
        else if (value.equals("2")) {
            return ParameterType.FLOAT;
        }

        throw new UnknownParameterType("The parameter type " + value
                + " is unknown");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (this.programConfig.toString() + this.name).hashCode();
    }

    /**
     * An enumeration for parameter types. Program parameters can be of
     * different type and this enumeration helps distinguishing them.
     *
     * @author Christian Wiwie
     *
     */
    public enum ParameterType {
        /**
         * A string parameter holding string values.
         */
        STRING,
        /**
         * An integer parameter holding integer values.
         */
        INTEGER,
        /**
         * A float parameter holding float values.
         */
        FLOAT
    }
}
