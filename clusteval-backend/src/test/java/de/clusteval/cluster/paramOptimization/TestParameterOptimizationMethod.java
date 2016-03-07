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
package de.clusteval.cluster.paramOptimization;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.wiwie.wiutils.utils.SimilarityMatrix.NUMBER_PRECISION;
import ch.qos.logback.classic.Level;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.cluster.quality.ClusteringQualityMeasureParameters;
import de.clusteval.cluster.quality.ClusteringQualityMeasureValue;
import de.clusteval.cluster.quality.ClusteringQualitySet;
import de.clusteval.cluster.quality.UnknownClusteringQualityMeasureException;
import de.clusteval.context.Context;
import de.clusteval.context.UnknownContextException;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.RelativeDataSet;
import de.clusteval.data.dataset.format.ConversionInputToStandardConfiguration;
import de.clusteval.data.dataset.format.ConversionStandardToInputConfiguration;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.dataset.format.InvalidDataSetFormatVersionException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.distance.DistanceMeasure;
import de.clusteval.data.distance.UnknownDistanceMeasureException;
import de.clusteval.data.preprocessing.DataPreprocessor;
import de.clusteval.framework.ClustevalBackendServer;
import de.clusteval.framework.repository.InvalidRepositoryException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.framework.repository.RepositoryAlreadyExistsException;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.program.ParameterSet;
import de.clusteval.program.ProgramConfig;
import de.clusteval.run.Run;
import de.clusteval.run.result.RunResultParseException;
import de.clusteval.utils.AbstractClustEvalTest;
import de.clusteval.utils.FormatConversionException;
import de.clusteval.utils.InternalAttributeException;
import de.clusteval.utils.RNotAvailableException;

/**
 * @author Christian Wiwie
 * 
 */
public class TestParameterOptimizationMethod extends AbstractClustEvalTest {

