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
package de.clusteval.framework.repository;

import de.clusteval.cluster.Clustering;
import de.clusteval.cluster.paramOptimization.ParameterOptimizationMethod;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.context.Context;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetConfig;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.dataset.format.DataSetFormatParser;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.dataset.generator.DataSetGenerator;
import de.clusteval.data.dataset.type.DataSetType;
import de.clusteval.data.distance.DistanceMeasure;
import de.clusteval.data.goldstandard.GoldStandard;
import de.clusteval.data.goldstandard.GoldStandardConfig;
import de.clusteval.data.goldstandard.format.GoldStandardFormat;
import de.clusteval.data.preprocessing.DataPreprocessor;
import de.clusteval.data.randomizer.DataRandomizer;
import de.clusteval.data.statistics.DataStatistic;
import de.clusteval.data.statistics.DataStatisticCalculator;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.RLibraryNotLoadedException;
import de.clusteval.framework.repository.config.DefaultRepositoryConfig;
import de.clusteval.framework.repository.config.RepositoryConfig;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.db.DatabaseConnectException;
import de.clusteval.framework.repository.db.DefaultSQLCommunicator;
import de.clusteval.framework.repository.db.RunResultSQLCommunicator;
import de.clusteval.framework.repository.db.SQLCommunicator;
import de.clusteval.framework.repository.db.StubSQLCommunicator;
import de.clusteval.framework.threading.RepositorySupervisorThread;
import de.clusteval.framework.threading.RunResultRepositorySupervisorThread;
import de.clusteval.framework.threading.SupervisorThread;
import de.clusteval.program.DoubleProgramParameter;
import de.clusteval.program.IntegerProgramParameter;
import de.clusteval.program.Program;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.program.StringProgramParameter;
import de.clusteval.program.r.RProgram;
import de.clusteval.run.Run;
import de.clusteval.run.result.RunResult;
import de.clusteval.run.result.format.RunResultFormat;
import de.clusteval.run.result.format.RunResultFormatParser;
import de.clusteval.run.result.postprocessing.RunResultPostprocessor;
import de.clusteval.run.statistics.RunDataStatistic;
import de.clusteval.run.statistics.RunDataStatisticCalculator;
import de.clusteval.run.statistics.RunStatistic;
import de.clusteval.run.statistics.RunStatisticCalculator;
import de.clusteval.utils.Finder;
import de.clusteval.utils.InternalAttributeException;
import de.clusteval.utils.NamedDoubleAttribute;
import de.clusteval.utils.NamedIntegerAttribute;
import de.clusteval.utils.NamedStringAttribute;
import de.wiwie.wiutils.file.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The repository is the central object of the backend, where objects are
 * registered and centrally controlled. Objects can be registered and
 * unregistered and get certain functions for free. For example duplication
 * recognition, automatic detection of changes of objects and informing other
 * objects (as listeners) about changes of other objects.
 *
 * <p>
 * General hint: This class contains a lot of hashmaps for performance reasons.
 * All the hashmaps of this class are updated with current objects. The maps
 * then contain old key objects and current value objects. Therefore you should
 * never iterate over the result of keySet() of the maps, but instead use
 * values().
 *
 * @author Christian Wiwie
 *
 */
public class Repository {

	/**
	 * A map containing all repository objects. This includes this repository
	 * but also all run result repositories or other child repositories, that
	 * are contained within this repository.
	 */
	protected static Map<String, Repository> repositories = new HashMap<String, Repository>();

	/**
	 * This method returns a repository (if available) with the given root path.
	 *
	 * @param absFilePath
	 *            The absolute root path of the repository.
	 * @return The repository with the given root path.
	 */
	public static Repository getRepositoryForExactPath(final String absFilePath) {
		return Repository.repositories.get(absFilePath);
	}

	/**
	 * This method returns the lowest repository in repository-hierarchy, that
	 * contains the given path. That means, if there are several nested
	 * repositories for the given path, this method will return the lowest one
	 * of the hierarchy.
	 *
	 * @param absFilePath
	 *            The absolute file path we want to find the repository for.
	 * @return The repository for the given path, which is lowest in the
	 *         repository-hierarchy.
	 * @throws NoRepositoryFoundException
	 */
	public static Repository getRepositoryForPath(final String absFilePath) throws NoRepositoryFoundException {
		String resultPath = null;
		for (String repoPath : Repository.repositories.keySet())
			if (absFilePath.startsWith(repoPath + System.getProperty("file.separator")))
				if (resultPath == null || repoPath.length() > resultPath.length())
					resultPath = repoPath;
		if (resultPath == null)
			throw new NoRepositoryFoundException(absFilePath);
		return Repository.repositories.get(resultPath);
	}

	/**
	 * This method checks, whether the given string represents an internal
	 * attribute placeholder, that means it follows the format of
	 * {@value #internalAttributePattern}.
	 *
	 * @param value
	 *            The string to check whether it is a internal attribute.
	 * @return True, if the given string is an internal attribute, false
	 *         otherwise.
	 */
	public static boolean isInternalAttribute(final String value) {
		Pattern p = internalAttributePattern;
		return p.matcher(value).matches();
	}

	/**
	 * Register a new repository.
	 *
	 * @param repository
	 *            The new repository to register.
	 * @return The old repository, if the new repository replaced an old one
	 *         with equal root path. Null otherwise.
	 * @throws RepositoryAlreadyExistsException
	 * @throws InvalidRepositoryException
	 */
	public static Repository register(Repository repository)
			throws RepositoryAlreadyExistsException, InvalidRepositoryException {
		Repository other = null;
		try {
			other = Repository.getRepositoryForPath(repository.basePath);
		} catch (NoRepositoryFoundException e) {
		}
		if (other == null)
			return Repository.repositories.put(repository.basePath, repository);
		if (other.basePath.equals(repository.basePath))
			throw new RepositoryAlreadyExistsException(other.basePath);
		if (repository.parent == null || !repository.parent.equals(other))
			throw new InvalidRepositoryException("Repositories must not be nested without parental relationship");
		return Repository.repositories.put(repository.basePath, repository);
	}

	/**
	 * Unregister the given repository.
	 *
	 * @param repository
	 *            The repository to remove.
	 * @return The removed repository. If null, the given repository was not
	 *         registered.
	 */
	public static Repository unregister(Repository repository) {
		return Repository.repositories.remove(repository.basePath);
	}

	/**
	 * In case the backend is connected to a mysql database in the frontend,
	 * this attribute is set to a sql communicator, which updates the database
	 * after changes of repository objects (removal, addition).
	 */
	protected SQLCommunicator sqlCommunicator;

	/**
	 * The supervisor thread is responsible for starting and keeping alive all
	 * threads that check the repository on the filesystem for changes.
	 */
	protected SupervisorThread supervisorThread;

	/**
	 * A repository can have a parent repository, which means, that the root
	 * folder of this repository is located inside the parent repository.
	 *
	 * <p>
	 * As a consequence if a child repository cannot complete a lookup operation
	 * sucessfully, that means cannot find a certain object, it will also look
	 * for this object in the parent repository.
	 *
	 * <p>
	 * This relationship is only allowed (located inside a subfolder), if the
	 * parental relationship is indicated by setting this parent repository
	 * attribute.
	 */
	protected Repository parent;

	/**
	 * The absolute path of the root of this repository.
	 */
    public String basePath;

	/**
	 * The absolute path to the directory within this repository, where all
	 * generators are stored.
	 */
	protected String generatorBasePath;

	/**
	 * The absolute path to the directory within this repository, where all
	 * randomizers are stored.
	 */
	protected String randomizerBasePath;

	/**
	 * The absolute path to the directory, where for a certain runresult
	 * (identified by its unique run identifier) all analysis results are
	 * stored.
	 */
	protected String analysisResultsBasePath;

	/**
	 * The absolute path to the directory within this repository, where all
	 * supplementary materials are stored.
	 *
	 * <p>
	 * Supplementary materials contain e.g. jar files of parameter optimization
	 * methods or clustering quality measures.
	 */
	protected String supplementaryBasePath;

	/**
	 * The absolute path to the directory within this repository, where all
	 * supplementary materials related to clustering are stored.
	 */
	protected String suppClusteringBasePath;

	/**
	 * The absolute path to the directory within this repository, where all type
	 * jars are stored.
	 */
	protected String typesBasePath;

