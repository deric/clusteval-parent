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
package de.clusteval.run.runnable;

import de.clusteval.api.exceptions.FormatConversionException;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RException;
import de.clusteval.api.run.IterationRunnable;
import de.clusteval.api.stats.IStatistic;
import de.clusteval.api.stats.StatisticCalculateException;
import de.clusteval.api.stats.RunStatistic;
import de.clusteval.utils.FileUtils;
import de.clusteval.api.stats.StatisticCalculator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.openide.util.Exceptions;

/**
 * @author Christian Wiwie
 * @param <S>
 * @param <IW>
 *
 */
public abstract class AnalysisIterationRunnable<S extends IStatistic, IW extends AnalysisIterationWrapper<S>> extends IterationRunnable<IW> {

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
                   InvocationTargetException, UnknownDataSetFormatException, UnknownProviderException;

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
            IStatistic statistic = iterationWrapper.getStatistic();
            String output = getOutputPath();

            final File outputFile = new File(output);
            if (this.iterationWrapper.isResume() && outputFile.exists()) {
                return;
            }

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
        } catch (IOException | SecurityException | IllegalArgumentException | NoSuchMethodException |
                InstantiationException | IllegalAccessException | InvocationTargetException |
                StatisticCalculateException | InvalidDataSetFormatVersionException | UnknownDataSetFormatException | RegisterException | FormatConversionException e) {
            e.printStackTrace();
        } catch (RException | UnknownProviderException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            // TODO
            // currentPos++;
            // int iterationPercent = Math.min((int) (currentPos
            // / (double) this.statistics.size() * 100), 100);
            // this.progress.update(iterationPercent);
        }
    }
;

}