	@Test
	public void testTransClustCassini250()
			throws UnknownParameterOptimizationMethodException,
			UnknownClusteringQualityMeasureException,
			UnknownDataSetFormatException,
			InvalidDataSetFormatVersionException, IllegalArgumentException,
			IOException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, RunResultParseException,
			InternalAttributeException, RegisterException,
			ParameterOptimizationException, FormatConversionException,
			UnknownDistanceMeasureException, NoParameterSetFoundException,
			UnknownContextException, RNotAvailableException,
			InterruptedException, ParameterSetAlreadyEvaluatedException {

		ClustevalBackendServer.logLevel(Level.INFO);

		Context context = Context.parseFromString(getRepository(),
				"ClusteringContext");

		DataConfig dataConfig = getRepository().getStaticObjectWithName(
				DataConfig.class, "synthetic_cassini250");
		DataSet ds = dataConfig.getDatasetConfig().getDataSet();
		DataSetFormat internal = DataSetFormat.parseFromString(getRepository(),
				"SimMatrixDataSetFormat");
		ds = ds.preprocessAndConvertTo(
				context,
				internal,
				new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());
		ds.loadIntoMemory();
		if (ds instanceof RelativeDataSet) {
			RelativeDataSet dataSet = (RelativeDataSet) ds;
			dataConfig
					.getRepository()
					.getInternalDoubleAttribute(
							"$("
									+ dataConfig.getDatasetConfig()
											.getDataSet().getOriginalDataSet()
											.getAbsolutePath()
									+ ":minSimilarity)")
					.setValue(dataSet.getDataSetContent().getMinValue());
			dataConfig
					.getRepository()
					.getInternalDoubleAttribute(
							"$("
									+ dataConfig.getDatasetConfig()
											.getDataSet().getOriginalDataSet()
											.getAbsolutePath()
									+ ":maxSimilarity)")
					.setValue(dataSet.getDataSetContent().getMaxValue());
			dataConfig
					.getRepository()
					.getInternalDoubleAttribute(
							"$("
									+ dataConfig.getDatasetConfig()
											.getDataSet().getOriginalDataSet()
											.getAbsolutePath()
									+ ":meanSimilarity)")
					.setValue(dataSet.getDataSetContent().getMean());
		}
		dataConfig
				.getRepository()
				.getInternalIntegerAttribute(
						"$("
								+ dataConfig.getDatasetConfig().getDataSet()
										.getOriginalDataSet().getAbsolutePath()
								+ ":numberOfElements)")
				.setValue(ds.getIds().size());
		ds.unloadFromMemory();
		ProgramConfig programConfig = getRepository().getStaticObjectWithName(
				ProgramConfig.class, "TransClust_2");

		ClusteringQualityMeasure f2 = ClusteringQualityMeasure.parseFromString(
				getRepository(), "TransClustF2ClusteringQualityMeasure",
				new ClusteringQualityMeasureParameters());
		ParameterOptimizationMethod method = ParameterOptimizationMethod
				.parseFromString(
						getRepository(),
						"LayeredDivisiveParameterOptimizationMethod",
						getRepository().getStaticObjectWithName(Run.class,
								"paper_run_synthetic"), programConfig,
						dataConfig, programConfig.getOptimizableParams(), f2,
						1001, false);
		method.reset(new File(
				"testCaseRepository/results/04_05_2013-12_16_32_paper_run_synthetic/clusters/TransClust_2_synthetic_cassini250.results.qual.complete.test"));
		List<ClusteringQualitySet> qualitySets = new ArrayList<ClusteringQualitySet>();
		double[] f2s = new double[]{0.7264957264957266, 0.7264957264957266,
				0.7264957264957266, 0.7264957264957266, 0.7264957264957266,
				0.7264957264957266, 0.7264957264957266, 0.7264957264957266,
				0.7264957264957266, 0.7264957264957266, 0.7264957264957266,
				0.7264957264957266, 0.7264957264957266, 0.7264957264957266,
				0.715097084662302, 0.8639251416164275, 0.8639251416164275,
				0.8639251416164275, 0.8611811776871334, 0.8385703838766421,
				0.8091360586128375, 0.7495054796301976, 0.6757538267472042,
				0.667991169977925, 0.6523107177974435, 0.6285536339765723,
				0.4586472183038707, 0.3428102608569182, 0.22291932707789683,
				0.13585246871291612, 0.0786382521734358, 0.014950186722249105};

		for (double d : f2s) {
			ClusteringQualitySet qualitySet = new ClusteringQualitySet();
			qualitySet.put(f2, ClusteringQualityMeasureValue.getForDouble(d));
			qualitySets.add(qualitySet);
		}

		List<ParameterSet> expectedParameterSets = new ArrayList<ParameterSet>();
		double[] thresholds = new double[]{0.0, 0.1263292298185384,
				0.2526584596370768, 0.3789876894556152, 0.5053169192741536,
				0.631646149092692, 0.7579753789112303, 0.8843046087297688,
				1.0106338385483071, 1.1369630683668457, 1.263292298185384,
				1.3896215280039224, 1.5159507578224607, 1.642279987640999,
				1.7686092174595376, 1.8949384472780761, 2.0212676770966143,
				2.147596906915153, 2.2739261367336914, 2.4002553665522295,
				2.526584596370768, 2.652913826189306, 2.7792430560078447,
				2.9055722858263833, 3.0319015156449214, 3.15823074546346,
				3.284559975281998, 3.4108892051005366, 3.537218434919075,
				3.6635476647376133, 3.7898768945561523, 3.9162061243746904,
				0.9158869161844035};
		for (double T : thresholds) {
			ParameterSet paramSet = new ParameterSet();
			paramSet.put("T", T + "");
			expectedParameterSets.add(paramSet);
		}

		Iterator<ClusteringQualitySet> it = qualitySets.iterator();
		Iterator<ParameterSet> itParams = expectedParameterSets.iterator();
		while (method.hasNext() && it.hasNext() && itParams.hasNext()) {
			ParameterSet paramSet = method.next();
			System.out.println(paramSet);
			Assert.assertEquals(itParams.next(), paramSet);
			method.giveQualityFeedback(paramSet, it.next());
		}
	}

