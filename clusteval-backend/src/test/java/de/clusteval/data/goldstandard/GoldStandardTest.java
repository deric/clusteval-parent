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
package de.clusteval.data.goldstandard;

import de.clusteval.api.cluster.Cluster;
import de.clusteval.api.cluster.ClusterItem;
import de.clusteval.api.cluster.IClustering;
import de.clusteval.api.data.GoldStandard;
import de.clusteval.api.data.IGoldStandard;
import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.repository.RepositoryConfigurationException;
import de.clusteval.api.repository.RepositoryObject;
import de.clusteval.cluster.Clustering;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.repository.db.StubSQLCommunicator;
import de.clusteval.utils.AbstractClustEvalTest;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Christian Wiwie
 *
 */
public class GoldStandardTest extends AbstractClustEvalTest {

    /**
     * @throws NoRepositoryFoundException
     * @throws GoldStandardNotFoundException
     * @throws RegisterException
     */
    @Test
    public void testParseFromFile() throws NoRepositoryFoundException,
                                           GoldStandardNotFoundException, RegisterException {
        IGoldStandard newObject = GoldStandard
                .parseFromFile(new File(
                        "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                        .getAbsoluteFile());
        assertEquals(
                newObject,
                new GoldStandard(
                        getRepository(),
                        new File(
                                "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                        .lastModified(),
                        new File(
                                "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                        .getAbsoluteFile()));
    }

    /**
     * @throws NoRepositoryFoundException
     * @throws GoldStandardNotFoundException
     * @throws RegisterException
     */
    @Test(expected = GoldStandardNotFoundException.class)
    public void testParseFromNotExistingFile()
            throws NoRepositoryFoundException, GoldStandardNotFoundException,
                   RegisterException {
        GoldStandard
                .parseFromFile(new File(
                        "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard2.txt")
                        .getAbsoluteFile());
    }

    /**
     * Registering a goldstandard of a runresult repository that is not present
     * in the parent repository should not be possible.
     *
     * @throws NoRepositoryFoundException
     * @throws RepositoryConfigurationException
     * @throws InvalidRepositoryException
     * @throws RepositoryAlreadyExistsException
     * @throws FileNotFoundException
     * @throws RegisterException
     * @throws GoldStandardNotFoundException
     * @throws NoSuchAlgorithmException
     */
    @Test(expected = RegisterException.class)
    public void testRegisterRunResultRepositoryNotPresentInParent()
            throws FileNotFoundException, RepositoryAlreadyExistsException,
                   InvalidRepositoryException, RepositoryConfigurationException, NoRepositoryFoundException,
                   GoldStandardNotFoundException, RegisterException,
                   NoSuchAlgorithmException, InterruptedException {
        getRepository().initialize();
        try {
            Repository runResultRepository = new RunResultRepository(
                    new File(
                            "testCaseRepository/results/12_04_2012-14_05_42_tc_vs_DS1")
                    .getAbsolutePath(), getRepository());
            runResultRepository.setSQLCommunicator(new StubSQLCommunicator(
                    runResultRepository));
            runResultRepository.initialize();
            try {
                GoldStandard
                        .parseFromFile(new File(
                                "testCaseRepository/results/12_04_2012-14_05_42_tc_vs_DS1/goldstandards/DS1/testCaseGoldstandardNotPresentInParentRepository.txt"));
            } finally {
                runResultRepository.terminateSupervisorThread();
            }
        } catch (DatabaseConnectException e) {
            // cannot happen
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see utils.TestRepositoryObject#testHashCode()
     */
    public void testHashCode() throws NoRepositoryFoundException,
                                      GoldStandardNotFoundException, RegisterException {
        this.repositoryObject = (RepositoryObject) GoldStandard
                .parseFromFile(new File(
                        "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                        .getAbsoluteFile());
        String absPath = new File(
                "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                .getAbsolutePath();
        assertEquals(
                (this.getRepository().toString() + absPath).hashCode(),
                this.repositoryObject.hashCode());
    }

    /*
     * (non-Javadoc)
     *
     * @see utils.TestRepositoryObject#testGetRepository()
     */
    public void testGetRepository() throws NoRepositoryFoundException,
                                           GoldStandardNotFoundException, RegisterException {
        this.repositoryObject = GoldStandard.parseFromFile(
                new File("testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                .getAbsoluteFile());
        assertEquals(this.getRepository(),
                this.repositoryObject.getRepository());
    }

    /*
     * (non-Javadoc)
     *
     * @see utils.TestRepositoryObject#testRegister()
     */
    public void testRegister() throws NoRepositoryFoundException,
                                      GoldStandardNotFoundException, RegisterException {
        this.repositoryObject = GoldStandard
                .parseFromFile(new File(
                        "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                        .getAbsoluteFile());
        assertEquals(this.repositoryObject, this.getRepository()
                .getRegisteredObject((GoldStandard) this.repositoryObject));

        // adding a gold standard equal to another one already registered does
        // not register the second object.
        this.repositoryObject = new GoldStandard(
                (GoldStandard) this.repositoryObject);
        assertEquals(
                this.getRepository().getRegisteredObject(
                        (GoldStandard) this.repositoryObject),
                this.repositoryObject);
        assertFalse(this.getRepository().getRegisteredObject(
                (GoldStandard) this.repositoryObject) == this.repositoryObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see utils.TestRepositoryObject#testRegister()
     */
    public void testUnregister() throws NoRepositoryFoundException,
                                        GoldStandardNotFoundException, RegisterException {
        this.repositoryObject = GoldStandard
                .parseFromFile(new File(
                        "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                        .getAbsoluteFile());
        assertEquals(this.repositoryObject, this.getRepository()
                .getRegisteredObject((GoldStandard) this.repositoryObject));
        this.repositoryObject.unregister();
        // is not registered anymore
        assertTrue(this.getRepository().getRegisteredObject(
                (GoldStandard) this.repositoryObject) == null);
    }

    /*
     * (non-Javadoc)
     *
     * @see utils.TestRepositoryObject#testEqualsObject()
     */
    @Test
    public void testEqualsObject() throws RegisterException {
        File f = new File(
                "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                .getAbsoluteFile();
        this.repositoryObject = new GoldStandard(this.getRepository(),
                f.lastModified(), f);
        assertEquals(
                new GoldStandard(this.getRepository(), f.lastModified(), f),
                this.repositoryObject);

        File f2 = new File(
                "testCaseRepository/data/goldstandards/sfld/sfld_brown_et_al_amidohydrolases_families_gold_standard.txt");
        assertFalse(this.repositoryObject.equals(new GoldStandard(this
                .getRepository(), f2.lastModified(), f2)));
    }

    /**
     * @throws GoldStandardNotFoundException
     * @throws NoRepositoryFoundException
     * @throws UnknownGoldStandardFormatException
     * @throws RegisterException
     *
     */
    @Test
    public void testLoadIntoMemory() throws NoRepositoryFoundException,
                                            GoldStandardNotFoundException, UnknownGoldStandardFormatException,
                                            RegisterException {
        File f = new File(
                "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                .getAbsoluteFile();
        this.repositoryObject = GoldStandard.parseFromFile(f);
        boolean success = ((GoldStandard) this.repositoryObject)
                .loadIntoMemory();
        assertTrue(success);

        IClustering clustering = ((IGoldStandard) repositoryObject).getClustering();

        assertTrue(((GoldStandard) this.repositoryObject).isInMemory());

        assertTrue(clustering != null);

        Clustering expected = new Clustering(this.getRepository(),
                System.currentTimeMillis(), new File(""));
        Cluster cluster1 = new Cluster("0");
        cluster1.add(new ClusterItem("0"), 1.0f);
        cluster1.add(new ClusterItem("1"), 1.0f);
        cluster1.add(new ClusterItem("2"), 1.0f);
        cluster1.add(new ClusterItem("3"), 1.0f);
        cluster1.add(new ClusterItem("4"), 1.0f);
        cluster1.add(new ClusterItem("5"), 1.0f);
        cluster1.add(new ClusterItem("6"), 1.0f);
        cluster1.add(new ClusterItem("7"), 1.0f);
        cluster1.add(new ClusterItem("10"), 1.0f);
        cluster1.add(new ClusterItem("11"), 1.0f);
        cluster1.add(new ClusterItem("12"), 1.0f);
        cluster1.add(new ClusterItem("13"), 1.0f);
        cluster1.add(new ClusterItem("16"), 1.0f);
        cluster1.add(new ClusterItem("17"), 1.0f);
        cluster1.add(new ClusterItem("19"), 1.0f);
        cluster1.add(new ClusterItem("21"), 1.0f);
        Cluster cluster2 = new Cluster("1");
        cluster2.add(new ClusterItem("8"), 1.0f);
        cluster2.add(new ClusterItem("9"), 1.0f);
        cluster2.add(new ClusterItem("14"), 1.0f);
        cluster2.add(new ClusterItem("15"), 1.0f);
        cluster2.add(new ClusterItem("18"), 1.0f);
        cluster2.add(new ClusterItem("20"), 1.0f);
        cluster2.add(new ClusterItem("22"), 1.0f);
        cluster2.add(new ClusterItem("23"), 1.0f);
        cluster2.add(new ClusterItem("24"), 1.0f);
        cluster2.add(new ClusterItem("25"), 1.0f);
        cluster2.add(new ClusterItem("26"), 1.0f);
        cluster2.add(new ClusterItem("27"), 1.0f);
        cluster2.add(new ClusterItem("28"), 1.0f);
        cluster2.add(new ClusterItem("29"), 1.0f);
        cluster2.add(new ClusterItem("30"), 1.0f);
        cluster2.add(new ClusterItem("31"), 1.0f);
        cluster2.add(new ClusterItem("32"), 1.0f);
        cluster2.add(new ClusterItem("33"), 1.0f);
        expected.addCluster(cluster1);
        expected.addCluster(cluster2);

        assertEquals(expected, clustering);
    }

    /**
     * @throws GoldStandardNotFoundException
     * @throws NoRepositoryFoundException
     * @throws UnknownGoldStandardFormatException
     * @throws RegisterException
     *
     */
    @Test
    public void testUnloadFromMemory() throws NoRepositoryFoundException,
                                              GoldStandardNotFoundException, UnknownGoldStandardFormatException,
                                              RegisterException {
        File f = new File(
                "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                .getAbsoluteFile();
        this.repositoryObject = GoldStandard.parseFromFile(f);
        boolean success = ((GoldStandard) this.repositoryObject)
                .loadIntoMemory();
        assertTrue(success);

        assertTrue(((GoldStandard) this.repositoryObject).isInMemory());

        success = ((GoldStandard) this.repositoryObject).unloadFromMemory();
        assertTrue(success);

        assertFalse(((GoldStandard) this.repositoryObject).isInMemory());
    }

    /**
     * @throws UnknownGoldStandardFormatException
     * @throws GoldStandardNotFoundException
     * @throws NoRepositoryFoundException
     * @throws RegisterException
     *
     */
    @Test
    public void testIsInMemory() throws UnknownGoldStandardFormatException,
                                        NoRepositoryFoundException, GoldStandardNotFoundException,
                                        RegisterException {
        File f = new File(
                "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                .getAbsoluteFile();
        this.repositoryObject = GoldStandard.parseFromFile(f);
        ((GoldStandard) this.repositoryObject).loadIntoMemory();

        assertTrue(((GoldStandard) this.repositoryObject).isInMemory());

        ((GoldStandard) this.repositoryObject).unloadFromMemory();

        assertFalse(((GoldStandard) this.repositoryObject).isInMemory());
    }

    /**
     * @throws GoldStandardNotFoundException
     * @throws NoRepositoryFoundException
     * @throws UnknownGoldStandardFormatException
     * @throws RegisterException
     *
     */
    @Test
    public void testSize() throws NoRepositoryFoundException,
                                  GoldStandardNotFoundException, UnknownGoldStandardFormatException,
                                  RegisterException {
        File f = new File(
                "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                .getAbsoluteFile();
        this.repositoryObject = GoldStandard.parseFromFile(f);
        ((GoldStandard) this.repositoryObject).loadIntoMemory();

        IClustering clustering = ((IGoldStandard) this.repositoryObject).getClustering();

        assertEquals(34, clustering.size());
    }

    /**
     * @throws GoldStandardNotFoundException
     * @throws NoRepositoryFoundException
     * @throws UnknownGoldStandardFormatException
     * @throws RegisterException
     *
     */
    @Test
    public void testFuzzySize() throws NoRepositoryFoundException,
                                       GoldStandardNotFoundException, UnknownGoldStandardFormatException,
                                       RegisterException {
        File f = new File(
                "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                .getAbsoluteFile();
        this.repositoryObject = GoldStandard.parseFromFile(f);
        ((GoldStandard) this.repositoryObject).loadIntoMemory();

        IClustering clustering = ((IGoldStandard) this.repositoryObject).getClustering();

        assertEquals(34f, clustering.fuzzySize(), 1e-5);
    }

    /**
     * Test method for {@link data.goldstandard.GoldStandard#getFullName()}.
     *
     * @throws NoRepositoryFoundException
     * @throws GoldStandardNotFoundException
     * @throws RegisterException
     */
    @Test
    public void testGetFullName() throws NoRepositoryFoundException,
                                         GoldStandardNotFoundException, RegisterException {
        this.repositoryObject = GoldStandard
                .parseFromFile(new File(
                        "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                        .getAbsoluteFile());
        assertEquals("DS1/Zachary_karate_club_gold_standard.txt",
                ((GoldStandard) this.repositoryObject).getFullName());
    }

    /**
     * Test method for {@link data.goldstandard.GoldStandard#getMajorName()}.
     *
     * @throws NoRepositoryFoundException
     *
     * @throws RegisterException
     * @throws GoldStandardNotFoundException
     */
    @Test
    public void testGetMajorName() throws NoRepositoryFoundException,
                                          RegisterException, GoldStandardNotFoundException {
        this.repositoryObject = GoldStandard
                .parseFromFile(new File(
                        "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                        .getAbsoluteFile());
        assertEquals("DS1",
                ((GoldStandard) this.repositoryObject).getMajorName());
    }

    /**
     * Test method for {@link data.goldstandard.GoldStandard#getMinorName()}.
     *
     * @throws NoRepositoryFoundException
     * @throws RegisterException
     * @throws GoldStandardNotFoundException
     */
    @Test
    public void testGetMinorName() throws NoRepositoryFoundException,
                                          RegisterException, GoldStandardNotFoundException {
        this.repositoryObject = GoldStandard
                .parseFromFile(new File(
                        "testCaseRepository/data/goldstandards/DS1/Zachary_karate_club_gold_standard.txt")
                        .getAbsoluteFile());
        GoldStandard casted = ((GoldStandard) this.repositoryObject);
        assertEquals("Zachary_karate_club_gold_standard.txt",
                casted.getMinorName());
        assertEquals(casted.getMinorName(), casted.getAbsolutePath()
                .substring(casted.getAbsolutePath().lastIndexOf("/") + 1));
    }
}
