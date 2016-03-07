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
package de.clusteval.data.goldstandard;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.wiwie.wiutils.utils.text.TextFileMapParser;
import de.clusteval.cluster.Cluster;
import de.clusteval.cluster.ClusterItem;
import de.clusteval.cluster.Clustering;
import de.clusteval.data.goldstandard.format.UnknownGoldStandardFormatException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryObject;

/**
 * A wrapper class for a goldstandard on the filesystem.
 * 
 * @author Christian Wiwie
 * 
 */
public class GoldStandard extends RepositoryObject {

	/**
	 * This attribute holds the clustering that corresponds to the goldstandard.
	 * Every goldstandard can be interpreted as a clustering: A partition of the
	 * data objects into several groups.
	 * 
	 * @see {@link Clustering}
	 */
	protected Clustering clustering;

	/**
	 * Instantiates a new goldstandard object.
	 * 
	 * @param repository
	 *            the repository this goldstandard should be registered at.
	 * @param changeDate
	 *            The change date of this goldstandard is used for equality
	 *            checks.
	 * @param absGoldStandardPath
	 *            The absolute path of this goldstandard.
	 * @throws RegisterException
	 */
	public GoldStandard(final Repository repository, final long changeDate,
			final File absGoldStandardPath) throws RegisterException {
		super(repository, false, changeDate, absGoldStandardPath);

		this.absPath = absGoldStandardPath;

		this.log.trace("Goldstandard file: \"" + this.getAbsolutePath() + "\"");
		this.register();
	}

	/**
	 * Copy constructor for the GoldStandard class.
	 * 
	 * @param goldStandard
	 *            The goldstandard to be cloned.
	 * @throws RegisterException
	 */
	public GoldStandard(final GoldStandard goldStandard)
			throws RegisterException {
		super(goldStandard);
		this.absPath = new File(goldStandard.absPath.getAbsolutePath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.repository.RepositoryObject#clone()
	 */
	@Override
	public GoldStandard clone() {
		try {
			return new GoldStandard(this);
		} catch (RegisterException e) {
			// should not occur
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method parses a goldstandard from a file. Since goldstandard files
	 * do not require a header, this method simply creates a wrapper object for
	 * the file on the filesystem.
	 * 
	 * <p>
	 * The actual parsing of the clustering contained in the goldstandard file
	 * happens later in the {@link #loadIntoMemory()} method.
	 * 
	 * @param absGoldStandardPath
	 *            The absolute path to the goldstandard file that should be
	 *            parsed.
	 * @return The goldstandard object.
	 * @throws NoRepositoryFoundException
	 * @throws GoldStandardNotFoundException
	 * @throws RegisterException
	 */
	public static GoldStandard parseFromFile(final File absGoldStandardPath)
			throws NoRepositoryFoundException, GoldStandardNotFoundException,
			RegisterException {

		if (!absGoldStandardPath.exists())
			throw new GoldStandardNotFoundException("Goldstandard \""
					+ absGoldStandardPath + "\" does not exist!");

		final long changeDate = absGoldStandardPath.lastModified();

		return new GoldStandard(
				Repository.getRepositoryForPath(absGoldStandardPath
						.getAbsolutePath()), changeDate, absGoldStandardPath);
	}

	/**
	 * Checks whether this goldstandard is loaded into the memory.
	 * 
	 * @return true, if is in memory
	 */
	public boolean isInMemory() {
		return this.clustering != null;
	}

	/**
	 * Load this goldstandard into memory. When this method is invoked, it
	 * parses the goldstandard file on the filesystem
	 * 
	 * @return true, if successful
	 * @throws UnknownGoldStandardFormatException
	 */
	public boolean loadIntoMemory() throws UnknownGoldStandardFormatException {
		try {
			TextFileMapParser parser = new TextFileMapParser(
					this.absPath.getAbsolutePath(), 0, 1);
			parser.process();

			Map<String, Cluster> clusterMap = new HashMap<String, Cluster>();

			Map<String, String> mapping = parser.getResult();
			for (String itemId : mapping.keySet()) {
				ClusterItem item = new ClusterItem(itemId);

				String fuzzyClusterIds = mapping.get(itemId);
				String[] clusterIds = fuzzyClusterIds.split(";");

				for (String fuzzyClusterId : clusterIds) {
					try {
						String[] split = fuzzyClusterId.split(":");
						String clusterId = split[0];
						String fuzzy;
						// with fuzzy coefficient
						if (split.length == 2) {
							fuzzy = split[1];
						}
						// without fuzzy coefficient
						else if (split.length == 1) {
							fuzzy = "1.0";
						}
						// unsupported
						else {
							throw new ArrayIndexOutOfBoundsException();
						}

						if (!clusterMap.containsKey(clusterId)) {
							Cluster newCluster = new Cluster(clusterId);
							clusterMap.put(clusterId, newCluster);
						}
						clusterMap.get(clusterId).add(item,
								Float.parseFloat(fuzzy));
					} catch (ArrayIndexOutOfBoundsException e) {
						throw new UnknownGoldStandardFormatException(
								"The gold standard format is not valid near '"
										+ itemId
										+ "': Please ensure that it follows the per-row format ID{tab}CLASS_1:FUZZY_COEFF_1;CLASS_2:FUZZY_COEFF_2;...;CLASS_K:FUZZY_COEFF_K");
					}
				}
			}

			this.clustering = new Clustering(this.repository,
					this.absPath.lastModified(), this.absPath);
			for (String clusterId : clusterMap.keySet())
				this.clustering.addCluster(clusterMap.get(clusterId));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownGoldStandardFormatException(e.getMessage());
		}
	}

	/**
	 * Unload the contents of this dataset from memory.
	 * 
	 * @return true, if successful
	 */
	public boolean unloadFromMemory() {
		this.clustering = null;
		return true;
	}

	/**
	 * Size.
	 * 
	 * @return the int
	 */
	public int size() {
		return this.clustering.size();
	}

	/**
	 * Fuzzy size.
	 * 
	 * @return the float
	 */
	public float fuzzySize() {
		return this.clustering.fuzzySize();
	}

	/**
	 * This method returns a reference to the clustering object representing the
	 * contents of the goldstandard file.
	 * 
	 * <p>
	 * If this is not already the case, the contents of the file are parsed by
	 * invoking {@link #loadIntoMemory()}.
	 * 
	 * @return The clustering object representing the goldstandard.
	 * @throws UnknownGoldStandardFormatException
	 *             the unknown gold standard format exception
	 */
	public Clustering getClustering() throws UnknownGoldStandardFormatException {
		if (!isInMemory()) {
			loadIntoMemory();
		}
		return this.clustering;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getFullName();
	}

	/**
	 * Gets the full name of this goldstandard. The full name consists of the
	 * minor and the major name, separated by a slash: MAJOR/MINOR
	 * 
	 * @return The full name
	 */
	public String getFullName() {
		return getMajorName() + "/" + getMinorName();
	}

	/**
	 * Gets the major name of this goldstandard. The major name corresponds to
	 * the folder the goldstandard resides in in the filesystem.
	 * 
	 * @return The major name
	 */
	public String getMajorName() {
		return absPath.getParentFile().getName();
	}

	/**
	 * Gets the minor name of this goldstandard. The minor name corresponds to
	 * the name of the file of this goldstandard.
	 * 
	 * @return The minor name
	 */
	public String getMinorName() {
		return absPath.getName();
	}
}
