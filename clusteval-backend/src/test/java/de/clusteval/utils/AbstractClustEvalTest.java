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
package de.clusteval.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.Appender;
import de.clusteval.api.ContextFactory;
import de.clusteval.api.IContext;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.repository.RepositoryController;
import de.clusteval.api.run.IRunResult;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.config.DefaultRepositoryConfig;
import de.clusteval.framework.repository.db.StubSQLCommunicator;
import java.io.File;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Wiwie
 *
 */
public abstract class AbstractClustEvalTest {

    public static double DELTA = 1e-9;

    @Rule
    public TestName name = new TestName();
    private Repository repository;
    protected IRepositoryObject repositoryObject;
    protected IContext context;
    protected boolean useDatabase;
    static Logger logger = LoggerFactory.getLogger(AbstractClustEvalTest.class);

    public AbstractClustEvalTest() {
        this(false);
    }

    /**
     *
     * @param useDatabase
     */
    public AbstractClustEvalTest(final boolean useDatabase) {
        super();
        this.useDatabase = useDatabase;
    }

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ClustevalBackendServer.logLevel(Level.TRACE);
        BasicConfigurator.configure();
        ch.qos.logback.classic.Logger lg = ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.apache.commons.configuration.ConfigurationUtils"));
        Appender app = lg.getAppender("org.apache.commons.configuration.ConfigurationUtils");
        lg.detachAppender(app);
        //ignore certain classes
        ClustevalBackendServer.logLevel("org.apache.commons.configuration.ConfigurationUtils", Level.OFF);
        logger.info("Starting tests");
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
        System.out.println(
                "################## Testcase: " + this.getClass().getSimpleName() + "." + name.getMethodName());
        File path = new File("testCaseRepository");
        if (this.useDatabase) {
            this.repository = new Repository(path.getAbsolutePath(), null);
        } else {
            this.repository = new Repository(path.getAbsolutePath(), null, new DefaultRepositoryConfig());
            getRepository().setSQLCommunicator(new StubSQLCommunicator(getRepository()));
        }
        ClustevalBackendServer.getBackendServerConfiguration().setCheckForRunResults(false);
        getRepository().initialize();

        if (ClustevalBackendServer.getBackendServerConfiguration().getCheckForRunResults()) {
            while (!getRepository().isInitialized(IRunResult.class)) {
                logger.debug("no run result");
                Thread.sleep(100);
            }
        }

        repositoryObject = new StubRepositoryObject(this.getRepository(), false, System.currentTimeMillis(),
                new File("test"));
        context = ContextFactory.parseFromString(getRepository(), "ClusteringContext");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        this.repositoryObject = null;
        getRepository().terminateSupervisorThread(true);
        while (getRepository().getSupervisorThread().isAlive()) {
            Thread.sleep(100);
        }
        RepositoryController.getInstance().unregister(getRepository());
    }

    protected IRepository getRepository() {
        return repository;
    }
}
