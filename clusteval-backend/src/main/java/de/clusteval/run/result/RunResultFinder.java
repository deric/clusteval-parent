/** *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ***************************************************************************** */
package de.clusteval.run.result;

import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.run.Run;
import de.clusteval.utils.FileFinder;
import de.clusteval.framework.RUN_STATUS;
import de.wiwie.wiutils.utils.ArrayIterator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Christian Wiwie
 *
 */
public class RunResultFinder extends FileFinder<RunResult> {

    // the iterator to have access to the parsed runresult objects
    protected RunResultIterator iter;

    /**
     * Instantiates a new run result finder.
     *
     * @param repository
     *                   The repository to register the new run results at.
     * @throws RegisterException
     */
    public RunResultFinder(IRepository repository) throws RegisterException {
        super(repository, RunResult.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.FileFinder#parseObjectFromFile(java.io.File)
     */
    @Override
    protected RunResult parseObjectFromFile(File file) throws Exception {
        iter.getRunResult().loadIntoMemory();
        try {
            iter.getRunResult().register();
        } finally {
            iter.getRunResult().unloadFromMemory();
        }
        return iter.getRunResult();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Finder#getIterator()
     */
    @Override
    protected Iterator<File> getIterator() {
        iter = new RunResultIterator(this.repository, this.getBaseDir());
        return iter;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Finder#checkFile(java.io.File)
     */
    @Override
    // Fixed 17.03.2014: changed !isRunning(file.getName()) to
    // !isRunning(file.getParentFile().getParentFile().getName())
    protected boolean checkFile(File file) {
        String uniqueRunId;
        File f = file;
        while (!f.getParentFile().getName().equals("results")) {
            f = f.getParentFile();
        }
        uniqueRunId = f.getName();
        IRepositoryObject registered = repository.getRegisteredObject(file);
        return !isRunning(uniqueRunId)
                && (registered == null || registered.getChangeDate() < file.lastModified());
    }

    protected boolean isRunning(final String uniqueRunIdentifier) {
        RunSchedulerThread runScheduler = repository.getSupervisorThread()
                .getRunScheduler();
        Collection<Run> runs = runScheduler.getRuns();
        for (Run run : runs) {
            if ((run.getStatus().equals(RUN_STATUS.RUNNING) || run.getStatus()
                    .equals(RUN_STATUS.SCHEDULED))
                    && run.getRunIdentificationString() != null
                    && run.getRunIdentificationString().equals(
                            uniqueRunIdentifier)) {
                return true;
            }
        }
        return false;
    }
}

class RunResultIterator implements Iterator<File> {

    protected IRepository repo;

    protected ArrayIterator<File> basePath;

    protected List<RunResult> parsedResults;

    protected RunResult lastReturnedResult;

    public RunResultIterator(final IRepository repo, final File basePath) {
        this.repo = repo;
        this.basePath = new ArrayIterator<>(basePath.listFiles());
        this.parsedResults = new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        // take already parsed runresults
        if (this.parsedResults.size() > 0) {
            return true;
        }

        // parse new runresults
        boolean exception = true;
        while ((exception || this.parsedResults.isEmpty())
                && basePath.hasNext()) {
            try {
                List<RunResult> newResults = new ArrayList<>();
                RunResult.parseFromRunResultFolder(repo, basePath.next(),
                        newResults, false, false, false);
                this.parsedResults.addAll(newResults);
                exception = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            } catch (Exception e) {
                // just ignore that runresult if it cannot be parsed
            }
        }
        return this.parsedResults.size() > 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#next()
     */
    @Override
    public File next() {
        this.lastReturnedResult = this.parsedResults.remove(0);
        return new File(lastReturnedResult.getAbsolutePath());
    }

    public RunResult getRunResult() {
        return this.lastReturnedResult;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        // unsupported
    }
}
