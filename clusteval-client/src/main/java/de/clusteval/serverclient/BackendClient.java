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
package de.clusteval.serverclient;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import de.clusteval.api.run.RUN_STATUS;
import de.clusteval.framework.IBackendServer;
import de.clusteval.utils.FileUtils;
import de.clusteval.utils.Formatter;
import de.clusteval.utils.ArraysExt;
import de.clusteval.api.Pair;
import de.clusteval.api.Triple;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A backend client can give commands to the backend server (see
 * {@link IBackendServer}).
 *
 * @author Christian Wiwie
 *
 */
public class BackendClient extends Thread {

	protected static String VERSION;

	/**
	 * This variable holds the command line options of the backend server.
	 */
	public static Options clientCLIOptions = new Options();

	static {
		// read properties file with version number
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream("client.date");
		try {
			prop.load(stream);
			VERSION = "Jar built: " + prop.getProperty("buildtime") + "\nGit:\n\tCommit: " + prop.getProperty("gitrev")
					+ "\n\tBranch: " + prop.getProperty("gitbranch") + "\n\tRepository: " + prop.getProperty("gitrepo");
		} catch (IOException e) {
			e.printStackTrace();
		}

		OptionBuilder.withArgName("level");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription(
				"The verbosity this client should use during its execution. 0=ALL, 1=TRACE, 2=DEBUG, 3=INFO, 4=WARN, 5=ERROR, 6=OFF");
		OptionBuilder.withType(Integer.class);
		Option option = OptionBuilder.create("logLevel");
		clientCLIOptions.addOption(option);

		OptionBuilder.withDescription("Print this help and usage information");
		Option optionHelp = OptionBuilder.create("help");
		clientCLIOptions.addOption(optionHelp);

		OptionBuilder.withDescription("Print the version of this client");
		Option optionVersion = OptionBuilder.create("version");
		clientCLIOptions.addOption(optionVersion);

		// init valid command line options
		OptionBuilder.withArgName("ip");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("The ip address of the backend server to connect to.");
		option = OptionBuilder.create("ip");
		clientCLIOptions.addOption(option);

		OptionBuilder.withArgName("port");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("The port number of the backend server to connect to.");
		option = OptionBuilder.create("port");
		clientCLIOptions.addOption(option);

		OptionBuilder.withArgName("id");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("The client id for identification purposes of this client with the server.");
		option = OptionBuilder.create("clientId");
		clientCLIOptions.addOption(option);

		OptionBuilder.withDescription("Queries the available datasets from the server.");
		option = OptionBuilder.create("getDataSets");
		clientCLIOptions.addOption(option);

		OptionBuilder.withDescription("Queries the available programs from the server.");
		option = OptionBuilder.create("getPrograms");
		clientCLIOptions.addOption(option);

		OptionBuilder.withDescription("Queries the available runs from the server.");
		option = OptionBuilder.create("getRuns");
		clientCLIOptions.addOption(option);

		OptionBuilder.withDescription("Queries the available run resumes from the server.");
		option = OptionBuilder.create("getRunResumes");
		clientCLIOptions.addOption(option);

		OptionBuilder.withDescription("Queries the available run results from the server.");
		option = OptionBuilder.create("getRunResults");
		clientCLIOptions.addOption(option);

		OptionBuilder.withArgName("runName");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Queries the status of a certain run");
		option = OptionBuilder.create("getRunStatus");
		clientCLIOptions.addOption(option);

		OptionBuilder.withArgName("runName");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Queries the optimization status of a certain run");
		option = OptionBuilder.create("getOptRunStatus");
		clientCLIOptions.addOption(option);

		OptionBuilder.withDescription("Gets the enqueued runs and run resumes of the backend server");
		option = OptionBuilder.create("getQueue");
		clientCLIOptions.addOption(option);

		OptionBuilder.withDescription(
				"Gets the currently active threads and the corresponding iterations which they perform");
		option = OptionBuilder.create("getActiveThreads");
		clientCLIOptions.addOption(option);

		OptionBuilder.withArgName("threadNumber");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Sets the maximal number of parallel threads.");
		option = OptionBuilder.create("setThreadNumber");
		clientCLIOptions.addOption(option);

		OptionBuilder.withArgName("runName");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Performs a certain run (if not already running)");
		option = OptionBuilder.create("performRun");
		clientCLIOptions.addOption(option);

		OptionBuilder.withArgName("runName");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Resumes a certain run that was started and terminated earlier.");
		option = OptionBuilder.create("resumeRun");
		clientCLIOptions.addOption(option);

		OptionBuilder.withArgName("runName");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Terminates a certain run that was started earlier.");
		option = OptionBuilder.create("terminateRun");
		clientCLIOptions.addOption(option);

		OptionBuilder.withDescription("Shut down ClustEval.");
		option = OptionBuilder.create("shutdown");
		clientCLIOptions.addOption(option);

		OptionBuilder.withDescription("The client will wait until all runs that this client started are finished.");
		option = OptionBuilder.create("waitForRuns");
		clientCLIOptions.addOption(option);

		OptionBuilder.withArgName("generatorName");
		OptionBuilder.hasArg();
		OptionBuilder
				.withDescription("Generates a dataset using the generator " + "with the given name. Parameters for "
						+ "this generator can be specified after " + "this command has been executed.");
		option = OptionBuilder.create("generateDataSet");
		clientCLIOptions.addOption(option);

		OptionBuilder.withArgName("randomizerName");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription(
				"Randomizes a dataconfig using the randomizer " + "with the given name. Parameters for "
						+ "this randomizer can be specified after " + "this command has been executed.");
		option = OptionBuilder.create("randomizeDataConfig");
		clientCLIOptions.addOption(option);
	}

