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
package de.clusteval.api.run.result;

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IRun;
import java.io.File;

/**
 * @author Christian Wiwie
 *
 */
public abstract class ExecutionRunResult extends RunResult {

    /** The data config. */
    protected IDataConfig dataConfig;

    /** The program config. */
    protected IProgramConfig programConfig;

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param runIdentString
     * @param run
     * @param dataConfig
     * @param programConfig
     * @throws RegisterException
     */
    public ExecutionRunResult(IRepository repository, long changeDate,
            File absPath, String runIdentString, final IRun run,
            final IDataConfig dataConfig, final IProgramConfig programConfig)
            throws RegisterException {
        super(repository, changeDate, absPath, runIdentString, run);
        this.dataConfig = dataConfig;
        this.programConfig = programConfig;
    }

    /**
     * The copy constructor of run results.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public ExecutionRunResult(final ExecutionRunResult other)
            throws RegisterException {
        super(other);
        this.dataConfig = other.dataConfig.clone();
        this.programConfig = other.programConfig.clone();
    }

    /*
     * (non-Javadoc)
     *
     * @see run.result.RunResult#clone()
     */
    @Override
    public abstract ExecutionRunResult clone();

    /**
     * @return The program configuration wrapping the program that produced this
     *         runresult.
     */
    public IProgramConfig getProgramConfig() {
        return this.programConfig;
    }

    /**
     * @return The data configuration wrapping the dataset on which this
     *         runresult was produced.
     */
    public IDataConfig getDataConfig() {
        return this.dataConfig;
    }

}