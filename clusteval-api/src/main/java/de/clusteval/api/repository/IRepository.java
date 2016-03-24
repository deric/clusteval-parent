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
package de.clusteval.api.repository;

import de.clusteval.api.Database;
import de.clusteval.api.SQLConfig;
import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.data.IDataSetFormatParser;
import de.clusteval.api.exceptions.InternalAttributeException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RLibraryNotLoadedException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.run.IRun;
import de.clusteval.api.run.IRunResultFormatParser;
import de.clusteval.api.run.ISupervisorThread;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.script.ScriptException;

/**
 *
 * @author deric
 */
public interface IRepository {

    /**
     * Initializes this repository by creating a supervisor thread
     * {@link #createSupervisorThread()} and waiting until
     * {@link #isInitialized()} returns true.
     *
     * @throws InterruptedException Is thrown, if the current thread is
     *                              interrupted while waiting for finishing the initialization process.
     */
    void initialize() throws InterruptedException;

    /**
     * Register a new repository.
     *
     * @param repository The new repository to register.
     * @return The old repository, if the new repository replaced an old one
     *         with equal root path. Null otherwise.
     * @throws RepositoryAlreadyExistsException
     * @throws InvalidRepositoryException
     */
    IRepository register(IRepository repository) throws RepositoryAlreadyExistsException, InvalidRepositoryException;

    /**
     *
     * @return The parent repository of this repository, or null if this
     *         repository has no parent.
     */
    IRepository getParent();

    /**
     * @return The absolute path to the root of this repository.
     */
    public String getBasePath();

    String getBasePath(final Class<? extends IRepositoryObject> c);

    /**
     * @return The absolute path to the directory, where for a certain runresult
     *         (identified by its unique run identifier) all log files are stored.
     */
    public String getLogBasePath();

    /**
     * This method looks up and returns (if it exists) the repository object
     * that belongs to the passed absolute path.
     *
     * @param absFilePath The absolute path for which we want to find the
     *                    repository object.
     * @return The repository object which has the given absolute path.
     */
    IRepositoryObject getRegisteredObject(final File absFilePath);

    /**
     * @return The configuration of this repository.
     */
    IRepositoryConfig getRepositoryConfig();

    SQLConfig getDbConfig();

    <T extends IRepositoryObject> T getStaticObjectWithName(final Class<T> c, final String name);

    <T extends IRepositoryObject> Collection<T> getCollectionStaticEntities(final Class<T> c);
    //<T extends IRepositoryObject> T getCollectionStaticEntities(final Class<T> c);

    <T extends IRepositoryObject> boolean isClassRegistered(final Class<T> c);

    <T extends IRepositoryObject> boolean isClassRegistered(final Class<T> base, final String classSimpleName);

    <T extends IRepositoryObject> boolean isClassRegistered(final String classFullName);

    <T extends IRepositoryObject> boolean registerClass(final Class<T> c);

    <T extends IRepositoryObject> boolean unregisterClass(final Class<T> c);

    <T extends IRepositoryObject, S extends T> boolean unregisterClass(final Class<T> base, final Class<S> c);

    <T extends IRepositoryObject> Class<? extends T> getRegisteredClass(final Class<T> c,
            final String className);

    <T extends IRepositoryObject> Collection<Class<? extends T>> getClasses(Class<T> c);

    int getCurrentDataSetFormatVersion(final String formatClass) throws UnknownDataSetFormatException;

    IRengine getRengineForCurrentThread() throws RException;

    public String getAnalysisResultsBasePath();

    public String getClusterResultsBasePath();

    boolean updateStatusOfRun(final IRun run, final String newStatus);

    <T extends IRepositoryObject, S extends T> boolean registerClass(final Class<T> base, final Class<S> c);

    //boolean registerRunDataStatisticCalculator(Class<? extends RunDataStatisticCalculator<? extends RunDataStatistic>> runDataStatisticCalculator);
    /**
     * This method evaluates all internal attribute placeholders contained in
     * the passed string.
     *
     * @param old           The string which might contain internal attribute
     *                      placeholders.
     * @param dataConfig    The data configuration which might be needed to
     *                      evaluate the placeholders.
     * @param programConfig The program configuration which might be needed to
     *                      evaluate the placeholders.
     * @return The parameter value with evaluated placeholders.
     * @throws InternalAttributeException
     */
    String evaluateInternalAttributes(final String old, final IDataConfig dataConfig,
            final IProgramConfig programConfig) throws InternalAttributeException;

    void terminateSupervisorThread() throws InterruptedException;

    StaticRepositoryEntityMap getStaticEntities();

    DynamicRepositoryEntityMap getDynamicEntities();

    boolean isInitialized(final Class<? extends IRepositoryObject> c);

    <T extends IRepositoryObject> T getRegisteredObject(final T object);

