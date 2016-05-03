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
package de.clusteval.api.run;

import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryObject;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Run results (e.g. clusterings) can have different formats. For all kinds of
 * operations the framework needs to know which format a runresult has and how
 * it can be converted to an understandable (standard) format.
 *
 * <p>
 * Every runresult format comes together with a parser class (see
 * {@link RunResultFormatParser}).
 *
 *
 * @author Christian Wiwie
 */
public abstract class RunResultFormat extends RepositoryObject implements IRunResultFormat {

    /**
     * Instantiates a new runresult format.
     *
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public RunResultFormat(final IRepository repo, final boolean register,
            final long changeDate, final File absPath) throws RegisterException {
        super(repo, register, changeDate, absPath);
    }

    /**
     * The copy constructor of runresult formats.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public RunResultFormat(final IRunResultFormat other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RunResultFormat)) {
            return false;
        }

        RunResultFormat other = (RunResultFormat) obj;

        return this.getClass().equals(other.getClass());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public final RunResultFormat clone() {
        try {
            return this.getClass().getConstructor(this.getClass())
                    .newInstance(this);
        } catch (IllegalArgumentException | SecurityException |
                InstantiationException | IllegalAccessException |
                InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class "
                + this.getClass().getSimpleName() + " failed");
        return null;
    }
}
