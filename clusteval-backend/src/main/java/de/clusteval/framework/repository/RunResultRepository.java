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
package de.clusteval.framework.repository;

import de.clusteval.api.ClusteringEvaluation;
import de.clusteval.api.IContext;
import de.clusteval.api.IDistanceMeasure;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.api.data.IDataPreprocessor;
import de.clusteval.api.data.IDataRandomizer;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.data.IDataSetGenerator;
import de.clusteval.api.data.IDataSetType;
import de.clusteval.api.exceptions.DatabaseConnectException;
import de.clusteval.api.opt.IParameterOptimizationMethod;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRProgram;
import de.clusteval.api.r.InvalidRepositoryException;
import de.clusteval.api.r.RepositoryAlreadyExistsException;
import de.clusteval.api.repository.DynamicRepositoryEntityMap;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.StaticRepositoryEntity;
import de.clusteval.api.repository.StaticRepositoryEntityMap;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.api.run.IRunResultPostprocessor;
import de.clusteval.api.stats.IDataStatistic;
import de.clusteval.api.stats.IRunDataStatistic;
import de.clusteval.api.stats.IRunStatistic;
import de.clusteval.cluster.Clustering;
import de.clusteval.api.data.DataConfig;
import de.clusteval.api.data.DataSetConfig;
import de.clusteval.data.dataset.DataSetRegisterException;
import de.clusteval.api.data.GoldStandard;
import de.clusteval.api.data.GoldStandardConfig;
import de.clusteval.data.preprocessing.DataPreprocessor;
import de.clusteval.data.randomizer.DataRandomizer;
import de.clusteval.framework.repository.config.RepositoryConfigNotFoundException;
import de.clusteval.framework.repository.config.RepositoryConfigurationException;
import de.clusteval.framework.repository.db.RunResultSQLCommunicator;
import de.clusteval.framework.repository.db.SQLCommunicator;
import de.clusteval.framework.repository.db.StubSQLCommunicator;
import de.clusteval.framework.threading.RunResultRepositorySupervisorThread;
import de.clusteval.framework.threading.SupervisorThread;
import de.clusteval.program.DoubleProgramParameter;
import de.clusteval.program.IntegerProgramParameter;
import de.clusteval.program.Program;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.StringProgramParameter;
import de.clusteval.run.Run;
import de.clusteval.run.result.RunResult;
import de.clusteval.run.result.postprocessing.RunResultPostprocessor;
import de.clusteval.run.statistics.RunStatistic;
import de.clusteval.utils.FileUtils;
import de.clusteval.utils.Finder;
import de.clusteval.utils.NamedDoubleAttribute;
import de.clusteval.utils.NamedIntegerAttribute;
import de.clusteval.utils.NamedStringAttribute;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A runresult repository corresponds to a runresult directory in the results
 * directory of its parent repository.
 *
 * <p>
 * The runresult directories contain copies of the inputs and configurations at
 * the time, the corresponding runs were started. Therefore every runresult
 * directory can be treated as an individual smaller repository which contains a
 * subset of the files as a regular {@link Repository}.
 *
 * @author Christian Wiwie
 *
 */
public class RunResultRepository extends Repository implements IRepository {