	/**
	 * The ip address on which this client should look for the server.
	 */
	private String ip;

	/**
	 * The port on which this client should connect to the server.
	 */
	private String port;

	/**
	 * The client id this client. Either the client id was specified in the
	 * command line options, or the server told this client its id.
	 *
	 * <p>
	 * The first scenario is helpful, if a client wants to connect multiple
	 * times to the server. After a client reconnects to the server he is only
	 * able to get the status of those runs, that belong to his client id.
	 * Therefore in this case, the client needs to reconnect with the same id.
	 */
	private String clientId;

	/**
	 * The server this client connected to.
	 */
	private IBackendServer server;

	private Logger log;

	/**
	 * A wrapper object holding the parsed command line parameters of this
	 * client. There are some connection parameters:
	 * <ul>
	 * <li><b>ip</b>: The ip address of the server</li>
	 * <li><b>port</b>: The port of the server</li>
	 * <li><b>clientId</b>: The id this client should use</li>
	 * </ul>
	 * There are other command line parameters, that cause the client to
	 * terminate immediately after the command has been executed.
	 * <ul>
	 * <li><b>getDataSets</b>: This tells the client to get and print the
	 * available datasets of the server.</li>
	 * <li><b>getPrograms</b>: This tells the client to get and print the
	 * available programs of the server.</li>
	 * <li><b>getRuns</b>: This tells the client to get and print the available
	 * runs of the server.</li>
	 * <li><b>getRunResumes</b>: This tells the client to get and print all the
	 * run result directories contained in the repository of this server. Those
	 * run result directories can be resumed, if they were terminated before.
	 * </li>
	 * <li><b>getRunResults</b>: This tells the client to get and print all the
	 * run result directories contained in the repository of this server, that
	 * contain a clusters subfolder and at least one *.complete file containing
	 * results (can be slow if many run result folders are present).</li>
	 * <li><b>getRunStatus</b>: This tells the client to get the status and
	 * percentage (if) of a certain run.</li>
	 * <li><b>performRun XXXX</b>: This tells the client to perform a run with a
	 * certain name.</li>
	 * <li><b>resumeRun XXXX</b>: This tells the client to resume a run
	 * previously performed identified by its run result identifier.</li>
	 * <li><b>terminateRun XXXX</b>: This tells the client to terminate the
	 * execution of a run with a certain name.</li>
	 * <li><b>shutdown</b>: This tells the client to shutdown the framework.
	 * </li>
	 * <li><b>waitForRuns</b>: This option can be used together with
	 * getRunStatus, in order to cause the client to wait until the run has
	 * finished its execution.</li>
	 * </ul>
	 */
	private CommandLine params;

	/**
	 * This attribute holds only those arguments not yet parsed. Those can be
	 * passed on to another commands like dataset generators.
	 */
	private String[] args;

