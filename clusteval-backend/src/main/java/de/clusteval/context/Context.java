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
package de.clusteval.context;

import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.api.IContext;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.framework.repository.RepositoryObject;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Wiwie
 *
 */
public abstract class Context extends RepositoryObject implements IContext {

    /**
     * @param repository
     * @param contextName
     * @return A context object of the class with the given simple name
     * @throws UnknownContextException
     */
    public static Context parseFromString(final IRepository repository,
            final String contextName) throws UnknownContextException {

        Class<? extends Context> c = repository.getRegisteredClass(
                Context.class, "de.clusteval.context." + contextName);
        Constructor<? extends Context> constr;
        try {
            constr = c.getConstructor(IRepository.class, boolean.class,
                    long.class, File.class);
            return constr.newInstance(repository, false,
                    System.currentTimeMillis(), new File(contextName));
        } catch (NoSuchMethodException | SecurityException | InstantiationException |
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Logger log = LoggerFactory.getLogger(Context.class);
            log.error("Context not found", e);
            e.printStackTrace();
        } catch (NullPointerException e) {
        }
        throw new UnknownContextException("\"" + contextName
                + "\" is not a known context.");
    }

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public Context(IRepository repository, boolean register, long changeDate,
            File absPath) throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * @param other
     * @throws RegisterException
     */
    public Context(final Context other) throws RegisterException {
        this(other.repository, false, other.changeDate, other.absPath);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.framework.repository.RepositoryObject#equals(java.lang.Object
     * )
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Context)) {
            return false;
        }

        Context other = (Context) obj;
        return this.getName().equals(other.getName());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.framework.repository.RepositoryObject#hashCode()
     */
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public final Context clone() {
        try {
            return this.getClass().getConstructor(this.getClass())
                    .newInstance(this);
        } catch (IllegalArgumentException | SecurityException | InstantiationException |
                IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class "
                + this.getClass().getSimpleName() + " failed");
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
