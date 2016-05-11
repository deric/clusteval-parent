/**
 *
 */
package de.clusteval.framework.repository;

import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.repository.StaticRepositoryEntity;

/**
 * @author Christian Wiwie
 *
 */
public class ClusteringRepositoryEntity extends StaticRepositoryEntity<IClustering> {

    /**
     * @param repository
     * @param parent
     * @param basePath
     */
    public ClusteringRepositoryEntity(Repository repository,
            StaticRepositoryEntity<IClustering> parent, String basePath) {
        super(repository, parent, basePath);
        this.printOnRegister = false;
    }
}
