/**
 * *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 *****************************************************************************
 */
package de.clusteval.api.run.result;

import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryObject;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IRunResult;
import java.io.File;

/**
 * A wrapper class for runresults produced by runs of the framework.
 *
 * @author Christian Wiwie
 *
 */
public abstract class RunResult extends RepositoryObject implements IRunResult {

    /**
     * The run ident string.
     */
    protected String runIdentString;

    protected IRun run;

    protected boolean changedSinceLastRegister;

    /**
     * @param repository
     * @param changeDate
     * @param absPath
     * @param runIdentString
     * @param run
     * @throws RegisterException
     */
    public RunResult(IRepository repository, long changeDate, File absPath, final String runIdentString, final IRun run)
            throws RegisterException {
        super(repository, false, changeDate, absPath);
        this.runIdentString = runIdentString;
        this.run = run;
    }

    /**
     * The copy constructor of run results.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public RunResult(final RunResult other) throws RegisterException {
        super(other);
        this.runIdentString = other.runIdentString;
        this.run = other.run.clone();
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.RepositoryObject#clone()
     */
    @Override
    public abstract RunResult clone();

    /**
     * @return The unique identifier of this runresult, equal to the name of the
     *         runresult folder.
     */
    public String getIdentifier() {
        return this.runIdentString;
    }

    /**
     * @return The run this runresult belongs to.
     */
    public IRun getRun() {
        return this.run;
    }

    public boolean hasChangedSinceLastRegister() {
        return this.changedSinceLastRegister;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.framework.repository.RepositoryObject#register()
     */
    @Override
    public boolean register() throws RegisterException {
        boolean result = super.register();
        if (result) {
            this.changedSinceLastRegister = false;
        }
        return result;
    }
}
