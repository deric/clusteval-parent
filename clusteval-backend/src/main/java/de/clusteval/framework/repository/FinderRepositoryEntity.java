/**
 * 
 */
package de.clusteval.framework.repository;

import de.clusteval.utils.Finder;

/**
 * @author Christian Wiwie
 * 
 */
public class FinderRepositoryEntity extends StaticRepositoryEntity<Finder> {

	/**
	 * @param repository
	 * @param parent
	 * @param basePath
	 */
	public FinderRepositoryEntity(Repository repository,
			StaticRepositoryEntity<Finder> parent, String basePath) {
		super(repository, parent, basePath);
		this.printOnRegister = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.framework.repository.StaticRepositoryEntity#
	 * unregisterAfterRemove(de.clusteval.framework.repository.RepositoryObject)
	 */
	@Override
	protected <S extends Finder> void unregisterAfterRemove(S object) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.StaticRepositoryEntity#registerWhenExisting
	 * (de.clusteval.framework.repository.RepositoryObject,
	 * de.clusteval.framework.repository.RepositoryObject)
	 */
	@Override
	protected <S extends Finder> boolean registerWhenExisting(S old, S object)
			throws RegisterException {
		return false;
	}
}
