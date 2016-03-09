/**
 * *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 *****************************************************************************
 */
/**
 *
 */
package de.clusteval.program.r;

import de.clusteval.data.dataset.format.IncompatibleDataSetFormatException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.threading.RunSchedulerThread;
import de.clusteval.run.Run;
import de.clusteval.run.RunInitializationException;
import de.clusteval.run.result.RunResult;
import de.clusteval.run.runnable.RunRunnable;
import de.clusteval.run.runnable.RunRunnableInitializationException;
import de.clusteval.utils.AbstractClustEvalTest;
import de.wiwie.wiutils.file.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class TestKMeansClusteringRProgram extends AbstractClustEvalTest {

    /**
     * @throws RepositoryAlreadyExistsException
     * @throws InvalidRepositoryException
     * @throws RepositoryConfigNotFoundException
     * @throws RepositoryConfigurationException
     * @throws IOException
     * @throws RunRunnableInitializationException
     * @throws java.lang.InterruptedException
     * @throws de.clusteval.data.randomizer.UnknownDataRandomizerException
     * @throws RunInitializationException
     */
    @Test
    public void testApplyToRelativeDataSet()
            throws RepositoryAlreadyExistsException, InvalidRepositoryException, RepositoryConfigNotFoundException,
                   RepositoryConfigurationException, IOException, RunRunnableInitializationException, InterruptedException,
                   UnknownDataRandomizerException, RunInitializationException {
        RunSchedulerThread scheduler = this.getRepository().getSupervisorThread().getRunScheduler();

        Run run = this.getRepository().getStaticObjectWithName(Run.class, "test_kmeans_sfld_layered_f2");
        try {
            run.perform(scheduler);

            List<RunRunnable> runnables = run.getRunRunnables();
            assertEquals(1, runnables.size());
            RunRunnable runnable = runnables.get(0);
            List<Throwable> exceptions = runnable.getExceptions();
            assertEquals(1, exceptions.size());

            Throwable t = exceptions.get(0);
            assertEquals(IncompatibleDataSetFormatException.class, t.getClass());
        } finally {
            if (run != null) {
                FileUtils.delete(new File(FileUtils.buildPath(this.getRepository().getBasePath(RunResult.class),
                        run.getRunIdentificationString())));
            }
        }
    }
}
