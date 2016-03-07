/*******************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.clusteval.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.repository.config.DefaultRepositoryConfig;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.db.DatabaseConnectException;

/**
 * @author Christian Wiwie
 * 
 */
public class TestRepository {

	protected Repository parent;

	@Rule
	public TestName name = new TestName();

	/**
	 * @throws FileNotFoundException
	 * @throws RepositoryAlreadyExistsException
	 * @throws InvalidRepositoryException
	 * @throws NoRepositoryFoundException
	 * @throws RepositoryConfigurationException
	 * @throws RepositoryConfigNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws DatabaseConnectException
	 */
	@Before
	public void setUp() throws FileNotFoundException, RepositoryAlreadyExistsException, InvalidRepositoryException,
			RepositoryConfigNotFoundException, RepositoryConfigurationException, NoRepositoryFoundException,
			NoSuchAlgorithmException, DatabaseConnectException {
		System.out.println(
				"################## Testcase: " + this.getClass().getSimpleName() + "." + name.getMethodName());
		parent = new Repository(new File("testCaseRepository").getAbsolutePath(), null, new DefaultRepositoryConfig());
	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
		if (parent != null)
			Repository.unregister(parent);
	}

	/**
	 * @throws FileNotFoundException
	 * @throws RepositoryAlreadyExistsException
	 * @throws InvalidRepositoryException
	 * @throws NoRepositoryFoundException
	 * @throws RepositoryConfigurationException
	 * @throws RepositoryConfigNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws DatabaseConnectException
	 */
	@Test(expected = InvalidRepositoryException.class)
	public void test1() throws FileNotFoundException, RepositoryAlreadyExistsException, InvalidRepositoryException,
			RepositoryConfigNotFoundException, RepositoryConfigurationException, NoRepositoryFoundException,
			NoSuchAlgorithmException, DatabaseConnectException {
		/*
		 * Nested without parantal relationship not allowed
		 */
		Repository child = new RunResultRepository("testCaseRepository/results/01_30_2013-21_31_25_tc_vs_DS1", null);
	}

	/**
	 * @throws FileNotFoundException
	 * @throws RepositoryAlreadyExistsException
	 * @throws InvalidRepositoryException
	 * @throws NoRepositoryFoundException
	 * @throws RepositoryConfigurationException
	 * @throws RepositoryConfigNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws DatabaseConnectException
	 */
	@Test
	public void test2() throws FileNotFoundException, RepositoryAlreadyExistsException, InvalidRepositoryException,
			RepositoryConfigNotFoundException, RepositoryConfigurationException, NoRepositoryFoundException,
			NoSuchAlgorithmException, DatabaseConnectException {
		Repository child = new RunResultRepository("testCaseRepository/results/01_30_2013-21_31_25_tc_vs_DS1", parent);
	}

	/**
	 * @throws InvalidRepositoryException
	 * @throws RepositoryAlreadyExistsException
	 * @throws FileNotFoundException
	 * @throws NoRepositoryFoundException
	 * @throws RepositoryConfigurationException
	 * @throws RepositoryConfigNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws DatabaseConnectException
	 * 
	 */
	@Test
	public void testGetRepositoryForPathString() throws FileNotFoundException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException, RepositoryConfigurationException,
			NoRepositoryFoundException, NoSuchAlgorithmException, DatabaseConnectException {
		File f = new File("repository2");
		f.deleteOnExit();
		Repository child = new Repository(f.getAbsolutePath(), null, new DefaultRepositoryConfig());
	}
}
