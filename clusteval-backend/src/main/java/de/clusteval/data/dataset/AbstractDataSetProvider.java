/**
 * 
 */
package de.clusteval.data.dataset;

import java.io.File;
import java.util.ArrayList;

import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.data.dataset.format.ConversionStandardToInputConfiguration;
import de.clusteval.data.distance.DistanceMeasure;
import de.clusteval.data.distance.UnknownDistanceMeasureException;
import de.clusteval.data.goldstandard.GoldStandard;
import de.clusteval.data.goldstandard.GoldStandardConfig;
import de.clusteval.data.preprocessing.DataPreprocessor;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryObject;
import de.clusteval.framework.repository.RepositoryObjectDumpException;
import de.wiwie.wiutils.file.FileUtils;
import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;

/**
 * Subclasses of this abstract class correspond to objects, that provide in some
 * way data sets to the framework. For example by generating new ones
 * (generators) or by deriving them from existing ones (e.g. randomizer).
 * 
 * This class encapsulate the logic that is common to all these data set
 * providers, e.g. the logic to write configuration files for the newly
 * generated data set.
 * 
 * @author Christian Wiwie
 *
 */
public abstract class AbstractDataSetProvider extends RepositoryObject {

	/**
	 * @param repository
	 * @param register
	 * @param changeDate
	 * @param absPath
	 * @throws RegisterException
	 */
	public AbstractDataSetProvider(Repository repository, boolean register, long changeDate, File absPath)
			throws RegisterException {
		super(repository, register, changeDate, absPath);
	}

	/**
	 * The copy constructor of dataset generators.
	 * 
	 * @param other
	 *            The object to clone.
	 * @throws RegisterException
	 */
	public AbstractDataSetProvider(AbstractDataSetProvider other) throws RegisterException {
		super(other);
	}

	protected DataConfig writeConfigFiles(final DataSet newDataSet, final GoldStandard newGoldStandard,
			final String configFileName)
					throws RepositoryObjectDumpException, RegisterException, UnknownDistanceMeasureException {
		// write dataset config file
		File dsConfigFile = new File(
				FileUtils.buildPath(repository.getBasePath(DataSetConfig.class), configFileName + ".dsconfig"));
		DataSetConfig dsConfig = new DataSetConfig(this.repository, System.currentTimeMillis(), dsConfigFile,
				newDataSet,
				new ConversionInputToStandardConfiguration(
						DistanceMeasure.parseFromString(repository, "EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE, new ArrayList<DataPreprocessor>(), new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());

		dsConfig.dumpToFile();

		GoldStandardConfig gsConfig = null;

		if (newGoldStandard != null) {

			File gsConfigFile = new File(FileUtils.buildPath(repository.getBasePath(GoldStandardConfig.class),
					configFileName + ".gsconfig"));

			// write goldstandard config file
			gsConfig = new GoldStandardConfig(this.repository, System.currentTimeMillis(), gsConfigFile,
					newGoldStandard);

			gsConfig.dumpToFile();
		}

		// write data config file
		File dataConfigFile = new File(
				FileUtils.buildPath(repository.getBasePath(DataConfig.class), configFileName + ".dataconfig"));
		DataConfig dataConfig = new DataConfig(this.repository, System.currentTimeMillis(), dataConfigFile, dsConfig,
				gsConfig);

		dataConfig.dumpToFile();

		return dataConfig;
	}
}
