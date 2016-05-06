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
package de.clusteval.api.program;

import de.clusteval.api.program.Program;
import de.clusteval.api.IContext;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A type of program that corresponds to executables on the filesystem.
 * <p>
 *
 * {@code
 *
 * Standalone programs can be added to ClustEval by
 *
 * 1. putting the executable file (together with all required shared libraries) into a respective folder in the repository programs directory
 *
 *   * <REPOSITORY ROOT>/programs/<programFolder>/<executable>
 *
 * 2. putting a program configuration file (see 4.9.7) into the repository program configuration directory
 *
 *   * <REPOSITORY ROOT>/programs/configs
 *
 * 3. if the program requires a new input format, follow the instructions under 11.3 for the new input format
 * 4. if the program has an unknown output format, follow the instructions under 11.4 for the new output format
 *
 * }
 *
 * @author Christian Wiwie
 *
 */
public class StandaloneProgram extends Program {

    protected String alias;
    protected IContext context;
    protected Map<String, String> envVars;

    /**
     * @param repository
     *                   the repository this program should be registered at.
     * @param context
     *                   The context of this program
     * @param register
     * @param changeDate
     *                   The change date of this program is used for equality checks.
     * @param absPath
     *                   The absolute path of this program.
     * @param alias
     *                   The alias of this program.
     * @param envVars
     *                   The environmental variables to set when this program is
     *                   executed.
     * @throws RegisterException
     */
    public StandaloneProgram(IRepository repository, final IContext context,
            final boolean register, long changeDate, File absPath,
            final String alias, final Map<String, String> envVars)
            throws RegisterException {
        super(repository, false, changeDate, absPath);
        this.alias = alias;
        this.context = context;
        this.envVars = envVars;

        if (register) {
            this.register();
        }
    }

    /**
     * The copy constructor of standalone programs.
     *
     * @param program
     *                The standalone program to clone.
     * @throws RegisterException
     */
    public StandaloneProgram(final StandaloneProgram program)
            throws RegisterException {
        super(program);
        this.alias = program.alias;
        this.envVars = new HashMap<>(program.envVars);
    }

    /*
     * (non-Javadoc)
     *
     * @see program.Program#clone()
     */
    @Override
    public StandaloneProgram duplicate() {
        try {
            return new StandaloneProgram(this);
        } catch (RegisterException e) {
            // should not occur
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see program.Program#exec(program.ProgramConfig, java.lang.String,
     * java.util.Map)
     */
    @SuppressWarnings("unused")
    @Override
    public Process exec(final IDataConfig dataConfig,
            final IProgramConfig programConfig, final String[] invocationLine,
            Map<String, String> effectiveParams,
            Map<String, String> internalParams) throws IOException {
        String[] envVarsArray = new String[this.envVars.size() + 1];
        // TODO, check whether this works everywhere
        envVarsArray[0] = "TERM=xterm";
        int i = 1;
        for (Map.Entry<String, String> e : this.envVars.entrySet()) {
            envVarsArray[i] = String.format("%s=%s", e.getKey(), e.getValue());
            i++;
        }

        return Runtime.getRuntime().exec(
                invocationLine,
                envVarsArray,
                new File(programConfig.getProgram().getAbsolutePath())
                .getParentFile());
    }

    /*
     * (non-Javadoc)
     *
     * @see program.Program#getName()
     */
    @Override
    public String getName() {
        return this.alias;
    }

    @Override
    public IContext getContext() {
        return this.context;
    }

}
