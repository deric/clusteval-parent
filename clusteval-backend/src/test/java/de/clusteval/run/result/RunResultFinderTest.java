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

import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.api.run.RUN_STATUS;
import de.clusteval.api.run.Run;
import de.clusteval.api.run.result.RunResult;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.db.SQLCommunicator;
import de.clusteval.framework.repository.db.StubSQLCommunicator;
import de.clusteval.framework.threading.RepositorySupervisorThread;
import de.clusteval.framework.threading.SupervisorThread;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Christian Wiwie
 *
 */
public class RunResultFinderTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    // @Test
    public void testRunInProgressNotFound() throws FileNotFoundException,
                                                   RepositoryAlreadyExistsException, InvalidRepositoryException,
                                                   RepositoryConfigurationException, NoRepositoryFoundException,
                                                   InterruptedException, NoSuchAlgorithmException,
                                                   DatabaseConnectException {

        TestRepository repository = new TestRepository(new File(
                "testCaseRepository").getAbsolutePath(), null);
        ClustevalBackendServer framework = new ClustevalBackendServer(
                repository, false);
        try {
            framework.performRun("1", "tc_vs_DS1");
            Run run = repository
                    .findByName(Run.class, "tc_vs_DS1");
            while (!run.getStatus().equals(RUN_STATUS.FINISHED)) {
                Thread.sleep(100);
            }
            assertFalse(repository.assertionFailed);
        } finally {
            repository.terminateSupervisorThread();
        }
    }

    // @Test
    public void testRunNotInProgressNotFound() throws FileNotFoundException,
                                                      RepositoryAlreadyExistsException, InvalidRepositoryException,
                                                      RepositoryConfigurationException, NoRepositoryFoundException,
                                                      InterruptedException, NoSuchAlgorithmException,
                                                      DatabaseConnectException {

        TestRepository repository = new TestRepository(new File(
                "testCaseRepository").getAbsolutePath(), null);
        repository.setSQLCommunicator(new StubSQLCommunicator(repository));
        ClustevalBackendServer framework = new ClustevalBackendServer(
                repository, false);
        try {
            while (!repository.isInitialized()) {
                Thread.sleep(100);
            }
            // We do not perform a new run, so we should not have the failed
            // assertion
            assertFalse(repository.assertionFailed);
        } finally {
            repository.terminateSupervisorThread();
        }
    }

}

class TestRepository extends Repository {

    protected boolean assertionFailed;

    /**
     * @param basePath
     * @param parent
     * @throws FileNotFoundException
     * @throws RepositoryAlreadyExistsException
     * @throws InvalidRepositoryException
     * @throws RepositoryConfigNotFoundException
     * @throws RepositoryConfigurationException
     * @throws NoRepositoryFoundException
     * @throws NoSuchAlgorithmException
     * @throws DatabaseConnectException
     */
    public TestRepository(String basePath, Repository parent)
            throws FileNotFoundException, RepositoryAlreadyExistsException,
                   InvalidRepositoryException,
                   RepositoryConfigurationException, NoRepositoryFoundException,
                   NoSuchAlgorithmException, DatabaseConnectException {
        super(basePath, parent);
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.Repository#createSQLCommunicator()
     */
    @Override
    protected SQLCommunicator createSQLCommunicator() {
        return new StubSQLCommunicator(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.Repository#createSupervisorThread()
     */
    @Override
    protected SupervisorThread createSupervisorThread() {
        // no scanning for runresults
        return new RepositorySupervisorThread(this,
                this.repositoryConfig.getThreadSleepTimes(), false, false);
    }

    public boolean register(RunResult object) throws RegisterException {
        String runIdent = object.getIdentifier();
        Run run = this.findByName(Run.class, object.getRun().toString());
        if (!assertionFailed) {
            assertionFailed = !(run.getStatus().equals(RUN_STATUS.FINISHED) || run
                    .getStatus().equals(RUN_STATUS.INACTIVE))
                    && (run.getRunIdentificationString() != null && run
                    .getRunIdentificationString().equals(runIdent));
        }
        return super.register(object);
    }

}
