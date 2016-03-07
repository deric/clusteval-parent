/**
 * 
 */
package de.clusteval.framework.repository;

import de.clusteval.program.ProgramParameter;

/**
 * @author Christian Wiwie
 * 
 */
public class ProgramParameterRepositoryEntity<T extends ProgramParameter<?>>
		extends
			StaticRepositoryEntity<T> {

	/**
	 * @param repository
	 * @param parent
	 * @param basePath
	 */
	public ProgramParameterRepositoryEntity(Repository repository,
			StaticRepositoryEntity<T> parent, String basePath) {
		super(repository, parent, basePath);
		this.printOnRegister = false;
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
	protected <S extends T> boolean registerWhenExisting(S old, S object)
			throws RegisterException {
		return false;
	}
}
