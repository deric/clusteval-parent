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

import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.repository.IRepository;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author deric
 */
public class RepositoryController {

    private static RepositoryController instance;
    /**
     * A map containing all repository objects. This includes this repository
     * but also all run result repositories or other child repositories, that
     * are contained within this repository.
     */
    protected Map<String, IRepository> repositories = new HashMap<>();

    private RepositoryController() {

    }

    public static RepositoryController getInstance() {
        if (instance == null) {
            instance = new RepositoryController();
        }
        return instance;
    }

    /**
     * This method returns a repository (if available) with the given root path.
     *
     * @param absFilePath The absolute root path of the repository.
     * @return The repository with the given root path.
     */
    public IRepository getRepositoryForExactPath(final String absFilePath) {
        return repositories.get(absFilePath);
    }

    /**
     * This method returns the lowest repository in repository-hierarchy, that
     * contains the given path. That means, if there are several nested
     * repositories for the given path, this method will return the lowest one
     * of the hierarchy.
     *
     * @param absFilePath The absolute file path we want to find the repository
     * for.
     * @return The repository for the given path, which is lowest in the
     * repository-hierarchy.
     * @throws NoRepositoryFoundException
     */
    public IRepository getRepositoryForPath(final String absFilePath) throws NoRepositoryFoundException {
        String resultPath = null;
        for (String repoPath : repositories.keySet()) {
            if (absFilePath.startsWith(repoPath + System.getProperty("file.separator"))) {
                if (resultPath == null || repoPath.length() > resultPath.length()) {
                    resultPath = repoPath;
                }
            }
        }
        if (resultPath == null) {
            throw new NoRepositoryFoundException(absFilePath);
        }
        return repositories.get(resultPath);
    }

    /**
     * Register a new repository.
     *
     * @param repository The new repository to register.
     * @return The old repository, if the new repository replaced an old one
     * with equal root path. Null otherwise.
     * @throws RepositoryAlreadyExistsException
     * @throws InvalidRepositoryException
     */
    public IRepository register(IRepository repository)
            throws RepositoryAlreadyExistsException, InvalidRepositoryException {
        IRepository other = null;
        RepositoryController ctrl = RepositoryController.getInstance();
        try {
            other = ctrl.getRepositoryForPath(repository.getBasePath());
        } catch (NoRepositoryFoundException e) {
        }
        if (other == null) {
            return repositories.put(repository.getBasePath(), repository);
        }
        if (other.getBasePath().equals(repository.getBasePath())) {
            throw new RepositoryAlreadyExistsException(other.getBasePath());
        }
        if (repository.getParent() == null || !repository.getParent().equals(other)) {
            throw new InvalidRepositoryException("Repositories must not be nested without parental relationship");
        }
        return repositories.put(repository.getBasePath(), repository);
    }

    /**
     * Unregister the given repository.
     *
     * @param repository The repository to remove.
     * @return The removed repository. If null, the given repository was not
     * registered.
     */
    public IRepository unregister(IRepository repository) {
        return repositories.remove(repository.getBasePath());
    }

}