	@Test
	public void testResumeTransClustCassini250()
			throws UnknownParameterOptimizationMethodException,
			UnknownClusteringQualityMeasureException,
			UnknownDataSetFormatException,
			InvalidDataSetFormatVersionException, IllegalArgumentException,
			IOException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, RunResultParseException,
			InternalAttributeException, RegisterException,
			ParameterOptimizationException, FormatConversionException,
			UnknownDistanceMeasureException, NoParameterSetFoundException,
			UnknownContextException, RNotAvailableException,
			InterruptedException, ParameterSetAlreadyEvaluatedException {

		ClustevalBackendServer.logLevel(Level.INFO);

		Context context = Context.parseFromString(getRepository(),
				"ClusteringContext");

		DataConfig dataConfig = getRepository().getStaticObjectWithName(
				DataConfig.class, "synthetic_cassini250");
		DataSet ds = dataConfig.getDatasetConfig().getDataSet();
		DataSetFormat internal = DataSetFormat.parseFromString(getRepository(),
				"SimMatrixDataSetFormat");
		ds = ds.preprocessAndConvertTo(
				context,
				internal,
				new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());
		ds.loadIntoMemory();
		if (ds instanceof RelativeDataSet) {
			RelativeDataSet dataSet = (RelativeDataSet) ds;
			dataConfig
					.getRepository()
					.getInternalDoubleAttribute(
							"$("
									+ dataConfig.getDatasetConfig()
											.getDataSet().getOriginalDataSet()
											.getAbsolutePath()
									+ ":minSimilarity)")
					.setValue(dataSet.getDataSetContent().getMinValue());
			dataConfig
					.getRepository()
					.getInternalDoubleAttribute(
							"$("
									+ dataConfig.getDatasetConfig()
											.getDataSet().getOriginalDataSet()
											.getAbsolutePath()
									+ ":maxSimilarity)")
					.setValue(dataSet.getDataSetContent().getMaxValue());
			dataConfig
					.getRepository()
					.getInternalDoubleAttribute(
							"$("
									+ dataConfig.getDatasetConfig()
											.getDataSet().getOriginalDataSet()
											.getAbsolutePath()
									+ ":meanSimilarity)")
					.setValue(dataSet.getDataSetContent().getMean());
		}
		dataConfig
				.getRepository()
				.getInternalIntegerAttribute(
						"$("
								+ dataConfig.getDatasetConfig().getDataSet()
										.getOriginalDataSet().getAbsolutePath()
								+ ":numberOfElements)")
				.setValue(ds.getIds().size());
		ds.unloadFromMemory();
		ProgramConfig programConfig = getRepository().getStaticObjectWithName(
				ProgramConfig.class, "TransClust_2");

		ClusteringQualityMeasure f2 = ClusteringQualityMeasure.parseFromString(
				getRepository(), "TransClustF2ClusteringQualityMeasure",
				new ClusteringQualityMeasureParameters());
		ParameterOptimizationMethod method = ParameterOptimizationMethod
				.parseFromString(
						getRepository(),
						"LayeredDivisiveParameterOptimizationMethod",
						getRepository().getStaticObjectWithName(Run.class,
								"paper_run_synthetic"), programConfig,
						dataConfig, programConfig.getOptimizableParams(), f2,
						1001, true);
		method.reset(new File(
				"testCaseRepository/results/04_05_2013-12_16_32_paper_run_synthetic/clusters/TransClust_2_synthetic_cassini250.results.qual.complete.test"));
		List<ClusteringQualitySet> qualitySets = new ArrayList<ClusteringQualitySet>();

		List<Integer> iterationNumbers = Arrays.asList(33);
		double[] f2s = new double[]{0.5238095238095238};

		for (double d : f2s) {
			ClusteringQualitySet qualitySet = new ClusteringQualitySet();
			qualitySet.put(f2, ClusteringQualityMeasureValue.getForDouble(d));
			qualitySets.add(qualitySet);
		}

		List<ParameterSet> expectedParameterSets = new ArrayList<ParameterSet>();
		double[] thresholds = new double[]{0.9158869161844035};
		for (double T : thresholds) {
			ParameterSet paramSet = new ParameterSet();
			paramSet.put("T", T + "");
			expectedParameterSets.add(paramSet);
		}

		Iterator<Integer> itItNum = iterationNumbers.iterator();
		Iterator<ClusteringQualitySet> it = qualitySets.iterator();
		Iterator<ParameterSet> itParams = expectedParameterSets.iterator();
		while (method.hasNext() && it.hasNext() && itParams.hasNext()) {
			ParameterSet paramSet = method.next();
			System.out.println(paramSet);
			Assert.assertEquals(itParams.next(), paramSet);
			method.giveQualityFeedback(paramSet, it.next());
			Assert.assertEquals(itItNum.next().intValue(), method.currentCount);
		}
	}