	/**
	 * The absolute path to the directory within this repository, where all
	 * format jars are stored, e.g. dataset formats.
	 */
	protected String formatsBasePath;

	/**
	 * This map contains the absolute path of every repository object registered
	 * in this repository and maps it to the object itself.
	 */
	protected Map<File, RepositoryObject> pathToRepositoryObject;

	protected StaticRepositoryEntityMap staticRepositoryEntities;

	protected DynamicRepositoryEntityMap dynamicRepositoryEntities;

	/**
	 * A map containing all goldstandard formats registered in this repository.
	 */
	protected Map<GoldStandardFormat, GoldStandardFormat> goldStandardFormats;

	/**
	 * The pattern that is used to scan a string ofr internal attribute
	 * placeholders in {@link #isInternalAttribute(String)}.
	 */
	protected static Pattern internalAttributePattern = Pattern.compile("\\$\\([^\\$)]*\\)");

	/**
	 * This map holds all available internal float attributes, which can be used
	 * by any kind of configuration file as a option value, which is not
	 * available before starting of a run.
	 */
	protected Map<String, NamedDoubleAttribute> internalDoubleAttributes;

	/**
	 * This map holds all available internal string attributes, which can be
	 * used by any kind of configuration file as a option value, which is not
	 * available before starting of a run.
	 */
	protected Map<String, NamedStringAttribute> internalStringAttributes;

	/**
	 * This map holds all available internal integer attributes, which can be
	 * used by any kind of configuration file as a option value, which is not
	 * available before starting of a run.
	 */
	protected Map<String, NamedIntegerAttribute> internalIntegerAttributes;

    public Logger log;

	/**
	 * The configuration of this repository holds options that can specify the
	 * behaviour of this repository. For example it can be specified, whether
	 * the repository should communicate and insert its information into a sql
	 * database.
	 */
	protected RepositoryConfig repositoryConfig;

	/**
	 * This attribute maps the names of a class to all exceptions of required R
	 * libraries that could not be loaded.
	 */
	protected Map<String, Set<RLibraryNotLoadedException>> missingRLibraries;

	/**
	 * All exceptions thrown during parsing of finder instances are being
	 * inserted into this map. New exceptions with messages equal to messages of
	 * exceptions in this list will not be thrown again.
	 */
	protected Map<String, List<Throwable>> knownFinderExceptions;

	/**
	 * The class loaders used by the finders to load classes dynamically.
	 */
	protected Map<URL, URLClassLoader> finderClassLoaders;

	/**
	 * A map containing dependencies between jar files that are loaded
	 * dynamically.
	 */
	protected Map<File, List<File>> finderWaitingFiles;

	/**
	 * The change dates of the jar files that were loaded dynamically by jar
	 * finder instances.
	 */
	protected Map<String, Long> finderLoadedJarFileChangeDates;

	private Map<Thread, MyRengine> rEngines;

	/**
	 * Instantiates a new repository.
	 *
	 * @param parent
	 *            Can be null, if this repository has no parent repository.
	 * @param basePath
	 *            The absolute path of the root of this repository.
	 * @throws FileNotFoundException
	 * @throws InvalidRepositoryException
	 * @throws RepositoryAlreadyExistsException
	 * @throws RepositoryConfigurationException
	 * @throws RepositoryConfigNotFoundException
	 * @throws DatabaseConnectException
	 */
	public Repository(final String basePath, final Repository parent)
			throws FileNotFoundException, RepositoryAlreadyExistsException, InvalidRepositoryException,
			RepositoryConfigNotFoundException, RepositoryConfigurationException, DatabaseConnectException {
		this(basePath, parent, null);
	}

	/**
	 * Instantiates a new repository.
	 *
	 * @param parent
	 *            Can be null, if this repository has no parent repository.
	 * @param basePath
	 *            The absolute path of the root of this repository.
	 * @param overrideConfig
	 *            Set this parameter != null, if you want to override the
	 *            repository.config file.
	 * @throws FileNotFoundException
	 * @throws InvalidRepositoryException
	 * @throws RepositoryAlreadyExistsException
	 * @throws RepositoryConfigurationException
	 * @throws RepositoryConfigNotFoundException
	 * @throws DatabaseConnectException
	 */
	public Repository(final String basePath, final Repository parent, final RepositoryConfig overrideConfig)
			throws FileNotFoundException, RepositoryAlreadyExistsException, InvalidRepositoryException,
			RepositoryConfigNotFoundException, RepositoryConfigurationException, DatabaseConnectException {
		super();

		this.log = LoggerFactory.getLogger(this.getClass());

		this.basePath = basePath;
		// remove trailing file separator
		if (this.basePath.length() > 1 && this.basePath.endsWith(System.getProperty("file.separator")))
			this.basePath = this.basePath.substring(0,
					this.basePath.length() - System.getProperty("file.separator").length());
		this.parent = parent;
		this.missingRLibraries = new ConcurrentHashMap<String, Set<RLibraryNotLoadedException>>();

		this.initializePaths();

		this.initAttributes();

		this.ensureFolderStructure();

		this.pathToRepositoryObject = new ConcurrentHashMap<File, RepositoryObject>();

		File repositoryConfigFile = new File(FileUtils.buildPath(this.basePath, "repository.config"));

		Repository.register(this);

		if (overrideConfig != null)
			this.repositoryConfig = overrideConfig;
		else if (repositoryConfigFile.exists()) {
			/*
			 * Parsing the configuration file (if it exists)
			 */
			this.repositoryConfig = RepositoryConfig.parseFromFile(repositoryConfigFile);
			this.log.debug("Using repository configuration: " + repositoryConfigFile);
		} else {
			this.repositoryConfig = new DefaultRepositoryConfig();
			this.log.debug("Using default repository configuration");
		}

		this.sqlCommunicator = createSQLCommunicator();

		// try {
		// this.rEngineForLibraryInstalledChecks = new REnginePool(5);
		// } catch (RserveException e) {
		// // if there is no R, we will not be using this field in the future
		// this.rEngineForLibraryInstalledChecks = null;
		// this.rEngineException = e;
		// }
		this.rEngines = new HashMap<Thread, MyRengine>();
	}

	/**
	 * @param e
	 *            The new exception to add.
	 * @return A boolean indicating, whether the exception was new.
	 */
	public boolean addMissingRLibraryException(RLibraryNotLoadedException e) {
		if (!(this.missingRLibraries.containsKey(e.getClassName())))
			this.missingRLibraries.put(e.getClassName(),
					Collections.synchronizedSet(new HashSet<RLibraryNotLoadedException>()));
		return this.missingRLibraries.get(e.getClassName()).add(e);
	}

	/**
	 * This method clears the existing exceptions for missing R libraries for
	 * the given class name.
	 *
	 * @param className
	 *            The class name for which we want to clear the missing
	 *            libraries.
	 * @return The old exceptions that were present for this class.
	 */
	public Set<RLibraryNotLoadedException> clearMissingRLibraries(String className) {
		return this.missingRLibraries.remove(className);
	}

	/**
	 * This method is a helper method for sql communication. The sql
	 * communicator usually does not commit after every change. Therefore we
	 * provide this method, to allow for commiting at certain points such that
	 * we can afterwards guarantee a certain state of the DB and operate on it.
	 */
	public void commitDB() {
		synchronized (this.sqlCommunicator) {
			this.sqlCommunicator.commitDB();
		}
	}

	/**
	 * This method creates a sql communicator for this repository depending on
	 * the fact, whether mysql should be used by this repository.
	 *
	 * <p>
	 * Override this method in subclasses, if you want to change the type of sql
	 * communicator for your subtype. You can see an example in
	 * {@link RunResultRepository#createSQLCommunicator()}, where instead of
	 * {@link DefaultSQLCommunicator} a {@link RunResultSQLCommunicator} is
	 * created.
	 *
	 * @return A new instance of sql communicator.
	 * @throws DatabaseConnectException
	 */
	protected SQLCommunicator createSQLCommunicator() throws DatabaseConnectException {
		if (this.repositoryConfig.getMysqlConfig().usesSql())
			return new DefaultSQLCommunicator(this, this.repositoryConfig.getMysqlConfig());

		return new StubSQLCommunicator(this);
	}

