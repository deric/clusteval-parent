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
package de.clusteval.data.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.data.dataset.format.ConversionStandardToInputConfiguration;
import de.clusteval.data.preprocessing.DataPreprocessor;
import de.clusteval.framework.repository.DumpableRepositoryObject;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryEvent;
import de.clusteval.framework.repository.RepositoryMoveEvent;
import de.clusteval.framework.repository.RepositoryObjectDumpException;
import de.clusteval.framework.repository.RepositoryRemoveEvent;
import de.clusteval.framework.repository.RepositoryReplaceEvent;

/**
 * 
 * A dataset configuration encapsulates options and settings for a dataset.
 * During the execution of a run, when programs are applied to datasets,
 * settings are required that control the behaviour of how the dataset has to be
 * handled.
 * 
 * <p>
 * A dataset configuration corresponds to and is parsed from a file on the
 * filesystem in the corresponding folder of the repository (see
 * {@link Repository#dataSetConfigBasePath} and {@link DataSetConfigFinder}).
 * 
 * <p>
 * There are several options, that can be specified in the dataset configuration
 * file (see {@link #parseFromFile(File)}).
 * 
 * @author Christian Wiwie
 * 
 */
public class DataSetConfig extends DumpableRepositoryObject {

	/**
	 * A dataset configuration encapsulates a dataset. This attribute stores a
	 * reference to the dataset wrapper object.
	 */
	protected DataSet dataset;

	/**
	 * This variable holds the configuration needed, when {@link #dataset} is
	 * converted from its original input format to the internal standard format
	 * of the framework.
	 */
	protected ConversionInputToStandardConfiguration configInputToStandard;

	/**
	 * This variable holds the configuration needed, when {@link #dataset} is
	 * converted from the internal standard format of the framework to the input
	 * format of a program.
	 */
	protected ConversionStandardToInputConfiguration configStandardToInput;

	/**
	 * Instantiates a new dataset configuration.
	 * 
	 * @param repository
	 *            The repository this dataset configuration should be registered
	 *            at.
	 * @param changeDate
	 *            The change date of this dataset configuration is used for
	 *            equality checks.
	 * @param absPath
	 *            The absolute path of this dataset configuration.
	 * @param ds
	 *            The encapsulated dataset.
	 * @param configInputToStandard
	 *            The configuration needed, when {@link #dataset} is converted
	 *            from its original input format to the internal standard format
	 *            of the framework.
	 * @param configStandardToInput
	 *            The configuration needed, when {@link #dataset} is converted
	 *            from the internal standard format of the framework to the
	 *            input format of a program.
	 * @throws RegisterException
	 */
	public DataSetConfig(final Repository repository, final long changeDate,
			final File absPath, final DataSet ds,
			final ConversionInputToStandardConfiguration configInputToStandard,
			final ConversionStandardToInputConfiguration configStandardToInput)
			throws RegisterException {
		super(repository, false, changeDate, absPath);

		this.dataset = ds;
		this.configInputToStandard = configInputToStandard;
		this.configStandardToInput = configStandardToInput;

		if (this.register()) {
			this.dataset.addListener(this);

			// added 21.03.2013: register only, if this dataset config has been
			// registered before
			this.configInputToStandard.getDistanceMeasureAbsoluteToRelative()
					.register();
			this.configInputToStandard.getDistanceMeasureAbsoluteToRelative()
					.addListener(this);
		}
	}