	@Test
	public void testResumeTransClustCassini250LastLayer()
			throws UnknownParameterOptimizationMethodException,
			UnknownClusteringQualityMeasureException,
			UnknownDataSetFormatException,
			InvalidDataSetFormatVersionException, IllegalArgumentException,
			IOException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, RunResultParseException,
			InternalAttributeException, RegisterException,
			ParameterOptimizationException, FormatConversionException,
			UnknownDistanceMeasureException, NoParameterSetFoundException,
			UnknownContextException, RNotAvailableException,
			InterruptedException {

		ClustevalBackendServer.logLevel(Level.INFO);

		Context context = Context.parseFromString(getRepository(),
				"ClusteringContext");

		DataConfig dataConfig = getRepository().getStaticObjectWithName(
				DataConfig.class, "synthetic_cassini250");
		DataSet ds = dataConfig.getDatasetConfig().getDataSet();
		DataSetFormat internal = DataSetFormat.parseFromString(getRepository(),
				"SimMatrixDataSetFormat");
		ds = ds.preprocessAndConvertTo(
				context,
				internal,
				new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"EuclidianDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());
		ds.loadIntoMemory();
		if (ds instanceof RelativeDataSet) {
			RelativeDataSet dataSet = (RelativeDataSet) ds;
			dataConfig
					.getRepository()
					.getInternalDoubleAttribute(
							"$("
									+ dataConfig.getDatasetConfig()
											.getDataSet().getOriginalDataSet()
											.getAbsolutePath()
									+ ":minSimilarity)")
					.setValue(dataSet.getDataSetContent().getMinValue());
			dataConfig
					.getRepository()
					.getInternalDoubleAttribute(
							"$("
									+ dataConfig.getDatasetConfig()
											.getDataSet().getOriginalDataSet()
											.getAbsolutePath()
									+ ":maxSimilarity)")
					.setValue(dataSet.getDataSetContent().getMaxValue());
			dataConfig
					.getRepository()
					.getInternalDoubleAttribute(
							"$("
									+ dataConfig.getDatasetConfig()
											.getDataSet().getOriginalDataSet()
											.getAbsolutePath()
									+ ":meanSimilarity)")
					.setValue(dataSet.getDataSetContent().getMean());
		}
		dataConfig
				.getRepository()
				.getInternalIntegerAttribute(
						"$("
								+ dataConfig.getDatasetConfig().getDataSet()
										.getOriginalDataSet().getAbsolutePath()
								+ ":numberOfElements)")
				.setValue(ds.getIds().size());
		ds.unloadFromMemory();
		ProgramConfig programConfig = getRepository().getStaticObjectWithName(
				ProgramConfig.class, "TransClust_2");

		ClusteringQualityMeasure f2 = ClusteringQualityMeasure.parseFromString(
				getRepository(), "TransClustF2ClusteringQualityMeasure",
				new ClusteringQualityMeasureParameters());
		ParameterOptimizationMethod method = ParameterOptimizationMethod
				.parseFromString(
						getRepository(),
						"LayeredDivisiveParameterOptimizationMethod",
						getRepository().getStaticObjectWithName(Run.class,
								"paper_run_synthetic"), programConfig,
						dataConfig, programConfig.getOptimizableParams(), f2,
						1001, true);
		method.reset(new File(
				"testCaseRepository/results/04_06_2013-15_56_18_paper_run_synthetic/clusters/TransClust_2_synthetic_cassini250.results.qual.complete.test"));

		Assert.assertFalse(method.hasNext());
	}

	@Test
	public void testResumeTransClustBaechler2003()
			throws UnknownParameterOptimizationMethodException,
			UnknownClusteringQualityMeasureException,
			UnknownDataSetFormatException,
			InvalidDataSetFormatVersionException, IllegalArgumentException,
			IOException, RepositoryAlreadyExistsException,
			InvalidRepositoryException, RepositoryConfigNotFoundException,
			RepositoryConfigurationException, RunResultParseException,
			InternalAttributeException, RegisterException,
			ParameterOptimizationException, FormatConversionException,
			UnknownDistanceMeasureException, NoParameterSetFoundException,
			UnknownContextException, RNotAvailableException,
			InterruptedException, ParameterSetAlreadyEvaluatedException {

		ClustevalBackendServer.logLevel(Level.INFO);

		Context context = Context.parseFromString(getRepository(),
				"ClusteringContext");

		DataConfig dataConfig = getRepository().getStaticObjectWithName(
				DataConfig.class, "baechler2003");
		DataSet ds = dataConfig.getDatasetConfig().getDataSet();
		DataSetFormat internal = DataSetFormat.parseFromString(getRepository(),
				"SimMatrixDataSetFormat");
		ds = ds.preprocessAndConvertTo(
				context,
				internal,
				new ConversionInputToStandardConfiguration(DistanceMeasure
						.parseFromString(getRepository(),
								"SpearmanCorrelationRDistanceMeasure"),
						NUMBER_PRECISION.DOUBLE,
						new ArrayList<DataPreprocessor>(),
						new ArrayList<DataPreprocessor>()),
				new ConversionStandardToInputConfiguration());
		ds.loadIntoMemory();
		if (ds instanceof RelativeDataSet) {
			RelativeDataSet dataSet = (RelativeDataSet) ds;
			dataConfig
					.getRepository()
					.getInternalDoubleAttribute(
							"$("
									+ dataConfig.getDatasetConfig()
											.getDataSet().getOriginalDataSet()
											.getAbsolutePath()
									+ ":minSimilarity)")
					.setValue(dataSet.getDataSetContent().getMinValue());
			dataConfig
					.getRepository()
					.getInternalDoubleAttribute(
							"$("
									+ dataConfig.getDatasetConfig()
											.getDataSet().getOriginalDataSet()
											.getAbsolutePath()
									+ ":maxSimilarity)")
					.setValue(dataSet.getDataSetContent().getMaxValue());
			dataConfig
					.getRepository()
					.getInternalDoubleAttribute(
							"$("
									+ dataConfig.getDatasetConfig()
											.getDataSet().getOriginalDataSet()
											.getAbsolutePath()
									+ ":meanSimilarity)")
					.setValue(dataSet.getDataSetContent().getMean());
		}
		dataConfig
				.getRepository()
				.getInternalIntegerAttribute(
						"$("
								+ dataConfig.getDatasetConfig().getDataSet()
										.getOriginalDataSet().getAbsolutePath()
								+ ":numberOfElements)")
				.setValue(ds.getIds().size());
		ds.unloadFromMemory();
		ProgramConfig programConfig = getRepository().getStaticObjectWithName(
				ProgramConfig.class, "TransClust_2");

		ClusteringQualityMeasure f2 = ClusteringQualityMeasure.parseFromString(
				getRepository(), "TransClustF2ClusteringQualityMeasure",
				new ClusteringQualityMeasureParameters());
		ParameterOptimizationMethod method = ParameterOptimizationMethod
				.parseFromString(
						getRepository(),
						"LayeredDivisiveParameterOptimizationMethod",
						getRepository().getStaticObjectWithName(Run.class,
								"baechler2003"), programConfig, dataConfig,
						programConfig.getOptimizableParams(), f2, 1001, true);
		method.reset(new File(
				"testCaseRepository/results/04_15_2013-16_39_59_baechler2003/clusters/TransClust_2_baechler2003.results.qual.complete.test"));
		List<ClusteringQualitySet> qualitySets = new ArrayList<ClusteringQualitySet>();

		List<Integer> iterationNumbers = Arrays.asList(34);
		double[] f2s = new double[]{0.8337456704601682};

		for (double d : f2s) {
			ClusteringQualitySet qualitySet = new ClusteringQualitySet();
			qualitySet.put(f2, ClusteringQualityMeasureValue.getForDouble(d));
			qualitySets.add(qualitySet);
		}

		List<ParameterSet> expectedParameterSets = new ArrayList<ParameterSet>();
		double[] thresholds = new double[]{0.005706059388063329};
		for (double T : thresholds) {
			ParameterSet paramSet = new ParameterSet();
			paramSet.put("T", T + "");
			expectedParameterSets.add(paramSet);
		}

		Iterator<Integer> itItNum = iterationNumbers.iterator();
		Iterator<ClusteringQualitySet> it = qualitySets.iterator();
		Iterator<ParameterSet> itParams = expectedParameterSets.iterator();
		while (method.hasNext() && it.hasNext() && itParams.hasNext()) {
			try {
				ParameterSet paramSet = method.next();
				System.out.println(paramSet);
				Assert.assertEquals(itParams.next(), paramSet);
				method.giveQualityFeedback(paramSet, it.next());
				Assert.assertEquals(itItNum.next().intValue(),
						method.currentCount);
			} catch (ParameterSetAlreadyEvaluatedException e) {
				continue;
			}
		}
	}

	@Test
	public void testDivisiveNumberIterationsTwoParamsWithOptions()
			throws UnknownClusteringQualityMeasureException,
			UnknownParameterOptimizationMethodException,
			UnknownContextException, IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException,
			InternalAttributeException, ParameterOptimizationException {

		ClustevalBackendServer.logLevel(Level.INFO);

		Context context = Context.parseFromString(getRepository(),
				"ClusteringContext");

		DataConfig dataConfig = getRepository().getStaticObjectWithName(
				DataConfig.class, "DS1");
		// DataSet ds = dataConfig.getDatasetConfig().getDataSet();
		// DataSetFormat internal =
		// DataSetFormat.parseFromString(getRepository(),
		// "SimMatrixDataSetFormat");
		// ds = ds.preprocessAndConvertTo(
		// context,
		// internal,
		// new ConversionInputToStandardConfiguration(DistanceMeasure
		// .parseFromString(getRepository(),
		// "EuclidianDistanceMeasure"),
		// NUMBER_PRECISION.DOUBLE,
		// new ArrayList<DataPreprocessor>(),
		// new ArrayList<DataPreprocessor>()),
		// new ConversionStandardToInputConfiguration());
		// ds.loadIntoMemory();
		// if (ds instanceof RelativeDataSet) {
		// RelativeDataSet dataSet = (RelativeDataSet) ds;
		// dataConfig
		// .getRepository()
		// .getInternalDoubleAttribute(
		// "$("
		// + dataConfig.getDatasetConfig()
		// .getDataSet().getOriginalDataSet()
		// .getAbsolutePath()
		// + ":minSimilarity)")
		// .setValue(dataSet.getDataSetContent().getMinValue());
		// dataConfig
		// .getRepository()
		// .getInternalDoubleAttribute(
		// "$("
		// + dataConfig.getDatasetConfig()
		// .getDataSet().getOriginalDataSet()
		// .getAbsolutePath()
		// + ":maxSimilarity)")
		// .setValue(dataSet.getDataSetContent().getMaxValue());
		// dataConfig
		// .getRepository()
		// .getInternalDoubleAttribute(
		// "$("
		// + dataConfig.getDatasetConfig()
		// .getDataSet().getOriginalDataSet()
		// .getAbsolutePath()
		// + ":meanSimilarity)")
		// .setValue(dataSet.getDataSetContent().getMean());
		// }
		// dataConfig
		// .getRepository()
		// .getInternalIntegerAttribute(
		// "$("
		// + dataConfig.getDatasetConfig().getDataSet()
		// .getOriginalDataSet().getAbsolutePath()
		// + ":numberOfElements)")
		// .setValue(ds.getIds().size());
		// ds.unloadFromMemory();
		ProgramConfig programConfig = getRepository().getStaticObjectWithName(
				ProgramConfig.class, "Hierarchical_Clustering");

		ClusteringQualityMeasure f2 = ClusteringQualityMeasure.parseFromString(
				getRepository(), "TransClustF2ClusteringQualityMeasure",
				new ClusteringQualityMeasureParameters());
		ParameterOptimizationMethod method = ParameterOptimizationMethod
				.parseFromString(
						getRepository(),
						"DivisiveParameterOptimizationMethod",
						getRepository().getStaticObjectWithName(Run.class,
								"hclust_vs_DS1"), programConfig, dataConfig,
						programConfig.getOptimizableParams(), f2, 70, true);
		method.initParameterValues();
		Field field = method.getClass().getDeclaredField(
				"iterationPerParameter");
		field.setAccessible(true);
		int[] iterations = (int[]) field.get(method);
		Assert.assertArrayEquals(new int[]{10, 7}, iterations);
	}
}
