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
package de.clusteval.tools;

import ch.qos.logback.classic.Level;
import de.clusteval.cluster.Clustering;
import de.clusteval.cluster.paramOptimization.IncompatibleParameterOptimizationMethodException;
import de.clusteval.cluster.paramOptimization.InvalidOptimizationParameterException;
import de.clusteval.cluster.paramOptimization.UnknownParameterOptimizationMethodException;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.cluster.quality.ClusteringQualityMeasureParameters;
import de.clusteval.cluster.quality.ClusteringQualitySet;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.context.IncompatibleContextException;
import de.clusteval.context.UnknownContextException;
import de.clusteval.data.DataConfig;
import de.clusteval.data.DataConfigNotFoundException;
import de.clusteval.data.DataConfigurationException;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetConfigNotFoundException;
import de.clusteval.data.dataset.DataSetConfigurationException;
import de.clusteval.data.dataset.DataSetNotFoundException;
import de.clusteval.data.dataset.IncompatibleDataSetConfigPreprocessorException;
import de.clusteval.data.dataset.NoDataSetException;
import de.clusteval.data.dataset.format.InvalidDataSetFormatVersionException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.dataset.type.UnknownDataSetTypeException;
import de.clusteval.data.distance.UnknownDistanceMeasureException;
import de.clusteval.data.goldstandard.GoldStandardConfigNotFoundException;
import de.clusteval.data.goldstandard.GoldStandardConfigurationException;
import de.clusteval.data.goldstandard.GoldStandardNotFoundException;
import de.clusteval.data.goldstandard.format.UnknownGoldStandardFormatException;
import de.clusteval.data.preprocessing.UnknownDataPreprocessorException;
import de.clusteval.data.randomizer.UnknownDataRandomizerException;
import de.clusteval.data.statistics.UnknownDataStatisticException;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.db.DatabaseConnectException;
import de.clusteval.framework.repository.parse.Parser;
import de.clusteval.program.NoOptimizableProgramParameterException;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.UnknownParameterType;
import de.clusteval.program.UnknownProgramParameterException;
import de.clusteval.program.UnknownProgramTypeException;
import de.clusteval.program.r.UnknownRProgramException;
import de.clusteval.run.InvalidRunModeException;
import de.clusteval.run.ParameterOptimizationRun;
import de.clusteval.run.RunException;
import de.clusteval.run.result.ParameterOptimizationResult;
import de.clusteval.run.result.RunResultParseException;
import de.clusteval.run.result.format.UnknownRunResultFormatException;
import de.clusteval.run.result.postprocessing.UnknownRunResultPostprocessorException;
import de.clusteval.run.statistics.UnknownRunDataStatisticException;
import de.clusteval.run.statistics.UnknownRunStatisticException;
import de.clusteval.utils.FormatConversionException;
import de.clusteval.utils.InvalidConfigurationFileException;
import de.clusteval.utils.RNotAvailableException;
import de.wiwie.wiutils.file.FileUtils;
import de.wiwie.wiutils.utils.ProgressPrinter;
import de.wiwie.wiutils.utils.parse.TextFileParser;
import de.wiwie.wiutils.utils.parse.TextFileParser.OUTPUT_MODE;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Wiwie
 *
 */
public class ClustQualityEval {

	protected Repository repo;
	protected DataConfig dataConfig;
	protected ProgressPrinter printer;
	protected Logger log;