	/**
	 * This method creates the supervisor thread object for this repository.
	 *
	 * <p>
	 * Override this method in subclasses, if you want to change the type of
	 * supervisor thread for your subtype. You can see an example in
	 * {@link RunResultRepository#createSupervisorThread()}, where instead of a
	 * {@link RepositorySupervisorThread} a
	 * {@link RunResultRepositorySupervisorThread} is created.
	 *
	 * @return
	 */
	protected SupervisorThread createSupervisorThread() {
		return new RepositorySupervisorThread(this, this.repositoryConfig.getThreadSleepTimes(), false,
				ClustevalBackendServer.getBackendServerConfiguration().getCheckForRunResults());
	}

	/**
	 * Helper method of {@link #ensureFolderStructure()}, which ensures that a
	 * single folder exists.
	 *
	 * @param absFolderPath
	 *            The absolute path of the folder to ensure.
	 * @return true, if successful
	 * @throws FileNotFoundException
	 */
	private boolean ensureFolder(final String absFolderPath) throws FileNotFoundException {
		final File folder = new File(absFolderPath);
		if (!(folder.exists())) {
			folder.mkdirs();
			this.info("Recreating repository folder " + folder);
		}
		if (!(folder.exists()))
			throw new FileNotFoundException("Could not create folder " + folder.getAbsolutePath());
		return true;
	}

