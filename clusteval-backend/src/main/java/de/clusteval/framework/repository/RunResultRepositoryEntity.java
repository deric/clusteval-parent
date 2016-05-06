/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.framework.repository;

import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryController;
import de.clusteval.api.repository.StaticRepositoryEntity;
import de.clusteval.api.run.IRunResult;
import de.clusteval.run.result.ParameterOptimizationResult;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Wiwie
 *
 */
public class RunResultRepositoryEntity extends StaticRepositoryEntity<IRunResult> {

    protected Map<String, IRunResult> runResultIdentifier;

    /**
     * @param repository
     * @param parent
     * @param basePath
     */
    public RunResultRepositoryEntity(IRepository repository,
            StaticRepositoryEntity<IRunResult> parent, String basePath) {
        super(repository, parent, basePath);
        this.runResultIdentifier = new HashMap<>();
    }

    @Override
    protected <S extends IRunResult> boolean registerWhenExisting(S old, S object) {
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
    protected <S extends IRunResult> boolean registerWithoutExisting(S object) {
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
    protected <S extends IRunResult> void unregisterAfterRemove(S object) {
        this.runResultIdentifier.remove(object.getIdentifier());

        // added 07.01.2013: add unregister of sqlcommunicator
        // if the runresult folder exists, only delete the parameter
        // optimization run result from the database
        if (object instanceof ParameterOptimizationResult) {
            RepositoryController ctrl = RepositoryController.getInstance();
            if (new File(object.getAbsolutePath()).getParentFile().exists()) {
                repository.getDb().unregister((ParameterOptimizationResult) object);
            } else if (ctrl.getRepositoryForExactPath(object
                    .getRepository().getBasePath()) != null) {
                ctrl.unregister(ctrl.getRepositoryForExactPath(object.getRepository().getBasePath()));
            }
        }

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
     *         (identified by its unique run identifier) all analysis results are
     *         stored.
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