	public ClustQualityEval(final String absRepoPath,
			final String dataConfigName, final String... qualityMeasures)
			throws RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException,
			UnknownClusteringQualityMeasureException, InterruptedException,
			UnknownDataSetFormatException, UnknownGoldStandardFormatException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigurationException, DataSetNotFoundException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, NoDataSetException,
			DataConfigurationException, DataConfigNotFoundException,
			NumberFormatException, RunResultParseException,
			ConfigurationException, RegisterException, UnknownContextException,
			UnknownParameterType, IOException, UnknownRunResultFormatException,
			InvalidRunModeException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownProgramParameterException,
			InvalidConfigurationFileException, NoRepositoryFoundException,
			InvalidOptimizationParameterException, RunException,
			UnknownDataStatisticException, UnknownProgramTypeException,
			UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownDistanceMeasureException, UnknownRunStatisticException,
			UnknownDataSetTypeException, UnknownRunDataStatisticException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleContextException, InvalidDataSetFormatVersionException,
			RNotAvailableException, FormatConversionException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException, DatabaseConnectException {
		super();
		ClustevalBackendServer.logLevel(Level.INFO);
		ClustevalBackendServer.getBackendServerConfiguration().setNoDatabase(
				true);
		ClustevalBackendServer.getBackendServerConfiguration()
				.setCheckForRunResults(false);
		this.log = LoggerFactory.getLogger(this.getClass());
		final Repository parent = new Repository(new File(absRepoPath)
				.getParentFile().getParentFile().getAbsolutePath(), null);
		parent.initialize();
		this.repo = new RunResultRepository(absRepoPath, parent);
		this.repo.initialize();

		List<ParameterOptimizationResult> result = new ArrayList<ParameterOptimizationResult>();
		final ParameterOptimizationRun run = (ParameterOptimizationRun) ParameterOptimizationResult
                        .parseFromRunResultFolder2(parent, new File(absRepoPath),
                          						result, false, false, false);

		this.dataConfig = this.repo.getStaticObjectWithName(DataConfig.class,
				dataConfigName);

		final List<ClusteringQualityMeasure> measures = new ArrayList<ClusteringQualityMeasure>();

		if (qualityMeasures.length == 0) {
			log.error("Please add at least one quality measure to the command line arguments.");
			this.repo.terminateSupervisorThread();
			return;
		}
		for (String measureString : qualityMeasures) {
			String[] measureSplit = measureString.split(":");
			String[] paramSplit = measureSplit.length > 1 ? measureSplit[1]
					.split(";") : new String[0];

			ClusteringQualityMeasureParameters params = new ClusteringQualityMeasureParameters();
			for (String param : paramSplit) {
				String[] paramNameValue = param.split("=");
				params.put(paramNameValue[0], paramNameValue[1]);
			}

			measures.add(ClusteringQualityMeasure.parseFromString(this.repo,
					measureSplit[0], params));
		}

		Set<Thread> threads = new HashSet<Thread>();
		System.out.println("Program configurations:");
		System.out.println(run.getProgramConfigs());
		for (final ProgramConfig pc : run.getProgramConfigs()) {
			// get the dataset for this program config
			DataSet dsIn = Parser.parseFromFile(
					DataSet.class,
					new File(FileUtils.buildPath(absRepoPath, "inputs",
							pc.toString() + "_" + dataConfig.toString(),
							dataConfig.getDatasetConfig().getDataSet()
									.getMajorName(), dataConfig
									.getDatasetConfig().getDataSet()
									.getMinorName())));
			// get dataset in standard format
			final DataSet ds = dsIn.preprocessAndConvertTo(run.getContext(),
					run.getContext().getStandardInputFormat(), dataConfig
							.getDatasetConfig()
							.getConversionInputToStandardConfiguration(),
					dataConfig.getDatasetConfig()
							.getConversionStandardToInputConfiguration());

			ds.loadIntoMemory();

			Thread t = new Thread() {

				public void run() {
					try {
						DataConfig dc = dataConfig.clone();
						dc.getDatasetConfig().setDataSet(ds);

						File f = new File(FileUtils.buildPath(
								repo.getBasePath(), "clusters"));
						File[] childs = f.listFiles(new FilenameFilter() {

							/*
							 * (non-Javadoc)
							 *
							 * @see java.io.FilenameFilter#accept(java.io.File,
							 * java.lang.String)
							 */
							@Override
							public boolean accept(File dir, String name) {
								return name.startsWith(pc.getName() + "_"
										+ dataConfig.getName())
										&& name.endsWith(".results.conv");
							}
						});
						// printer = new MyProgressPrinter(childs.length, true);
						((ch.qos.logback.classic.Logger) LoggerFactory
								.getLogger(Logger.ROOT_LOGGER_NAME))
								.info("Assessing qualities of clusterings ...");

						final Map<Long, ClusteringQualitySet> qualsMap = new HashMap<Long, ClusteringQualitySet>();

						for (File clusteringFile : childs) {
							try {
								Clustering cl = Clustering.parseFromFile(repo,
										clusteringFile.getAbsoluteFile(), true)
										.getSecond();

								// only recalculate for those, for which the
								// measure
								// hasn't
								// been evaluated
								List<ClusteringQualityMeasure> toEvaluate = new ArrayList<ClusteringQualityMeasure>(
										measures);
								try {
									if (cl.getQualities() != null)
										toEvaluate.removeAll(cl.getQualities()
												.keySet());
								} catch (NullPointerException e) {
									System.out.println(clusteringFile);
									throw e;
								}
								ClusteringQualitySet quals = new ClusteringQualitySet();
								// evaluate the new quality measures
								if (!toEvaluate.isEmpty()) {
									quals.putAll(cl.assessQuality(dc,
											toEvaluate));
									System.out.println(quals);

									// write the new qualities into the
									// results.qual
									// file
									for (ClusteringQualityMeasure m : quals
											.keySet())
										FileUtils
												.appendStringToFile(
														clusteringFile
																.getAbsolutePath()
																.replaceFirst(
																		".results.conv",
																		".results.qual"),
														String.format(
																"%s\t%s",
																m.toString(),
																quals.get(m)
																		.getValue())
																+ "\n");
								}

								long iterationNumber = Long
										.parseLong(clusteringFile
												.getName()
												.replaceFirst(
														String.format("%s_%s.",
																pc.toString(),
																dc.toString()),
														"")
												.replaceFirst(".results.conv",
														""));

								// store all qualities of the clustering in one
								// set
								ClusteringQualitySet allQuals = new ClusteringQualitySet();
								if (cl.getQualities() != null)
									allQuals.putAll(cl.getQualities());
								allQuals.putAll(quals);
								qualsMap.put(iterationNumber, allQuals);

							} catch (IOException e) {
								e.printStackTrace();
							} catch (UnknownGoldStandardFormatException e) {
								e.printStackTrace();
							} catch (UnknownDataSetFormatException e) {
								e.printStackTrace();
							} catch (InvalidDataSetFormatVersionException e) {
								e.printStackTrace();
							}
						}

						// update complete quality file
						// we want to have the same lines conserving the same NT
						// and
						// skipped
						// iterations infos (missing lines), therefore we parse
						// the
						// old file
						// first, iterate over all lines and write the same
						// lines
						// but add
						// the additional infos (if there are any)
						TextFileParser parser = new TextFileParser(
								FileUtils.buildPath(repo.getBasePath(),
										"clusters", String.format(
												"%s_%s.results.qual.complete",
												pc.toString(), dc.toString())),
								new int[0],
								new int[0],
								FileUtils.buildPath(
										repo.getBasePath(),
										"clusters",
										String.format(
												"%s_%s.results.qual.complete.new",
												pc.toString(), dc.toString())),
								OUTPUT_MODE.STREAM) {

							protected List<ClusteringQualityMeasure> measures;

							/*
							 * (non-Javadoc)
							 *
							 * @see
							 * de.wiwie.wiutils.utils.parse.TextFileParser#processLine(java.lang.
							 * String[], java.lang.String[])
							 */
							@Override
							protected void processLine(String[] key,
									String[] value) {
							}

							/*
							 * (non-Javadoc)
							 *
							 * @see
							 * de.wiwie.wiutils.utils.parse.TextFileParser#getLineOutput(java
							 * .lang .String[], java.lang.String[])
							 */
							@Override
							protected String getLineOutput(String[] key,
									String[] value) {
								StringBuffer sb = new StringBuffer();
								// sb.append(combineColumns(value));
								sb.append(combineColumns(Arrays
										.copyOf(value, 2)));

								if (currentLine == 0) {
									sb.append(outSplit);
									sb.append(combineColumns(Arrays
											.copyOfRange(value, 2, value.length)));
									measures = new ArrayList<ClusteringQualityMeasure>();
									for (int i = 2; i < value.length; i++)
										try {
											measures.add(ClusteringQualityMeasure
													.parseFromString(
															parent,
															value[i],
															new ClusteringQualityMeasureParameters()));
										} catch (UnknownClusteringQualityMeasureException e) {
											e.printStackTrace();
											this.terminate();
										}

									// get measures, which are not in the
									// complete
									// file
									// header
									if (qualsMap.keySet().iterator().hasNext()) {
										Set<ClusteringQualityMeasure> requiredMeasures = qualsMap
												.get(qualsMap.keySet()
														.iterator().next())
												.keySet();
										requiredMeasures.removeAll(measures);

										for (ClusteringQualityMeasure m : requiredMeasures) {
											sb.append(outSplit);
											sb.append(m.toString());
										}

										measures.addAll(requiredMeasures);
									}
								} else if (value[0].contains("*")) {
									// do nothing
								} else {
									long iterationNumber = Long
											.parseLong(value[0]);
									ClusteringQualitySet quals = qualsMap
											.get(iterationNumber);

									boolean notTerminated = value[3]
											.equals("NT");

									// for (int i = value.length - 2; i <
									// measures
									// .size(); i++) {
									// sb.append(outSplit);
									// if (notTerminated)
									// sb.append("NT");
									// else
									// sb.append(quals.get(measures.get(i)));
									// }
									for (int i = 0; i < measures.size(); i++) {
										sb.append(outSplit);
										if (notTerminated)
											sb.append("NT");
										else if (quals.containsKey(measures
												.get(i)))
											sb.append(quals.get(measures.get(i)));
										else
											sb.append(value[i + 2]);
									}
								}

								sb.append(System.getProperty("line.separator"));
								return sb.toString();
							}
						};
						try {
							parser.process();
						} catch (Exception e) {
							e.printStackTrace();
						}

						ds.unloadFromMemory();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			threads.add(t);
			t.start();
		}
		// add the new clustering quality measures into the run config file
		TextFileParser p = new TextFileParser(run.getAbsolutePath(), null,
				null, false, "", run.getAbsolutePath() + ".new",
				OUTPUT_MODE.STREAM) {

			/*
			 * (non-Javadoc)
			 *
			 * @see de.wiwie.wiutils.utils.parse.TextFileParser#processLine(java.lang.String[],
			 * java.lang.String[])
			 */
			@Override
			protected void processLine(String[] key, String[] value) {
			}

			/*
			 * (non-Javadoc)
			 *
			 * @see de.wiwie.wiutils.utils.parse.TextFileParser#getLineOutput(java.lang.String[],
			 * java.lang.String[])
			 */
			@Override
			protected String getLineOutput(String[] key, String[] value) {
				StringBuilder sb = new StringBuilder();
				sb.append(value[0]);
				if (value[0].contains("qualityMeasures = "))
					for (ClusteringQualityMeasure m : measures)
						if (!value[0].contains(m.toString())) {
							sb.append(",");
							sb.append(m.toString());
						}

				sb.append(System.getProperty("line.separator"));
				return sb.toString();
			}
		}.process();
		for (Thread t : threads)
			t.join();
		System.exit(0);
	}

	public static void main(String[] args)
			throws RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException,
			UnknownClusteringQualityMeasureException, InterruptedException,
			UnknownDataSetFormatException, UnknownGoldStandardFormatException,
			GoldStandardNotFoundException, GoldStandardConfigurationException,
			DataSetConfigurationException, DataSetNotFoundException,
			DataSetConfigNotFoundException,
			GoldStandardConfigNotFoundException, NoDataSetException,
			DataConfigurationException, DataConfigNotFoundException,
			NumberFormatException, RunResultParseException,
			ConfigurationException, RegisterException, UnknownContextException,
			UnknownParameterType, IOException, UnknownRunResultFormatException,
			InvalidRunModeException,
			UnknownParameterOptimizationMethodException,
			NoOptimizableProgramParameterException,
			UnknownProgramParameterException,
			InvalidConfigurationFileException, NoRepositoryFoundException,
			InvalidOptimizationParameterException, RunException,
			UnknownDataStatisticException, UnknownProgramTypeException,
			UnknownRProgramException,
			IncompatibleParameterOptimizationMethodException,
			UnknownDistanceMeasureException, UnknownRunStatisticException,
			UnknownDataSetTypeException, UnknownRunDataStatisticException,
			UnknownDataPreprocessorException,
			IncompatibleDataSetConfigPreprocessorException,
			IncompatibleContextException, InvalidDataSetFormatVersionException,
			RNotAvailableException, FormatConversionException,
			UnknownRunResultPostprocessorException,
			UnknownDataRandomizerException, DatabaseConnectException {
		new ClustQualityEval(args[0], args[1], Arrays.copyOfRange(args, 2,
				args.length));
	}
}

class MyProgressPrinter extends ProgressPrinter {

	/**
	 *
	 */
	public MyProgressPrinter(final long upperLimit,
			final boolean printOnNewPercent) {
		super(upperLimit, printOnNewPercent);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.ProgressPrinter#log(java.lang.String)
	 */
	@Override
	protected void log(String message) {
		this.log.info(message);
	}
}
