/*******************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.clusteval.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryObject;
import de.clusteval.framework.threading.ClustevalThread;
import de.clusteval.framework.threading.SupervisorThread;

/**
 * @author Christian Wiwie
 * 
 */
public abstract class FinderThread<T extends RepositoryObject>
		extends
			ClustevalThread {

	protected Repository repository;

	protected Class<T> classToFind;

	protected long sleepTime;

	protected boolean checkOnce;

	protected Logger log;

	protected Finder<T> currentFinder;

	/**
	 * @param supervisorThread
	 * @param repository
	 * @param classToFind
	 * @param checkOnce
	 * 
	 */
	public FinderThread(final SupervisorThread supervisorThread,
			final Repository repository, final Class<T> classToFind,
			boolean checkOnce) {
		this(supervisorThread, repository, classToFind, 60000, checkOnce);
	}

	/**
	 * @param supervisorThread
	 * @param repository
	 * @param classToFind
	 * @param sleepTime
	 * @param checkOnce
	 * 
	 */
	public FinderThread(final SupervisorThread supervisorThread,
			final Repository repository, final Class<T> classToFind,
			final long sleepTime, boolean checkOnce) {
		super(supervisorThread);
		this.classToFind = classToFind;
		this.setName(this.getName().replace("Thread",
				this.getClass().getSimpleName()));
		this.log = LoggerFactory.getLogger(this.getClass());
		this.repository = repository;
		this.sleepTime = sleepTime;
		this.checkOnce = checkOnce;
		this.start();
	}

	protected void beforeFind() {
		this.log.debug("Checking for " + classToFind.getSimpleName() + "...");
	}

	protected abstract Finder<T> getFinder() throws RegisterException;

	protected void afterFind() {
		repository.setInitialized(classToFind);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (!this.isInterrupted()) {
			try {
				try {
					this.beforeFind();
					if (this.isInterrupted())
						return;
					currentFinder = getFinder();
					currentFinder.findAndRegisterObjects();
					if (this.isInterrupted())
						return;
					this.afterFind();
					try {
						// try release the lock, if we still keep it (only
						// before first finishing of finding)
						this.setInitialized();
					} catch (IllegalMonitorStateException e) {

					}
					this.repository.commitDB();
				} catch (RegisterException e) {
					e.printStackTrace();
				}
				if (checkOnce)
					return;
				sleep(sleepTime);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
