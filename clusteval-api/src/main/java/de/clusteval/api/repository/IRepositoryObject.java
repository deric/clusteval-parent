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
import java.io.File;

/**
 *
 * @author deric
 */
public interface IRepositoryObject extends RepositoryListener {

    /**
     * Instantiates a new repository object.
     *
     * @param repository The repository this object is registered in.
     * @param changeDate The changedate of this object can be used for
     *                   identification and equality checks of objects.
     * @param absPath    The absolute path of this object is used for
     *                   identification and equality checks of objects.
     */
    void init(final IRepository repository, final long changeDate, final File absPath);

    /**
     * Any subclass needs to implement this method. It will be responsible to
     * register a new object of the subclass at the repository.
     *
     * `init()` must be called before registering
     *
     * @return true, if successful
     * @throws RegisterException An exception is thrown if something goes wrong
     * during the registering process, that might be interesting to handle
     * individually.
     */
    boolean register() throws RegisterException;

    /**
     * Any subclass needs to implement this method. It will be responsible to
     * unregister an object of the subclass from the repository.
     *
     * @return true, if successful
     */
    boolean unregister();

    IRepository getRepository();

    /**
     * @return The absolute path of this repository object.
     */
    String getAbsolutePath();

    /**
     * @param absFilePath The new absolute file path.
     * @see #absPath
     */
    void setAbsolutePath(final File absFilePath);

    File getAbsPath();

    boolean addListener(final RepositoryListener listener);

    /**
     * Remove a listener from this objects listener.
     *
     * @param listener The listener to remove.
     * @return True, if the listener was removed successfully
     */
    boolean removeListener(final RepositoryListener listener);

    long getChangeDate();

    void notify(RepositoryEvent e) throws RegisterException;

    /**
     * A convenience method for {@link #copyTo(File, boolean)}, with overwriting
     * enabled.
     *
     * @param copyDestination The absolute path to the destination file.
     * @return True, if the copy operation was successful.
     */
    boolean copyTo(final File copyDestination);

    /**
     * A convenience method for {@link #copyTo(File, boolean, boolean)}, with
     * waiting for the operation to finish.
     *
     * @param copyDestination The absolute path to the destination file.
     * @param overwrite       Whether the possibly already existing target file should
     *                        be overwritten.
     * @return True, if the copy operation was successful.
     */
    boolean copyTo(final File copyDestination, final boolean overwrite);

    /**
     * This method copies the file corresponding to this repository object to
     * the destination.
     *
     * <p>
     * <b>Hint:</b> Use the wait parameter with caution: It might increase the
     * ressource load of this method considerably. Also the wait operation might
     * not terminate, if source and target filesystem use different encodings
     * and the equality checks return false.
     *
     * @param copyDestination The absolute path to the destination file.
     * @param overwrite       Whether the possibly already existing target file should
     *                        be overwritten.
     * @param wait            Whether to wait for this operation to finish, were the
     *                        completion of the operation is determined by steadily comparing source
     *                        and target file for equality.
     * @return True, if the copy operation was successful.
     */
    boolean copyTo(final File copyDestination, final boolean overwrite, final boolean wait);

    /**
     * A convenience method for {@link #copyToFolder(File, boolean)}, with
     * overwriting enabled.
     *
     * @param copyFolderDestination The folder in which this file should be
     *                              copied
     * @return True, if the copy operation was successful.
     */
    boolean copyToFolder(final File copyFolderDestination);

    /**
     * This method copies the file corresponding to this repository object into
     * the destination folder.
     *
     * @param copyFolderDestination The folder in which this file should be
     *                              copied
     * @param overwrite             Whether a possibly already existing target file within
     *                              the destination folder should be overwritten.
     * @return True, if the copy operation was successful.
     */
    boolean copyToFolder(final File copyFolderDestination, final boolean overwrite);

    /**
     * A convenience method for {@link #moveTo(File, boolean)}, with overwriting
     * enabled.
     *
     * @param moveDestination The absolute path to the destination file.
     * @return True, if the move operation was successful.
     */
    boolean moveTo(final File moveDestination);

    /**
     * This method moves the file corresponding to this repository object to the
     * destination.
     *
     * <p>
     * <b>Hint:</b> Use the wait parameter with caution: It might increase the
     * ressource load of this method considerably. Also the wait operation might
     * not terminate, if source and target filesystem use different encodings
     * and the equality checks return false.
     *
     * @param moveDest  The absolute path to the destination file.
     * @param overwrite Whether the possibly already existing target file should
     *                  be overwritten.
     * @return True, if the move operation was successful.
     */
    boolean moveTo(final File moveDest, final boolean overwrite);

    /**
     * A convenience method for {@link #moveToFolder(File, boolean)}, with
     * overwriting enabled.
     *
     * @param moveFolderDestination The folder into which this file should be
     *                              move
     * @return True, if the move operation was successful.
     */
    boolean moveToFolder(final File moveFolderDestination);

}
