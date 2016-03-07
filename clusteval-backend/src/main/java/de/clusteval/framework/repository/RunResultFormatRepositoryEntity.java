/**
 * 
 */
package de.clusteval.framework.repository;

import java.util.HashMap;
import java.util.Map;

import de.clusteval.run.result.format.RunResultFormat;
import de.clusteval.run.result.format.RunResultFormatParser;

/**
 * @author Christian Wiwie
 * 
 */
public class RunResultFormatRepositoryEntity
		extends
			DynamicRepositoryEntity<RunResultFormat> {

	/**
	 * A map containing all runresult format parsers registered in this
	 * repository.
	 */
	protected Map<String, Class<? extends RunResultFormatParser>> runResultFormatParser;

	/**
	 * @param repository
	 * @param parent
	 * @param basePath
	 */
	public RunResultFormatRepositoryEntity(Repository repository,
			DynamicRepositoryEntity<RunResultFormat> parent, String basePath) {
		super(repository, parent, basePath);
		this.runResultFormatParser = new HashMap<String, Class<? extends RunResultFormatParser>>();
	}

	/**
	 * This method looks up and returns (if it exists) the class of the
	 * runresult format parser corresponding to the runresult format with the
	 * given name.
	 * 
	 * @param runResultFormatName
	 *            The runresult format name.
	 * @return The runresult format parser for the given runresult format name,
	 *         or null if it does not exist.
	 */
	public Class<? extends RunResultFormatParser> getRunResultFormatParser(
			final String runResultFormatName) {
		Class<? extends RunResultFormatParser> result = this.runResultFormatParser
				.get(runResultFormatName);
		if (result == null && parent != null)
			result = ((RunResultFormatRepositoryEntity) parent)
					.getRunResultFormatParser(runResultFormatName);
		return result;
	}

	/**
	 * This method checks whether a parser has been registered for the given
	 * runresult format class.
	 * 
	 * @param runResultFormat
	 *            The class for which we want to know whether a parser has been
	 *            registered.
	 * @return True, if the parser has been registered.
	 */
	public boolean isRegisteredForRunResultFormat(
			final Class<? extends RunResultFormat> runResultFormat) {
		return this.runResultFormatParser
				.containsKey(runResultFormat.getName())
				|| (this.parent != null && ((RunResultFormatRepositoryEntity) this.parent)
						.isRegisteredForRunResultFormat(runResultFormat));
	}

	/**
	 * This method checks whether a parser has been registered for the dataset
	 * format with the given class name.
	 * 
	 * @param runResultFormatName
	 *            The class for which we want to know whether a parser has been
	 *            registered.
	 * @return True, if the parser has been registered.
	 */
	public boolean isRegisteredForRunResultFormat(
			final String runResultFormatName) {
		return this.runResultFormatParser.containsKey(runResultFormatName)
				|| (this.parent != null && ((RunResultFormatRepositoryEntity) this.parent)
						.isRegisteredForRunResultFormat(runResultFormatName));
	}

	/**
	 * This method registers a new runresult format parser class.
	 * 
	 * @param runResultFormatParser
	 *            The new class to register.
	 * @return True, if the new class replaced an old one.
	 */
	public boolean registerRunResultFormatParser(
			final Class<? extends RunResultFormatParser> runResultFormatParser) {
		this.runResultFormatParser.put(
				runResultFormatParser.getName().replace("Parser", ""),
				runResultFormatParser);
		return true;
	}

	/**
	 * This method unregisters the passed object.
	 * 
	 * @param object
	 *            The object to be removed.
	 * @return True, if the object was remved successfully
	 */
	public boolean unregisterRunResultFormatParser(
			final Class<? extends RunResultFormatParser> object) {
		return this.runResultFormatParser.remove(object.getName().replace(
				"Parser", "")) != null;
	}
}
