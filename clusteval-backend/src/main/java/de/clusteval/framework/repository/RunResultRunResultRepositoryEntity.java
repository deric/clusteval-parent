/**
 *
 */
package de.clusteval.framework.repository;

import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.StaticRepositoryEntity;
import de.clusteval.api.run.IRunResult;
import de.clusteval.utils.FileUtils;

/**
 * @author Christian Wiwie
 *
 */
public class RunResultRunResultRepositoryEntity extends RunResultRepositoryEntity {

    /**
     * @param repository
     * @param parent
     * @param basePath
     */
    public RunResultRunResultRepositoryEntity(IRepository repository,
            StaticRepositoryEntity<IRunResult> parent, String basePath) {
        super(repository, parent, basePath);
    }

    @Override
    public String getBasePath() {
        return this.parent.getBasePath();
    }

    @Override
    public String getClusterResultsBasePath() {
        return FileUtils.buildPath(this.getBasePath(), "clusters");
    }

    @Override
    public String getClusterResultsQualityBasePath() {
        return FileUtils.buildPath(this.getBasePath(), "clusters");
    }

    @Override
    public String getAnalysisResultsBasePath() {
        return FileUtils.buildPath(this.getBasePath(), "analyses");
    }

    @Override
    public String getResultLogBasePath() {
        return FileUtils.buildPath(this.getBasePath(), "logs");
    }
}
