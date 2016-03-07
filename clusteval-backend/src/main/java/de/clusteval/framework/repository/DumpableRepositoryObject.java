/**
 * 
 */
package de.clusteval.framework.repository;

import java.io.File;

/**
 * @author Christian Wiwie
 *
 */
public abstract class DumpableRepositoryObject extends RepositoryObject {

	/**
	 * Instantiates a new dumpable repository object.
	 * 
	 * @param repository
	 *            The repository this object is registered in.
	 * @param register
	 *            Whether this object should be registered implicitely in the
	 *            repository or if the user wants to register manually later.
	 * @param changeDate
	 *            The changedate of this object can be used for identification
	 *            and equality checks of objects.
	 * @param absPath
	 *            The absolute path of this object is used for identification
	 *            and equality checks of objects.
	 * @throws RegisterException
	 */
	public DumpableRepositoryObject(final Repository repository,
			final boolean register, final long changeDate, final File absPath)
			throws RegisterException {
		super(repository, register, changeDate, absPath);
	}

	/**
	 * Instantiates a new dumpable repository object.
	 * 
	 * <p>
	 * This is a convenience constructor which implicitely registers the new
	 * object in its repository.
	 * 
	 * @param repository
	 *            The repository this object is registered in.
	 * @param changeDate
	 *            The changedate of this object can be used for identification
	 *            and equality checks of objects.
	 * @param absPath
	 *            The absolute path of this object is used for identification
	 *            and equality checks of objects.
	 * @throws RegisterException
	 */
	public DumpableRepositoryObject(final Repository repository,
			final long changeDate, final File absPath) throws RegisterException {
		this(repository, true, changeDate, absPath);
	}

	/**
	 * The copy constructor for dumpable repository objects.
	 * 
	 * <p>
	 * Cloned repository objects are never registered at the repository.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public DumpableRepositoryObject(final RepositoryObject other)
			throws RegisterException {
		this(other.repository, false, other.changeDate, new File(
				other.absPath.getAbsolutePath()));
	}

	/**
	 * @throws RepositoryObjectDumpException
	 */
	public final void dumpToFile() throws RepositoryObjectDumpException {
		this.dumpToFileHelper();
		this.changeDate = this.absPath.lastModified();
	}

	protected abstract void dumpToFileHelper()
			throws RepositoryObjectDumpException;

}
