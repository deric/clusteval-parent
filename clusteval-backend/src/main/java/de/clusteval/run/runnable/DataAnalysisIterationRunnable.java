package de.clusteval.run.runnable;

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.factory.DataStatisticFactory;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.stats.IDataStatistic;
import de.clusteval.data.DataConfig;
import de.clusteval.data.statistics.DataStatisticCalculator;
import de.clusteval.utils.StatisticCalculator;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Christian Wiwie
 *
 */
public class DataAnalysisIterationRunnable extends AnalysisIterationRunnable<IDataStatistic, DataAnalysisIterationWrapper> {

    /**
     * @param iterationWrapper
     */
    public DataAnalysisIterationRunnable(DataAnalysisIterationWrapper iterationWrapper) {
        super(iterationWrapper);
    }

    public IDataConfig getDataConfig() {
        return this.iterationWrapper.getDataConfig();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.run.runnable.AnalysisIterationRunnable#beforeStatisticCalculate
     * ()
     */
    @Override
    protected void beforeStatisticCalculate() {
        this.log.info("Run " + this.getRun() + " - (" + this.getDataConfig()
                + ") Analysing " + getStatistic().getIdentifier());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * run.runnable.AnalysisRunRunnable#getStatisticCalculator(java.lang.Class)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected StatisticCalculator<IDataStatistic> getStatisticCalculator()
            throws SecurityException, NoSuchMethodException,
                   IllegalArgumentException, InstantiationException,
                   IllegalAccessException, InvocationTargetException {

        Class<? extends IDataStatistic> calcClass = DataStatisticFactory.getInstance()
                .getProvider(getStatistic().getName()).getClass();

        Constructor<? extends IDataStatistic> constr = calcClass
                .getConstructor(IRepository.class, long.class, File.class,
                        DataConfig.class);
        DataStatisticCalculator calc = (DataStatisticCalculator) constr.newInstance(this.getRun()
                .getRepository(), calcFile.lastModified(), calcFile, this
                .getDataConfig());
        return calc;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.runnable.AnalysisIterationRunnable#getOutputPath()
     */
    @Override
    protected String getOutputPath() {
        return FileUtils.buildPath(this.iterationWrapper.getAnalysesFolder(),
                this.getDataConfig() + "_"
                + this.getStatistic().getClass().getSimpleName()
                + ".txt");
    }
}
