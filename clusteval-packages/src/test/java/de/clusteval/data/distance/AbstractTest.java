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
package de.clusteval.data.distance;

import de.clusteval.api.repository.IRepositoryObject;
import de.clusteval.api.AbsContext;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.config.DefaultRepositoryConfig;
import de.clusteval.framework.repository.db.StubSQLCommunicator;
import de.clusteval.run.result.RunResult;
import java.io.File;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public class AbstractTest {

    protected static Repository repository;
    protected static IRepositoryObject repositoryObject;
    protected static AbsContext context;
    protected boolean useDatabase;
    static Logger logger = LoggerFactory.getLogger(AbstractTest.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        File path = new File("testCaseRepository");

        repository = new Repository(path.getAbsolutePath(), null, new DefaultRepositoryConfig());
        repository.setSQLCommunicator(new StubSQLCommunicator(repository));

        ClustevalBackendServer.getBackendServerConfiguration().setCheckForRunResults(false);
        repository.initialize();

        if (ClustevalBackendServer.getBackendServerConfiguration().getCheckForRunResults()) {
            while (!repository.isInitialized(RunResult.class)) {
                Thread.sleep(100);
            }
        }
        //repositoryObject = new StubRepositoryObject(repository, false, System.currentTimeMillis(),new File("test"));
        //context = Context.parseFromString(repository, "ClusteringContext");
    }

}
