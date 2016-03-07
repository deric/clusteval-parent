/**
 * 
 */
package de.clusteval.framework.repository;

import de.clusteval.run.result.RunResult;
import de.wiwie.wiutils.file.FileUtils;

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
	public RunResultRunResultRepositoryEntity(Repository repository,
			StaticRepositoryEntity<RunResult> parent, String basePath) {
		super(repository, parent, basePath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.framework.repository.RepositoryEntity#getBasePath()
	 */
	@Override
	public String getBasePath() {
		return this.parent.getBasePath();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.RunResultEntity#getClusterResultsBasePath
	 * ()
	 */
	@Override
	public String getClusterResultsBasePath() {
		return FileUtils.buildPath(this.getBasePath(), "clusters");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.framework.repository.RunResultEntity#
	 * getClusterResultsQualityBasePath()
	 */
	@Override
	public String getClusterResultsQualityBasePath() {
		return FileUtils.buildPath(this.getBasePath(), "clusters");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.RunResultEntity#getAnalysisResultsBasePath
	 * ()
	 */
	@Override
	public String getAnalysisResultsBasePath() {
		return FileUtils.buildPath(this.getBasePath(), "analyses");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.RunResultEntity#getResultLogBasePath()
	 */
	@Override
	public String getResultLogBasePath() {
		return FileUtils.buildPath(this.getBasePath(), "logs");
	}
}