	/**
	 * This method ensures the complete folder structure of this repository. If
	 * a folder does not exist, it is recreated. In case a folder creation is
	 * not successful an exception is thrown.
	 *
	 * <p>
	 * A helper method of
	 * {@link #Repository(String, Repository, long, long, long, long, long, long, long)}
	 * .
	 *
	 * @return true, if successful
	 * @throws FileNotFoundException
	 */
	private boolean ensureFolderStructure() throws FileNotFoundException {
		// TODO: replace by for loop over entries of #repositoryObjectEntities
		this.ensureFolder(this.basePath);
		this.ensureFolder(this.getBasePath(DataConfig.class));
		this.ensureFolder(this.getBasePath(DataSet.class));
		this.ensureFolder(this.getBasePath(DataSetFormat.class));
		this.ensureFolder(this.getBasePath(DataSetType.class));
		this.ensureFolder(this.getBasePath(DataSetConfig.class));
		this.ensureFolder(this.getBasePath(GoldStandard.class));
		this.ensureFolder(this.getBasePath(GoldStandardConfig.class));
		this.ensureFolder(this.getBasePath(Program.class));
		this.ensureFolder(this.getBasePath(ProgramConfig.class));
		this.ensureFolder(this.getBasePath(Run.class));
		this.ensureFolder(this.getBasePath(RunResult.class));
		this.ensureFolder(this.getBasePath(RunResultFormat.class));
		this.ensureFolder(this.supplementaryBasePath);
		this.ensureFolder(this.suppClusteringBasePath);
		this.ensureFolder(this.getBasePath(Context.class));
		this.ensureFolder(this.getBasePath(ParameterOptimizationMethod.class));
		this.ensureFolder(this.getBasePath(DataStatistic.class));
		this.ensureFolder(this.getBasePath(RunStatistic.class));
		this.ensureFolder(this.getBasePath(RunDataStatistic.class));
		this.ensureFolder(this.getBasePath(DistanceMeasure.class));
		this.ensureFolder(this.generatorBasePath);
		this.ensureFolder(this.getBasePath(DataSetGenerator.class));
		this.ensureFolder(this.getBasePath(DataRandomizer.class));
		this.ensureFolder(this.getBasePath(DataPreprocessor.class));

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Repository))
			return false;

		Repository repo = (Repository) obj;

		return this.basePath.equals(repo.basePath);
	}

	/**
	 * This method evaluates all internal attribute placeholders contained in
	 * the passed string.
	 *
	 * @param old
	 *            The string which might contain internal attribute
	 *            placeholders.
	 * @param dataConfig
	 *            The data configuration which might be needed to evaluate the
	 *            placeholders.
	 * @param programConfig
	 *            The program configuration which might be needed to evaluate
	 *            the placeholders.
	 * @return The parameter value with evaluated placeholders.
	 * @throws InternalAttributeException
	 */
	public String evaluateInternalAttributes(final String old, final DataConfig dataConfig,
			final ProgramConfig programConfig) throws InternalAttributeException {

		final String extended = extendInternalAttributes(old, dataConfig, programConfig);

		StringBuilder result = new StringBuilder(extended);
		int pos = -1;
		while ((pos = result.indexOf("$(")) != -1) {
			int endPos = result.indexOf(")", pos + 1);

			String attributeName = result.substring(pos + 2, endPos);
			String replaceValue;
			if (this.internalDoubleAttributes.containsKey(attributeName)) {
				replaceValue = this.internalDoubleAttributes.get(attributeName) + "";
			} else if (this.internalIntegerAttributes.containsKey(attributeName)) {
				replaceValue = this.internalIntegerAttributes.get(attributeName) + "";
			} else if (this.internalStringAttributes.containsKey(attributeName)) {
				replaceValue = this.internalStringAttributes.get(attributeName) + "";
			} else {
				throw new InternalAttributeException("The internal attribute " + attributeName + " does not exist.");
			}
			result.replace(pos, endPos + 1, replaceValue);
		}

		return result.toString();
	}

	/**
	 * This method is used to evaluate parameter values containing javascript
	 * arithmetic operations.
	 *
	 * <p>
	 * A helper method of
	 * {@link ProgramParameter#evaluateDefaultValue(DataConfig, ProgramConfig)},
	 * {@link ProgramParameter#evaluateMinValue(DataConfig, ProgramConfig)} and
	 * {@link ProgramParameter#evaluateMaxValue(DataConfig, ProgramConfig)}.
	 *
	 * @param script
	 *            The parameter value containing javascript arithmetic
	 *            operations.
	 * @return The evaluated expression.
	 * @throws ScriptException
	 */
	public String evaluateJavaScript(final String script) throws ScriptException {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");
		// define min function
		engine.eval("function min(n1,n2) {" + "if(n1 <= n2) return n1; " + "  else return n2; " + "};");
		// define max function
		engine.eval("function max(n1,n2) {" + "if(n1 >= n2) return n1; " + "  else return n2; " + "};");
		return engine.eval(script) + "";
	}

	/**
	 * This method prefixes placeholder of internal attributes with their data
	 * configuration such that they can be replaced in an unambigious way later
	 * in {@link Repository#evaluateInternalAttributes(String)}.
	 *
	 * <p>
	 * A helper method for
	 * {@link #evaluateInternalAttributes(String, DataConfig, ProgramConfig)}.
	 *
	 * @param old
	 *            The parameter value that might contain placeholders which need
	 *            to be extended.
	 * @param dataConfig
	 *            The data configuration which might be needed to extend the
	 *            placeholders.
	 * @param programConfig
	 *            The program configuration which might be needed to extend the
	 *            placeholders.
	 * @return The parameter value with extended placeholders.
	 */
	@SuppressWarnings("unused")
	private String extendInternalAttributes(final String old, final DataConfig dataConfig,
			final ProgramConfig programConfig) {
		String result = old.replaceAll("\\$\\(minSimilarity\\)",
				"\\$\\(" + dataConfig.getDatasetConfig().getDataSet().getOriginalDataSet().getAbsolutePath()
						+ ":minSimilarity\\)");
		result = result.replaceAll("\\$\\(maxSimilarity\\)",
				"\\$\\(" + dataConfig.getDatasetConfig().getDataSet().getOriginalDataSet().getAbsolutePath()
						+ ":maxSimilarity\\)");
		result = result.replaceAll("\\$\\(meanSimilarity\\)",
				"\\$\\(" + dataConfig.getDatasetConfig().getDataSet().getOriginalDataSet().getAbsolutePath()
						+ ":meanSimilarity\\)");
		result = result.replaceAll("\\$\\(numberOfElements\\)",
				"\\$\\(" + dataConfig.getDatasetConfig().getDataSet().getOriginalDataSet().getAbsolutePath()
						+ ":numberOfElements\\)");
		return result;
	}

	/**
	 * @throws InterruptedException
	 */
	public void terminateSupervisorThread() throws InterruptedException {
		this.terminateSupervisorThread(false);
	}

	public void terminateSupervisorThread(final boolean closeRengines) throws InterruptedException {
		if (closeRengines) {
			// close Rengine pool
			// this.rEngineForLibraryInstalledChecks.close();
			for (MyRengine rEngine : this.rEngines.values()) {
				rEngine.close();
				// rEngine.shutdown();
			}
			this.rEngines.clear();
		}

		// terminate supervisor thread
		if (this.supervisorThread == null)
			return;
		this.supervisorThread.interrupt();
		this.supervisorThread.join();
	}

	/**
	 * @return The absolute path to the root of this repository.
	 */
	public String getBasePath() {
		return this.basePath;
	}

	public String getBasePath(final Class<? extends RepositoryObject> c) {
		if (this.staticRepositoryEntities.containsKey(c))
			return this.staticRepositoryEntities.get(c).getBasePath();
		return this.dynamicRepositoryEntities.get(c).getBasePath();
	}

	public <T extends RepositoryObject> Collection<T> getCollectionStaticEntities(final Class<T> c) {
		return this.staticRepositoryEntities.get(c).asCollection();
	}

	public <T extends RepositoryObject> T getStaticObjectWithName(final Class<T> c, final String name) {
		return this.staticRepositoryEntities.get(c).findByString(name);
	}

	public boolean isInitialized(final Class<? extends RepositoryObject> c) {
		if (this.staticRepositoryEntities.containsKey(c))
			return this.staticRepositoryEntities.get(c).isInitialized();
		return this.dynamicRepositoryEntities.get(c).isInitialized();
	}

	public <T extends RepositoryObject> T getRegisteredObject(final T object) {
		return this.getRegisteredObject(object, true);
	}

	public <T extends RepositoryObject> T getRegisteredObject(final T object, final boolean ignoreChangeDate) {
		@SuppressWarnings("unchecked")
		Class<T> c = (Class<T>) object.getClass();
		return this.getRegisteredObject(c, object, ignoreChangeDate);
	}

	public <T extends RepositoryObject, S extends T> S getRegisteredObject(final Class<T> c, final S object,
			final boolean ignoreChangeDate) {
		boolean staticEntityFound = false;
		boolean dynamicEntityFound = false;
		if (!((staticEntityFound = this.staticRepositoryEntities.containsKey(c))
				|| (dynamicEntityFound = this.dynamicRepositoryEntities.containsKey(c)))
				&& object.getClass().getSuperclass() != null
				&& RepositoryObject.class.isAssignableFrom(c.getSuperclass())) {
                    return this.getRegisteredObject((Class<RepositoryObject>) c.getSuperclass(), object,
                       					ignoreChangeDate);
		}
		if (staticEntityFound)
			return this.staticRepositoryEntities.get(c).getRegisteredObject(object, ignoreChangeDate);
		else if (dynamicEntityFound)
			return this.dynamicRepositoryEntities.get(c).getRegisteredObject(object, ignoreChangeDate);
		return null;
	}

	public <T extends RepositoryObject, S extends T> boolean unregister(final S object) {
		@SuppressWarnings("unchecked")
		Class<S> c = (Class<S>) object.getClass();
		return this.unregister(c, object);
	}

	@SuppressWarnings("unchecked")
	public <T extends RepositoryObject, S extends T> boolean unregister(final Class<T> c, final S object) {
		boolean staticEntityFound = false;
		boolean dynamicEntityFound = false;
		if (!((staticEntityFound = this.staticRepositoryEntities.containsKey(c))
				|| (dynamicEntityFound = this.dynamicRepositoryEntities.containsKey(c)))
				&& object.getClass().getSuperclass() != null
				&& RepositoryObject.class.isAssignableFrom(c.getSuperclass())) {
                    return this.unregister((Class<RepositoryObject>) c.getSuperclass(), object);
		}

		if (staticEntityFound)
			return this.staticRepositoryEntities.get(c).unregister(object);
		else if (dynamicEntityFound)
			return this.dynamicRepositoryEntities.get(c).unregister(object);
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T extends RepositoryObject, S extends T> boolean register(final S object) throws RegisterException {
		Class<S> c = (Class<S>) object.getClass();
		return this.register(c, object);
	}

	@SuppressWarnings("unchecked")
	public <T extends RepositoryObject, S extends T> boolean register(final Class<T> c, final S object)
			throws RegisterException {
		boolean staticEntityFound = false;
		boolean dynamicEntityFound = false;
		if (!((staticEntityFound = this.staticRepositoryEntities.containsKey(c))
				|| (dynamicEntityFound = this.dynamicRepositoryEntities.containsKey(c)))
				&& object.getClass().getSuperclass() != null
				&& RepositoryObject.class.isAssignableFrom(c.getSuperclass())) {
			// we only return, if we found the right class
			return this.register((Class<RepositoryObject>) c.getSuperclass(), object);
		}

		if (staticEntityFound)
			return this.staticRepositoryEntities.get(c).register(object);
		else if (dynamicEntityFound)
			return this.dynamicRepositoryEntities.get(c).register(object);
		return false;
	}

	public <T extends RepositoryObject> void setInitialized(final Class<T> c) {
		if (this.staticRepositoryEntities.containsKey(c))
			this.staticRepositoryEntities.get(c).setInitialized();
		else
			this.dynamicRepositoryEntities.get(c).setInitialized();
	}

	public <T extends RepositoryObject> boolean isClassRegistered(final Class<T> c) {
		return this.isClassRegistered(c, c.getSimpleName());
	}

	public <T extends RepositoryObject> boolean isClassRegistered(final String classFullName) {
		return DynamicRepositoryEntity.isClassAvailable(classFullName);
	}

	public <T extends RepositoryObject> boolean isClassRegistered(final Class<T> base, final String classSimpleName) {
		if (!this.dynamicRepositoryEntities.containsKey(base) && base.getSuperclass() != null
				&& RepositoryObject.class.isAssignableFrom(base.getSuperclass())) {
			return this.isClassRegistered((Class<? extends RepositoryObject>) base.getSuperclass(), classSimpleName);
		}
		return this.dynamicRepositoryEntities.get(base).isClassRegistered(classSimpleName);
	}

	public <T extends RepositoryObject> boolean registerClass(final Class<T> c) {
		return this.registerClass(c, c);
	}

	public <T extends RepositoryObject, S extends T> boolean registerClass(final Class<T> base, final Class<S> c) {
		if (!this.dynamicRepositoryEntities.containsKey(base) && base.getSuperclass() != null
				&& RepositoryObject.class.isAssignableFrom(base.getSuperclass())) {
                    return this.registerClass((Class<RepositoryObject>) base.getSuperclass(), c);
		}
		return this.dynamicRepositoryEntities.get(base).registerClass(c);
	}

	public <T extends RepositoryObject> boolean unregisterClass(final Class<T> c) {
		return this.unregisterClass(c, c);
	}

	@SuppressWarnings("unchecked")
	public <T extends RepositoryObject, S extends T> boolean unregisterClass(final Class<T> base, final Class<S> c) {
		if (!this.dynamicRepositoryEntities.containsKey(base) && base.getSuperclass() != null
				&& RepositoryObject.class.isAssignableFrom(base.getSuperclass())) {
                    return this.unregisterClass((Class<RepositoryObject>) base.getSuperclass(), c);
		}
		return this.dynamicRepositoryEntities.get(base).unregisterClass(c);
	}

	public <T extends RepositoryObject> Class<? extends T> getRegisteredClass(final Class<T> c,
			final String className) {
		return this.dynamicRepositoryEntities.get(c).getRegisteredClass(className);
	}

	public <T extends RepositoryObject> Collection<Class<? extends T>> getClasses(Class<T> c) {
		return this.dynamicRepositoryEntities.get(c).getClasses();
	}

	public String getAnalysisResultsBasePath() {
		return ((RunResultRepositoryEntity) this.staticRepositoryEntities.get(RunResult.class))
				.getAnalysisResultsBasePath();
	}

	public String getClusterResultsBasePath() {
		return ((RunResultRepositoryEntity) this.staticRepositoryEntities.get(RunResult.class))
				.getClusterResultsBasePath();
	}

	public String getClusterResultsQualityBasePath() {
		return ((RunResultRepositoryEntity) this.staticRepositoryEntities.get(RunResult.class))
				.getClusterResultsQualityBasePath();
	}

	public boolean registerDataStatisticCalculator(
			Class<? extends DataStatisticCalculator<? extends DataStatistic>> dataStatisticCalculator) {
		return ((DataStatisticRepositoryEntity) this.dynamicRepositoryEntities.get(DataStatistic.class))
				.registerDataStatisticCalculator(dataStatisticCalculator);
	}

	public boolean registerRunDataStatisticCalculator(
			Class<? extends RunDataStatisticCalculator<? extends RunDataStatistic>> runDataStatisticCalculator) {
		return ((RunDataStatisticRepositoryEntity) this.dynamicRepositoryEntities.get(RunDataStatistic.class))
				.registerRunDataStatisticCalculator(runDataStatisticCalculator);
	}

	public boolean registerRunStatisticCalculator(
			Class<? extends RunStatisticCalculator<? extends RunStatistic>> runStatisticCalculator) {
		return ((RunStatisticRepositoryEntity) this.dynamicRepositoryEntities.get(RunStatistic.class))
				.registerRunStatisticCalculator(runStatisticCalculator);
	}

	public Class<? extends DataStatisticCalculator<? extends DataStatistic>> getDataStatisticCalculator(
			final String dataStatisticClassName) {
		return ((DataStatisticRepositoryEntity) this.dynamicRepositoryEntities.get(DataStatistic.class))
				.getDataStatisticCalculator(dataStatisticClassName);
	}

	public Class<? extends RunDataStatisticCalculator<? extends RunDataStatistic>> getRunDataStatisticCalculator(
			final String runDataStatisticClassName) {
		return ((RunDataStatisticRepositoryEntity) this.dynamicRepositoryEntities.get(RunDataStatistic.class))
				.getRunDataStatisticCalculator(runDataStatisticClassName);
	}

	public Class<? extends RunStatisticCalculator<? extends RunStatistic>> getRunStatisticCalculator(
			final String runStatisticClassName) {
		return ((RunStatisticRepositoryEntity) this.dynamicRepositoryEntities.get(RunStatistic.class))
				.getRunStatisticCalculator(runStatisticClassName);
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
	public int getCurrentDataSetFormatVersion(final String formatClass) throws UnknownDataSetFormatException {
		return ((DataSetFormatRepositoryEntity) this.dynamicRepositoryEntities.get(DataSetFormat.class))
				.getCurrentDataSetFormatVersion(formatClass);
	}

	/**
	 * @param formatClass
	 *            The dataset format class for which we want to set the current
	 *            version.
	 * @param version
	 *            The new version of the dataset format class.
	 */
	public void putCurrentDataSetFormatVersion(final String formatClass, final int version) {
		((DataSetFormatRepositoryEntity) this.dynamicRepositoryEntities.get(DataSetFormat.class))
				.putCurrentDataSetFormatVersion(formatClass, version);
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
	public Class<? extends DataSetFormatParser> getDataSetFormatParser(final String dataSetFormatName) {
		return ((DataSetFormatRepositoryEntity) this.dynamicRepositoryEntities.get(DataSetFormat.class))
				.getDataSetFormatParser(dataSetFormatName);
	}

	/**
	 * This method checks whether the given string is a valid and internal
	 * double attribute by invoking {@link #isInternalAttribute(String)}. Then
	 * the internal double attribute is looked up and returned if it exists.
	 *
	 * @param value
	 *            The name of the internal double attribute.
	 * @return The internal double attribute with the given name or null, if
	 *         there is no attribute with the given name
	 */
	public NamedDoubleAttribute getInternalDoubleAttribute(final String value) {
		if (!isInternalAttribute(value)) {
			return null;
		}
		NamedDoubleAttribute result = this.internalDoubleAttributes.get(value.substring(2, value.length() - 1));
		if (result == null && parent != null)
			result = parent.getInternalDoubleAttribute(value);
		return result;
	}

	/**
	 * This method checks whether the given string is a valid and internal
	 * integer attribute by invoking {@link #isInternalAttribute(String)}. Then
	 * the internal integer attribute is looked up and returned if it exists.
	 *
	 * @param value
	 *            The name of the internal integer attribute.
	 * @return The internal integer attribute with the given name or null, if
	 *         there is no attribute with the given name
	 */
	public NamedIntegerAttribute getInternalIntegerAttribute(final String value) {
		if (!isInternalAttribute(value)) {
			return null;
		}
		NamedIntegerAttribute result = this.internalIntegerAttributes.get(value.substring(2, value.length() - 1));
		if (result == null && parent != null)
			result = parent.getInternalIntegerAttribute(value);
		return result;
	}

	/**
	 * This method checks whether the given string is a valid and internal
	 * string attribute by invoking {@link #isInternalAttribute(String)}. Then
	 * the internal string attribute is looked up and returned if it exists.
	 *
	 * @param value
	 *            The name of the internal string attribute.
	 * @return The internal string attribute with the given name or null, if
	 *         there is no attribute with the given name
	 */
	public NamedStringAttribute getInternalStringAttribute(final String value) {
		if (!isInternalAttribute(value)) {
			return null;
		}
		NamedStringAttribute result = this.internalStringAttributes.get(value.substring(2, value.length() - 1));
		if (result == null && parent != null)
			result = parent.getInternalStringAttribute(value);
		return result;
	}

	/**
	 * @return The absolute path to the directory, where for a certain runresult
	 *         (identified by its unique run identifier) all log files are
	 *         stored.
	 */
	public String getLogBasePath() {
		return ((RunResultRepositoryEntity) this.staticRepositoryEntities.get(RunResult.class)).getResultLogBasePath();
	}

	/**
	 *
	 * @return The parent repository of this repository, or null if this
	 *         repository has no parent.
	 */
	public Repository getParent() {
		return this.parent;
	}

	/**
	 * This method looks up and returns (if it exists) the repository object
	 * that belongs to the passed absolute path.
	 *
	 * @param absFilePath
	 *            The absolute path for which we want to find the repository
	 *            object.
	 * @return The repository object which has the given absolute path.
	 */
	public RepositoryObject getRegisteredObject(final File absFilePath) {
		return this.pathToRepositoryObject.get(absFilePath);
	}

	/**
	 * This method checks, whether there is a named double attribute registered,
	 * that is equal to the passed object and returns it.
	 *
	 * <p>
	 * Equality is checked in terms of
	 * <ul>
	 * <li><b>object.hashCode == other.hashCode</b></li>
	 * <li><b>object.equals(other)</b></li>
	 * </ul>
	 * since internally the repository uses hash datastructures.
	 *
	 * <p>
	 * By default the {@link RepositoryObject#equals(Object)} method is only
	 * based on the absolute path of the repository object and the repositories
	 * of the two objects, this means two repository objects are considered the
	 * same if they are stored in the same repository and they have the same
	 * absolute path.
	 *
	 * @param object
	 *            The object for which we want to find an equal registered
	 *            object.
	 * @return The registered object equal to the passed object.
	 */
	public NamedDoubleAttribute getRegisteredObject(final NamedDoubleAttribute object) {
		NamedDoubleAttribute other = this.internalDoubleAttributes.get(object.getName());
		if (other == null && parent != null)
			return parent.getRegisteredObject(object);
		return other;
	}

	/**
	 * This method checks, whether there is a named integer attribute
	 * registered, that is equal to the passed object and returns it.
	 *
	 * <p>
	 * Equality is checked in terms of
	 * <ul>
	 * <li><b>object.hashCode == other.hashCode</b></li>
	 * <li><b>object.equals(other)</b></li>
	 * </ul>
	 * since internally the repository uses hash datastructures.
	 *
	 * <p>
	 * By default the {@link RepositoryObject#equals(Object)} method is only
	 * based on the absolute path of the repository object and the repositories
	 * of the two objects, this means two repository objects are considered the
	 * same if they are stored in the same repository and they have the same
	 * absolute path.
	 *
	 * @param object
	 *            The object for which we want to find an equal registered
	 *            object.
	 * @return The registered object equal to the passed object.
	 */
	public NamedIntegerAttribute getRegisteredObject(final NamedIntegerAttribute object) {
		NamedIntegerAttribute other = this.internalIntegerAttributes.get(object.getName());
		if (other == null && parent != null)
			return parent.getRegisteredObject(object);
		return other;
	}

	/**
	 * This method checks, whether there is a named string attribute registered,
	 * that is equal to the passed object and returns it.
	 *
	 * <p>
	 * Equality is checked in terms of
	 * <ul>
	 * <li><b>object.hashCode == other.hashCode</b></li>
	 * <li><b>object.equals(other)</b></li>
	 * </ul>
	 * since internally the repository uses hash datastructures.
	 *
	 * <p>
	 * By default the {@link RepositoryObject#equals(Object)} method is only
	 * based on the absolute path of the repository object and the repositories
	 * of the two objects, this means two repository objects are considered the
	 * same if they are stored in the same repository and they have the same
	 * absolute path.
	 *
	 * @param object
	 *            The object for which we want to find an equal registered
	 *            object.
	 * @return The registered object equal to the passed object.
	 */
	public NamedStringAttribute getRegisteredObject(final NamedStringAttribute object) {
		NamedStringAttribute other = this.internalStringAttributes.get(object.getName());
		if (other == null && parent != null)
			return parent.getRegisteredObject(object);
		return other;
	}

	/**
	 * This method looks up and returns (if it exists) the runresult with the
	 * given unique identifier.
	 *
	 * @param runIdentifier
	 *            The identifier of the runresult.
	 * @return The runresult with the given identifier.
	 */
	public RunResult getRegisteredRunResult(final String runIdentifier) {
		return ((RunResultRepositoryEntity) this.staticRepositoryEntities.get(RunResult.class)).runResultIdentifier
				.get(runIdentifier);
	}

	/**
	 * @return The configuration of this repository.
	 */
	public RepositoryConfig getRepositoryConfig() {
		return this.repositoryConfig;
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
	public Class<? extends RunResultFormatParser> getRunResultFormatParser(final String runResultFormatName) {
		return ((RunResultFormatRepositoryEntity) this.dynamicRepositoryEntities.get(RunResultFormat.class))
				.getRunResultFormatParser(runResultFormatName);
	}

	/**
	 * @return A collection with the names of those runresult directories
	 *         contained in the repository of this server, that contain a
	 *         clusters subfolder and at least one *.complete file containing
	 *         results (can be slow if many run result folders are present).
	 */
	public Collection<String> getRunResultIdentifier() {
		Collection<String> result = new HashSet<String>();

		for (File resultDir : new File(this.getBasePath(RunResult.class)).listFiles()) {
			if (resultDir.isDirectory()) {
				File clustersDir = new File(FileUtils.buildPath(resultDir.getAbsolutePath(), "clusters"));
				if (clustersDir.exists() && clustersDir.isDirectory()) {
					/*
					 * Take only those, that contain at least one *.complete
					 * file
					 */
					for (File resultsFile : clustersDir.listFiles()) {
						if (resultsFile.getName().endsWith(".complete")) {
							result.add(resultDir.getName());
							break;
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * @return A collection with the names of all run result directories
	 *         contained in the repository of this server. Those run result
	 *         directories can be resumed, if they were terminated before.
	 */
	public Collection<String> getRunResumes() {
		Collection<String> result = new HashSet<String>();

		for (File resultDir : new File(this.getBasePath(RunResult.class)).listFiles()) {
			if (resultDir.isDirectory()) {
				result.add(resultDir.getName());
			}
		}

		return result;
	}

	/**
	 * @return In case the backend is connected to a mysql database in the
	 *         frontend, this returns an sql communicator, which updates the
	 *         database after changes of repository objects (removal, addition),
	 *         otherwise it returns a stub sql communicator.
	 */
	public SQLCommunicator getSqlCommunicator() {
		return sqlCommunicator;
	}

	/**
	 *
	 * @return The supervisor thread is responsible for starting and keeping
	 *         alive all threads that check the repository on the filesystem for
	 *         changes.
	 */
	public SupervisorThread getSupervisorThread() {
		return supervisorThread;
	}

	/**
	 * @return The absolute path to the directory within this repository, where
	 *         all supplementary materials are stored.
	 */
	public String getSupplementaryBasePath() {
		return this.supplementaryBasePath;
	}

	/**
	 * @return The absolute path to the directory within this repository, where
	 *         all supplementary materials related to clustering are stored.
	 */
	public String getSupplementaryClusteringBasePath() {
		return this.suppClusteringBasePath;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.basePath.hashCode();
	}

	/**
	 * A helper method for logging, which can overwritten to change the
	 * logger-level in subclasses of this class. For example in
	 * RunResultRepostories we do not want to log everything, therefore we
	 * change the log level to debug.
	 *
	 * @param The
	 *            message to log.
	 */
	protected void info(final String message) {
		this.log.info(message);
	}

	protected <T extends RepositoryObject> void createAndAddStaticEntity(final Class<T> c, final String basePath) {
		this.staticRepositoryEntities.put(c, new StaticRepositoryEntity<T>(this,
				this.parent != null ? this.parent.staticRepositoryEntities.get(c) : null, basePath));
	}

	protected <T extends RepositoryObject> void createAndAddDynamicEntity(final Class<T> c, final String basePath) {
		this.dynamicRepositoryEntities.put(c, new DynamicRepositoryEntity<T>(this,
				this.parent != null ? this.parent.dynamicRepositoryEntities.get(c) : null, basePath));
	}

	/**
	 * This method initializes all attribute maps and all variables, that keep
	 * registered repository objects.
	 *
	 * <p>
	 * A helper method for and invoked by
	 * {@link #Repository(String, Repository, long, long, long, long, long, long, long)}
	 * .
	 */
	protected void initAttributes() {

		this.staticRepositoryEntities = new StaticRepositoryEntityMap();

		this.dynamicRepositoryEntities = new DynamicRepositoryEntityMap();

		this.createAndAddStaticEntity(DataSet.class, FileUtils.buildPath(this.basePath, "data", "datasets"));
		this.createAndAddStaticEntity(DataSetConfig.class,
				FileUtils.buildPath(this.basePath, "data", "datasets", "configs"));
		this.createAndAddStaticEntity(GoldStandard.class, FileUtils.buildPath(this.basePath, "data", "goldstandards"));
		this.createAndAddStaticEntity(GoldStandardConfig.class,
				FileUtils.buildPath(this.basePath, "data", "goldstandards", "configs"));
		this.createAndAddStaticEntity(DataConfig.class, FileUtils.buildPath(this.basePath, "data", "configs"));
		this.createAndAddStaticEntity(Run.class, FileUtils.buildPath(this.basePath, "runs"));
		this.createAndAddStaticEntity(ProgramConfig.class, FileUtils.buildPath(this.basePath, "programs", "configs"));
		this.createAndAddStaticEntity(Program.class, FileUtils.buildPath(this.basePath, "programs"));
		// this.createAndAddStaticEntity(Clustering.class,
		// FileUtils.buildPath(this.basePath, "results"));

		this.staticRepositoryEntities.put(Clustering.class,
				new ClusteringRepositoryEntity(this,
						this.parent != null ? this.parent.staticRepositoryEntities.get(Clustering.class) : null,
						FileUtils.buildPath(this.basePath, "results")));

		this.staticRepositoryEntities.put(RunResult.class,
				new RunResultRepositoryEntity(this,
						this.parent != null ? this.parent.staticRepositoryEntities.get(RunResult.class) : null,
						FileUtils.buildPath(this.basePath, "results")));

		this.staticRepositoryEntities.put(Finder.class, new FinderRepositoryEntity(this,
				this.parent != null ? this.parent.staticRepositoryEntities.get(Finder.class) : null, null));

		this.staticRepositoryEntities
				.put(DoubleProgramParameter.class,
						new ProgramParameterRepositoryEntity<DoubleProgramParameter>(this,
								this.parent != null
										? this.parent.staticRepositoryEntities.get(DoubleProgramParameter.class)
										: null,
								null));
		this.staticRepositoryEntities.put(IntegerProgramParameter.class,
				new ProgramParameterRepositoryEntity<IntegerProgramParameter>(this,
						this.parent != null
								? this.parent.staticRepositoryEntities.get(IntegerProgramParameter.class)
								: null,
						null));
		this.staticRepositoryEntities
				.put(StringProgramParameter.class,
						new ProgramParameterRepositoryEntity<StringProgramParameter>(this,
								this.parent != null
										? this.parent.staticRepositoryEntities.get(StringProgramParameter.class)
										: null,
								null));

		this.createAndAddDynamicEntity(DistanceMeasure.class,
				FileUtils.buildPath(this.supplementaryBasePath, "distanceMeasures"));

		this.dynamicRepositoryEntities.put(DataStatistic.class,
				new DataStatisticRepositoryEntity(this, this.parent != null
						? (DataStatisticRepositoryEntity) this.parent.dynamicRepositoryEntities.get(DataStatistic.class)
						: null, FileUtils.buildPath(this.supplementaryBasePath, "statistics", "data")));

		this.dynamicRepositoryEntities.put(RunStatistic.class,
				new RunStatisticRepositoryEntity(this, this.parent != null
						? (RunStatisticRepositoryEntity) this.parent.dynamicRepositoryEntities.get(RunStatistic.class)
						: null, FileUtils.buildPath(this.supplementaryBasePath, "statistics", "run")));

		this.dynamicRepositoryEntities.put(RunDataStatistic.class,
				new RunDataStatisticRepositoryEntity(this,
						this.parent != null
								? (RunDataStatisticRepositoryEntity) this.parent.dynamicRepositoryEntities
										.get(RunDataStatistic.class)
								: null,
						FileUtils.buildPath(this.supplementaryBasePath, "statistics", "rundata")));

		this.createAndAddDynamicEntity(DataSetGenerator.class, FileUtils.buildPath(this.generatorBasePath, "dataset"));
		this.createAndAddDynamicEntity(DataRandomizer.class, FileUtils.buildPath(this.randomizerBasePath, "data"));
		this.createAndAddDynamicEntity(DataPreprocessor.class,
				FileUtils.buildPath(this.supplementaryBasePath, "preprocessing"));
		this.createAndAddDynamicEntity(RunResultPostprocessor.class,
				FileUtils.buildPath(this.supplementaryBasePath, "postprocessing"));

		this.dynamicRepositoryEntities.put(RProgram.class,
				new RProgramRepositoryEntity(this, this.staticRepositoryEntities.get(Program.class),
						this.parent != null
								? (RProgramRepositoryEntity) this.parent.dynamicRepositoryEntities.get(RProgram.class)
								: null,
						this.getBasePath(Program.class)));

		this.createAndAddDynamicEntity(ClusteringQualityMeasure.class,
				FileUtils.buildPath(this.suppClusteringBasePath, "qualityMeasures"));

		this.createAndAddDynamicEntity(Context.class, FileUtils.buildPath(this.supplementaryBasePath, "contexts"));

		this.createAndAddDynamicEntity(ParameterOptimizationMethod.class,
				FileUtils.buildPath(this.suppClusteringBasePath, "paramOptimization"));

		this.createAndAddDynamicEntity(DataSetType.class, FileUtils.buildPath(this.typesBasePath, "dataset"));

		this.dynamicRepositoryEntities.put(DataSetFormat.class,
				new DataSetFormatRepositoryEntity(this, this.parent != null
						? (DataSetFormatRepositoryEntity) this.parent.dynamicRepositoryEntities.get(DataSetFormat.class)
						: null, FileUtils.buildPath(this.formatsBasePath, "dataset")));

		this.dynamicRepositoryEntities.put(RunResultFormat.class,
				new RunResultFormatRepositoryEntity(this,
						this.parent != null
								? (RunResultFormatRepositoryEntity) this.parent.dynamicRepositoryEntities
										.get(RunResultFormat.class)
								: null,
						FileUtils.buildPath(this.formatsBasePath, "runresult")));

		this.goldStandardFormats = new ConcurrentHashMap<GoldStandardFormat, GoldStandardFormat>();

		this.internalDoubleAttributes = new ConcurrentHashMap<String, NamedDoubleAttribute>();
		this.internalStringAttributes = new ConcurrentHashMap<String, NamedStringAttribute>();
		this.internalIntegerAttributes = new ConcurrentHashMap<String, NamedIntegerAttribute>();

		// added 14.04.2013
		this.knownFinderExceptions = new ConcurrentHashMap<String, List<Throwable>>();
		this.finderClassLoaders = new ConcurrentHashMap<URL, URLClassLoader>();
		this.finderWaitingFiles = new ConcurrentHashMap<File, List<File>>();
		this.finderLoadedJarFileChangeDates = new ConcurrentHashMap<String, Long>();
	}

	/**
	 * Initializes this repository by creating a supervisor thread
	 * {@link #createSupervisorThread()} and waiting until
	 * {@link #isInitialized()} returns true.
	 *
	 * @throws InterruptedException
	 *             Is thrown, if the current thread is interrupted while waiting
	 *             for finishing the initialization process.
	 */
	public void initialize() throws InterruptedException {
		if (isInitialized() || this.supervisorThread != null)
			return;

		this.supervisorThread = createSupervisorThread();

		// wait until repository initialized
		try {
			while (!this.isInitialized())
				Thread.sleep(100);
		} catch (InterruptedException e) {
			this.terminateSupervisorThread();
			throw e;
		}

		this.info("Repository initialization finished");

		/**
		 * Print warnings for all required R libraries, that could not be loaded
		 */
		if (ClustevalBackendServer.isRAvailable()) {
			if (this.missingRLibraries.size() > 0) {
				this.warn(
						"The following R library dependencies are not satisified (the corresponding class has not been loaded):");
				this.warn("Please ensure that those libraries are installed in your R installation:");

				StringBuilder sb = new StringBuilder();
				sb.append("install.packages(c(");

				for (String className : this.missingRLibraries.keySet())
					for (RLibraryNotLoadedException e : this.missingRLibraries.get(className)) {
						this.warn("Class '" + e.getClassName() + "' requires the unavailable R library '"
								+ e.getRLibrary() + "'");
						sb.append(String.format("\"%s\",", e.getRLibrary()));
					}
				sb.deleteCharAt(sb.length() - 1);

				sb.append("))");

				this.warn("You can use the following command to install them in R:");
				this.warn(sb.toString());
			}
		}
	}

	/**
	 * This method sets all the absolute paths used by the repository to store
	 * any kinds of files and data on the filesystem.
	 *
	 * <p>
	 * This method only initializes the attributes itself to valid paths, but
	 * does not create or ensure any folder structure.
	 * <p>
	 * A helper method of
	 * {@link #Repository(String, Repository, long, long, long, long, long, long, long)}
	 * .
	 *
	 * @throws InvalidRepositoryException
	 *
	 */
	@SuppressWarnings("unused")
	protected void initializePaths() throws InvalidRepositoryException {
		this.supplementaryBasePath = FileUtils.buildPath(this.basePath, "supp");
		this.suppClusteringBasePath = FileUtils.buildPath(this.supplementaryBasePath, "clustering");
		this.formatsBasePath = FileUtils.buildPath(this.supplementaryBasePath, "formats");
		this.generatorBasePath = FileUtils.buildPath(this.supplementaryBasePath, "generators");
		this.randomizerBasePath = FileUtils.buildPath(this.supplementaryBasePath, "randomizers");
		this.typesBasePath = FileUtils.buildPath(this.supplementaryBasePath, "types");
	}

	/**
	 * This method checks, whether this repository has been initialized. A
	 * repository is initialized, if the following invocations return true:
	 *
	 * <ul>
	 * <li><b>getDataSetFormatsInitialized()</b></li>
	 * <li><b>getDataSetTypesInitialized()</b></li>
	 * <li><b>getDataStatisticsInitialized()</b></li>
	 * <li><b>getRunStatisticsInitialized()</b></li>
	 * <li><b>getRunDataStatisticsInitialized()</b></li>
	 * <li><b>getRunResultFormatsInitialized()</b></li>
	 * <li><b>getClusteringQualityMeasuresInitialized()</b></li>
	 * <li><b>getParameterOptimizationMethodsInitialized()</b></li>
	 * <li><b>getRunsInitialized()</b></li>
	 * <li><b>getRProgramsInitialized()</b></li>
	 * <li><b>getDataSetConfigsInitialized()</b></li>
	 * <li><b>getGoldStandardConfigsInitialized()</b></li>
	 * <li><b>getDataConfigsInitialized()</b></li>
	 * <li><b>getProgramConfigsInitialized()</b></li>
	 * <li><b>getDataSetGeneratorsInitialized()</b></li>
	 * <li><b>getDistanceMeasuresInitialized()</b></li>
	 * </ul>
	 *
	 * @return True, if this repository is initialized.
	 */
	public boolean isInitialized() {
		// TODO: for loop?
		return isInitialized(DataSetFormat.class) && isInitialized(DataSetType.class)
				&& isInitialized(DataStatistic.class) && isInitialized(RunStatistic.class)
				&& isInitialized(RunDataStatistic.class) && isInitialized(RunResultFormat.class)
				&& isInitialized(ClusteringQualityMeasure.class) && isInitialized(ParameterOptimizationMethod.class)
				&& isInitialized(Run.class) && isInitialized(RProgram.class) && isInitialized(DataSetConfig.class)
				&& isInitialized(DataSet.class) && isInitialized(GoldStandardConfig.class)
				&& isInitialized(DataConfig.class) && isInitialized(ProgramConfig.class)
				&& isInitialized(DataSetGenerator.class) && isInitialized(Context.class)
				&& isInitialized(DataPreprocessor.class) && isInitialized(DistanceMeasure.class);
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
	public boolean isRegisteredForRunResultFormat(final Class<? extends RunResultFormat> runResultFormat) {
		return ((RunResultFormatRepositoryEntity) this.dynamicRepositoryEntities.get(RunResultFormat.class))
				.isRegisteredForRunResultFormat(runResultFormat);
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
	public boolean isRegisteredForRunResultFormat(final String runResultFormatName) {
		return ((RunResultFormatRepositoryEntity) this.dynamicRepositoryEntities.get(RunResultFormat.class))
				.isRegisteredForRunResultFormat(runResultFormatName);
	}

	/**
	 * This method registers a new named double attribute. If an old object was
	 * already registered that equals the new object, the new object is not
	 * registered.
	 *
	 * @param object
	 *            The new object to register.
	 * @return True, if the new object has been registered.
	 */
	public boolean register(final NamedDoubleAttribute object) {
		if (this.getRegisteredObject(object) != null)
			return false;
		this.internalDoubleAttributes.put(object.getName(), object);
		this.pathToRepositoryObject.put(object.absPath, object);
		return true;
	}

	/**
	 * This method registers a new named integer attribute. If an old object was
	 * already registered that equals the new object, the new object is not
	 * registered.
	 *
	 * @param object
	 *            The new object to register.
	 * @return True, if the new object has been registered.
	 */
	public boolean register(final NamedIntegerAttribute object) {
		if (this.getRegisteredObject(object) != null)
			return false;
		this.internalIntegerAttributes.put(object.getName(), object);
		this.pathToRepositoryObject.put(object.absPath, object);
		return true;
	}

	/**
	 * This method registers a new named string attribute. If an old object was
	 * already registered that equals the new object, the new object is not
	 * registered.
	 *
	 * @param object
	 *            The new object to register.
	 * @return True, if the new object has been registered.
	 */
	public boolean register(final NamedStringAttribute object) {
		if (this.getRegisteredObject(object) != null)
			return false;
		this.internalStringAttributes.put(object.getName(), object);
		this.pathToRepositoryObject.put(object.absPath, object);
		return true;
	}

	/**
	 * @return The MyRengine object corresponding to the current thread.
	 * @throws RserveException
	 */
	public MyRengine getRengineForCurrentThread() throws RserveException {
		Thread currentThread = Thread.currentThread();
		synchronized (this.rEngines) {
			if (!this.rEngines.containsKey(currentThread))
				this.rEngines.put(currentThread, new MyRengine(""));
			return this.rEngines.get(currentThread);
		}
	}

	public MyRengine getRengine(final Thread thread) throws RserveException {
		synchronized (this.rEngines) {
			if (!this.rEngines.containsKey(thread))
				this.rEngines.put(thread, new MyRengine(""));
			return this.rEngines.get(thread);
		}
	}

	public void clearRengineForCurrentThread() {
		Thread currentThread = Thread.currentThread();
		synchronized (this.rEngines) {
			if (this.rEngines.containsKey(currentThread))
				this.rEngines.remove(currentThread);
		}
	}

	public void clearRengine(final Thread thread) {
		synchronized (this.rEngines) {
			if (this.rEngines.containsKey(thread))
				this.rEngines.remove(thread);
		}
	}

	/**
	 * This method registers a dataset format parser.
	 *
	 * @param dsFormatParser
	 *            The dataset format parser to register.
	 * @return True, if the dataset format parser replaced an old object.
	 */
	public boolean registerDataSetFormatParser(final Class<? extends DataSetFormatParser> dsFormatParser) {
		return ((DataSetFormatRepositoryEntity) this.dynamicRepositoryEntities.get(DataSetFormat.class))
				.registerDataSetFormatParser(dsFormatParser);
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
	public boolean isRegisteredForDataSetFormat(final Class<? extends DataSetFormat> dsFormat) {
		return ((DataSetFormatRepositoryEntity) this.dynamicRepositoryEntities.get(DataSetFormat.class))
				.isRegisteredForDataSetFormat(dsFormat);
	}

	/**
	 * This method registers a new runresult format parser class.
	 *
	 * @param runResultFormatParser
	 *            The new class to register.
	 * @return True, if the new class replaced an old one.
	 */
	public boolean registerRunResultFormatParser(final Class<? extends RunResultFormatParser> runResultFormatParser) {
		return ((RunResultFormatRepositoryEntity) this.dynamicRepositoryEntities.get(RunResultFormat.class))
				.registerRunResultFormatParser(runResultFormatParser);
	}

	/**
	 * @param comm
	 *            The new sql communicator.
	 */
	public void setSQLCommunicator(final SQLCommunicator comm) {
		this.sqlCommunicator = comm;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.basePath;
	}

	/**
	 * This method unregisters the passed object.
	 *
	 * @param object
	 *            The object to be removed.
	 * @return True, if the object was remved successfully
	 */
	public boolean unregister(NamedDoubleAttribute object) {
		return this.internalDoubleAttributes.remove(object) != null;
	}

	/**
	 * This method unregisters the passed object.
	 *
	 * @param object
	 *            The object to be removed.
	 * @return True, if the object was remved successfully
	 */
	public boolean unregister(NamedIntegerAttribute object) {
		return this.internalIntegerAttributes.remove(object) != null;
	}

	/**
	 * This method unregisters the passed object.
	 *
	 * @param object
	 *            The object to be removed.
	 * @return True, if the object was remved successfully
	 */
	public boolean unregister(NamedStringAttribute object) {
		return this.internalStringAttributes.remove(object) != null;
	}

	/**
	 * This method unregisters the passed object.
	 *
	 * @param object
	 *            The object to be removed.
	 * @return True, if the object was remved successfully
	 */
	public boolean unregisterRunResultFormatParser(final Class<? extends RunResultFormatParser> object) {
		return ((RunResultFormatRepositoryEntity) this.dynamicRepositoryEntities.get(RunResultFormat.class))
				.unregisterRunResultFormatParser(object);
	}

	/**
	 * This method is invoked by
	 * {@link Run#setStatus(de.clusteval.run.RUN_STATUS)} and ensures that the
	 * new status is passed to the whole framework, e.g. the frontend database.
	 *
	 * @param run
	 *            The run which changed its status.
	 * @param newStatus
	 *            The new status of the run.
	 * @return True, if the propagation of the new status was successful.
	 */
	public boolean updateStatusOfRun(final Run run, final String newStatus) {
		return this.sqlCommunicator.updateStatusOfRun(run, newStatus);
	}

	/**
	 * A helper method for logging, which can overwritten to change the
	 * logger-level in subclasses of this class. For example in
	 * RunResultRepostories we do not want to log everything, therefore we
	 * change the log level to debug.
	 *
	 * @param The
	 *            message to log.
	 */
	protected void warn(final String message) {
		this.log.warn(message);
	}

	/**
	 * @return The map containing all known finder exceptions.
	 */
	public Map<String, List<Throwable>> getKnownFinderExceptions() {
		return this.knownFinderExceptions;
	}

	/**
	 * @return The class loaders used by the finders to load classes
	 *         dynamically.
	 */
	public Map<URL, URLClassLoader> getJARFinderClassLoaders() {
		return this.finderClassLoaders;
	}

	/**
	 *
	 * @return A map containing dependencies between jar files that are loaded
	 *         dynamically.
	 */
	public Map<File, List<File>> getJARFinderWaitingFiles() {
		return this.finderWaitingFiles;
	}

	/**
	 *
	 * @return The change dates of the jar files that were loaded dynamically by
	 *         jar finder instances.
	 */
	public Map<String, Long> getFinderLoadedJarFileChangeDates() {
		return this.finderLoadedJarFileChangeDates;
	}
}