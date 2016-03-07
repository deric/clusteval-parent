/**
 * 
 */
package de.clusteval.framework.repository;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.clusteval.run.result.ParameterOptimizationResult;
import de.clusteval.run.result.RunResult;
import de.wiwie.wiutils.file.FileUtils;

/**
 * @author Christian Wiwie
 * 
 */
public class RunResultRepositoryEntity extends StaticRepositoryEntity<RunResult> {

	protected Map<String, RunResult> runResultIdentifier;

	/**
	 * @param repository
	 * @param parent
	 * @param basePath
	 */
	public RunResultRepositoryEntity(Repository repository,
			StaticRepositoryEntity<RunResult> parent, String basePath) {
		super(repository, parent, basePath);
		this.runResultIdentifier = new HashMap<String, RunResult>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.RepositoryObjectEntity#registerWhenExisting
	 * (de.clusteval.framework.repository.RepositoryObject,
	 * de.clusteval.framework.repository.RepositoryObject)
	 */
	@Override
	protected <S extends RunResult> boolean registerWhenExisting(S old, S object) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.framework.repository.RepositoryObjectEntity#
	 * registerWithoutExisting
	 * (de.clusteval.framework.repository.RepositoryObject)
	 */
	@Override
	protected <S extends RunResult> boolean registerWithoutExisting(S object) {
		this.runResultIdentifier.put(object.getIdentifier(), object);
		return super.registerWithoutExisting(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.clusteval.framework.repository.RepositoryObjectEntity#
	 * unregisterAfterRemove(de.clusteval.framework.repository.RepositoryObject)
	 */
	@Override
	protected <S extends RunResult> void unregisterAfterRemove(S object) {
		this.runResultIdentifier.remove(object.getIdentifier());

		// added 07.01.2013: add unregister of sqlcommunicator
		if (object instanceof ParameterOptimizationResult)
			// if the runresult folder exists, only delete the parameter
			// optimization run result from the database
			if (new File(object.getAbsolutePath()).getParentFile().exists())
				this.repository.sqlCommunicator
						.unregister((ParameterOptimizationResult) object);
			else if (Repository.getRepositoryForExactPath(object
					.getRepository().getBasePath()) != null)
				Repository.unregister(Repository
						.getRepositoryForExactPath(object.getRepository()
								.getBasePath()));

		super.unregisterAfterRemove(object);
	}

	/**
	 * The absolute path to the directory, where for a certain runresult
	 * (identified by its unique run identifier) all clustering results are
	 * stored.
	 */
	public String getClusterResultsBasePath() {
		return FileUtils.buildPath(this.getBasePath(), "%RUNIDENTSTRING",
				"clusters");
	}

	/**
	 * The absolute path to the directory, where for a certain runresult
	 * (identified by its unique run identifier) all qualities of clustering
	 * results are stored.
	 */
	public String getClusterResultsQualityBasePath() {
		return FileUtils.buildPath(this.getBasePath(), "%RUNIDENTSTRING",
				"clusters");
	}

	/**
	 * @return The absolute path to the directory, where for a certain runresult
	 *         (identified by its unique run identifier) all analysis results
	 *         are stored.
	 */
	public String getAnalysisResultsBasePath() {
		return FileUtils.buildPath(this.getBasePath(), "%RUNIDENTSTRING",
				"analyses");
	}

	public String getResultLogBasePath() {
		return FileUtils.buildPath(this.getBasePath(), "%RUNIDENTSTRING",
				"logs");
	}
}