	/**
	 * Instantiates a new eval client.
	 *
	 * <p>
	 * If no port is specified in the options, the default 1099 will be used.
	 *
	 * <p>
	 * If no ip is specified, the localhost will be used.
	 *
	 * <p>
	 * If no clientId is specified, the client will retrieve a new one from the
	 * server.
	 *
	 * @param params
	 *            The command line parameters for this client (see
	 *            {@link #params}).
	 *
	 * @throws ConnectException
	 * @throws ParseException
	 */
	public BackendClient(final String[] params) throws ConnectException, ParseException {
		super();

		this.log = LoggerFactory.getLogger(this.getClass());

		try {
			this.params = parseParams(params, true);
		} catch (ParseException e) {

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ClustEvalBackendClient.jar", "", clientCLIOptions, "Available commands are:", false);

			throw e;
		}

		/**
		 * Keep a list of not parsed arguments. This has to be done, to maintain
		 * the - of options passed, since they are not kept in the
		 * CommandLine#args attribute.
		 */
		List<String> notParsedArgs = new ArrayList<String>();
		for (String s : params) {
			String raw;
			if (s.startsWith("-"))
				raw = s.substring(1);
			else
				raw = s;

			if (this.params.getArgList().contains(raw) || this.params.getArgList().contains(s))
				notParsedArgs.add(s);
		}
		this.args = notParsedArgs.toArray(new String[0]);

		if (this.params.hasOption("clientId"))
			this.clientId = this.params.getOptionValue("clientId");
		if (this.params.hasOption("port"))
			this.port = this.params.getOptionValue("port");
		else
			// default port
			this.port = "1099";

		try {
			Registry registry;
			if (this.ip == null)
				registry = LocateRegistry.getRegistry(null, Integer.parseInt(this.port));
			else
				registry = LocateRegistry.getRegistry(this.ip, Integer.parseInt(this.port));
			server = (IBackendServer) registry.lookup("EvalServer");
			if (this.clientId == null)
				this.clientId = server.getClientId();
			this.log.debug("Connected to server using ClientId=" + this.clientId);
		} catch (ConnectException e) {
			this.log.error("Could not connect to server");
			throw e;
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		this.start();
	}

	/**
	 * A helper method for {@link #main(String[])} to parse the command line
	 * parameters and put them into a wrapper object.
	 *
	 * @param params
	 *            The input command line parameters.
	 * @param stopAtNonOptions
	 *            A boolean indicating, whether to throw an exception on unknown
	 *            options.
	 * @return A wrapper object containing the parsed and valid parameters.
	 * @throws ParseException
	 */
	private static CommandLine parseParams(String[] params, boolean stopAtNonOptions) throws ParseException {

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(clientCLIOptions, params, stopAtNonOptions);

		Level logLevel;
		if (cmd.hasOption("logLevel")) {
			switch (Integer.parseInt(cmd.getOptionValue("logLevel"))) {
				case 0 :
					logLevel = Level.ALL;
					break;
				case 1 :
					logLevel = Level.TRACE;
					break;
				case 2 :
					logLevel = Level.DEBUG;
					break;
				case 3 :
					logLevel = Level.INFO;
					break;
				case 4 :
					logLevel = Level.WARN;
					break;
				case 5 :
					logLevel = Level.ERROR;
					break;
				case 6 :
					logLevel = Level.OFF;
					break;
				default :
					throw new ParseException("The logLevel argument requires one of the value of [0,1,2,3,4,5,6]");
			}
		} else {
			logLevel = Level.INFO;
		}
		((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(logLevel);

		return cmd;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		boolean checkForRunStatus = false;
		boolean checkForOptRunStatus = false;
		try {
			if (params.hasOption("performRun")) {
				this.performRun(params.getOptionValue("performRun"));
			}
			if (params.hasOption("resumeRun")) {
				this.resumeRun(params.getOptionValue("resumeRun"));
			}
			if (params.hasOption("terminateRun")) {
				this.terminateRun(params.getOptionValue("terminateRun"));
			}
			if (params.hasOption("getRuns")) {
				System.out.println("Runs: " + this.getRuns());
			}
			if (params.hasOption("getRunResumes")) {
				System.out.println("RunResumes: " + this.getRunResumes());
			}
			if (params.hasOption("getQueue")) {
				System.out.println("Queue: " + this.getQueue());
			}
			if (params.hasOption("getActiveThreads")) {
				Map<String, Triple<String, String, Long>> activeThreads = this.server.getActiveThreads();
				if (activeThreads.isEmpty())
					System.out.println("No active threads");
				else {
					System.out.println("Active threads:");
					System.out.format("%10s%20s%50s%30s%40s%10s%20s\n", "Thread #", "Thread", "Run", "ProgramConfig",
							"DataConfig", "Iteration", "Running time");
					List<String> threadNames = new ArrayList<String>(activeThreads.keySet());
					Collections.sort(threadNames);
					int i = 1;
					for (String t : threadNames) {
						Triple<String, String, Long> value = activeThreads.get(t);
						String[] split1 = value.getFirst().split(": ");
						String[] split2 = split1[1].split(",");

						if (value.getSecond().equals("-1")) {
							System.out.format("%10d%20s%50s%30s%40s%10s%20s\n", i++, t, split1[0], split2[0], split2[1],
									"isoMDS",
									Formatter.formatMsToDuration(System.currentTimeMillis() - value.getThird(), false));
						} else if (value.getSecond().equals("-2")) {
							System.out.format("%10d%20s%50s%30s%40s%10s%20s\n", i++, t, split1[0], split2[0], split2[1],
									"PCA",
									Formatter.formatMsToDuration(System.currentTimeMillis() - value.getThird(), false));
						} else
							System.out.format("%10d%20s%50s%30s%40s%10s%20s\n", i++, t, split1[0], split2[0], split2[1],
									value.getSecond(),
									Formatter.formatMsToDuration(System.currentTimeMillis() - value.getThird(), false));
					}
				}
			}
			if (params.hasOption("setThreadNumber")) {
				this.server.setThreadNumber(Integer.valueOf(params.getOptionValue("setThreadNumber")));
			}
			if (params.hasOption("getRunResults")) {
				Map<Pair<String, String>, Map<String, Double>> result = this
						.getRunResults(params.getOptionValue("getRunResults"));
				boolean first = true;
				for (Pair<String, String> p : result.keySet()) {
					if (first) {
						for (String m : result.get(p).keySet()) {
							System.out.format("%-30s", "");
							System.out.print("\t" + m);
						}
						System.out.println();
					}
					System.out.format("%-30s", "(" + p.getFirst() + "," + p.getSecond() + ")");
					for (String m : result.get(p).keySet()) {
						System.out.println("\t" + result.get(p).get(m));
					}
					first = false;
				}
			}
			if (params.hasOption("getDataSets")) {
				System.out.println("DataSets: " + this.getDataSets());
			}
			if (params.hasOption("getPrograms")) {
				System.out.println("Programs: " + this.getPrograms());
			}
			if (params.hasOption("getRunStatus")) {
				checkForRunStatus = true;
			}
			if (params.hasOption("getOptRunStatus")) {
				checkForOptRunStatus = true;
			}
			if (params.hasOption("shutdown")) {
				this.shutdownFramework();
			}
			if (params.hasOption("generateDataSet")) {
				this.generateDataSet();
			}
			if (params.hasOption("randomizeDataConfig")) {
				String randomizerName = params.getOptionValue("randomizeDataConfig");

				CommandLineParser parser = new PosixParser();
				Options options = getOptionsForDataRandomizer(randomizerName);

				try {
					parser.parse(options, this.args);
					this.server.randomizeDataConfig(randomizerName, this.args);
				} catch (ParseException e1) {
					try {
						reader.println();
						HelpFormatter formatter = new HelpFormatter();
						formatter.setOptionComparator(new MyOptionComparator());
						formatter.printHelp("randomizeDataConfig " + randomizerName, options, true);
						reader.println();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			if (checkForRunStatus) {
				try {
					String runName = params.getOptionValue("getRunStatus");

					Map<String, Pair<RUN_STATUS, Float>> status = null;
					if ((status = this.getMyRunStatus()) != null && status.size() > 0) {
						RUN_STATUS newStatus;
						Float percent;
						if (!status.containsKey(runName)) {
							log.info("No run with name " + runName + " running.");
							return;
						}
						newStatus = status.get(runName).getFirst();
						percent = status.get(runName).getSecond();
						System.out.println();
						System.out.print("\r" + newStatus + " " + String.format("%.3f", percent) + "%");
					}
					System.out.println();
				} catch (ConnectException e2) {
					this.log.warn("The server terminated the connection...");
				} catch (RemoteException e2) {
					e2.printStackTrace();
				}
			}

			if (checkForOptRunStatus) {
				try {

					String runName = params.getOptionValue("getOptRunStatus");

					// runId ->
					// ((Status,%),(ProgramConfig,DataConfig)->(QualityMeasure->(ParameterSet->Quality)))
					Map<String, Pair<Pair<RUN_STATUS, Float>, Map<Pair<String, String>, Pair<Double, Map<String, Pair<Map<String, String>, String>>>>>> optStatus = null;
					if ((optStatus = this.getMyOptimizationRunStatus()) != null && optStatus.size() > 0) {
						RUN_STATUS newStatus;
						Float percent;

						if (!optStatus.containsKey(runName)) {
							log.info("No run with name " + runName + " running.");
							return;
						}
						newStatus = optStatus.get(runName).getFirst().getFirst();
						percent = optStatus.get(runName).getFirst().getSecond();
						System.out.println();
						System.out.println("\r Status:\t" + newStatus + " " + String.format("%.3f", percent) + "%");
						Map<Pair<String, String>, Pair<Double, Map<String, Pair<Map<String, String>, String>>>> qualities = optStatus
								.get(runName).getSecond();

						// print the quality measures; just take them from the
						// first pair of programConfig and dataConfig (runnable)
						String[] qualityMeasures = qualities.values().iterator().next().getSecond().keySet()
								.toArray(new String[0]);
						Arrays.sort(qualityMeasures);
						int pos = 0;
						while (true) {
							boolean foundMeasure = false;
							for (String measure : qualityMeasures) {
								if (pos < measure.length()) {
									System.out.printf("\t%s", measure.charAt(pos));
									foundMeasure = true;
								} else
									System.out.print("\t");
							}
							System.out.println();
							if (!foundMeasure)
								break;
							pos++;
						}

						// 06.06.2014: added sets to keep order when printing
						// the results
						Set<String> programConfigs = new HashSet<String>();
						Set<String> dataConfigs = new HashSet<String>();

						for (Pair<String, String> pcDcPair : qualities.keySet()) {
							programConfigs.add(pcDcPair.getFirst());
							dataConfigs.add(pcDcPair.getSecond());
						}

						for (String programConfig : programConfigs) {
							System.out.printf("%s:\n", programConfig);
							for (String dataConfig : dataConfigs) {
								Pair<String, String> pcDcPair = Pair.getPair(programConfig, dataConfig);
								Map<String, Pair<Map<String, String>, String>> qualitiesPcDc = qualities.get(pcDcPair)
										.getSecond();
								System.out.printf("-- %s:\t\t\tStatus:\t%.1f%%\n", dataConfig,
										qualities.get(pcDcPair).getFirst());
								for (String measure : qualityMeasures) {
									if (!qualitiesPcDc.containsKey(measure)) {
										System.out.print("\t");
										continue;
									}
									String quality = qualitiesPcDc.get(measure).getSecond();
									if (quality.equals("NT"))
										System.out.print("\tNT");
									else {
										double qualityDouble = Double.valueOf(quality);
										if (Double.isInfinite(qualityDouble))
											System.out.printf("\t%s%s", qualityDouble < 0 ? "-" : "", "Inf");
										else
											System.out.printf("\t%.4f", qualityDouble);
									}
								}
								System.out.println();
							}
						}
					}
					System.out.println();
				} catch (ConnectException e2) {
					this.log.warn("The server terminated the connection...");
				} catch (RemoteException e2) {
					e2.printStackTrace();
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws RemoteException
	 *
	 */
	private void generateDataSet() throws RemoteException {
		String generatorName = params.getOptionValue("generateDataSet");

		CommandLineParser parser = new PosixParser();
		Options options = getOptionsForDataSetGenerator(generatorName);

		try {
			List<String[]> parameterSets = splitParameterValues(options, this.args);

			for (String[] vals : parameterSets) {
				parser.parse(options, vals);
				this.server.generateDataSet(generatorName, vals);
			}
		} catch (ParseException e1) {
			try {
				reader.println();
				HelpFormatter formatter = new HelpFormatter();
				formatter.setOptionComparator(new MyOptionComparator());
				formatter.printHelp("generateDataSet " + generatorName, options, true);
				reader.println();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * This method takes the original parameters, where one parameter value can
	 * contain semicolon separated values. It then creates one parameter set for
	 * each possible combination of parameter value. <br>
	 * We assume parameter values to be separated by semicolon. And we assume
	 * parameter names in the fileName and alias to contain placeholders of the
	 * form %paramName%. <br>
	 * The fileName and alias parameters have to contain all parameter names,
	 * such that the resulting names are unique.
	 *
	 * @param args2
	 * @return
	 * @throws ParseException
	 */
	private List<String[]> splitParameterValues(final Options options, final String[] args2) throws ParseException {
		Map<String, String[]> splitValues = new HashMap<String, String[]>();

		Set<String> paramsRequiredInName = new HashSet<String>();

		String currParam = null;

		for (int i = 0; i < args2.length; i++) {
			String origVal = args2[i];

			if (origVal.startsWith("-")) {
				// if it is a switch parameter, we can denote it with a ; at the
				// end to indicate,
				// that we want to treat it as present and non present.
				String[] s = origVal.substring(1).split(";");

				currParam = s[0];

				// switch parameter
				if (!(options.getOption(currParam).hasArg())) {
					if (origVal.contains(";")) {
						splitValues.put(currParam, new String[]{"true", "false"});
						paramsRequiredInName.add(currParam);
					} else
						splitValues.put(currParam, new String[]{"true"});
				}
			} else {
				String[] valSplit = origVal.split(";");
				splitValues.put(currParam, valSplit);
				if (valSplit.length > 1)
					paramsRequiredInName.add(currParam);
			}
		}

		// check, that the fileName and alias parameter contain all
		// parameters with more than 1 value;
		if (!splitValues.containsKey("fileName") || !splitValues.containsKey("alias"))
			throw new ParseException("");

		String fileName = splitValues.get("fileName")[0];
		String alias = splitValues.get("alias")[0];

		for (String requiredParamInName : paramsRequiredInName) {
			if (!fileName.contains(String.format("%%%s%%", requiredParamInName)))
				throw new IllegalArgumentException(String.format(
						"The fileName has to contain a placeholder for the %s parameter, because it has more than 1 value.",
						requiredParamInName));
			if (!alias.contains(String.format("%%%s%%", requiredParamInName)))
				throw new IllegalArgumentException(String.format(
						"The alias has to contain a placeholder for the %s parameter, because it has more than 1 value.",
						requiredParamInName));
		}

		List<String[]> result = new ArrayList<String[]>();
		// storing a mapping from parameter to replacement value; to replace the
		// parameter placeholders in fileName and alias
		List<Map<String, String>> paramValueMaps = new ArrayList<Map<String, String>>();
		result.add(new String[0]);
		paramValueMaps.add(new HashMap<String, String>());

		for (String paramName : splitValues.keySet()) {
			if (paramName == null || paramName.equals("fileName") || paramName.equals("alias"))
				continue;
			boolean isSwitch = !options.getOption(paramName).hasArg();
			List<String[]> newParamSets = new ArrayList<String[]>();
			List<Map<String, String>> newParamValueMaps = new ArrayList<Map<String, String>>();
			for (int i = 0; i < result.size(); i++) {
				String[] incompleteParams = result.get(i);
				Map<String, String> incompleteParamMap = paramValueMaps.get(i);
				for (String paramValue : splitValues.get(paramName)) {

					// switch parameter
					if (isSwitch) {
						if (paramValue.equals("true")) {
							newParamSets.add(ArraysExt.merge(incompleteParams, new String[]{"-" + paramName}));
							Map<String, String> newParamMap = new HashMap<String, String>(incompleteParamMap);
							newParamMap.put(paramName, paramName + "_true");
							newParamValueMaps.add(newParamMap);
						} else {
							newParamSets.add(incompleteParams);
							Map<String, String> newParamMap = new HashMap<String, String>(incompleteParamMap);
							newParamMap.put(paramName, paramName + "_false");
							newParamValueMaps.add(newParamMap);
						}
					} else {
						newParamSets.add(ArraysExt.merge(incompleteParams, new String[]{"-" + paramName, paramValue}));
						Map<String, String> newParamMap = new HashMap<String, String>(incompleteParamMap);
						newParamMap.put(paramName, paramValue);
						newParamValueMaps.add(newParamMap);
					}
				}
			}
			result = newParamSets;
			paramValueMaps = newParamValueMaps;
		}

		// replace placeholders in fileName and alias
		for (int i = 0; i < paramValueMaps.size(); i++) {
			Map<String, String> paramValueMap = paramValueMaps.get(i);
			String newFileName = fileName;
			String newAlias = alias;
			for (String paramName : paramValueMap.keySet()) {
				newFileName = newFileName.replace(String.format("%%%s%%", paramName), paramValueMap.get(paramName));
				newAlias = newAlias.replace(String.format("%%%s%%", paramName), paramValueMap.get(paramName));
			}

			result.set(i, ArraysExt.merge(result.get(i), new String[]{"-fileName", newFileName, "-alias", newAlias}));
		}

		return result;
	}

	/**
	 * This method will tell the server to shutdown the framework immediately (0
	 * timeout).
	 */
	public void shutdownFramework() {
		try {
			this.server.shutdown(this.clientId, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method retrieves the status of all the runs of this client.
	 *
	 * @return A map containing the status as well as the percentage (if) for
	 *         every run, this client has scheduled.
	 * @throws RemoteException
	 */
	public Map<String, Pair<RUN_STATUS, Float>> getMyRunStatus() throws RemoteException {
		return server.getRunStatusForClientId(this.clientId);
	}

	/**
	 * @return A collection with the names of all runs and run results that are
	 *         currently enqueued but not yet running.
	 * @throws RemoteException
	 */
	public Collection<String> getQueue() throws RemoteException {
		return server.getQueue();
	}

	/**
	 *
	 * @param uniqueRunIdentifier
	 *            The unique run identifier of a run result stored in the
	 *            corresponding directory of the repository.
	 * @return The run results for the given unique run identifier.
	 * @throws RemoteException
	 */
	public Map<Pair<String, String>, Map<String, Double>> getRunResults(final String uniqueRunIdentifier)
			throws RemoteException {
		return server.getRunResults(uniqueRunIdentifier);
	}

	/**
	 *
	 * @return A collection with the names of those run result directories
	 *         contained in the repository of this server, that contain a
	 *         clusters subfolder and at least one *.complete file containing
	 *         results (can be slow if many run result folders are present).
	 * @throws RemoteException
	 */
	public Collection<String> getRunResults() throws RemoteException {
		return server.getRunResults();
	}

	/**
	 * @return A collection with the names of all run result directories
	 *         contained in the repository of this server. Those run result
	 *         directories can be resumed, if they were terminated before.
	 * @throws RemoteException
	 */
	public Collection<String> getRunResumes() throws RemoteException {
		return server.getRunResumes();
	}

	public Map<String, Pair<Pair<RUN_STATUS, Float>, Map<Pair<String, String>, Pair<Double, Map<String, Pair<Map<String, String>, String>>>>>> getMyOptimizationRunStatus()
			throws RemoteException {
		return server.getOptimizationRunStatusForClientId(this.clientId);
	}

	/**
	 * @return The id of this client.
	 */
	public String getClientId() {
		return this.clientId;
	}

	/**
	 *
	 * @return A collection with all runs contained in the server's repository.
	 * @throws RemoteException
	 */
	public Collection<String> getRuns() throws RemoteException {
		return server.getRuns();
	}

	/**
	 * @return A collection with all programs contained in the server's
	 *         repository.
	 * @throws RemoteException
	 */
	public Collection<String> getPrograms() throws RemoteException {
		return server.getPrograms();
	}

	/**
	 *
	 * @return A collection with the names of all dataset generators registered
	 *         at the repository of this server.
	 * @throws RemoteException
	 */
	public Collection<String> getDataSetGenerators() throws RemoteException {
		return server.getDataSetGenerators();
	}

	/**
	 *
	 * @param generatorName
	 *            The simple name of the class of the dataset generator.
	 * @return A wrapper objects keeping all the options of the specified
	 *         dataset generator.
	 * @throws RemoteException
	 */
	public Options getOptionsForDataSetGenerator(final String generatorName) throws RemoteException {
		return server.getOptionsForDataSetGenerator(generatorName);
	}

	/**
	 *
	 * @param randomizerName
	 *            The simple name of the class of the data randomizer.
	 * @return A wrapper objects keeping all the options of the specified data
	 *         randomizer.
	 * @throws RemoteException
	 */
	public Options getOptionsForDataRandomizer(final String randomizerName) throws RemoteException {
		return server.getOptionsForDataRandomizer(randomizerName);
	}

	/**
	 * @return A collection with all datasets contained in the server's
	 *         repository.
	 * @throws RemoteException
	 *             the remote exception
	 */
	public Collection<String> getDataSets() throws RemoteException {
		return server.getDataSets();
	}

	/**
	 * This method tells the server that this client wants to perform the
	 * specified run.
	 *
	 * @param runId
	 *            The id of the run to be performed
	 * @return true, if successful
	 * @throws RemoteException
	 */
	public boolean performRun(final String runId) throws RemoteException {
		return server.performRun(this.clientId, runId);
	}

	/**
	 * This method tells the server that this client wants to resume the run
	 * that corresponds to the run results folder with the specified unique
	 * identifier.
	 *
	 * @param uniqueRunIdentifier
	 *            The identifier of the run result that should be resumed.
	 * @return True, if successful
	 * @throws RemoteException
	 */
	public boolean resumeRun(final String uniqueRunIdentifier) throws RemoteException {
		return server.resumeRun(this.clientId, uniqueRunIdentifier);
	}

	/**
	 * This method tells the server, that this client wants to terminate the
	 * specified run.
	 *
	 * <p>
	 * This method only succeeds, if the run has been started before and is
	 * currently being performed.
	 *
	 * @param runId
	 *            The id of the run to be terminated.
	 * @return true, if successful
	 * @throws RemoteException
	 *             the remote exception
	 */
	public boolean terminateRun(final String runId) throws RemoteException {
		return server.terminateRun(this.clientId, runId);
	}

	/**
	 *
	 * @return A collection with the names of all data randomizers registered at
	 *         the repository of this server.
	 * @throws RemoteException
	 */
	public Collection<String> getDataRandomizers() throws RemoteException {
		return server.getDataRandomizers();
	}

	static ConsoleReader reader;

	/**
	 * @param args
	 *            Command line parameters for the backend client (see
	 *            {@link #params}).
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		try {
			CommandLine params = parseParams(args, false);

			if (params.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("clustevalClient", clientCLIOptions);
				System.exit(0);
			}

			if (params.hasOption("version")) {
				System.out.println(VERSION);
				System.exit(0);
			}

			initLogging(params);

			Logger log = LoggerFactory.getLogger(BackendClient.class);

			System.out.println("Starting clusteval client");
			System.out.println(VERSION);
			System.out.println("=========================");

			// if command line arguments (except connection parameters) are
			// passed, we do not start a console
			Set<String> paramKeys = new HashSet<String>();
			@SuppressWarnings("unchecked")
			Iterator<Option> it = params.iterator();
			while (it.hasNext()) {
				paramKeys.add(it.next().getOpt());
			}

			paramKeys.remove("ip");
			paramKeys.remove("port");
			paramKeys.remove("clientId");
			if (paramKeys.size() > 0) {
				try {
					new BackendClient(args);
				} catch (Exception e) {
				}
			} else {
				String clientId;
				if (params.hasOption("clientId"))
					clientId = params.getOptionValue("clientId");
				else
					// parse args because of possible connection parameters
					clientId = new BackendClient(args).clientId;

				reader = new ConsoleReader();

				List<Completer> completers = new LinkedList<Completer>();
				completers.add(new BackendClientCompleter(clientId, args));

				String ip = params.hasOption("ip") ? params.getOptionValue("ip") : "localhost";
				String port = params.hasOption("port") ? params.getOptionValue("port") : "1099";

				setDefaultPromptAndCompleter(ip, port, completers);

				String line;

				while ((line = reader.readLine()) != null) {

					if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
						break;
					}

					boolean connectException = false;
					do {
						try {
							new BackendClient(
									ArraysExt.merge(args, ("-clientId " + clientId + " -" + line).split(" ")));
							connectException = false;
						} catch (ConnectException e) {
							e.printStackTrace();
							connectException = true;
						} catch (Exception e) {
							log.warn(e.getMessage());
						}
						Thread.sleep(1000);
					} while (connectException);
					// }
				}
			}
		} catch (ParseException e) {
			System.err.println("Parsing failed.  Reason: " + e.getMessage());

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("clustevalClient",
					"Invoking this client without any parameters will open a shell with tab-completion.",
					clientCLIOptions, "", true);
		} catch (Throwable t) {
			// t.printStackTrace();
		}
	}

	protected static void setDefaultPromptAndCompleter(String ip, String port, Collection<Completer> newCompleters) {
		reader.setPrompt("ClustEval @" + ip + ":" + port + "> ");

		List<Completer> oldCompleters = new LinkedList<Completer>(reader.getCompleters());
		for (Completer c : oldCompleters)
			reader.removeCompleter(c);

		for (Completer c : newCompleters) {
			reader.addCompleter(c);
		}
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
	 * the file clustevalClient.log</li>
	 * <li><b>FileAppender</b>: Writes the logging output in lilith binary
	 * format to the file clustevalClient.lilith</li>
	 * </ul>
	 *
	 * @param cmd
	 *            The command line parameters including possible options of
	 *            logging
	 * @throws ParseException
	 */
	private static void initLogging(CommandLine cmd) throws ParseException {
		Logger log = LoggerFactory.getLogger(BackendClient.class);

		Level logLevel;
		if (cmd.hasOption("logLevel")) {
			switch (Integer.parseInt(cmd.getOptionValue("logLevel"))) {
				case 0 :
					logLevel = Level.ALL;
					break;
				case 1 :
					logLevel = Level.TRACE;
					break;
				case 2 :
					logLevel = Level.DEBUG;
					break;
				case 3 :
					logLevel = Level.INFO;
					break;
				case 4 :
					logLevel = Level.WARN;
					break;
				case 5 :
					logLevel = Level.ERROR;
					break;
				case 6 :
					logLevel = Level.OFF;
					break;
				default :
					throw new ParseException("The logLevel argument requires one of the value of [0,1,2,3,4,5,6]");
			}
		} else {
			logLevel = Level.INFO;
		}

		ch.qos.logback.classic.Logger logger = ((ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(Logger.ROOT_LOGGER_NAME));
		logger.setLevel(logLevel);

		// file appender for clustevalServer.log plaintext file
		FileAppender<ILoggingEvent> fileApp = new FileAppender<ILoggingEvent>();
		fileApp.setName("clientLogFile");
		String logFilePath = FileUtils.buildPath(System.getProperty("user.dir"), "clustevalClient.log");
		fileApp.setFile(logFilePath);

		fileApp.setAppend(true);
		fileApp.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
		fileApp.setEncoder(new PatternLayoutEncoder());
		PatternLayout layout = new PatternLayout();
		layout.setPattern("%date{dd MMM yyyy HH:mm:ss.SSS} [%thread] %-5level %logger{35} - %msg%n");
		layout.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
		layout.start();
		fileApp.setLayout(layout);
		fileApp.start();
		logger.addAppender(fileApp);

		// file appender for clustevalServer.lilith binary file
		// removed 30.01.2013
		// FileAppender fileAppLilith = new FileAppender();
		// fileAppLilith.setName("clientLogFileLilith");
		// logFilePath = FileUtils.buildPath(System.getProperty("user.dir"),
		// "clustevalClient.lilith");
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
}
