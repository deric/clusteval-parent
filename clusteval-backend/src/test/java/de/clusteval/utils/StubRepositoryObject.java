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

import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryEvent;
import de.clusteval.api.repository.RepositoryObject;
import de.clusteval.api.repository.RepositoryRemoveEvent;
import de.clusteval.api.repository.RepositoryReplaceEvent;
import java.io.File;

/**
 * @author Christian Wiwie
 *
 */
public class StubRepositoryObject extends RepositoryObject {

    public boolean notified;

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public StubRepositoryObject(IRepository repository, boolean register,
            long changeDate, File absPath) throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * The copy constructor of stub repository objects.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public StubRepositoryObject(final StubRepositoryObject other) throws RegisterException {
        super(other);
    }

    @Override
    public StubRepositoryObject clone() {
        try {
            return new StubRepositoryObject(this);
        } catch (RegisterException e) {
            // should not occur
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void notify(RepositoryEvent e) throws RegisterException {
        if (e instanceof RepositoryReplaceEvent) {
            RepositoryReplaceEvent event = (RepositoryReplaceEvent) e;
            super.notify(event);
            notified = true;
        } else if (e instanceof RepositoryRemoveEvent) {
            RepositoryRemoveEvent event = (RepositoryRemoveEvent) e;
            super.notify(event);
            notified = true;
        }
    }
}