	/**
	 * The copy constructor for dataset configurations.
	 * 
	 * @param datasetConfig
	 *            The dataset configuration to be cloned.
	 * @throws RegisterException
	 */
	public DataSetConfig(DataSetConfig datasetConfig) throws RegisterException {
		super(datasetConfig);

		this.dataset = datasetConfig.dataset.clone();
		this.configInputToStandard = datasetConfig.configInputToStandard
				.clone();
		this.configStandardToInput = datasetConfig.configStandardToInput
				.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.repository.RepositoryObject#clone()
	 */
	@Override
	public DataSetConfig clone() {
		try {
			return new DataSetConfig(this);
		} catch (RegisterException e) {
			// should not occur
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return The dataset, this configuration belongs to.
	 */
	public DataSet getDataSet() {
		return dataset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wiwie.wiutils.utils.RepositoryObject#notify(utils.RepositoryEvent)
	 */
	@Override
	public void notify(RepositoryEvent e) throws RegisterException {
		if (e instanceof RepositoryReplaceEvent) {
			RepositoryReplaceEvent event = (RepositoryReplaceEvent) e;
			if (event.getOld().equals(this)) {
				super.notify(event);
			} else {
				if (event.getOld().equals(dataset)) {
					event.getOld().removeListener(this);
					this.log.info("DataSetConfig "
							+ this.absPath.getName()
							+ ": Dataset reloaded due to modifications in filesystem");
					event.getReplacement().addListener(this);
					// added 06.07.2012
					this.dataset = (DataSet) event.getReplacement();
				}
			}
		} else if (e instanceof RepositoryRemoveEvent) {
			RepositoryRemoveEvent event = (RepositoryRemoveEvent) e;
			if (event.getRemovedObject().equals(this))
				super.notify(event);
			else {
				if (event.getRemovedObject().equals(dataset)) {
					event.getRemovedObject().removeListener(this);
					this.log.info("DataSetConfig " + this
							+ ": Removed, because DataSet " + dataset
							+ " was removed.");
					RepositoryRemoveEvent newEvent = new RepositoryRemoveEvent(
							this);
					this.unregister();
					this.notify(newEvent);
				} else if (this.configInputToStandard
						.getDistanceMeasureAbsoluteToRelative().equals(
								event.getRemovedObject())) {
					event.getRemovedObject().removeListener(this);
					this.log.info("DataSetConfig " + this
							+ ": Removed, because DistanceMeasure "
							+ event.getRemovedObject() + " was removed.");
					RepositoryRemoveEvent newEvent = new RepositoryRemoveEvent(
							this);
					this.unregister();
					this.notify(newEvent);
				}
			}
		} else if (e instanceof RepositoryMoveEvent) {
			RepositoryMoveEvent event = (RepositoryMoveEvent) e;
			if (event.getObject().equals(this)) {
				super.notify(event);
			} else {
				if (event.getObject().equals(dataset)) {
					this.log.info("DataSetConfig " + this.absPath.getName()
							+ ": updated with new dataset path.");
					// dump this dataset config to file with the updated dataset
					// file path.
					try {
						this.dumpToFile();
						// TODO: replace with new exception type
					} catch (RepositoryObjectDumpException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.absPath.getName().replace(".dsconfig", "");
	}

	/**
	 * @return The configuration for conversion from the original input format
	 *         to the standard format.
	 * @see #configInputToStandard
	 */
	public ConversionInputToStandardConfiguration getConversionInputToStandardConfiguration() {
		return this.configInputToStandard;
	}

	/**
	 * @return The configuration for conversion from standard format to the
	 *         input format of the clustering method.
	 * @see #configStandardToInput
	 */
	public ConversionStandardToInputConfiguration getConversionStandardToInputConfiguration() {
		return this.configStandardToInput;
	}

	/**
	 * @param dataset
	 *            The new dataset
	 */
	public void setDataSet(DataSet dataset) {
		this.dataset = dataset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.clusteval.framework.repository.DumpableRepositoryObject#dumpToFile()
	 */
	@Override
	public void dumpToFileHelper() throws RepositoryObjectDumpException {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(this.absPath));
			writer.append("datasetName = " + this.dataset.getMajorName());
			writer.newLine();
			writer.append("datasetFile = " + this.dataset.getMinorName());
			writer.newLine();
			writer.append("distanceMeasureAbsoluteToRelative = "
					+ configInputToStandard
							.getDistanceMeasureAbsoluteToRelative().getClass()
							.getSimpleName());
			writer.newLine();

			String simString = "";
			if (configInputToStandard.getSimilarityPrecision() == NUMBER_PRECISION.DOUBLE)
				simString = "double";
			else if (configInputToStandard.getSimilarityPrecision() == NUMBER_PRECISION.FLOAT)
				simString = "float";
			else if (configInputToStandard.getSimilarityPrecision() == NUMBER_PRECISION.SHORT)
				simString = "short";
			writer.append("similarityPrecision = " + simString);
			writer.newLine();

			StringBuilder sb = new StringBuilder();
			if (!configInputToStandard.getPreprocessorsBeforeDistance()
					.isEmpty()) {
				for (DataPreprocessor pre : configInputToStandard
						.getPreprocessorsBeforeDistance()) {
					sb.append(pre.getClass().getSimpleName());
					sb.append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				writer.append("preprocessorBeforeDistance = " + sb.toString());
				writer.newLine();
			}

			if (!configInputToStandard.getPreprocessorsAfterDistance()
					.isEmpty()) {
				sb = new StringBuilder();
				for (DataPreprocessor pre : configInputToStandard
						.getPreprocessorsAfterDistance()) {
					sb.append(pre.getClass().getSimpleName());
					sb.append(",");
				}
				sb.deleteCharAt(sb.length() - 1);
				writer.append("preprocessorAfterDistance = " + sb.toString());
				writer.newLine();
			}

			writer.close();
		} catch (IOException e) {
			throw new RepositoryObjectDumpException(e);
		}
	}
}
