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

import de.clusteval.api.program.IProgram;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.RepositoryEvent;
import de.clusteval.api.repository.RepositoryRemoveEvent;
import de.clusteval.api.repository.RepositoryReplaceEvent;
import de.clusteval.data.DataConfig;
import de.clusteval.framework.repository.RepositoryObject;
import de.clusteval.framework.repository.RunResultRepository;
import java.io.File;
import java.util.Map;

/**
 * A wrapper class for programs used by this framework.
 *
 * <p>
 * A program object encapsulates a executable, that can be executed using the
 * {@link #exec(DataConfig, ProgramConfig, String[], Map, Map)} method. This
 * method takes the data and its configuration, the program and its
 * configuration, the complete invocation line and all parameters used for this
 * invocation.
 *
 * @author Christian Wiwie
 *
 */
public abstract class Program extends RepositoryObject implements IProgram {

    /**
     * Instantiates a new program.
     *
     * @param repository
     *                   the repository this program should be registered at.
     * @param register
     *                   Whether this program should be registered in the repository.
     * @param changeDate
     *                   The change date of this program is used for equality checks.
     * @param absPath
     *                   The absolute path of this program.
     * @throws RegisterException
     */
    public Program(final IRepository repository, final boolean register,
            final long changeDate, final File absPath) throws RegisterException {
        // we register ourselves after initializing
        super(repository instanceof RunResultRepository ? repository
                .getParent() : repository, false, changeDate, absPath);

        if (register) {
            this.register();
        }
    }

    /**
     * The copy constructor for programs.
     *
     * @param program
     *                The program to clone.
     * @throws RegisterException
     */
    protected Program(final IProgram program) throws RegisterException {
        super(program);
    }


    /**
     * Gets the absolute path of the executable.
     *
     * @return the executable
     */
    public String getExecutable() {
        return absPath.getAbsolutePath();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getMajorName();
    }

    /**
     * This method returns the major name of this program. The major name of the
     * program is defined as the foldername its executable lies in.
     *
     * @return The major name of this program.
     */
    @Override
    public String getMajorName() {
        return this.absPath.getParentFile().getName();
    }

    /**
     * This method returns the minor name of this program. The minor name
     * corresponds to the name of the executable of this program.
     *
     * @return The minor name.
     */
    public String getMinorName() {
        return this.absPath.getName();
    }

    /**
     * This method returns the full name of this program. The full name
     * corresponds to the concatenated major and minor name separated by a
     * slash: MAJOR/MINOR
     *
     * @return The full name.
     */
    public String getFullName() {
        return getMajorName() + "/" + getMinorName();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.RepositoryObject#notify(utils.RepositoryEvent)
     */
    @Override
    public void notify(RepositoryEvent e) throws RegisterException {
        if (e instanceof RepositoryReplaceEvent) {
            RepositoryReplaceEvent event = (RepositoryReplaceEvent) e;
            if (event.getOld().equals(this)) {
                super.notify(event);
            }
        } else if (e instanceof RepositoryRemoveEvent) {
            RepositoryRemoveEvent event = (RepositoryRemoveEvent) e;
            if (event.getRemovedObject().equals(this)) {
                super.notify(event);
            }
        }
    }

}
