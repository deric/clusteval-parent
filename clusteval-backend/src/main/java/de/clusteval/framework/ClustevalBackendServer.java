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
package de.clusteval.framework;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.Pair;
import de.clusteval.api.Triple;
import de.clusteval.api.exceptions.DataSetGenerationException;
import de.clusteval.api.exceptions.DataSetNotFoundException;
import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.exceptions.GoldStandardConfigNotFoundException;
import de.clusteval.api.exceptions.GoldStandardConfigurationException;
import de.clusteval.api.exceptions.GoldStandardGenerationException;
import de.clusteval.api.exceptions.GoldStandardNotFoundException;
import de.clusteval.api.exceptions.IncompatibleContextException;
import de.clusteval.api.exceptions.NoDataSetException;
import de.clusteval.api.exceptions.NoOptimizableProgramParameterException;
import de.clusteval.api.exceptions.NoRepositoryFoundException;
import de.clusteval.api.exceptions.RepositoryObjectDumpException;
import de.clusteval.api.exceptions.RunResultParseException;
import de.clusteval.api.exceptions.UnknownContextException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownDataSetGeneratorException;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.exceptions.UnknownGoldStandardFormatException;
import de.clusteval.api.exceptions.UnknownParameterType;
import de.clusteval.api.exceptions.UnknownProgramParameterException;
import de.clusteval.api.exceptions.UnknownProgramTypeException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.exceptions.UnknownRunResultPostprocessorException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.opt.InvalidOptimizationParameterException;
import de.clusteval.api.opt.UnknownParameterOptimizationMethodException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.r.UnknownRProgramException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IScheduler;
import de.clusteval.api.run.IterationRunnable;
import de.clusteval.api.run.IterationWrapper;
import de.clusteval.api.run.RUN_STATUS;
import de.clusteval.api.stats.UnknownDataStatisticException;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.generator.DataSetGenerator;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.DataRandomizeException;
import de.clusteval.data.randomizer.DataRandomizer;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.framework.repository.MyRengine;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryController;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.threading.SupervisorThread;
import de.clusteval.program.Program;
import de.clusteval.run.InvalidRunModeException;
import de.clusteval.run.Run;
import de.clusteval.run.RunException;
import de.clusteval.run.result.ParameterOptimizationResult;
import de.clusteval.run.result.RunResult;
import de.clusteval.run.runnable.AnalysisIterationRunnable;
import de.clusteval.run.runnable.DataAnalysisIterationRunnable;
import de.clusteval.run.runnable.DataAnalysisRunRunnable;
import de.clusteval.run.runnable.ExecutionIterationRunnable;
import de.clusteval.run.runnable.ExecutionRunRunnable;
import de.clusteval.run.runnable.RunAnalysisIterationRunnable;
import de.clusteval.run.runnable.RunAnalysisRunRunnable;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.clusteval.utils.FileUtils;
import de.clusteval.utils.InvalidConfigurationFileException;
import de.clusteval.utils.MyHighlightingCompositeConverter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.configuration.ConfigurationException;
import org.openide.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the server of the backend of the framework. The server
 * takes commands from the client (see {@link BackendClient}) like performing,
 * resuming or terminating runs, shutdown the framework or get status
 * information about various objects available in the repository (e.g. datasets,
 * runs, programs,...).
 *
 * <p>
 * You can start the server by invoking the {@link #main(String[])} method. If
 * you do so, you can pass either a path to an existing repository or a new
 * repository is automatically created in the subfolder 'repository'.
 *
 * <p>
 * When the server is started it registers itself in the RMI registry (remote
 * method invocation), either with the default port 1099 or if specified with
 * -hostport xxxx under any other port.
 *
 * <p>
 * The start of the server requires a running Rserve instance. If this cannot be
 * found, the server will not start.
 *
 * @author Christian Wiwie
 */
public class ClustevalBackendServer implements IBackendServer {

    /**
     * This variable holds the command line options of the backend server.
     */
    public static Options serverCLIOptions = new Options();

    protected static BackendServerConfig config = new BackendServerConfig();

    protected static String VERSION;

    /**
     * @return The configuration of this backend server.
     */
    public static BackendServerConfig getBackendServerConfiguration() {
        return config;
    }

    static {
        // read properties file with version number
        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("server.date");
        try {
            prop.load(stream);
            VERSION = "Jar built: " + prop.getProperty("buildtime") + "\nGit:\n\tCommit: " + prop.getProperty("gitrev")
                    + "\n\tBranch: " + prop.getProperty("gitbranch") + "\n\tRepository: " + prop.getProperty("gitrepo");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // init valid command line options
        OptionBuilder.withArgName("absRepositoryPath");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The absolute path to the repository");
        Option optionAbsRepoPath = OptionBuilder.create("absRepoPath");
        serverCLIOptions.addOption(optionAbsRepoPath);

        OptionBuilder.withDescription("Print this help and usage information");
        Option optionHelp = OptionBuilder.create("help");
        serverCLIOptions.addOption(optionHelp);

        OptionBuilder.withDescription("Print the version of the server");
        Option optionVersion = OptionBuilder.create("version");
        serverCLIOptions.addOption(optionVersion);

        OptionBuilder.withArgName("port");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The port this server should listen on");
        Option optionServerPort = OptionBuilder.create("port");
        serverCLIOptions.addOption(optionServerPort);

        OptionBuilder.withArgName("level");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(
                "The verbosity this server should use during the logging process. 0=ALL, 1=TRACE, 2=DEBUG, 3=INFO, 4=WARN, 5=ERROR, 6=OFF");
        OptionBuilder.withType(Integer.class);
        Option optionLogLevel = OptionBuilder.create("logLevel");
        serverCLIOptions.addOption(optionLogLevel);

        OptionBuilder.withArgName("number");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(
                "The maximal number of threads that should be created in parallel when executing runs.");
        OptionBuilder.withType(Integer.class);
        Option optionNoOfThreads = OptionBuilder.create("numberOfThreads");
        serverCLIOptions.addOption(optionNoOfThreads);

        OptionBuilder.withArgName("check");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("Indicates, whether this server should check for run results in its repository.");
        OptionBuilder.withType(Boolean.class);
        Option checkForRunResults = OptionBuilder.create("checkForRunResults");
        serverCLIOptions.addOption(checkForRunResults);

        OptionBuilder.withDescription("Indicates, whether this server should connect to a database.");
        Option noDatabase = OptionBuilder.create("noDatabase");
        serverCLIOptions.addOption(noDatabase);

        OptionBuilder.withArgName("rServeHost");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The address on which Rserve is listening.");
        OptionBuilder.withType(String.class);
        Option rServeHost = OptionBuilder.create("rServeHost");
        serverCLIOptions.addOption(rServeHost);

        OptionBuilder.withArgName("rServePort");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("The port on which Rserve is listening.");
        OptionBuilder.withType(Integer.class);
        Option rServePort = OptionBuilder.create("rServePort");
        serverCLIOptions.addOption(rServePort);
    }

    /**
     * This variable holds the port this server will be listening on. It can be
     * specified by passing -hostport xxx to the {@link #main(String[])} method.
     */
    protected static int port;

    protected static boolean isRAvailable;

    private Logger log;

    /**
     * Every backend server has exactly one repository, which stores all the
     * data on the filesystem.
     */
    protected IRepository repository;

    /**
     * The number of clients connected to this server so far. This number is
     * used to give new clients a new number for authentication in case, several
     * users connect to the server.
     */
    protected int clientCount;

    /**
     * This method returns file objects that can be used to synchronize process
     * wide access to files.
     *
     * @param file The file object for which you want a common file object.
     * @return A common file object for the passed file, that is stored
     *         centrally such that synchronize operations on this file object affect all
     *         other methods, that also use this method.
     */
    public static File getCommonFile(final File file) {
        return FileUtils.getCommonFile(file);
    }

    /**
     * Instantiates a new backend server.
     *
     * @param absRepositoryPath The absolute path to the repository used by this
     *                          server.
     * @throws FileNotFoundException
     * @throws InvalidRepositoryException
     * @throws RepositoryAlreadyExistsException
     * @throws RepositoryConfigurationException
     * @throws RepositoryConfigNotFoundException
     * @throws InterruptedException
     * @throws DatabaseConnectException
     */
    public ClustevalBackendServer(final String absRepositoryPath)
            throws FileNotFoundException, RepositoryAlreadyExistsException,
                   InvalidRepositoryException, RepositoryConfigNotFoundException,
                   RepositoryConfigurationException, InterruptedException, DatabaseConnectException {
        this(new Repository(absRepositoryPath, null));
    }

    /**
     * Instantiates a new backend server and registers the server at the RMI
     * registry.
     *
     * @param repository The repository used by this server.
     * @throws InterruptedException
     */
    public ClustevalBackendServer(final IRepository repository) throws InterruptedException {
        this(repository, true);
    }

    /**
     * @param repository
     * @param registerServer
     * @throws InterruptedException
     */
    public ClustevalBackendServer(final IRepository repository, final boolean registerServer)
            throws InterruptedException {
        super();

        this.log = LoggerFactory.getLogger(this.getClass());

        this.repository = repository;

        this.log.info("Using repository at '" + this.repository.getBasePath() + "'");

        if (!registerServer || ClustevalBackendServer.registerServer(this)) {
            /*
             * Check, whether the repository is being initialized
             */
            if (this.repository.getSupervisorThread() == null) {
                this.repository.initialize();
            }
        }
    }

    /**
     * Gets the repository.
     *
     * @return The repository used by this server.
     * @see #repository
     */
    public IRepository getRepository() {
        synchronized (this.repository) {
            return this.repository;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.EvalServer#performRun(java.lang.String,
     * java.lang.String)
     */
    @Override
    public boolean performRun(final String clientId, final String runId) {
        boolean result = this.repository.getSupervisorThread().getRunScheduler().schedule(clientId, runId);
        return result;
    }

    @Override
    public boolean resumeRun(final String clientId, final String uniqueRunIdentifier) {
        boolean result = this.repository.getSupervisorThread().getRunScheduler().scheduleResume(clientId,
                uniqueRunIdentifier);
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.EvalServer#performRun(java.lang.String,
     * java.lang.String)
     */
    @Override
    public boolean terminateRun(final String clientId, final String runId) {
        boolean result = this.repository.getSupervisorThread().getRunScheduler().terminate(clientId, runId);
        return result;
    }

    /**
     * This method can be used to start a backend server. The args parameter can
     * contain options that specify the behaviour of the server:
     * <ul>
     * <li><b>-absRepositoryPath <absRepoPath></b>: An absolute path to the
     * repository</li>
     * <li><b>-port <port></b>: The port on which this server should listen</li>
     * </ul>
     *
     * @param args Arguments to control the behaviour of the server
     * @throws FileNotFoundException
     * @throws InvalidRepositoryException
     * @throws RepositoryAlreadyExistsException
     * @throws RepositoryConfigurationException
     * @throws RepositoryConfigNotFoundException
     * @throws InterruptedException
     * @throws DatabaseConnectException
     */
    public static void main(String[] args) throws FileNotFoundException, RepositoryAlreadyExistsException,
                                                  InvalidRepositoryException, RepositoryConfigNotFoundException, RepositoryConfigurationException,
                                                  InterruptedException, DatabaseConnectException {

        // bugfix for log4j warning
        //org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        org.apache.log4j.Logger.getRootLogger().addAppender(
                new org.apache.log4j.ConsoleAppender(new org.apache.log4j.PatternLayout("%d %-5p %c - %F:%L - %m%n")));

        CommandLineParser parser = new PosixParser();
        try {
            CommandLine cmd = parser.parse(serverCLIOptions, args);

            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("clustevalServer", "clusteval backend server " + VERSION, serverCLIOptions, "");
                System.exit(0);
            }

            if (cmd.hasOption("version")) {
                System.out.println(VERSION);
                System.exit(0);
            }

            if (cmd.getArgList().size() > 0) {
                throw new ParseException("Unknown parameters: " + Arrays.toString(cmd.getArgs()));
            }

            if (cmd.hasOption("port")) {
                port = Integer.parseInt(cmd.getOptionValue("port"));
            } else {
                port = 1099;
            }

            initLogging(cmd);

            if (cmd.hasOption("numberOfThreads")) {
                config.numberOfThreads = Integer.parseInt(cmd.getOptionValue("numberOfThreads"));
            }

            if (cmd.hasOption("checkForRunResults")) {
                config.setCheckForRunResults(Boolean.parseBoolean(cmd.getOptionValue("checkForRunResults")));
            }

            if (cmd.hasOption("noDatabase")) {
                config.setNoDatabase(true);
            }

            if (cmd.hasOption("rServeHost")) {
                config.rServeHost = cmd.getOptionValue("rServeHost");
            }

            if (cmd.hasOption("rServePort")) {
                config.rServePort = Integer.parseInt(cmd.getOptionValue("rServePort"));
            }

            Logger log = LoggerFactory.getLogger(ClustevalBackendServer.class);

            System.out.println("Starting clusteval server");
            System.out.println(VERSION);
            System.out.println("=========================");

            try {
                // try to establish a connection to R
                log.info("Attempting connection to Rserve on " + config.rServeHost + ":" + config.rServePort);
                @SuppressWarnings("unused")
                IRengine myRengine = new MyRengine("");
                log.info("Success");
                isRAvailable = true;
            } catch (RException e) {
                log.error("Connection to Rserve could not be established, "
                        + "please ensure that your Rserve instance is " + "running before starting this framework.");
                log.error("Functionality that requires R will not be available" + " until Rserve has been started.");
                isRAvailable = false;
            }

            @SuppressWarnings("unused")
            ClustevalBackendServer clusteringEvalFramework;
            if (cmd.hasOption("absRepoPath")) {
                clusteringEvalFramework = new ClustevalBackendServer(cmd.getOptionValue("absRepoPath"));
            } else {
                clusteringEvalFramework = new ClustevalBackendServer(new File("repository").getAbsolutePath());
            }

        } catch (ParseException e1) {
            System.err.println("Parsing failed.  Reason: " + e1.getMessage());

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("clustevalServer", "clusteval backend server " + VERSION, serverCLIOptions, "");
        }
    }

    /**
     * @return True if R is available through Rserve, false otherwise.
     */
    public static boolean isRAvailable() {
        return isRAvailable;
    }

    /**
     * This method is responsible for creating all the appender that are added
     * to the logger.
     * <p>
     * Three appenders are created:
     * <ul>
     * <li><b>ConsoleAppender</b>: Writes the logging output to the standard out
     * </li>
     * <li><b>FileAppender</b>: Writes the logging output as formatter text to
     * the file clustevalServer.log</li>
     * <li><b>FileAppender</b>: Writes the logging output in lilith binary
     * format to the file clustevalServer.lilith</li>
     * </ul>
     *
     * @param cmd The command line parameters including possible options of
     *            logging
     * @throws ParseException
     */
    private static void initLogging(CommandLine cmd) throws ParseException {
        Logger log = LoggerFactory.getLogger(ClustevalBackendServer.class);

        Level logLevel;
        if (cmd.hasOption("logLevel")) {
            switch (Integer.parseInt(cmd.getOptionValue("logLevel"))) {
                case 0:
                    logLevel = Level.ALL;
                    break;
                case 1:
                    logLevel = Level.TRACE;
                    break;
                case 2:
                    logLevel = Level.DEBUG;
                    break;
                case 3:
                    logLevel = Level.INFO;
                    break;
                case 4:
                    logLevel = Level.WARN;
                    break;
                case 5:
                    logLevel = Level.ERROR;
                    break;
                case 6:
                    logLevel = Level.OFF;
                    break;
                default:
                    throw new ParseException("The logLevel argument requires one of the value of [0,1,2,3,4,5,6]");
            }
        } else {
            logLevel = Level.INFO;
        }

        ch.qos.logback.classic.Logger logger = ((ch.qos.logback.classic.Logger) LoggerFactory
                .getLogger(Logger.ROOT_LOGGER_NAME));
        logger.setLevel(logLevel);

        ConsoleAppender<ILoggingEvent> consoleApp = (ConsoleAppender<ILoggingEvent>) logger.iteratorForAppenders()
                .next();
        consoleApp.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        consoleApp.setEncoder(new PatternLayoutEncoder());
        consoleApp.setWithJansi(true);
        PatternLayout layout = new PatternLayout();
        layout.getDefaultConverterMap().put("highlight", MyHighlightingCompositeConverter.class.getName());
        layout.setPattern("@localhost:" + port
                + " %date{dd MMM yyyy HH:mm:ss.SSS} %highlight([%thread] %-5level %logger{35} - %msg) %n");
        layout.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        layout.start();
        consoleApp.setLayout(layout);
        consoleApp.start();
        logger.addAppender(consoleApp);

        // file appender for clustevalServer.log plaintext file
        FileAppender<ILoggingEvent> fileApp = new FileAppender<>();
        fileApp.setName("serverLogFile");
        String logFilePath = FileUtils.buildPath(System.getProperty("user.dir"), "clustevalServer.log");
        fileApp.setFile(logFilePath);

        fileApp.setAppend(true);
        fileApp.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        fileApp.setEncoder(new PatternLayoutEncoder());
        layout = new PatternLayout();
        layout.setPattern(
                "@localhost:" + port + " %date{dd MMM yyyy HH:mm:ss.SSS} [%thread] %-5level %logger{35} - %msg%n");
        layout.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        layout.start();
        fileApp.setLayout(layout);
        fileApp.start();
        logger.addAppender(fileApp);

        // file appender for clustevalServer.lilith binary file
        // removed 30.01.2013
        // FileAppender fileAppLilith = new FileAppender();
        // fileAppLilith.setName("serverLogFileLilith");
        // logFilePath = FileUtils.buildPath(System.getProperty("user.dir"),
        // "clustevalServer.lilith");
        // fileAppLilith.setFile(logFilePath);
        //
        // fileAppLilith.setAppend(true);
        // fileAppLilith.setContext((LoggerContext) LoggerFactory
        // .getILoggerFactory());
        // ClassicLilithEncoder encoder = new ClassicLilithEncoder();
        // encoder.setIncludeCallerData(true);
        // fileAppLilith.setEncoder(encoder);
        //
        // fileAppLilith.start();
        // logger.addAppender(fileAppLilith);
        log.debug("Using log level " + logLevel);
    }

    /**
     * A helper method for {@link #ClusteringEvalFramework(Repository)}, which
     * registers the new backend server instance in the RMI registry.
     *
     * @param framework The backend server to register.
     * @return True, if the server has been registered successfully
     */
    protected static boolean registerServer(ClustevalBackendServer framework) {
        Logger log = LoggerFactory.getLogger(ClustevalBackendServer.class);

        try {
            LocateRegistry.createRegistry(port);

            IBackendServer stub = (IBackendServer) UnicastRemoteObject.exportObject(framework, port);
            Registry registry = LocateRegistry.getRegistry(port);
            registry.bind("EvalServer", stub);
            log.info("Framework up and listening on port " + port);
            log.info("Used number of processors: " + config.numberOfThreads);
            return true;
        } catch (AlreadyBoundException | RemoteException e) {
            log.error("Another instance is already running...", e);
            return false;
        }
    }

    /**
     * A helper method for {@link #shutdown(String, long)}, which terminates
     * this framework after a certain timeout.
     *
     * <p>
     * This method first interrupts the supervisor thread (see
     * {@link SupervisorThread}) and waits for its termination until the timeout
     * was reached.
     *
     * <p>
     * Then the backend server instance is unregistered from the RMI registry
     * and the repository of this framework is removed from the set of all
     * registered repositories.
     *
     * @throws InterruptedException
     *
     */
    private void terminate(final long forceTimeout) throws InterruptedException {
        this.repository.terminateSupervisorThread();

        try {
            Registry registry = LocateRegistry.getRegistry(port);
            registry.unbind("EvalServer");
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException e) {
            e.printStackTrace();
        } catch (AccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        /*
         * unregister repository
         */
        RepositoryController.getInstance().unregister(this.repository);
        // should work without, just to be sure
        // System.exit(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.EvalServer#getClientId()
     */
    @SuppressWarnings("unused")
    @Override
    public String getClientId() throws RemoteException {
        return "" + this.clientCount++;
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.EvalServer#getRunStatusForClientId(java.lang.String)
     */
    @SuppressWarnings("unused")
    @Override
    public Map<String, Pair<RUN_STATUS, Float>> getRunStatusForClientId(String clientId) throws RemoteException {
        return this.repository.getSupervisorThread().getRunScheduler().getRunStatusForClientId(clientId);
    }

    // TODO
    @Override
    public Map<String, Pair<Pair<RUN_STATUS, Float>, Map<Pair<String, String>, Pair<Double, Map<String, Pair<Map<String, String>, String>>>>>> getOptimizationRunStatusForClientId(
            String clientId) throws RemoteException {
        return this.repository.getSupervisorThread().getRunScheduler().getOptimizationRunStatusForClientId(clientId);
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.EvalServer#getRuns()
     */
    @Override
    public Collection<String> getRuns() {
        Collection<String> result = new HashSet<>();
        for (Run run : this.repository.getCollectionStaticEntities(Run.class)) {
            result.add(run.getName());
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.EvalServer#getDataSets()
     */
    @Override
    public Collection<String> getDataSets() {
        Collection<String> result = new HashSet<>();
        for (DataSet dataSet : this.repository.getCollectionStaticEntities(DataSet.class)) {
            result.add(dataSet.getFullName());
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.EvalServer#getPrograms()
     */
    @Override
    public Collection<String> getPrograms() {
        Collection<String> result = new HashSet<String>();
        for (Program program : this.repository.getCollectionStaticEntities(Program.class)) {
            result.add(program.getMajorName());
        }
        return result;
    }

    // TODO: only certain clientids should be able to shutdown the framework
    @SuppressWarnings("unused")
    @Override
    public void shutdown(final String clientId, final long timeOut) {
        log.info("Shutting down framework...");
        try {
            this.terminate(timeOut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return True, if this framework is still running and the corresponding
     *         supervisor thread hasn't been interrupted.
     */
    public boolean isRunning() {
        return this.repository.getSupervisorThread().isAlive();
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.EvalServer#getRunResumes()
     */
    @SuppressWarnings("unused")
    @Override
    public Collection<String> getRunResumes() throws RemoteException {
        Collection<String> result = new HashSet<String>(this.repository.getRunResumes());
        return result;
    }

    @SuppressWarnings("unused")
    @Override
    public Collection<String> getRunResults() throws RemoteException {
        Collection<String> result = new HashSet<String>(this.repository.getRunResultIdentifier());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.EvalServer#getRunResults(java.lang.String,
     * java.lang.String)
     */
    @SuppressWarnings("unused")
    @Override
    public Map<Pair<String, String>, Map<String, Double>> getRunResults(String uniqueRunIdentifier)
            throws RemoteException {
        Map<Pair<String, String>, Map<String, Double>> result = new HashMap<>();

        List<ParameterOptimizationResult> list = new ArrayList<>();
        try {
            ParameterOptimizationResult.parseFromRunResultFolder2(repository,
                    new File(FileUtils.buildPath(repository.getBasePath(RunResult.class), uniqueRunIdentifier)), list,
                    false, false, false);
            for (ParameterOptimizationResult r : list) {
                String dataConfig = r.getMethod().getDataConfig().getName();
                String programConfig = r.getMethod().getProgramConfig().getName();
                Map<String, Double> measureToOptimalQuality = new HashMap<>();
                for (ClusteringEvaluation measure : r.getOptimalParameterSets().keySet()) {
                    measureToOptimalQuality.put(measure.getClass().getSimpleName(),
                            r.get(r.getOptimalParameterSets().get(measure)).get(measure).getValue());
                }
                result.put(Pair.getPair(dataConfig, programConfig), measureToOptimalQuality);
            }
        } catch (GoldStandardConfigurationException | DataSetConfigurationException |
                DataSetNotFoundException | DataSetConfigNotFoundException |
                GoldStandardConfigNotFoundException | DataConfigurationException |
                DataConfigNotFoundException | IOException | UnknownRunResultFormatException |
                UnknownDataSetFormatException | UnknownClusteringQualityMeasureException |
                InvalidRunModeException | UnknownParameterOptimizationMethodException |
                NoOptimizableProgramParameterException | UnknownProgramParameterException |
                UnknownGoldStandardFormatException | InvalidConfigurationFileException |
                RepositoryAlreadyExistsException | InvalidRepositoryException |
                NoRepositoryFoundException | GoldStandardNotFoundException |
                InvalidOptimizationParameterException | RunException |
                UnknownDataStatisticException | UnknownProgramTypeException |
                UnknownRProgramException | IncompatibleParameterOptimizationMethodException |
                UnknownDistanceMeasureException | UnknownRunStatisticException |
                RepositoryConfigNotFoundException | RepositoryConfigurationException |
                ConfigurationException | RegisterException | NumberFormatException | NoDataSetException | UnknownRunDataStatisticException |
                RunResultParseException | UnknownDataPreprocessorException |
                IncompatibleDataSetConfigPreprocessorException | UnknownContextException |
                IncompatibleContextException | UnknownParameterType | InterruptedException |
                UnknownRunResultPostprocessorException | UnknownDataRandomizerException e) {
            e.printStackTrace();
        } catch (UnknownProviderException ex) {
            Exceptions.printStackTrace(ex);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * serverclient.IBackendServer#setLogLevel(ch.qos.logback.classic.Level)
     */
    @SuppressWarnings("unused")
    @Override
    public void setLogLevel(Level logLevel) throws RemoteException {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(logLevel);
    }

    /**
     * Change the log level of this JVM.
     *
     * @param logLevel The new log level
     */
    public static void logLevel(Level logLevel) {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(logLevel);
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.IBackendServer#getDataSetGenerators()
     */
    @Override
    public Collection<String> getDataSetGenerators() {
        Collection<String> result = new HashSet<>();

        Collection<Class<? extends DataSetGenerator>> dataSetGenerators = this.repository
                .getClasses(DataSetGenerator.class);

        for (Class<? extends DataSetGenerator> generatorClass : dataSetGenerators) {
            result.add(generatorClass.getSimpleName());
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.IBackendServer#getDataSetRandomizers()
     */
    @Override
    public Collection<String> getDataRandomizers() {
        Collection<String> result = new HashSet<>();

        Collection<Class<? extends DataRandomizer>> dataRandomizers = this.repository.getClasses(DataRandomizer.class);

        for (Class<? extends DataRandomizer> randomizerClass : dataRandomizers) {
            result.add(randomizerClass.getSimpleName());
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.IBackendServer#getOptionsForDataSetGenerator(java.lang.
     * String )
     */
    @Override
    public Options getOptionsForDataSetGenerator(String generatorName) {
        try {

            DataSetGenerator generator = DataSetGenerator.parseFromString(repository, generatorName);
            return generator.getAllOptions();
        } catch (SecurityException | IllegalArgumentException | UnknownDataSetGeneratorException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.IBackendServer#generateDataSet(java.lang.String,
     * java.lang.String[])
     */
    @SuppressWarnings("unused")
    @Override
    public boolean generateDataSet(String generatorName, String[] args) throws RemoteException {
        try {
            DataSetGenerator generator = DataSetGenerator.parseFromString(this.repository, generatorName);
            generator.generate(args);
        } catch (UnknownDataSetGeneratorException | ParseException |
                DataSetGenerationException | GoldStandardGenerationException |
                InterruptedException | RepositoryObjectDumpException |
                RegisterException | UnknownDistanceMeasureException e) {
            e.printStackTrace();
        } catch (UnknownProviderException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.IBackendServer#getQueue()
     */
    @SuppressWarnings("unused")
    @Override
    public Collection<String> getQueue() throws RemoteException {
        final Collection<String> result = this.repository.getSupervisorThread().getRunScheduler().getQueue();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.serverclient.IBackendServer#getActiveThreads()
     */
    @Override
    public Map<String, Triple<String, String, Long>> getActiveThreads() throws RemoteException {
        Map<String, Triple<String, String, Long>> result = new HashMap<>();

        IScheduler scheduler = this.getRepository().getSupervisorThread().getRunScheduler();
        Map<Thread, IterationRunnable<? extends IterationWrapper>> map = scheduler.getActiveIterationRunnables();
        for (Map.Entry<Thread, IterationRunnable<? extends IterationWrapper>> e : map.entrySet()) {
            long startTime = e.getValue().getStartTime();

            String name = "";
            String status;
            if (e.getValue() instanceof ExecutionIterationRunnable) {
                ExecutionRunRunnable r = (ExecutionRunRunnable) (e.getValue().getParentRunnable());
                status = ((ExecutionIterationRunnable) e.getValue()).getIterationNumber() + "";
                name = r.getRun().getName() + ": " + r.getProgramConfig() + "," + r.getDataConfig();
            } else if (e.getValue() instanceof DataAnalysisIterationRunnable) {
                DataAnalysisRunRunnable r = (DataAnalysisRunRunnable) (e.getValue().getParentRunnable());

                status = ((AnalysisIterationRunnable) e.getValue()).getStatistic().getName();
                name = r.getRun().getName() + ": " + status + "," + r.getDataConfig();
            } else if (e.getValue() instanceof RunAnalysisIterationRunnable) {
                RunAnalysisRunRunnable r = (RunAnalysisRunRunnable) (e.getValue().getParentRunnable());

                status = ((AnalysisIterationRunnable) e.getValue()).getStatistic().getName();
                name = r.getRun().getName() + ": " + status + "," + r.getRunIdentifier();
            }
            // TODO
            // else if (e.getValue() instanceof
            // RunDataAnalysisIterationRunnable) {
            // RunDataAnalysisRunRunnable r = (RunDataAnalysisRunRunnable) (e
            // .getValue().getParentRunnable());
            //
            // status = ((AnalysisIterationRunnable) e.getValue())
            // .getStatistic().getAlias();
            // name = r.getRun().getName() + ": " + r.get;
            // }

            result.put(e.getKey().getName(), Triple.getTriple(name, "", startTime));
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * serverclient.IBackendServer#getOptionsForDataRandomizer(java.lang.String
     * )
     */
    @Override
    public Options getOptionsForDataRandomizer(String randomizerName) {
        try {

            DataRandomizer randomizer = DataRandomizer.parseFromString(repository, randomizerName);
            return randomizer.getAllOptions();
        } catch (SecurityException | IllegalArgumentException |
                UnknownDataRandomizerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see serverclient.IBackendServer#randomizeDataConfig(java.lang.String,
     * java.lang.String[])
     */
    @SuppressWarnings("unused")
    @Override
    public boolean randomizeDataConfig(String randomizerName, String[] args) throws RemoteException {
        try {
            DataRandomizer randomizer = DataRandomizer.parseFromString(this.repository, randomizerName);
            randomizer.randomize(args);
        } catch (UnknownDataRandomizerException | DataRandomizeException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.serverclient.IBackendServer#setThreadNumber(int)
     */
    @Override
    public void setThreadNumber(int threadNumber) throws RemoteException {
        this.repository.getSupervisorThread().getRunScheduler().updateThreadPoolSize(threadNumber);
    }
}
