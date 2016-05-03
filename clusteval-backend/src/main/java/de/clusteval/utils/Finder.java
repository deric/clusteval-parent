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
package de.clusteval.utils;

import de.clusteval.api.program.IFinder;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.RepositoryEvent;
import de.clusteval.api.repository.RepositoryObject;
import de.clusteval.api.repository.RepositoryRemoveEvent;
import de.clusteval.api.repository.RepositoryReplaceEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Wiwie
 * @param <T>
 *
 */
public abstract class Finder<T extends IRepositoryObject> extends RepositoryObject implements IFinder<T> {

    protected Class<T> classToFind;

    protected Map<String, List<Throwable>> knownExceptions;

    protected boolean foundInLastRun;

    /**
     * @param repository
     * @param classToFind
     * @throws RegisterException
     */
    public Finder(IRepository repository, Class<T> classToFind)
            throws RegisterException {
        super(repository, System.currentTimeMillis(), new File(
                repository.getBasePath(classToFind)));

        this.classToFind = classToFind;

        this.knownExceptions = this.repository.getKnownFinderExceptions();
    }

    /**
     * The copy constructor of finder.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public Finder(final Finder<T> other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Finder<T> clone() {
        try {
            return (Finder<T>) this.getClass().getConstructor(Finder.class)
                    .newInstance(this);
        } catch (IllegalArgumentException | SecurityException | InstantiationException |
                 IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class "
                + this.getClass().getSimpleName() + " failed");
        return null;
    }

    /**
     * Find files and try to parse them. If the parsing process is successful it
     * will implicitly register the new object at the repository. if the object
     * was known before it will updated, if and only if the changeDate of the
     * found object is newer.
     *
     * @throws RegisterException
     * @throws InterruptedException
     */
    @SuppressWarnings("unused")
    public void findAndRegisterObjects() throws RegisterException,
                                                InterruptedException {
        Iterator<File> fileIt = getIterator();

        while (fileIt.hasNext()) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            File programDir = fileIt.next();

            if (checkFile(programDir)) {
                foundInLastRun = true;
                try {
                    doOnFileFound(programDir);
                } catch (InterruptedException e) {
                    throw e;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected File getBaseDir() {
        return new File(this.repository.getBasePath(this.getClassToFind()));
    }

    protected final Class<T> getClassToFind() {
        return this.classToFind;
    }

    @Override
    public void notify(RepositoryEvent e) {
        if (e instanceof RepositoryReplaceEvent) {
            RepositoryReplaceEvent event = (RepositoryReplaceEvent) e;
            if (this.repository.getKnownFinderExceptions().containsKey(
                    event.getOld().getAbsolutePath())) {
                this.repository.getKnownFinderExceptions().remove(
                        event.getOld().getAbsolutePath());
            }
            event.getOld().removeListener(this);
            event.getReplacement().addListener(this);
        } else if (e instanceof RepositoryRemoveEvent) {
            RepositoryRemoveEvent event = (RepositoryRemoveEvent) e;
            // 31.01.2013: removed clearing of exceptions of this object. this
            // has to happen after a repeated and successful parsing of the same
            // file (e.g. FileFinder.doOnFileFound())
            event.getRemovedObject().removeListener(this);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj.getClass().equals(this.getClass()))) {
            return false;
        }

        Finder rf = (Finder) obj;
        return this.repository.equals(rf.getRepository());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.repository.hashCode();
    }

    public boolean foundInLastRun() {
        return this.foundInLastRun;
    }
}