    // TODO: check, whether all those are needed for a RunResultRepository
    /**
     * @param basePath The absolute path of the root directory of this
     *                 repository.
     * @param parent   The parent repository.
     * @throws FileNotFoundException
     * @throws RepositoryAlreadyExistsException
     * @throws InvalidRepositoryException
     * @throws RepositoryConfigurationException
     * @throws RepositoryConfigNotFoundException
     * @throws DatabaseConnectException
     */
    public RunResultRepository(String basePath, IRepository parent)
            throws FileNotFoundException, RepositoryAlreadyExistsException,
            InvalidRepositoryException, RepositoryConfigNotFoundException,
            RepositoryConfigurationException, DatabaseConnectException {
        super(basePath, parent);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Repository#createSQLCommunicator()
     */
    @Override
    protected SQLCommunicator createSQLCommunicator()
            throws DatabaseConnectException {
        if (this.parent.getRepositoryConfig().getDbConfig().usesSql()) {
            return new RunResultSQLCommunicator(this,
                    this.parent.getRepositoryConfig().getDbConfig());
        }
        return new StubSQLCommunicator(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Repository#initAttributes()
     */
    @Override
    protected void initAttributes() {

        this.staticRepositoryEntities = new StaticRepositoryEntityMap();
        this.dynamicRepositoryEntities = new DynamicRepositoryEntityMap();

        this.createAndAddStaticEntity(DataConfig.class,
                FileUtils.buildPath(this.basePath, "configs"));
        this.createAndAddStaticEntity(DataSetConfig.class,
                FileUtils.buildPath(this.basePath, "configs"));
        this.createAndAddStaticEntity(GoldStandardConfig.class,
                FileUtils.buildPath(this.basePath, "configs"));
        this.createAndAddStaticEntity(ProgramConfig.class,
                FileUtils.buildPath(this.basePath, "configs"));
        this.createAndAddStaticEntity(Run.class,
                FileUtils.buildPath(this.basePath, "configs"));

        this.staticRepositoryEntities.put(
                DataSet.class,
                new RunResultRepositoryDataSetObjectEntity(this,
                        this.parent != null
                        ? this.parent.getStaticEntities()
                                .get(DataSet.class) : null, FileUtils
                        .buildPath(this.basePath, "inputs")));

        this.staticRepositoryEntities.put(
                GoldStandard.class,
                new RunResultRepositoryGoldStandardObjectEntity(this,
                        this.parent != null
                        ? this.parent.getStaticEntities()
                                .get(GoldStandard.class) : null,
                        FileUtils.buildPath(this.basePath, "goldstandards")));

        this.staticRepositoryEntities.put(Program.class,
                this.parent.getStaticEntities().get(Program.class));
        this.staticRepositoryEntities.put(Clustering.class,
                this.parent.getStaticEntities().get(Clustering.class));

        // this.staticRepositoryEntities.put(
        // RunResult.class,
        // new RunResultRunResultRepositoryEntity(this,
        // this.parent.staticRepositoryEntities
        // .get(RunResult.class), this.getBasePath()));
        this.staticRepositoryEntities.put(RunResult.class,
                this.parent.getStaticEntities().get(RunResult.class));

        this.staticRepositoryEntities.put(Finder.class,
                this.parent.getStaticEntities().get(Finder.class));

        // this.staticRepositoryEntities.put(DoubleProgramParameter.class,
        // this.parent.staticRepositoryEntities
        // .get(DoubleProgramParameter.class));
        //
        // this.staticRepositoryEntities.put(IntegerProgramParameter.class,
        // this.parent.staticRepositoryEntities
        // .get(IntegerProgramParameter.class));
        //
        // this.staticRepositoryEntities.put(StringProgramParameter.class,
        // this.parent.staticRepositoryEntities
        // .get(StringProgramParameter.class));
        this.staticRepositoryEntities.put(
                DoubleProgramParameter.class,
                new ProgramParameterRepositoryEntity<DoubleProgramParameter>(
                        this, this.parent != null
                              ? this.parent.getStaticEntities()
                                .get(DoubleProgramParameter.class)
                              : null, null));
        this.staticRepositoryEntities.put(
                IntegerProgramParameter.class,
                new ProgramParameterRepositoryEntity<IntegerProgramParameter>(
                        this, this.parent != null
                              ? this.parent.getStaticEntities()
                                .get(IntegerProgramParameter.class)
                              : null, null));
        this.staticRepositoryEntities.put(
                StringProgramParameter.class,
                new ProgramParameterRepositoryEntity<StringProgramParameter>(
                        this, this.parent != null
                              ? this.parent.getStaticEntities()
                                .get(StringProgramParameter.class)
                              : null, null));

        this.dynamicRepositoryEntities.put(IDistanceMeasure.class,
                this.parent.getDynamicEntities()
                .get(IDistanceMeasure.class));

        this.dynamicRepositoryEntities.put(IDataSetGenerator.class,
                this.parent.getDynamicEntities()
                .get(IDataSetGenerator.class));

        this.dynamicRepositoryEntities
                .put(IDataRandomizer.class,
                        this.parent.getDynamicEntities()
                        .get(DataRandomizer.class));

        this.dynamicRepositoryEntities.put(IDataPreprocessor.class,
                this.parent.getDynamicEntities()
                .get(DataPreprocessor.class));

        this.dynamicRepositoryEntities.put(IRunResultPostprocessor.class,
                this.parent.getDynamicEntities()
                .get(RunResultPostprocessor.class));

        this.dynamicRepositoryEntities.put(IDataStatistic.class,
                this.parent.getDynamicEntities().get(IDataStatistic.class));

        this.dynamicRepositoryEntities.put(RunStatistic.class,
                this.parent.getDynamicEntities().get(IRunStatistic.class));

        this.dynamicRepositoryEntities.put(IRunDataStatistic.class,
                this.parent.getDynamicEntities()
                .get(IRunDataStatistic.class));

        this.dynamicRepositoryEntities.put(ClusteringEvaluation.class,
                this.parent.getDynamicEntities()
                .get(ClusteringEvaluation.class));

        this.dynamicRepositoryEntities.put(IRProgram.class,
                this.parent.getDynamicEntities().get(IRProgram.class));

        this.dynamicRepositoryEntities.put(IContext.class,
                this.parent.getDynamicEntities().get(IContext.class));

        this.dynamicRepositoryEntities.put(IParameterOptimizationMethod.class,
                this.parent.getDynamicEntities()
                .get(IParameterOptimizationMethod.class));

        this.dynamicRepositoryEntities.put(IDataSetType.class,
                this.parent.getDynamicEntities().get(IDataSetType.class));

        this.dynamicRepositoryEntities.put(IDataSetFormat.class,
                this.parent.getDynamicEntities().get(IDataSetFormat.class));

        this.dynamicRepositoryEntities.put(IRunResultFormat.class,
                this.parent.getDynamicEntities()
                .get(IRunResultFormat.class));

        this.goldStandardFormats = new ConcurrentHashMap<>();

        this.internalDoubleAttributes = (Map<String, NamedDoubleAttribute>) this.parent.getDoubleAttributes();
        this.internalStringAttributes = (Map<String, NamedStringAttribute>) this.parent.getStringAttributes();
        this.internalIntegerAttributes = (Map<String, NamedIntegerAttribute>) this.parent.getIntegerAttributes();

        // added 14.04.2013
        this.knownFinderExceptions = this.parent.getKnownFinderExceptions();
        this.finderClassLoaders = this.parent.getJARFinderClassLoaders();
        this.finderWaitingFiles = this.parent.getJARFinderWaitingFiles();
        this.finderLoadedJarFileChangeDates = this.parent.getFinderLoadedJarFileChangeDates();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Repository#initializePaths()
     */
    @Override
    public void initializePaths() throws InvalidRepositoryException {
        if (this.parent == null) {
            throw new InvalidRepositoryException(
                    "A RunResultRepository needs a valid parent repository");
        }

        this.supplementaryBasePath = this.parent.getSupplementaryBasePath();
        this.suppClusteringBasePath = this.parent.getSupplementaryClusteringBasePath();
        this.formatsBasePath = this.parent.getFormatsBasePath();
        this.generatorBasePath = this.parent.getGeneratorBasePath();
        this.typesBasePath = this.parent.getTypesBasePath();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Repository#log(java.lang.String)
     */
    @Override
    public void info(String message) {
        // reduce visibility of log messages
        this.log.debug(message);
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.Repository#warn(java.lang.String)
     */
    @Override
    public void warn(String message) {
        // reduce visibility of log messages
        this.log.debug(message);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Repository#createSupervisorThread()
     */
    @Override
    protected SupervisorThread createSupervisorThread() {
        return new RunResultRepositorySupervisorThread(this,
                this.getParent().getRepositoryConfig().getThreadSleepTimes());
    }
}

class RunResultRepositoryDataSetObjectEntity
        extends
        StaticRepositoryEntity<DataSet> {

    /**
     * @param repository
     * @param parent
     * @param basePath
     */
    public RunResultRepositoryDataSetObjectEntity(Repository repository,
            StaticRepositoryEntity<DataSet> parent, String basePath) {
        super(repository, parent, basePath);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.framework.repository.RepositoryObjectEntity#register(de.
     * clusteval.framework.repository.RepositoryObject)
     */
    @Override
    public boolean register(DataSet object) throws RegisterException {
        DataSet dataSetInParentRepository = object.getRepository().getParent()
                .getStaticObjectWithName(DataSet.class, object.getFullName());
        if (dataSetInParentRepository != null) {
            return super.register(object);
        }
        throw new DataSetRegisterException("The dataset '"
                + object.getAbsolutePath()
                + "' of a runresult is missing in its parent repository.");
    }
}

class RunResultRepositoryGoldStandardObjectEntity
        extends
        StaticRepositoryEntity<GoldStandard> {

    /**
     * @param repository
     * @param parent
     * @param basePath
     */
    public RunResultRepositoryGoldStandardObjectEntity(Repository repository,
            StaticRepositoryEntity<GoldStandard> parent, String basePath) {
        super(repository, parent, basePath);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.framework.repository.RepositoryObjectEntity#register(de.
     * clusteval.framework.repository.RepositoryObject)
     */
    @Override
    public boolean register(GoldStandard object) throws RegisterException {
        GoldStandard gsInParentRepository = object.getRepository().getParent()
                .getStaticObjectWithName(GoldStandard.class, object.toString());
        if (gsInParentRepository != null) {
            return super.register(object);
        }
        throw new DataSetRegisterException("The goldstandard '"
                + object.getAbsolutePath()
                + "' of a runresult is missing in its parent repository.");
    }
}
