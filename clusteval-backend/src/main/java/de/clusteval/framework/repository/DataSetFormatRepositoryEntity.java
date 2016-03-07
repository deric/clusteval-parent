/**
 * 
 */
package de.clusteval.framework.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.dataset.format.DataSetFormatParser;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;

/**
 * @author Christian Wiwie
 * 
 */
public class DataSetFormatRepositoryEntity
		extends
			DynamicRepositoryEntity<DataSetFormat> {

	/**
	 * This map holds the current versions of the available dataset formats.
	 */
	protected Map<String, Integer> dataSetFormatCurrentVersions;

	/**
	 * A map containing all classes of dataset format parsers registered in this
	 * repository.
	 */
	protected Map<String, Class<? extends DataSetFormatParser>> dataSetFormatParser;

	/**
	 * @param repository
	 * @param parent
	 * @param basePath
	 */
	public DataSetFormatRepositoryEntity(Repository repository,
			DynamicRepositoryEntity<DataSetFormat> parent, String basePath) {
		super(repository, parent, basePath);
		this.dataSetFormatParser = new ConcurrentHashMap<String, Class<? extends DataSetFormatParser>>();
		this.dataSetFormatCurrentVersions = new HashMap<String, Integer>();
	}

	/**
	 * This method returns the latest and current version of the given format.
	 * It is used by default, if no other version for a format is specified. If
	 * the current version of a format changes, add a static block to that
	 * formats class and overwrite the format version.
	 * 
	 * @param formatClass
	 *            The dataset format class for which we want to know the current
	 *            version.
	 * 
	 * @return The current version for the given dataset format class.
	 * @throws UnknownDataSetFormatException
	 */
	public int getCurrentDataSetFormatVersion(final String formatClass)
			throws UnknownDataSetFormatException {
		if (!dataSetFormatCurrentVersions.containsKey(formatClass))
			throw new UnknownDataSetFormatException("\"" + formatClass
					+ "\" is not a known dataset format.");
		return dataSetFormatCurrentVersions.get(formatClass);
	}

	/**
	 * @param formatClass
	 *            The dataset format class for which we want to set the current
	 *            version.
	 * @param version
	 *            The new version of the dataset format class.
	 */
	public void putCurrentDataSetFormatVersion(final String formatClass,
			final int version) {
		this.dataSetFormatCurrentVersions.put(formatClass, version);
	}

	/**
	 * This method looks up and returns (if it exists) the class of the parser
	 * corresponding to the dataset format with the given name.
	 * 
	 * @param dataSetFormatName
	 *            The name of the class of the dataset format.
	 * @return The class of the dataset format parser with the given name or
	 *         null, if it does not exist.
	 */
	public Class<? extends DataSetFormatParser> getDataSetFormatParser(
			final String dataSetFormatName) {
		Class<? extends DataSetFormatParser> result = this.dataSetFormatParser
				.get(dataSetFormatName);
		if (result == null && parent != null)
			return ((DataSetFormatRepositoryEntity) this.parent)
					.getDataSetFormatParser(dataSetFormatName);
		return result;
	}

	/**
	 * This method registers a dataset format parser.
	 * 
	 * @param dsFormatParser
	 *            The dataset format parser to register.
	 * @return True, if the dataset format parser replaced an old object.
	 */
	public boolean registerDataSetFormatParser(
			final Class<? extends DataSetFormatParser> dsFormatParser) {
		this.dataSetFormatParser.put(
				dsFormatParser.getName().replace("Parser", ""), dsFormatParser);
		return true;
	}

	/**
	 * This method checks whether a parser has been registered for the given
	 * dataset format class.
	 * 
	 * @param dsFormat
	 *            The class for which we want to know whether a parser has been
	 *            registered.
	 * @return True, if the parser has been registered.
	 */
	public boolean isRegisteredForDataSetFormat(
			final Class<? extends DataSetFormat> dsFormat) {
		return this.dataSetFormatParser.containsKey(dsFormat.getName())
				|| (this.parent != null && ((DataSetFormatRepositoryEntity) this.parent)
						.isRegisteredForDataSetFormat(dsFormat));
	}
}
