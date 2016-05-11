/**
 *
 */
package de.clusteval.framework.repository;

import de.clusteval.api.program.ProgramParameter;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.StaticRepositoryEntity;

/**
 * @author Christian Wiwie
 *
 */
public class ProgramParameterRepositoryEntity<T extends ProgramParameter<?>> extends
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

    @Override
    protected <S extends T> boolean registerWhenExisting(S old, S object)
            throws RegisterException {
        return false;
    }
}
