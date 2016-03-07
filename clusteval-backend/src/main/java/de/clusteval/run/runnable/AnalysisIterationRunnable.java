/**
 * 
 */
package de.clusteval.run.runnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.rosuda.REngine.REngineException;

import de.clusteval.data.dataset.format.InvalidDataSetFormatVersionException;
import de.clusteval.data.dataset.format.UnknownDataSetFormatException;
import de.clusteval.data.statistics.StatisticCalculateException;
import de.clusteval.framework.repository.RegisterException;
import de.clusteval.run.statistics.RunStatistic;
import de.clusteval.utils.FormatConversionException;
import de.clusteval.utils.RNotAvailableException;
import de.clusteval.utils.Statistic;
import de.clusteval.utils.StatisticCalculator;
import de.wiwie.wiutils.file.FileUtils;

/**
 * @author Christian Wiwie
 * 
 */
public abstract class AnalysisIterationRunnable<S extends Statistic, IW extends AnalysisIterationWrapper<S>>
		extends
			IterationRunnable<IW> {

	/**
	 * A temporary variable needed during execution of this runnable.
	 */
	protected File calcFile;

	protected S result;

	/**
	 * @param iterationWrapper
	 */
	public AnalysisIterationRunnable(final IW iterationWrapper) {
		super(iterationWrapper);
	}

	public S getStatistic() {
		return this.iterationWrapper.getStatistic();
	}

	public void setStatistic(final S statistic) {
		this.iterationWrapper.setStatistic(statistic);
	}

	/**
	 * A helper method of {@link #doRun()}, which can be overridden to do any
	 * kind of precalculations and operations needed before a statistic is
	 * assessed.
	 */
	protected abstract void beforeStatisticCalculate();

	/**
	 * A helper method for {@link #doRun()} which returns the statistic
	 * calculator for the current statistic to be calculated.
	 * 
	 * <p>
	 * This method is abstract since it has to provide different behaviour for
	 * different subtypes of this class.
	 * 
	 * @return
	 * @throws FormatConversionException
	 * @throws IOException
	 * @throws InvalidDataSetFormatVersionException
	 * @throws RegisterException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws UnknownDataSetFormatException
	 */
	protected abstract StatisticCalculator<S> getStatisticCalculator()
			throws FormatConversionException, IOException,
			InvalidDataSetFormatVersionException, RegisterException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, UnknownDataSetFormatException;

	/**
	 * A helper method for {@link #doRun()}. It returns the absolute path to the
	 * result file for the current statistic to be calculated.
	 * 
	 * <p>
	 * This method is abstract since it has to provide different behaviour for
	 * different subtypes of this class.
	 * 
	 * @return Abstract path to the output file.
	 */
	protected abstract String getOutputPath();

	@Override
	public void doRun() throws InterruptedException {
		try {
			Statistic statistic = iterationWrapper.getStatistic();
			String output = getOutputPath();

			final File outputFile = new File(output);
			if (this.iterationWrapper.isResume() && outputFile.exists())
				return;

			BufferedWriter bw;
			bw = new BufferedWriter(new FileWriter(outputFile));

			this.calcFile = new File(FileUtils.buildPath(this.getRun()
					.getRepository().getBasePath(RunStatistic.class),
					statistic.getIdentifier() + ".jar")).getAbsoluteFile();

			StatisticCalculator<S> calc = getStatisticCalculator();

			this.beforeStatisticCalculate();
			result = calc.calculate();
			bw.write(result.toString());
			bw.close();

			calc.writeOutputTo(new File(output));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (StatisticCalculateException e) {
			e.printStackTrace();
		} catch (RNotAvailableException e) {
			e.printStackTrace();
		} catch (REngineException e) {
			e.printStackTrace();
		} catch (InvalidDataSetFormatVersionException e) {
			e.printStackTrace();
		} catch (UnknownDataSetFormatException e) {
			e.printStackTrace();
		} catch (RegisterException e) {
			e.printStackTrace();
		} catch (FormatConversionException e) {
			e.printStackTrace();
		} finally {
			// TODO
			// currentPos++;
			// int iterationPercent = Math.min((int) (currentPos
			// / (double) this.statistics.size() * 100), 100);
			// this.progress.update(iterationPercent);
		}
	};

}
