/**
 * 
 */
package de.clusteval.framework.repository;

import de.clusteval.cluster.Clustering;

/**
 * @author Christian Wiwie
 *
 */
public class ClusteringRepositoryEntity
		extends
			StaticRepositoryEntity<Clustering> {

	/**
	 * @param repository
	 * @param parent
	 * @param basePath
	 */
	public ClusteringRepositoryEntity(Repository repository,
			StaticRepositoryEntity<Clustering> parent, String basePath) {
		super(repository, parent, basePath);
		this.printOnRegister = false;
	}
}