    <T extends IRepositoryObject> T getRegisteredObject(final T object, final boolean ignoreChangeDate);

    <T extends IRepositoryObject, S extends T> boolean unregister(final S object);

    /**
     * @return The map containing all known finder exceptions.
     */
    Map<String, List<Throwable>> getKnownFinderExceptions();

    <T extends IRepositoryObject> void setInitialized(final Class<T> c);

    /**
     * @return The class loaders used by the finders to load classes
     *         dynamically.
     */
    Map<URL, URLClassLoader> getJARFinderClassLoaders();

    /**
     *
     * @return A map containing dependencies between jar files that are loaded
     *         dynamically.
     */
    Map<File, List<File>> getJARFinderWaitingFiles();

    /**
     *
     * @return The change dates of the jar files that were loaded dynamically by
     *         jar finder instances.
     */
    Map<String, Long> getFinderLoadedJarFileChangeDates();

    /**
     * This method is a helper method for sql communication. The sql
     * communicator usually does not commit after every change. Therefore we
     * provide this method, to allow for commiting at certain points such that
     * we can afterwards guarantee a certain state of the DB and operate on it.
     */
    void commitDB();

    /**
     * @param formatClass The dataset format class for which we want to set the
     *                    current version.
     * @param version     The new version of the dataset format class.
     */
    void putCurrentDataSetFormatVersion(final String formatClass, final int version);

    /**
     * A helper method for logging, which can overwritten to change the
     * logger-level in subclasses of this class. For example in
     * RunResultRepostories we do not want to log everything, therefore we
     * change the log level to debug.
     *
     * @param message The message to log.
     */
    void warn(final String message);

    /**
     * Log info message
     *
     * @param message the message
     */
    void info(final String message);

    /**
     * @param comm The new sql communicator.
     */
    void setSQLCommunicator(final Database comm);

    /**
     * Get database driver
     *
     * @return
     */
    Database getDb();

    /**
     * This method registers a dataset format parser.
     *
     * @param dsFormatParser The dataset format parser to register.
     * @return True, if the dataset format parser replaced an old object.
     */
    boolean registerDataSetFormatParser(final Class<? extends IDataSetFormatParser> dsFormatParser);

    /**
     * This method checks whether a parser has been registered for the given
     * dataset format class.
     *
     * @param dsFormat The class for which we want to know whether a parser has
     *                 been registered.
     * @return True, if the parser has been registered.
     */
    boolean isRegisteredForDataSetFormat(final Class<? extends IDataSetFormat> dsFormat);

    /**
     *
     * @return The supervisor thread is responsible for starting and keeping
     *         alive all threads that check the repository on the filesystem for
     *         changes.
     */
    public ISupervisorThread getSupervisorThread();

    /**
     * This method is used to evaluate parameter values containing JavaScript
     * arithmetic operations.
     *
     * <p>
     * A helper method of null null null null null null null null null null null
     * null null null null null null null null null null null null null null null null null null null null     {@link ProgramParameter#evaluateDefaultValue(DataConfig, ProgramConfig)},
	 * {@link ProgramParameter#evaluateMinValue(DataConfig, ProgramConfig)} and
     * {@link ProgramParameter#evaluateMaxValue(DataConfig, ProgramConfig)}.
     *
     * @param script The parameter value containing javascript arithmetic
     *               operations.
     * @return The evaluated expression.
     * @throws ScriptException
     */
    public String evaluateJavaScript(final String script) throws ScriptException;

    /**
     * This method checks, whether this repository has been initialized. A
     * repository is initialized, if the following invocations return true:
     *
     * @return True, if this repository is initialized.
     */
    boolean isInitialized();

    /**
     * This method clears the existing exceptions for missing R libraries for
     * the given class name.
     *
     * @param className The class name for which we want to clear the missing
     *                  libraries.
     * @return The old exceptions that were present for this class.
     */
    Set<RLibraryNotLoadedException> clearMissingRLibraries(String className);

    /**
     * @param e The new exception to add.
     * @return A boolean indicating, whether the exception was new.
     */
    boolean addMissingRLibraryException(RLibraryNotLoadedException e);

    Map<File, IRepositoryObject> getPathToRepositoryObject();

    /**
     * Checks whether repository has a parent repository
     *
     * @return
     */
    boolean hasParent();

    IRengine getRengine(final Thread thread) throws RException;

    void clearRengineForCurrentThread();

    void clearRengine(final Thread thread);

    /**
     * This method looks up and returns (if it exists) the class of the
     * runresult format parser corresponding to the runresult format with the
     * given name.
     *
     * @param runResultFormatName The runresult format name.
     * @return The runresult format parser for the given runresult format name,
     *         or null if it does not exist.
     */
    Class<? extends IRunResultFormatParser> getRunResultFormatParser(final String runResultFormatName);

    String getClusterResultsQualityBasePath();
}
