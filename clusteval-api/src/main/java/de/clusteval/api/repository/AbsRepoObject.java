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
package de.clusteval.api.repository;

import de.clusteval.api.program.RegisterException;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class AbsRepoObject implements RepositoryListener, IRepositoryObject {

    /**
     * The repository this object is registered in.
     */
    protected IRepository repository;

    /**
     * The changedate of this object can be used for identification and equality
     * checks of objects.
     */
    protected long changeDate;

    /**
     * A set with all the listeners, that want to be informed about changes of
     * this object.
     */
    protected Set<RepositoryListener> listener;

    /**
     * The absolute path of this object is used for identification and equality
     * checks of objects.
     */
    public File absPath;

    protected Logger log;

    public AbsRepoObject() {

    }

    /**
     * The copy constructor for repository objects.
     *
     * <p>
     * Cloned repository objects are never registered at the repository.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public AbsRepoObject(final IRepositoryObject other) throws RegisterException {
        init(other.getRepository(), other.getChangeDate(), other.getAbsPath());
    }


    @Override
    public final void init(IRepository repository, long changeDate, File absPath) {
        this.repository = repository;
        this.changeDate = changeDate;
        this.listener = Collections.synchronizedSet(new HashSet<RepositoryListener>());
        this.absPath = absPath;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Any subclass needs to implement this method. It will be responsible to
     * register a new object of the subclass at the repository.
     *
     * @return true, if successful
     * @throws RegisterException An exception is thrown if something goes wrong
     *                           during the registering process, that might be interesting to handle
     *                           individually.
     */
    @Override
    public boolean register() throws RegisterException {
        return repository.register(this);
    }

    /**
     * Any subclass needs to implement this method. It will be responsible to
     * unregister an object of the subclass from the repository.
     *
     * @return true, if successful
     */
    @Override
    public boolean unregister() {
        return repository.unregister(this);
    }

    @Override
    public IRepository getRepository() {
        return this.repository;
    }

    @Override
    public String getAbsolutePath() {
        return absPath.getAbsolutePath();
    }

    @Override
    public void setAbsolutePath(File absFilePath) {
        this.absPath = absFilePath;
    }

    @Override
    public File getAbsPath() {
        return absPath;
    }

    @Override
    public boolean addListener(RepositoryListener listener) {
        if (listener.equals(this)) {
            return false;
        }
        return this.listener.add(listener);
    }

    @Override
    public boolean removeListener(RepositoryListener listener) {
        return this.listener.remove(listener);
    }

    @Override
    public long getChangeDate() {
        return this.changeDate;
    }

    /**
     * A convenience method for {@link #copyTo(File, boolean)}, with overwriting
     * enabled.
     *
     * @param copyDestination The absolute path to the destination file.
     * @return True, if the copy operation was successful.
     */
    @Override
    public boolean copyTo(File copyDestination) {
        return copyTo(copyDestination, true);
    }

    /**
     * A convenience method for {@link #copyTo(File, boolean, boolean)}, with
     * waiting for the operation to finish.
     *
     * @param copyDestination The absolute path to the destination file.
     * @param overwrite       Whether the possibly already existing target file should
     *                        be overwritten.
     * @return True, if the copy operation was successful.
     */
    @Override
    public boolean copyTo(File copyDestination, boolean overwrite) {
        // by default we wait until the copied file is equal
        return copyTo(copyDestination, overwrite, true);
    }

    @Override
    public boolean copyTo(File copyDestination, boolean overwrite, boolean wait) {
        try {
            if (!copyDestination.exists() || overwrite) {
                org.apache.commons.io.FileUtils.copyFile(this.absPath,
                        copyDestination);
            }
            while (wait
                    && !org.apache.commons.io.FileUtils.contentEquals(
                            this.absPath, copyDestination)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            log.error("io error", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean copyToFolder(File copyFolderDestination) {
        return copyToFolder(copyFolderDestination, true);
    }

    @Override
    public boolean copyToFolder(File copyFolderDestination, boolean overwrite) {
        File targetFile = new File(
                FileUtils.buildPath(copyFolderDestination.getAbsolutePath(),
                        this.absPath.getName()));
        return copyTo(targetFile, overwrite);
    }

    @Override
    public boolean moveTo(File moveDestination) {
        return moveTo(moveDestination, true);
    }

    @Override
    public boolean moveTo(File moveDest, boolean overwrite) {
        File targetFile = new File(
                FileUtils.buildPath(moveDest.getAbsolutePath(), absPath.getName()));
        return moveTo(targetFile, overwrite);
    }

    @Override
    public boolean moveToFolder(File moveFolderDestination) {
        return moveToFolder(moveFolderDestination, true);
    }

    /**
     * This method moves the file corresponding to this repository object into
     * the destination folder.
     *
     * @param moveFolderDestination The folder in which this file should be
     *                              copied
     * @param overwrite             Whether a possibly already existing target file within
     *                              the destination folder should be overwritten.
     * @return True, if the copy operation was successful.
     */
    public boolean moveToFolder(final File moveFolderDestination,
            final boolean overwrite) {
        File targetFile = new File(
                FileUtils.buildPath(moveFolderDestination.getAbsolutePath(), absPath.getName()));
        return moveTo(targetFile, overwrite);
    }

    /**
     * A helper method of {@link #notify(RepositoryEvent)}, in case this object
     * needs to inform its listeners about the passed event.
     *
     * @param event The event related to this object which is going to be
     *              propagated to the listeners of this object.
     * @throws RegisterException
     */
    private void notifyListener(final RepositoryEvent event) throws RegisterException {
        List<RepositoryListener> toNotify = new ArrayList<>(this.listener);
        for (RepositoryListener listener : toNotify) {
            listener.notify(event);
        }
    }
    @Override
    public void notify(RepositoryEvent e) throws RegisterException {
        if (e instanceof RepositoryReplaceEvent) {
            RepositoryReplaceEvent event = (RepositoryReplaceEvent) e;
            if (event.getOld().equals(this)) // this object is going to be removed and replaced from the
            // repository
            {
                this.notifyListener(event);
            } else {
                // do something in subclasses
            }
        } else if (e instanceof RepositoryRemoveEvent) {
            RepositoryRemoveEvent event = (RepositoryRemoveEvent) e;
            if (event.getRemovedObject().equals(this)) {
                // this object is going to be removed and replaced from the
                // repository
                this.notifyListener(event);
            } else {
                // do something in subclasses
            }
        } else if (e instanceof RepositoryMoveEvent) {
            RepositoryMoveEvent event = (RepositoryMoveEvent) e;
            if (event.getObject().equals(this)) {
                // this object has been moved
                this.notifyListener(event);
            } else {
                // do something in subclasses
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbsRepoObject) {
            AbsRepoObject other = (AbsRepoObject) obj;

            return this.repository.equals(other.repository)
                    && ((this.absPath == null && other.absPath == null) || this.absPath
                    .equals(other.absPath));
        }
        return false;
    }

    /**
     * @return The logger of this object.
     */
    public Logger getLog() {
        return log;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (int) (this.changeDate ^ (this.changeDate >>> 32));
        hash = 83 * hash + Objects.hashCode(this.absPath);
        return hash;
    }

}
