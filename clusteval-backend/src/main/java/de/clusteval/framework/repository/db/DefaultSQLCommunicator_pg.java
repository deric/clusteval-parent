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
/**
 *
 */
package de.clusteval.framework.repository.db;

import de.clusteval.cluster.Clustering;
import de.clusteval.cluster.paramOptimization.ParameterOptimizationMethod;
import de.clusteval.cluster.quality.ClusteringQualityMeasure;
import de.clusteval.cluster.quality.ClusteringQualityMeasureParameters;
import de.clusteval.cluster.quality.ClusteringQualitySet;
import de.clusteval.context.Context;
import de.clusteval.data.DataConfig;
import de.clusteval.data.dataset.DataSet;
import de.clusteval.data.dataset.DataSetConfig;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.data.dataset.type.DataSetType;
import de.clusteval.data.goldstandard.GoldStandard;
import de.clusteval.data.goldstandard.GoldStandardConfig;
import de.clusteval.data.statistics.DataStatistic;
import de.clusteval.framework.repository.NoRepositoryFoundException;
import de.clusteval.framework.repository.Repository;
import de.clusteval.framework.repository.RunResultRepository;
import de.clusteval.program.DoubleProgramParameter;
import de.clusteval.program.IntegerProgramParameter;
import de.clusteval.program.ParameterSet;
import de.clusteval.program.Program;
import de.clusteval.program.ProgramConfig;
import de.clusteval.program.ProgramParameter;
import de.clusteval.program.StringProgramParameter;
import de.clusteval.run.AnalysisRun;
import de.clusteval.run.ClusteringRun;
import de.clusteval.run.DataAnalysisRun;
import de.clusteval.run.ExecutionRun;
import de.clusteval.run.InternalParameterOptimizationRun;
import de.clusteval.run.ParameterOptimizationRun;
import de.clusteval.run.Run;
import de.clusteval.run.RunAnalysisRun;
import de.clusteval.run.RunDataAnalysisRun;
import de.clusteval.run.result.AnalysisRunResult;
import de.clusteval.run.result.ClusteringRunResult;
import de.clusteval.run.result.DataAnalysisRunResult;
import de.clusteval.run.result.ExecutionRunResult;
import de.clusteval.run.result.ParameterOptimizationResult;
import de.clusteval.run.result.RunAnalysisRunResult;
import de.clusteval.run.result.RunDataAnalysisRunResult;
import de.clusteval.run.result.RunResult;
import de.clusteval.run.result.format.RunResultFormat;
import de.clusteval.run.statistics.RunDataStatistic;
import de.clusteval.run.statistics.RunStatistic;
import de.clusteval.utils.Statistic;
import de.wiwie.wiutils.utils.Pair;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A default sql communicator is the standard implementation of the abstract
 * {@link SQLCommunicator} and is intended as a default for standard
 * repositories of type {@link Repository}.
 *
 * <p>
 * Subclasses of {@link Repository}, e.g. {@link RunResultRepository} have their
 * own sql communicator implementations.
 *
 * @author Christian Wiwie
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class DefaultSQLCommunicator_pg extends SQLCommunicator {

    /**
     * A sql communicator needs a mysql configuration to know, how to connect to
     * the database server.
     */
    protected SQLConfig mysqlConfig;

    /**
     * @param repository The repository this communicator belongs to.
     * @param mysqlConfig The mysql configuration this communicator should use.
     */
    public DefaultSQLCommunicator_pg(Repository repository,
            final SQLConfig mysqlConfig) {
        super(repository, mysqlConfig);

        try {
            initDB();
        } catch (DatabaseConnectException ex) {
            Logger.getLogger(DefaultSQLCommunicator_pg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getServer()
     */
    @Override
    protected String getServer() {
        return this.mysqlConfig.getHost();
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getDatabase()
     */
    @Override
    protected String getDatabase() {
        return this.mysqlConfig.getDatabase();
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getDBUsername()
     */
    @Override
    protected String getDBUsername() {
        return this.mysqlConfig.getUsername();
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getDBPassword()
     */
    @Override
    protected String getDBPassword() {
        return this.mysqlConfig.getPassword();
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRepositories()
     */
    @Override
    protected String getTableRepositories() {
        return "repositories";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#getTableClusterings()
     */
    @Override
    protected String getTableClusterings() {
        return "clusterings";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#getTableClusterObjects()
     */
    @Override
    protected String getTableClusterObjects() {
        return "cluster_objects";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#getTableClusters()
     */
    @Override
    protected String getTableClusters() {
        return "clusters";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableClusteringQualityMeasures()
     */
    @Override
    protected String getTableClusteringQualityMeasures() {
        return "clustering_quality_measures";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableDataConfigs()
     */
    @Override
    protected String getTableDataConfigs() {
        return "data_configs";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableDataets()
     */
    @Override
    protected String getTableDatasets() {
        return "datasets";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableDataSetConfigs()
     */
    @Override
    protected String getTableDataSetConfigs() {
        return "dataset_configs";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableDataSetFormats()
     */
    @Override
    protected String getTableDataSetFormats() {
        return "dataset_formats";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableGoldStandardConfigs()
     */
    @Override
    protected String getTableGoldStandardConfigs() {
        return "goldstandard_configs";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableGoldStandards()
     */
    @Override
    protected String getTableGoldStandards() {
        return "goldstandards";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableOptimizableProgramParameters()
     */
    @Override
    protected String getTableOptimizableProgramParameters() {
        return "optimizable_program_parameters";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableParameterOptimizationMethod()
     */
    @Override
    protected String getTableParameterOptimizationMethods() {
        return "parameter_optimization_methods";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableProgramConfigs()
     */
    @Override
    protected String getTableProgramConfigs() {
        return "program_configs";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableProgramParameter()
     */
    @Override
    protected String getTableProgramParameter() {
        return "program_parameters";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTablePrograms()
     */
    @Override
    protected String getTablePrograms() {
        return "programs";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableProgramsCompatibleDataSetFormats()
     */
    @Override
    protected String getTableProgramConfigsCompatibleDataSetFormats() {
        return "program_configs_compatible_dataset_formats";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsAnalysis()
     */
    @Override
    protected String getTableRunsAnalysis() {
        return "run_analyses";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsAnalysisData()
     */
    @Override
    protected String getTableRunsAnalysisData() {
        return "run_data_analyses";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsAnalysisDataDataIdentifiers()
     */
    @Override
    protected String getTableRunsAnalysisDataDataIdentifiers() {
        return "run_data_analysis_data_configs";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsAnalysisRun()
     */
    @Override
    protected String getTableRunsAnalysisRun() {
        return "run_run_analyses";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsAnalysisRunRunIdentifiers()
     */
    @Override
    protected String getTableRunsAnalysisRunRunIdentifiers() {
        return "run_run_analysis_run_identifiers";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsAnalysisRunData()
     */
    @Override
    protected String getTableRunsAnalysisRunData() {
        return "run_run_data_analyses";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsAnalysisStatistics()
     */
    @Override
    protected String getTableRunsAnalysisStatistics() {
        return "run_analysis_statistics";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsClustering()
     */
    @Override
    protected String getTableRunsClustering() {
        return "run_clusterings";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsExecution()
     */
    @Override
    protected String getTableRunsExecution() {
        return "run_executions";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsExecutionDataConfigs()
     */
    @Override
    protected String getTableRunsExecutionDataConfigs() {
        return "run_execution_data_configs";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsExecutionParameterValues()
     */
    @Override
    protected String getTableRunsExecutionParameterValues() {
        return "run_execution_parameter_values";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsExecutionProgramConfigs()
     */
    @Override
    protected String getTableRunsExecutionProgramConfigs() {
        return "run_execution_program_configs";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsExecutionQualityMeasures()
     */
    @Override
    protected String getTableRunsExecutionQualityMeasures() {
        return "run_execution_quality_measures";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsInternalParameterOptimization()
     */
    @Override
    protected String getTableRunsInternalParameterOptimization() {
        return "run_internal_parameter_optimizations";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsParameterOptimization()
     */
    @Override
    protected String getTableRunsParameterOptimization() {
        return "run_parameter_optimizations";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsParameterOptimizationMethods()
     */
    @Override
    protected String getTableRunsParameterOptimizationMethods() {
        return "run_parameter_optimization_methods";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#
	 * getTableRunsParameterOptimizationQualityMeasures()
     */
    @Override
    protected String getTableRunsParameterOptimizationQualityMeasures() {
        return "run_parameter_optimization_quality_measures";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunsParameterOptimizationParameters()
     */
    @Override
    protected String getTableRunsParameterOptimizationParameters() {
        return "run_parameter_optimization_parameters";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunResultExecution()
     */
    @Override
    protected String getTableRunResultsExecution() {
        return "run_results_executions";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunResultFormats()
     */
    @Override
    protected String getTableRunResultFormats() {
        return "run_result_formats";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunResultsClustering()
     */
    @Override
    protected String getTableRunResultsClustering() {
        return "run_results_clusterings";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunResultParameterOptimization()
     */
    @Override
    protected String getTableRunResultsParameterOptimization() {
        return "run_results_parameter_optimizations";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#getTableParameterSets()
     */
    @Override
    protected String getTableParameterSets() {
        return "run_results_parameter_optimizations_parameter_sets";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#getTableParameterSetParameters()
     */
    @Override
    protected String getTableParameterSetParameters() {
        return "run_results_parameter_optimizations_parameter_set_parameters";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#getTableParameterSetIterations()
     */
    @Override
    protected String getTableParameterSetIterations() {
        return "run_results_parameter_optimizations_parameter_set_iterations";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#getTableParameterSetParameterValues
	 * ()
     */
    @Override
    protected String getTableParameterSetParameterValues() {
        return "run_results_parameter_optimizations_parameter_values";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#getTableParameterOptimizationQualities
	 * ()
     */
    @Override
    protected String getTableParameterOptimizationQualities() {
        return "run_results_parameter_optimizations_qualities";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunResults()
     */
    @Override
    protected String getTableRunResults() {
        return "run_results";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunResultsDataAnalysis()
     */
    @Override
    protected String getTableRunResultsDataAnalysis() {
        return "run_results_data_analyses";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunResultsRunAnalysis()
     */
    @Override
    protected String getTableRunResultsRunAnalysis() {
        return "run_results_run_analyses";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunResultsRunDataAnalysis()
     */
    @Override
    protected String getTableRunResultsRunDataAnalysis() {
        return "run_results_run_data_analyses";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRuns()
     */
    @Override
    protected String getTableRuns() {
        return "runs";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRunTypes()
     */
    @Override
    protected String getTableRunTypes() {
        return "run_types";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableStatistics()
     */
    @Override
    protected String getTableStatistics() {
        return "statistics";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableStatisticsData()
     */
    @Override
    protected String getTableStatisticsData() {
        return "statistics_data";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableStatisticsRun()
     */
    @Override
    protected String getTableStatisticsRun() {
        return "statistics_runs";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableStatisticsRunData()
     */
    @Override
    protected String getTableStatisticsRunData() {
        return "statistics_run_data";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#registerRunResultFormat(java.lang.Class)
     */
    @Override
    protected boolean registerRunResultFormatClass(
            final Class<? extends RunResultFormat> object) {

        try {
            insert(this.getTableRunResultFormats(),
                    new String[]{"repository_id", "name"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + object.getSimpleName()});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(run.config.Run)
     */
    @Override
    protected boolean register(Run object, final boolean updateOnly) {
        try {
            String type;
            if (object instanceof AnalysisRun) {
                if (object instanceof DataAnalysisRun) {
                    type = "Data Analysis";
                } else if (object instanceof RunDataAnalysisRun) {
                    type = "Run-Data Analysis";
                } else {
                    type = "Run Analysis";
                }
            } else if (object instanceof ClusteringRun) {
                type = "Clustering";
            } else if (object instanceof ParameterOptimizationRun) {
                type = "Parameter Optimization";
            } else {
                type = "Internal Parameter Optimization";
            }

            int run_type_id = this.getRunTypeId(type);

            try {
                String[] columns;
                String[] values;
                if (object.getRepository() instanceof RunResultRepository) {
                    columns = new String[]{"repository_id", "abs_path", "name",
                        "run_type_id", "run_id", "status"};
                    values = new String[]{
                        this.updateRepositoryId() + "",
                        object.getAbsolutePath(),
                        new File(object.getAbsolutePath()).getName()
                        .replace(".run", ""),
                        "" + run_type_id,
                        ""
                        + getRunId(object
                        .getRepository()
                        .getParent()
                        .getStaticObjectWithName(Run.class,
                        object.toString())),
                        "" + object.getStatus()};
                } else {
                    columns = new String[]{"repository_id", "abs_path", "name",
                        "run_type_id", "status"};
                    values = new String[]{
                        this.updateRepositoryId() + "",
                        object.getAbsolutePath(),
                        new File(object.getAbsolutePath()).getName()
                        .replace(".run", ""), "" + run_type_id,
                        "" + object.getStatus()};
                }
                if (updateOnly) {
                    // TODO
                    /*
					 * in case of an update to this run config in the
					 * repository, we need to delete all references to this
					 * object "upstream", that means subtypes of this run in the
					 * database
                     */
                    int rowId = getRunId(object);

                    delete(getTableRunsExecution(), rowId + "");
                    delete(getTableRunsAnalysis(), rowId + "");

                    update(this.getTableRuns(), columns, values, rowId);
                } else {
                    insert(this.getTableRuns(), columns, values);
                }
            } catch (SQLException e) {

                e.printStackTrace();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#register(run.config.AnalysisRun
	 * )
     */
    @Override
    protected boolean register(AnalysisRun<Statistic> object,
            final boolean updateOnly) {
        try {
            this.register((Run) object, updateOnly);
            int run_id = getRunId(object);

            int run_analysis_id = insert(this.getTableRunsAnalysis(),
                    new String[]{"repository_id", "run_id"}, new String[]{
                        "" + this.updateRepositoryId(), "" + run_id});

            for (Statistic st : object.getStatistics()) {
                int statistic_id = getStatisticId(st.getIdentifier());
                insert(this.getTableRunsAnalysisStatistics(), new String[]{
                    "repository_id", "run_analysis_id", "statistic_id"},
                        new String[]{"" + this.updateRepositoryId(),
                            "" + run_analysis_id, "" + statistic_id});
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#register(run.config.
	 * DataAnalysisRun)
     */
    @Override
    protected int register(DataAnalysisRun object, final boolean updateOnly) {
        try {
            this.register((AnalysisRun) object, updateOnly);
            int run_id = getRunId(object);
            int run_analysis_id = getRunAnalysisId(run_id);

            int run_data_analysis_id = insert(this.getTableRunsAnalysisData(),
                    new String[]{"repository_id", "run_analysis_id"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + run_analysis_id});

            DataAnalysisRun dataAnalysis = object;

            for (DataConfig dataConfig : dataAnalysis.getDataConfigs()) {
                insert(this.getTableRunsAnalysisDataDataIdentifiers(),
                        new String[]{"repository_id", "run_data_analysis_id",
                            "data_config_id"},
                        new String[]{"" + this.updateRepositoryId(),
                            "" + run_data_analysis_id,
                            "" + this.getObjectId(dataConfig)});
            }
            return run_data_analysis_id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#register(run.config.RunAnalysisRun )
     */
    @Override
    protected int register(RunAnalysisRun object, final boolean updateOnly) {
        try {
            this.register((AnalysisRun) object, updateOnly);

            int run_id = getRunId(object);
            int run_analysis_id = getRunAnalysisId(run_id);

            int run_run_analysis_id = insert(this.getTableRunsAnalysisRun(),
                    new String[]{"repository_id", "run_analysis_id"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + run_analysis_id});

            RunAnalysisRun runAnalysis = object;

            for (String uniqueRunIdentifier : runAnalysis
                    .getUniqueRunAnalysisRunIdentifiers()) {
                insert(this.getTableRunsAnalysisRunRunIdentifiers(),
                        new String[]{"repository_id", "run_run_analysis_id",
                            "run_identifier"},
                        new String[]{"" + this.updateRepositoryId(),
                            "" + run_run_analysis_id,
                            "" + uniqueRunIdentifier});
            }
            return run_run_analysis_id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#register(run.config.
	 * RunDataAnalysisRun)
     */
    @Override
    protected int register(RunDataAnalysisRun object, final boolean updateOnly) {
        try {
            this.register((AnalysisRun) object, updateOnly);
            int run_id = getRunId(object);
            int run_analysis_id = getRunAnalysisId(run_id);
            // int run_run_analysis_id = getRunAnalysisRunId(run_analysis_id);

            int run_run_data_analysis_id = insert(
                    this.getTableRunsAnalysisRunData(), new String[]{
                        "repository_id", "run_analysis_id"}, new String[]{
                        "" + this.updateRepositoryId(),
                        "" + run_analysis_id});

            RunDataAnalysisRun runDataAnalysis = object;

            for (String uniqueDataIdentifier : runDataAnalysis
                    .getUniqueDataAnalysisRunIdentifiers()) {
                insert(this.getTableRunsAnalysisRunDataDataIdentifiers(),
                        new String[]{"repository_id",
                            "run_run_data_analysis_id", "dataIdentifier"},
                        new String[]{"" + this.updateRepositoryId(),
                            "" + run_run_data_analysis_id,
                            "" + uniqueDataIdentifier});
            }

            for (String uniqueRunIdentifier : runDataAnalysis
                    .getUniqueRunAnalysisRunIdentifiers()) {
                insert(this.getTableRunsAnalysisRunDataRunIdentifiers(),
                        new String[]{"repository_id",
                            "run_run_data_analysis_id", "run_identifier"},
                        new String[]{"" + this.updateRepositoryId(),
                            "" + run_run_data_analysis_id,
                            "" + uniqueRunIdentifier});
            }
            return run_run_data_analysis_id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#register(run.config.ExecutionRun )
     */
    @Override
    protected boolean register(ExecutionRun object, final boolean updateOnly) {
        try {
            this.register((Run) object, updateOnly);
            int run_id = getRunId(object);

            int run_execution_id;
            try {
                run_execution_id = insert(this.getTableRunsExecution(),
                        new String[]{"repository_id", "run_id"}, new String[]{
                            "" + this.updateRepositoryId(), "" + run_id});
            } catch (SQLException e) {
                e.printStackTrace();
                run_execution_id = getRunExecutionId(run_id);
            }

            ExecutionRun execRun = object;

            // insert dataConfigs into DB
            for (DataConfig dataConfig : execRun.getDataConfigs()) {
                int data_config_id = this.getObjectId(dataConfig);

                try {
                    insert(this.getTableRunsExecutionDataConfigs(),
                            new String[]{"repository_id", "run_execution_id",
                                "data_config_id"},
                            new String[]{"" + this.updateRepositoryId(),
                                "" + run_execution_id, "" + data_config_id});
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < execRun.getQualityMeasures().size(); i++) {
                int clustering_quality_measure_id = this.getObjectId(execRun
                        .getQualityMeasures().get(i));

                try {
                    insert(this.getTableRunsExecutionQualityMeasures(),
                            new String[]{"repository_id", "run_execution_id",
                                "clustering_quality_measure_id"},
                            new String[]{"" + this.updateRepositoryId(),
                                "" + run_execution_id,
                                "" + clustering_quality_measure_id});
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // insert programConfigs and parameter values into DB
            for (int i = 0; i < execRun.getProgramConfigs().size(); i++) {
                ProgramConfig programConfig = execRun.getProgramConfigs()
                        .get(i);

                int program_config_id = this.getObjectId(programConfig);
                try {
                    insert(this.getTableRunsExecutionProgramConfigs(),
                            new String[]{"repository_id", "run_execution_id",
                                "program_config_id"}, new String[]{
                                "" + this.updateRepositoryId(),
                                "" + run_execution_id,
                                "" + program_config_id});
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Map<ProgramParameter<?>, String> paramValues = execRun
                        .getParameterValues().get(i);

                for (ProgramParameter<?> param : paramValues.keySet()) {

                    int paramId = this.getObjectId(param);

                    try {
                        insert(this.getTableRunsExecutionParameterValues(),
                                new String[]{"repository_id",
                                    "run_execution_id",
                                    "program_config_id",
                                    "program_parameter_id", "value"},
                                new String[]{"" + this.updateRepositoryId(),
                                    "" + run_execution_id,
                                    "" + program_config_id, "" + paramId,
                                    "" + paramValues.get(param)});
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#register(run.config.ClusteringRun )
     */
    @Override
    protected int register(ClusteringRun run, final boolean updateOnly) {
        try {
            this.register((ExecutionRun) run, updateOnly);
            int run_id = getRunId(run);
            int run_execution_id = getRunExecutionId(run_id);
            int run_clustering_id = insert(this.getTableRunsClustering(),
                    new String[]{"repository_id", "run_execution_id"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + run_execution_id});
            return run_clustering_id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#register(run.config.
	 * InternalParameterOptimizationRun)
     */
    @Override
    protected int register(InternalParameterOptimizationRun run,
            final boolean updateOnly) {
        try {
            this.register((ExecutionRun) run, updateOnly);
            int run_id = getRunId(run);
            int run_execution_id = getRunExecutionId(run_id);
            int run_internal_id = insert(
                    this.getTableRunsInternalParameterOptimization(),
                    new String[]{"repository_id", "run_execution_id"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + run_execution_id});
            return run_internal_id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#register(run.config.
	 * ParameterOptimizationRun)
     */
    @Override
    protected int register(ParameterOptimizationRun object,
            final boolean updateOnly) {
        try {
            this.register((ExecutionRun) object, updateOnly);
            int run_id = getRunId(object);
            int run_execution_id = getRunExecutionId(run_id);

            int runParamOptId = insert(
                    this.getTableRunsParameterOptimization(), new String[]{
                        "repository_id", "run_execution_id"}, new String[]{
                        "" + this.updateRepositoryId(),
                        "" + run_execution_id});

            ParameterOptimizationRun paramOpt = object;

            for (int i = 0; i < paramOpt.getProgramConfigs().size(); i++) {
                int program_config_id = this.getObjectId(paramOpt
                        .getProgramConfigs().get(i));

                ParameterOptimizationMethod method = paramOpt
                        .getOptimizationMethods().get(i);

                int methodId = this.getParameterOptimizationMethodId(method
                        .getClass().getSimpleName());

                int clustering_quality_measure_id = this.getObjectId(method
                        .getOptimizationCriterion());

                insert(this.getTableRunsParameterOptimizationMethods(),
                        new String[]{"repository_id",
                            "run_parameter_optimization_id",
                            "program_config_id",
                            "parameter_optimization_method_id",
                            "clustering_quality_measure_id"}, new String[]{
                            "" + this.updateRepositoryId(),
                            "" + runParamOptId, "" + program_config_id,
                            "" + methodId,
                            "" + clustering_quality_measure_id});

                for (ProgramParameter<?> param : paramOpt
                        .getOptimizationParameters().get(i)) {

                    int paramId = this.getObjectId(param);

                    insert(this.getTableRunsParameterOptimizationParameters(),
                            new String[]{"repository_id",
                                "run_parameter_optimization_id",
                                "program_config_id", "program_parameter_id"},
                            new String[]{"" + this.updateRepositoryId(),
                                "" + runParamOptId, "" + program_config_id,
                                "" + paramId});
                }
            }

            // insert clustering quality measures into DB
            for (ClusteringQualityMeasure measure : object.getQualityMeasures()) {

                int measureId = getObjectId(measure);

                insert(this.getTableRunsParameterOptimizationQualityMeasures(),
                        new String[]{"repository_id",
                            "run_parameter_optimization_id",
                            "clustering_quality_measure_id"}, new String[]{
                            "" + this.updateRepositoryId(),
                            "" + runParamOptId, "" + measureId});
            }

            return runParamOptId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see de.wiwie.wiutils.utils.SQLCommunicator#register(run.Run)
    // */
    // @Override
    // protected boolean register(Run object, final boolean updateOnly) {
    // try {
    // int run_id = this.getRunId(object.getRun());
    //
    // String type;
    // if (object instanceof AnalysisRun) {
    // if (object instanceof DataAnalysisRun)
    // type = "Data Analysis";
    // else if (object instanceof RunDataAnalysisRun)
    // type = "Run-Data Analysis";
    // else
    // type = "Run Analysis";
    // } else {
    // if (object instanceof ClusteringRun)
    // type = "Clustering";
    // else if (object instanceof ParameterOptimizationRun)
    // type = "Parameter Optimization";
    // else
    // type = "Internal Parameter Optimization";
    // }
    //
    // int run_type_id = this.getRunTypeId(type);
    //
    // String[] columns;
    // String[] values;
    // if (object.getRepository() instanceof RunResultRepository) {
    // columns = new String[]{"repository_id", "run_id",
    // "run_type_id", "abs_path", "name", "status", "runOrigId"};
    // values = new String[]{
    // "" + this.repositoryId,
    // "" + run_id,
    // "" + run_type_id,
    // object.getAbsolutePath(),
    // new File(object.getAbsolutePath()).getName().replace(
    // ".run", ""),
    // "" + object.getStatus(),
    // ""
    // + getRunId(object
    // .getRepository()
    // .getParent()
    // .getObjectWithName(Run.class,
    // object.getAbsolutePath()
    // .substring(
    // object.getAbsolutePath()
    // .lastIndexOf(
    // "/") + 1)))};
    // } else {
    // columns = new String[]{"repository_id", "run_id",
    // "run_type_id", "abs_path", "name", "status"};
    // values = new String[]{
    // "" + this.repositoryId,
    // "" + run_id,
    // "" + run_type_id,
    // object.getAbsolutePath(),
    // new File(object.getAbsolutePath()).getName().replace(
    // ".run", ""), "" + object.getStatus()};
    // }
    // if (updateOnly) {
    // int rowId = getRunId(object);
    // update(this.getTableRuns(), columns, values, rowId);
    // } else
    // insert(this.getTableRuns(), columns, values);
    // return true;
    // } catch (SQLException e) {
    //
    // e.printStackTrace();
    // }
    // return false;
    // }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(program.ProgramConfig)
     */
    @Override
    protected int register(ProgramConfig object, final boolean updateOnly) {
        // TODO
        try {
            int program_id = getObjectId(object.getProgram());

            int run_result_format_id = getRunResultFormatId(object
                    .getOutputFormat().getClass().getSimpleName());

            String[] columns;
            String[] values;
            if (object.getRepository() instanceof RunResultRepository) {
                columns = new String[]{
                    "repository_id",
                    "program_id",
                    "run_result_format_id",
                    "name",
                    "abs_path",
                    "invocation_format",
                    "invocation_format_without_gold_standard",
                    "invocation_format_parameter_optimization",
                    "invocation_format_parameter_optimization_without_gold_standard",
                    "expects_normalized_data_set", "program_config_id"};
                values = new String[]{
                    "" + this.updateRepositoryId(),
                    "" + program_id,
                    "" + run_result_format_id,
                    "" + object.getName(),
                    "" + replaceNull(object.getAbsolutePath(), ""),
                    "" + replaceNull(object.getInvocationFormat(false), ""),
                    "" + replaceNull(object.getInvocationFormat(true), ""),
                    ""
                    + replaceNull(
                    object.getInvocationFormatParameterOptimization(false),
                    ""),
                    ""
                    + replaceNull(
                    object.getInvocationFormatParameterOptimization(true),
                    ""),
                    "" + (object.expectsNormalizedDataSet() ? 1 : 0),
                    ""
                    + getObjectId(object
                    .getRepository()
                    .getParent()
                    .getStaticObjectWithName(
                    ProgramConfig.class,
                    object.toString()))};
            } else {
                columns = new String[]{
                    "repository_id",
                    "program_id",
                    "run_result_format_id",
                    "name",
                    "abs_path",
                    "invocation_format",
                    "invocation_format_without_gold_standard",
                    "invocation_format_parameter_optimization",
                    "invocation_format_parameter_optimization_without_gold_standard",
                    "expects_normalized_data_set"};
                values = new String[]{
                    "" + this.updateRepositoryId(),
                    "" + program_id,
                    "" + run_result_format_id,
                    "" + object.getName(),
                    "" + replaceNull(object.getAbsolutePath(), ""),
                    "" + replaceNull(object.getInvocationFormat(false), ""),
                    "" + replaceNull(object.getInvocationFormat(true), ""),
                    ""
                    + replaceNull(
                    object.getInvocationFormatParameterOptimization(false),
                    ""),
                    ""
                    + replaceNull(
                    object.getInvocationFormatParameterOptimization(true),
                    ""),
                    "" + (object.expectsNormalizedDataSet() ? 1 : 0)};
            }

            int rowId;
            if (updateOnly) {
                rowId = getObjectId(object);

                delete(this.getTableOptimizableProgramParameters(), rowId + "",
                        "program_config_id");

                delete(this.getTableProgramConfigsCompatibleDataSetFormats(),
                        rowId + "", "program_config_id");

                update(this.getTableProgramConfigs(), columns, values, rowId);
            } else {
                rowId = insert(this.getTableProgramConfigs(), columns, values);
            }

            for (DataSetFormat dsFormat : object.getCompatibleDataSetFormats()) {
                int dataset_format_id = getDataSetFormatId(dsFormat.getClass()
                        .getSimpleName());

                insert(this.getTableProgramConfigsCompatibleDataSetFormats(),
                        new String[]{"repository_id", "program_config_id",
                            "dataset_format_id"}, new String[]{
                            "" + updateRepositoryId(), "" + rowId,
                            "" + dataset_format_id});
            }

            for (ProgramParameter<?> param : object.getOptimizableParams()) {
                int program_config_id = getObjectId(object);
                int program_parameter_id = getObjectId(param);

                insert(this.getTableOptimizableProgramParameters(),
                        new String[]{"repository_id", "program_config_id",
                            "program_parameter_id"},
                        new String[]{"" + this.updateRepositoryId(),
                            "" + program_config_id,
                            "" + program_parameter_id});
            }
            return rowId;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getRunId(java.lang.String)
     */
    @Override
    protected int getRunId(final Run run) throws SQLException {
        return this
                .select(this.getTableRuns(),
                        "id",
                        new String[]{"repository_id", "abs_path"},
                        new String[]{
                            getRepositoryId(run.getRepository()
                                    .getBasePath()) + "",
                            run.getAbsolutePath()});
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#getClusteringId(java.lang.String)
     */
    @Override
    protected int getClusteringId(String absPath) throws SQLException {
        return this.select(this.getTableClusterings(), "id",
                new String[]{"abs_path"}, new String[]{absPath});
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#getClusterId(int,
	 * java.lang.String)
     */
    @Override
    protected int getClusterId(int clusteringId, String name)
            throws SQLException {
        return this.select(this.getTableClusters(), "id", new String[]{
            "repository_id", "clustering_id", "name"},
                new String[]{this.updateRepositoryId() + "", clusteringId + "",
                    name});
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#getClusterObjectId(int,
	 * java.lang.String)
     */
    @Override
    protected int getClusterObjectId(int clusterId, String name)
            throws SQLException {
        return this.select(this.getTableClusterObjects(), "id", new String[]{
            "repository_id", "cluster_id", "name"},
                new String[]{this.updateRepositoryId() + "", clusterId + "",
                    name});
    }

    @Override
    protected int getDataSetTypeId(final String dataSetTypeClassSimpleName)
            throws SQLException {
        return this.select(this.getTableDataSetTypes(), "id", new String[]{
            "repository_id", "name"},
                new String[]{this.updateRepositoryId() + "",
                    dataSetTypeClassSimpleName});
    }

    @Override
    protected int getRepositoryTypeId(final String repositoryType)
            throws SQLException {
        return this.select(this.getTableRepositoryTypes(), "id",
                new String[]{"name"}, new String[]{repositoryType});
    }

    @Override
    protected int getParameterOptimizationMethodId(final String name)
            throws SQLException {
        return this.select(this.getTableParameterOptimizationMethods(), "id",
                new String[]{"repository_id", "name"},
                new String[]{this.updateRepositoryId() + "", name});
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#getParameterSetId(java.lang.String)
     */
    @Override
    protected int getParameterSetId(final int runResultParamOptId)
            throws SQLException {
        return this.select(this.getTableParameterSets(), "id", new String[]{
            "repository_id", "run_results_parameter_optimization_id"},
                new String[]{this.updateRepositoryId() + "",
                    runResultParamOptId + ""});
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#getParameterSetParameterId(int,
	 * int)
     */
    @Override
    protected int getParameterSetParameterId(
            int run_results_parameter_optimizations_parameter_set_id,
            int program_parameter_id) throws SQLException {
        return this.select(this.getTableParameterSetParameters(), "id",
                new String[]{
                    "run_results_parameter_optimizations_parameter_set_id",
                    "program_parameter_id"}, new String[]{
                    run_results_parameter_optimizations_parameter_set_id
                    + "", program_parameter_id + ""});
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#getParameterSetId(java.lang.String)
     */
    @Override
    protected int getParameterSetParameterValuesId(
            final int run_results_parameter_optimizations_parameter_set_id,
            final int program_parameter_id, final int iteration)
            throws SQLException {
        return this
                .select(this.getTableParameterSetParameterValues(),
                        "id",
                        new String[]{
                            "repository_id",
                            "run_results_parameter_optimizations_parameter_set_id",
                            "program_parameter_id", "iteration"},
                        new String[]{
                            this.updateRepositoryId() + "",
                            run_results_parameter_optimizations_parameter_set_id
                            + "", program_parameter_id + "",
                            iteration + ""});
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getRunTypeId(java.lang.String)
     */
    @Override
    protected int getRunTypeId(String name) throws SQLException {
        return this.select(this.getTableRunTypes(), "id", new String[]{"name"},
                new String[]{name});
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getRepositoryId(java.lang.String)
     */
    @Override
    protected int getRepositoryId(String absPath) throws SQLException {
        return this.select(this.getTableRepositories(), "id",
                new String[]{"base_path"}, new String[]{absPath});
    }

    @Override
    protected int getRunAnalysisId(final int run_id) throws SQLException {
        return this.select(this.getTableRunsAnalysis(), "id",
                new String[]{"run_id"}, new String[]{run_id + ""});
    }

    @Override
    protected int getRunExecutionId(final int run_id) throws SQLException {
        return this.select(this.getTableRunsExecution(), "id", new String[]{
            "repository_id", "run_id"},
                new String[]{this.updateRepositoryId() + "", run_id + ""});
    }

    @Override
    protected int getRunResultExecutionId(final int run_results_id)
            throws SQLException {
        return this.select(this.getTableRunResultsExecution(), "id",
                new String[]{"repository_id", "run_result_id"}, new String[]{
                    this.updateRepositoryId() + "", run_results_id + ""});
    }

    @Override
    protected int getRunResultFormatId(final String runResultFormatSimpleName)
            throws SQLException {
        return this.select(this.getTableRunResultFormats(), "id", new String[]{
            "repository_id", "name"},
                new String[]{this.updateRepositoryId() + "",
                    runResultFormatSimpleName});
    }

    @Override
    protected int getRunResultId(final String uniqueRunIdentifier)
            throws SQLException {
        return this.select(this.getTableRunResults(), "id", new String[]{
            "repository_id", "unique_run_identifier"},
                new String[]{this.updateRepositoryId() + "",
                    uniqueRunIdentifier});
    }

    @Override
    protected int getStatisticId(final String statisticsName)
            throws SQLException {
        return this.select(this.getTableStatistics(), "id", new String[]{
            "repository_id", "name"},
                new String[]{this.updateRepositoryId() + "", statisticsName});
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(program.Program)
     */
    @Override
    protected int register(Program object, final boolean updateOnly) {
        try {
            // in case of a update, we do nothing
            if (updateOnly) {
                return this.getObjectId(object);
            }
            int id = insert(
                    this.getTablePrograms(),
                    new String[]{"repository_id", "abs_path", "alias"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + object.getAbsolutePath(), object.getAlias()});

            return id;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(data.goldstandard.GoldStandardConfig)
     */
    @Override
    protected int register(GoldStandardConfig object, final boolean updateOnly) {
        try {
            int goldstandard_id = getObjectId(object.getGoldstandard());

            String[] columns;
            String[] values;
            if (object.getRepository() instanceof RunResultRepository) {
                columns = new String[]{"repository_id", "abs_path", "name",
                    "goldstandard_id", "goldstandard_config_id"};
                values = new String[]{
                    "" + this.updateRepositoryId(),
                    "" + object.getAbsolutePath(),
                    new File(object.getAbsolutePath()).getName().replace(
                    ".gsconfig", ""),
                    "" + goldstandard_id,
                    ""
                    + getObjectId(object
                    .getRepository()
                    .getParent()
                    .getStaticObjectWithName(
                    GoldStandardConfig.class,
                    object.toString()))};
            } else {
                columns = new String[]{"repository_id", "abs_path", "name",
                    "goldstandard_id"};
                values = new String[]{
                    "" + this.updateRepositoryId(),
                    "" + object.getAbsolutePath(),
                    new File(object.getAbsolutePath()).getName().replace(
                    ".gsconfig", ""), "" + goldstandard_id};
            }
            int rowId;
            if (updateOnly) {
                rowId = getObjectId(object);
                update(this.getTableGoldStandardConfigs(), columns, values,
                        rowId);
            } else {
                rowId = insert(this.getTableGoldStandardConfigs(), columns,
                        values);
            }
            return rowId;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(data.goldstandard.GoldStandard)
     */
    @Override
    protected int register(GoldStandard object, final boolean updateOnly) {
        try {
            String[] columns;
            String[] values;
            if (object.getRepository() instanceof RunResultRepository) {
                columns = new String[]{"repository_id", "abs_path",
                    "goldstandard_id"};
                values = new String[]{
                    "" + this.updateRepositoryId(),
                    "" + object.getAbsolutePath(),
                    ""
                    + getObjectId(object
                    .getRepository()
                    .getParent()
                    .getStaticObjectWithName(
                    GoldStandard.class,
                    object.toString()))};
            } else {
                columns = new String[]{"repository_id", "abs_path"};
                values = new String[]{"" + this.updateRepositoryId(),
                    "" + object.getAbsolutePath()};
            }
            int rowId;
            if (updateOnly) {
                rowId = getObjectId(object);
                update(this.getTableGoldStandards(), columns, values, rowId);
            } else {
                rowId = insert(this.getTableGoldStandards(), columns, values);
            }
            return rowId;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#registerDataSetFormat(java.lang.Class)
     */
    @Override
    protected boolean registerDataSetFormatClass(
            Class<? extends DataSetFormat> object) {
        try {
            DataSetFormat format = object.getConstructor(Repository.class,
                    boolean.class, long.class, File.class, int.class)
                    .newInstance(repository, false, System.currentTimeMillis(),
                            new File(object.getSimpleName()), 1);
            insert(this.getTableDataSetFormats(),
                    new String[]{"repository_id", "name", "alias"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + object.getSimpleName(), format.getAlias()});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#registerDataSetFormat(java.lang.Class)
     */
    @Override
    protected boolean registerDataSetTypeClass(
            Class<? extends DataSetType> object) {
        try {
            DataSetType type = object.getConstructor(Repository.class,
                    boolean.class, long.class, File.class).newInstance(
                            repository, false, System.currentTimeMillis(),
                            new File(object.getSimpleName()));
            insert(this.getTableDataSetTypes(),
                    new String[]{"repository_id", "name", "alias"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + object.getSimpleName(), type.getAlias()});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean isInfinity(final double d) {
        if (d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY) {
            return true;
        }
        return false;
    }

    protected String replaceInfinity(final double d) {
        if (isInfinity(d)) {
            return null;
        }
        return "" + (float) d;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.wiwie.wiutils.utils.SQLCommunicator#registerClusteringQualityMeasure(java.lang.Class)
     */
    @Override
    protected boolean registerClusteringQualityMeasureClass(
            Class<? extends ClusteringQualityMeasure> object) {
        try {
            ClusteringQualityMeasure measure = object.getConstructor(
                    Repository.class, boolean.class, long.class, File.class,
                    ClusteringQualityMeasureParameters.class).newInstance(
                            repository, false, System.currentTimeMillis(),
                            new File(object.getSimpleName()),
                            new ClusteringQualityMeasureParameters());
            int id = insert(
                    this.getTableClusteringQualityMeasures(),
                    new String[]{"repository_id", "name", "min_value",
                        "max_value", "requires_gold_standard", "alias"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + object.getSimpleName(),
                        replaceInfinity(measure.getMinimum()),
                        replaceInfinity(measure.getMaximum()),
                        "" + (measure.requiresGoldstandard() ? 1 : 0),
                        measure.getAlias()});
            this.objectIds.put(measure, id);
            return true;
        } catch (SQLException e) {

            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.wiwie.wiutils.utils.SQLCommunicator#registerParameterOptimizationMethod(java.lang.Class
	 * )
     */
    @Override
    protected boolean registerParameterOptimizationMethodClass(
            Class<? extends ParameterOptimizationMethod> object) {

        try {
            insert(this.getTableParameterOptimizationMethods(),
                    new String[]{"repository_id", "name"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + object.getSimpleName()});
            return true;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected boolean registerDataStatisticClass(
            Class<? extends DataStatistic> object) {

        try {
            int statisticId;
            try {
                DataStatistic statistic = object
                        .getConstructor(Repository.class, boolean.class,
                                long.class, File.class).newInstance(repository,
                                false, System.currentTimeMillis(),
                                new File(object.getSimpleName()));
                statisticId = insert(
                        this.getTableStatistics(),
                        new String[]{"repository_id", "name", "alias"},
                        new String[]{"" + this.updateRepositoryId(),
                            "" + object.getSimpleName(),
                            statistic.getAlias()});

                insert(this.getTableStatisticsData(), new String[]{
                    "repository_id", "statistic_id"}, new String[]{
                    "" + this.updateRepositoryId(), "" + statisticId});
            } catch (SQLException e) {
                e.printStackTrace();
                statisticId = getStatisticId(object.getSimpleName());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return true;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#registerRunStatistic(java.lang.Class)
     */
    @Override
    protected boolean registerRunStatisticClass(
            Class<? extends RunStatistic> object) {

        try {
            int statisticId;
            try {
                RunStatistic runStatistic = object
                        .getConstructor(Repository.class, boolean.class,
                                long.class, File.class).newInstance(repository,
                                false, System.currentTimeMillis(),
                                new File(object.getSimpleName()));
                statisticId = insert(
                        this.getTableStatistics(),
                        new String[]{"repository_id", "name", "alias"},
                        new String[]{"" + this.updateRepositoryId(),
                            "" + object.getSimpleName(),
                            runStatistic.getAlias()});

                insert(this.getTableStatisticsRun(), new String[]{
                    "repository_id", "statistic_id"}, new String[]{
                    "" + this.updateRepositoryId(), "" + statisticId});
            } catch (SQLException e) {
                e.printStackTrace();
                statisticId = getStatisticId(object.getSimpleName());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return true;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#registerRunDataStatistic(java.lang.Class)
     */
    @Override
    protected boolean registerRunDataStatisticClass(
            Class<? extends RunDataStatistic> object) {
        try {
            int statisticId;
            try {
                RunDataStatistic runDataStatistic = object
                        .getConstructor(Repository.class, boolean.class,
                                long.class, File.class).newInstance(repository,
                                false, System.currentTimeMillis(),
                                new File(object.getSimpleName()));
                statisticId = insert(
                        this.getTableStatistics(),
                        new String[]{"repository_id", "name", "alias"},
                        new String[]{"" + this.updateRepositoryId(),
                            "" + object.getSimpleName(),
                            runDataStatistic.getAlias()});
                insert(this.getTableStatisticsRunData(), new String[]{
                    "repository_id", "statistic_id"}, new String[]{
                    "" + this.updateRepositoryId(), "" + statisticId});
            } catch (SQLException e) {
                e.printStackTrace();
                statisticId = getStatisticId(object.getSimpleName());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return true;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(program.DoubleProgramParameter)
     */
    @Override
    protected int register(DoubleProgramParameter object) {

        ProgramConfig programConfig = object.getProgramConfig();
        int program_config_id = -1;
        try {

            program_config_id = getObjectId(programConfig);

            int parameterTypeId = getProgramParameterTypeId(object.getClass()
                    .getSimpleName());

            int paramIdd = insert(
                    this.getTableProgramParameter(),
                    new String[]{"repository_id", "program_config_id", "name",
                        "description", "min_value", "max_value", "def",
                        "program_parameter_type_id"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + program_config_id,
                        "" + replaceNull(object.getName(), ""),
                        "" + replaceNull(object.getDescription(), ""),
                        "" + replaceNull(object.getMinValue(), ""),
                        "" + replaceNull(object.getMaxValue(), ""),
                        "" + replaceNull(object.getDefault(), ""),
                        "" + parameterTypeId});
            return paramIdd;
        } catch (SQLException e) {
            System.out.println(program_config_id);
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(program.IntegerProgramParameter)
     */
    @Override
    protected int register(IntegerProgramParameter object) {

        ProgramConfig programConfig = object.getProgramConfig();

        try {
            int program_config_id = getObjectId(programConfig);

            int parameterTypeId = getProgramParameterTypeId(object.getClass()
                    .getSimpleName());

            int paramId = insert(
                    this.getTableProgramParameter(),
                    new String[]{"repository_id", "program_config_id", "name",
                        "description", "min_value", "max_value", "def",
                        "program_parameter_type_id"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + program_config_id,
                        "" + replaceNull(object.getName(), ""),
                        "" + replaceNull(object.getDescription(), ""),
                        "" + replaceNull(object.getMinValue(), ""),
                        "" + replaceNull(object.getMaxValue(), ""),
                        "" + replaceNull(object.getDefault(), ""),
                        "" + parameterTypeId});
            return paramId;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(program.StringProgramParameter)
     */
    @Override
    protected int register(StringProgramParameter object) {

        ProgramConfig programConfig = object.getProgramConfig();

        try {
            int program_config_id = getObjectId(programConfig);

            int parameterTypeId = getProgramParameterTypeId(object.getClass()
                    .getSimpleName());

            int paramId = insert(
                    this.getTableProgramParameter(),
                    new String[]{"repository_id", "program_config_id", "name",
                        "description", "min_value", "max_value", "def",
                        "program_parameter_type_id"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + program_config_id,
                        "" + replaceNull(object.getName(), ""),
                        "" + replaceNull(object.getDescription(), ""),
                        "" + replaceNull(object.getMinValue(), ""),
                        "" + replaceNull(object.getMaxValue(), ""),
                        "" + replaceNull(object.getDefault(), ""),
                        "" + parameterTypeId});
            return paramId;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(data.dataset.DataSet)
     */
    @Override
    protected int register(DataSet object, final boolean updateOnly) {

        try {
            int dataset_format_id = getDataSetFormatId(object
                    .getDataSetFormat().getClass().getSimpleName());

            String[] columns;
            String[] values;
            if (object.getRepository() instanceof RunResultRepository) {
                columns = new String[]{"repository_id", "abs_path",
                    "dataset_format_id", "dataset_id", "checksum",
                    "dataset_type_id", "alias"};
                values = new String[]{
                    "" + this.updateRepositoryId(),
                    "" + object.getAbsolutePath(),
                    "" + dataset_format_id,
                    ""
                    + getObjectId(object
                    .getRepository()
                    .getParent()
                    .getStaticObjectWithName(DataSet.class,
                    object.getFullName())),
                    object.getChecksum() + "",
                    getDataSetTypeId(object.getDataSetType().getClass()
                    .getSimpleName())
                    + "", object.getAlias()};
            } else {
                columns = new String[]{"repository_id", "abs_path",
                    "dataset_format_id", "checksum", "dataset_type_id",
                    "alias"};
                values = new String[]{
                    "" + this.updateRepositoryId(),
                    "" + object.getAbsolutePath(),
                    "" + dataset_format_id,
                    object.getChecksum() + "",
                    getDataSetTypeId(object.getDataSetType().getClass()
                    .getSimpleName())
                    + "", object.getAlias()};
            }
            int rowId;
            if (updateOnly) {
                rowId = getObjectId(object);
                update(this.getTableDatasets(), columns, values, rowId);
            } else {
                rowId = insert(this.getTableDatasets(), columns, values);
            }
            return rowId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.clusteval.framework.repository.SQLCommunicator#register(de.clusteval
	 * .cluster.Clustering)
     */
    @Override
    protected int register(Clustering object) {

        try {
            String[] columns;
            String[] values;

            columns = new String[]{"repository_id", "abs_path"};
            values = new String[]{"" + this.updateRepositoryId(),
                "" + object.getAbsolutePath()};
            int rowId = insert(this.getTableClusterings(), columns, values);
            return rowId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.clusteval.framework.repository.SQLCommunicator#unregister(de.clusteval
	 * .cluster.Clustering)
     */
    @Override
    protected int unregister(Clustering object) {
        try {
            if (this.delete(this.getTableClusterings(),
                    object.getAbsolutePath(), "abs_path")) {
                return this.getObjectId(object);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(data.DataConfig)
     */
    @Override
    protected int register(DataConfig object, final boolean updateOnly) {

        try {
            int dataset_config_id = getObjectId(object.getDatasetConfig());

            String[] columns;
            String[] values;
            if (object.getRepository() instanceof RunResultRepository) {
                if (object.hasGoldStandardConfig()) {
                    int goldstandard_config_id = getObjectId(object
                            .getGoldstandardConfig());

                    columns = new String[]{"repository_id", "abs_path", "name",
                        "dataset_config_id", "goldstandard_config_id",
                        "data_config_id"};
                    values = new String[]{
                        "" + this.updateRepositoryId(),
                        "" + object.getAbsolutePath(),
                        new File(object.getAbsolutePath()).getName()
                        .replace(".dataconfig", ""),
                        "" + dataset_config_id,
                        "" + goldstandard_config_id,
                        ""
                        + getObjectId(object
                        .getRepository()
                        .getParent()
                        .getStaticObjectWithName(
                        DataConfig.class,
                        object.getName()))};

                } else {
                    columns = new String[]{"repository_id", "abs_path", "name",
                        "dataset_config_id", "data_config_id"};
                    values = new String[]{
                        "" + this.updateRepositoryId(),
                        "" + object.getAbsolutePath(),
                        new File(object.getAbsolutePath()).getName()
                        .replace(".dataconfig", ""),
                        "" + dataset_config_id,
                        ""
                        + getObjectId(object
                        .getRepository()
                        .getParent()
                        .getStaticObjectWithName(
                        DataConfig.class,
                        object.getName()))};
                }
            } else if (object.hasGoldStandardConfig()) {
                int goldstandard_config_id = getObjectId(object
                        .getGoldstandardConfig());

                columns = new String[]{"repository_id", "abs_path", "name",
                    "dataset_config_id", "goldstandard_config_id"};
                values = new String[]{
                    "" + this.updateRepositoryId(),
                    "" + object.getAbsolutePath(),
                    new File(object.getAbsolutePath()).getName()
                    .replace(".dataconfig", ""),
                    "" + dataset_config_id, "" + goldstandard_config_id};

            } else {
                columns = new String[]{"repository_id", "abs_path", "name",
                    "dataset_config_id"};
                values = new String[]{
                    "" + this.updateRepositoryId(),
                    "" + object.getAbsolutePath(),
                    new File(object.getAbsolutePath()).getName()
                    .replace(".dataconfig", ""),
                    "" + dataset_config_id};
            }
            int rowId;
            if (updateOnly) {
                rowId = getObjectId(object);
                update(this.getTableDataConfigs(), columns, values, rowId);
            } else {
                rowId = insert(this.getTableDataConfigs(), columns, values);
            }
            return rowId;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(data.dataset.DataSetConfig)
     */
    @Override
    protected int register(DataSetConfig object, final boolean updateOnly) {

        try {
            int dataset_id = getObjectId(object.getDataSet());

            String[] columns;
            String[] values;
            if (object.getRepository() instanceof RunResultRepository) {
                columns = new String[]{"repository_id", "abs_path", "name",
                    "dataset_id", "dataset_config_id"};
                values = new String[]{
                    "" + this.updateRepositoryId(),
                    "" + object.getAbsolutePath(),
                    new File(object.getAbsolutePath()).getName().replace(
                    ".dsconfig", ""),
                    "" + dataset_id,
                    ""
                    + getObjectId(object
                    .getRepository()
                    .getParent()
                    .getStaticObjectWithName(
                    DataSetConfig.class,
                    object.toString()))};
            } else {
                columns = new String[]{"repository_id", "abs_path", "name",
                    "dataset_id"};
                values = new String[]{
                    "" + this.updateRepositoryId(),
                    "" + object.getAbsolutePath(),
                    new File(object.getAbsolutePath()).getName().replace(
                    ".dsconfig", ""), "" + dataset_id};
            }
            int rowId;
            if (updateOnly) {
                rowId = getObjectId(object);
                update(this.getTableDataSetConfigs(), columns, values, rowId);
            } else {
                rowId = insert(this.getTableDataSetConfigs(), columns, values);
            }
            return rowId;
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#unregisterRunResultFormat(java.lang.Class)
     */
    @Override
    protected boolean unregisterRunResultFormat(
            Class<? extends RunResultFormat> object) {
        try {
            return this.delete(
                    this.getTableRunResultFormats(),
                    new String[]{"repository_id", "name"},
                    new String[]{this.updateRepositoryId() + "",
                        object.getSimpleName()});
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#unregister(program.ProgramConfig)
     */
    @Override
    protected int unregister(ProgramConfig object) {
        try {
            int program_id = getObjectId(object.getProgram());

            for (ProgramParameter<?> param : object.getOptimizableParams()) {
                int program_config_id = getObjectId(object);
                int program_parameter_id = getObjectId(param);

                this.delete(this.getTableOptimizableProgramParameters(),
                        new String[]{program_config_id + "",
                            program_parameter_id + ""}, new String[]{
                            "program_config_id", "program_parameter_id"});
            }

            this.delete(this.getTableProgramConfigs(), new String[]{
                program_id + "", object.getName()}, new String[]{
                "program_id", "name"});
            return this.getObjectId(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#unregister(program.Program)
     */
    @Override
    protected int unregister(Program object) {
        try {
            this.delete(this.getTablePrograms(),
                    new String[]{object.getAbsolutePath()},
                    new String[]{"abs_path"});
            return this.getObjectId(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.wiwie.wiutils.utils.SQLCommunicator#unregister(data.goldstandard.GoldStandardConfig)
     */
    @Override
    protected int unregister(GoldStandardConfig object) {
        try {
            this.delete(this.getTableGoldStandardConfigs(),
                    new String[]{object.getAbsolutePath()},
                    new String[]{"abs_path"});
            return this.getObjectId(object);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#unregister(data.goldstandard.GoldStandard)
     */
    @Override
    protected int unregister(GoldStandard object) {
        try {
            this.delete(this.getTableGoldStandards(),
                    new String[]{object.getAbsolutePath()},
                    new String[]{"abs_path"});
            return this.getObjectId(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#unregisterDataSetFormat(java.lang.Class)
     */
    @Override
    protected boolean unregisterDataSetFormatClass(
            Class<? extends DataSetFormat> object) {
        try {
            this.delete(
                    this.getTableDataSetFormats(),
                    new String[]{this.updateRepositoryId() + "",
                        object.getSimpleName()}, new String[]{
                        "repository_id", "name"});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#unregister(data.dataset.DataSet)
     */
    @Override
    protected int unregister(DataSet object) {
        try {
            this.delete(this.getTableDatasets(),
                    new String[]{object.getAbsolutePath()},
                    new String[]{"abs_path"});
            return this.getObjectId(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#unregister(data.DataConfig)
     */
    @Override
    protected int unregister(DataConfig object) {
        try {
            this.delete(this.getTableDataConfigs(),
                    new String[]{object.getAbsolutePath()},
                    new String[]{"abs_path"});
            return this.getObjectId(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#unregister(data.dataset.DataSetConfig)
     */
    @Override
    protected int unregister(DataSetConfig object) {
        try {
            this.delete(this.getTableDataSetConfigs(),
                    new String[]{object.getAbsolutePath()},
                    new String[]{"abs_path"});
            return this.getObjectId(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#updateStatusOfRun(java.lang.String,
	 * java.lang.String)
     */
    @Override
    public boolean updateStatusOfRun(final Run run, String runStatus) {
        try {
            int runId = this.select(
                    this.getTableRuns(),
                    "id",
                    new String[]{"repository_id", "abs_path"},
                    new String[]{this.updateRepositoryId() + "",
                        run.getAbsolutePath()});
            this.update(this.getTableRuns(), new String[]{"status"},
                    new String[]{runStatus}, runId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(run.result.RunResult)
     */
    @Override
    public int register(RunResult object) {
        String type = null;
        String resultsPath = null;
        if (object instanceof ClusteringRunResult) {
            type = "Clustering";
            resultsPath = object.absPath.getParentFile().getParentFile()
                    .getAbsolutePath();
        } else if (object instanceof ParameterOptimizationResult) {
            type = "Parameter Optimization";
            resultsPath = object.absPath.getParentFile().getParentFile()
                    .getAbsolutePath();
        } else if (object instanceof DataAnalysisRunResult) {
            type = "Data Analysis";
            resultsPath = object.absPath.getParentFile().getAbsolutePath();
        } else if (object instanceof RunDataAnalysisRunResult) {
            type = "Run-Data Analysis";
            resultsPath = object.absPath.getParentFile().getAbsolutePath();
        } else if (object instanceof RunAnalysisRunResult) {
            type = "Run Analysis";
            resultsPath = object.absPath.getParentFile().getAbsolutePath();
        }

        String identifier = object.getIdentifier();
        String dateString = identifier.substring(0, 19);
        try {
            dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new SimpleDateFormat("MM_dd_yyyy-HH_mm_ss")
                            .parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            int run_type_id = getRunTypeId(type);
            int run_id = getRunId(object.getRun());

            // parameter optimization runs may already been inserted before,
            // thus the corresponding run result also.
            // because of this we use tryInsert instead of insert
            tryInsert(this.getTableRunResults(), new String[]{"repository_id",
                "unique_run_identifier", "run_type_id", "run_id", "date",
                "abs_path"}, new String[]{"" + this.updateRepositoryId(),
                "" + object.getIdentifier(), "" + run_type_id, "" + run_id,
                "" + dateString, resultsPath});
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return super.register(object);
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#register(run.result.ExecutionRunResult
	 * )
     */
    @Override
    public boolean register(ExecutionRunResult object) {
        // this.register((RunResult) object);

        try {
            int run_results_id = getRunResultId(object.getIdentifier());

            // parameter optimization runs may already been inserted before,
            // thus the corresponding execution run result also.
            // because of this we use tryInsert instead of insert
            tryInsert(this.getTableRunResultsExecution(), new String[]{
                "repository_id", "run_result_id"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + run_results_id});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#register(run.result.ClusteringRunResult)
     */
    @Override
    public int register(ClusteringRunResult object) {
        this.register((ExecutionRunResult) object);

        try {
            int run_results_id = getRunResultId(object.getIdentifier());
            int run_results_execution_id = getRunResultExecutionId(run_results_id);

            int clusteringId;
            try {
                clusteringId = insert(
                        this.getTableRunResultsClustering(),
                        new String[]{"repository_id",
                            "run_results_execution_id", "abs_path"},
                        new String[]{"" + this.updateRepositoryId(),
                            "" + run_results_execution_id,
                            "" + object.getAbsolutePath()});
            } catch (SQLException e) {
                clusteringId = select(
                        this.getTableRunResultsClustering(),
                        "id",
                        new String[]{"repository_id",
                            "run_results_execution_id", "abs_path"},
                        new String[]{"" + this.updateRepositoryId(),
                            "" + run_results_execution_id,
                            "" + object.getAbsolutePath()});
                e.printStackTrace();
            }

            int dataConfigId = getObjectId(object.getDataConfig());
            int programConfigId = getObjectId(object.getProgramConfig());

            final Pair<ParameterSet, Clustering> pair = object.getClustering();

            final ClusteringQualitySet qualities = pair.getSecond()
                    .getQualities();

            final StringBuilder paramString = new StringBuilder();
            for (String param : pair.getFirst().keySet()) {
                paramString.append(param);
                paramString.append("=");
                paramString.append(pair.getFirst().get(param));
                paramString.append(",");
            }
            for (ClusteringQualityMeasure measure : qualities.keySet()) {

                paramString.deleteCharAt(paramString.length() - 1);
                insert(this.getTableRunResultsClusteringsQuality(),
                        new String[]{"repository_id",
                            "run_results_clustering_id", "data_config_id",
                            "program_config_id",
                            "clustering_quality_measure_id", "paramString",
                            "quality"},
                        new String[]{"" + this.updateRepositoryId(),
                            "" + clusteringId, "" + dataConfigId,
                            "" + programConfigId,
                            "" + getObjectId(measure),
                            paramString.toString(),
                            "" + qualities.get(measure).getValue()});
            }

            return clusteringId;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.wiwie.wiutils.utils.SQLCommunicator#register(run.result.ParameterOptimizationResult)
     */
    @Override
    public int register(ParameterOptimizationResult object) {
        this.register((ExecutionRunResult) object);

        try {
            int repository_id;
            try {
                repository_id = getRepositoryId(Repository
                        .getRepositoryForPath(object.getAbsolutePath()).basePath);
            } catch (NoRepositoryFoundException e1) {
                e1.printStackTrace();
                repository_id = this.updateRepositoryId();
            }
            int run_results_id = getRunResultId(object.getIdentifier());
            int run_results_execution_id = getRunResultExecutionId(run_results_id);

            int runResultParamOptId;
            try {
                runResultParamOptId = insert(
                        this.getTableRunResultsParameterOptimization(),
                        new String[]{"repository_id",
                            "run_results_execution_id", "abs_path",
                            "data_config_id", "program_config_id"},
                        new String[]{"" + repository_id,
                            "" + run_results_execution_id,
                            "" + object.getAbsolutePath(),
                            getObjectId(object.getDataConfig()) + "",
                            getObjectId(object.getProgramConfig()) + ""});
            } catch (SQLException e) {
                e.printStackTrace();
                runResultParamOptId = getObjectId(object);
            }

            /*
			 * Insert parameterset
             */
            int run_results_parameter_optimizations_parameter_set_id = insert(
                    this.getTableParameterSets(), new String[]{"repository_id",
                        "run_results_parameter_optimization_id"},
                    new String[]{repository_id + "", "" + runResultParamOptId});

            int[] parameterIds = new int[object.getMethod()
                    .getOptimizationParameter().size()];
            /*
			 * Insert parameters
             */
            int paramNo = 0;
            for (ProgramParameter<?> optParam : object.getMethod()
                    .getOptimizationParameter()) {
                int program_parameter_id = getObjectId(optParam);
                parameterIds[paramNo] = insert(
                        this.getTableParameterSetParameters(),
                        new String[]{
                            "repository_id",
                            "run_results_parameter_optimization_id",
                            "run_results_parameter_optimizations_parameter_set_id",
                            "program_parameter_id"},
                        new String[]{
                            "" + repository_id,
                            "" + runResultParamOptId,
                            ""
                            + run_results_parameter_optimizations_parameter_set_id,
                            "" + program_parameter_id});
                paramNo++;
            }

            /*
			 * For every iteration in the result insert the parameter values
             */
            List<ParameterSet> paramSets = object.getParameterSets();
            // List<Pair<ParameterSet, Clustering>> clusterings = object
            // .getOptimizationClusterings();
            List<Long> iterationNumbers = object.getIterationNumbers();

            int[] iterationIds = new int[paramSets.size()];

            for (int i = 0; i < paramSets.size(); i++) {
                StringBuilder paramSetAsString = new StringBuilder();
                for (Map.Entry<String, String> paramValue : paramSets.get(i)
                        .entrySet()) {
                    paramSetAsString.append(paramValue.getKey());
                    paramSetAsString.append("=");
                    paramSetAsString.append(paramValue.getValue());
                    paramSetAsString.append(",");
                }
                paramSetAsString.deleteCharAt(paramSetAsString.length() - 1);

                int clustering_id;
                if (object.getClustering(paramSets.get(i)) == null) {
                    clustering_id = -1;

                    iterationIds[i] = insert(
                            this.getTableParameterSetIterations(),
                            new String[]{
                                "repository_id",
                                "run_results_parameter_optimizations_parameter_set_id",
                                "iteration", "param_set_as_string"},
                            new String[]{
                                "" + repository_id,
                                run_results_parameter_optimizations_parameter_set_id
                                + "", iterationNumbers.get(i) + "",
                                paramSetAsString.toString()});
                } else {
                    clustering_id = getClusteringId(object.getClustering(
                            paramSets.get(i)).getAbsolutePath());
                    /*
					 * insert iteration
                     */
                    iterationIds[i] = insert(
                            this.getTableParameterSetIterations(),
                            new String[]{
                                "repository_id",
                                "run_results_parameter_optimizations_parameter_set_id",
                                "iteration", "param_set_as_string",
                                "clustering_id"}, new String[]{
                                "" + repository_id,
                                run_results_parameter_optimizations_parameter_set_id
                                + "", iterationNumbers.get(i) + "",
                                paramSetAsString.toString(),
                                "" + clustering_id});
                }
            }

            String[] columns = new String[]{
                "repository_id",
                "run_results_parameter_optimization_id",
                "run_results_parameter_optimizations_parameter_set_parameter_id",
                "run_results_parameter_optimizations_parameter_set_iteration_id",
                "value"};
            List<String[]> values = new ArrayList<String[]>();

            for (int i = 0; i < iterationIds.length; i++) {
                int iteration_id = iterationIds[i];
                Pair<ParameterSet, ClusteringQualitySet> pair = Pair.getPair(
                        paramSets.get(i), object.get(paramSets.get(i)));

                paramNo = 0;
                for (ProgramParameter<?> optParam : object.getMethod()
                        .getOptimizationParameter()) {
                    int run_results_parameter_optimizations_parameter_sets_parameter_id = parameterIds[paramNo];
                    values.add(new String[]{
                        "" + repository_id,
                        "" + runResultParamOptId,
                        ""
                        + run_results_parameter_optimizations_parameter_sets_parameter_id,
                        "" + iteration_id,
                        "" + pair.getFirst().get(optParam.getName())});
                    paramNo++;
                }
            }
            if (!values.isEmpty()) {
                insert(this.getTableParameterSetParameterValues(), columns,
                        values);
            }

            columns = new String[]{
                "repository_id",
                "run_results_parameter_optimization_id",
                "run_results_parameter_optimizations_parameter_set_iteration_id",
                "clustering_quality_measure_id", "quality"};
            values = new ArrayList<String[]>();

            for (int i = 0; i < iterationIds.length; i++) {
                int iteration_id = iterationIds[i];
                Pair<ParameterSet, ClusteringQualitySet> pair = Pair.getPair(
                        paramSets.get(i), object.get(paramSets.get(i)));

                if (pair.getSecond() == null) {
                    continue;
                }

                for (ClusteringQualityMeasure measure : pair.getSecond()
                        .keySet()) {
                    int clustering_quality_measure_id = getObjectId(measure);
                    try {
                        if (pair.getSecond().get(measure).isTerminated()
                                && !Double.isNaN(pair.getSecond().get(measure)
                                        .getValue())
                                && !isInfinity(pair.getSecond().get(measure)
                                        .getValue())) {
                            values.add(new String[]{
                                "" + repository_id,
                                "" + runResultParamOptId,
                                "" + iteration_id,
                                "" + clustering_quality_measure_id,
                                replaceInfinity(pair.getSecond()
                                .get(measure).getValue())});
                        }
                    } catch (NullPointerException e) {
                        System.out.println(iterationNumbers.get(i));
                    }
                }
            }

            if (!values.isEmpty()) {
                insert(this.getTableParameterOptimizationQualities(), columns,
                        values);
            }

            // conn.commit();
            return runResultParamOptId;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#register(run.result.AnalysisRunResult
	 * )
     */
    @Override
    public boolean register(AnalysisRunResult object) {
        // this.register((RunResult) object);

        try {
            int run_results_id = getRunResultId(object.getIdentifier());

            insert(this.getTableRunResultsAnalysis(), new String[]{
                "repository_id", "run_result_id"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + run_results_id});

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.wiwie.wiutils.utils.SQLCommunicator#register(run.result.ParameterOptimizationResult)
     */
    @Override
    public int register(DataAnalysisRunResult object) {
        this.register((AnalysisRunResult) object);

        try {
            int run_results_id = getRunResultId(object.getIdentifier());
            int run_results_analysis_id = getRunResultAnalysisId(run_results_id);

            int run_results_analysis_data = insert(
                    this.getTableRunResultsDataAnalysis(), new String[]{
                        "repository_id", "run_results_analysis_id"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + run_results_analysis_id});

            return run_results_analysis_data;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableRepositoryTypes()
     */
    @Override
    protected String getTableRepositoryTypes() {
        return "repository_types";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#getTableRunResultsAnalysis()
     */
    @Override
    protected String getTableRunResultsAnalysis() {
        return "run_results_analyses";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#getRunResultAnalysisId(int)
     */
    @Override
    protected int getRunResultAnalysisId(int run_results_id)
            throws SQLException {
        return this.select(this.getTableRunResultsAnalysis(), "id",
                new String[]{"repository_id", "run_result_id"}, new String[]{
                    this.updateRepositoryId() + "", run_results_id + ""});
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#getRunResultRunAnalysisId(int)
     */
    @Override
    protected int getRunResultRunAnalysisId(int run_results_analysis_id)
            throws SQLException {
        return this.select(this.getTableRunResultsRunAnalysis(), "id",
                new String[]{"repository_id", "run_results_analysis_id"},
                new String[]{this.updateRepositoryId() + "",
                    run_results_analysis_id + ""});
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#register(run.result.RunAnalysisRunResult
	 * )
     */
    @Override
    public int register(RunAnalysisRunResult object) {
        this.register((AnalysisRunResult) object);

        try {
            int run_results_id = getRunResultId(object.getIdentifier());
            int run_results_analysis_id = getRunResultAnalysisId(run_results_id);
            int run_results_analysis_run_id = insert(
                    this.getTableRunResultsRunAnalysis(), new String[]{
                        "repository_id", "run_results_analysis_id"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + run_results_analysis_id});

            return run_results_analysis_run_id;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#register(run.result.
	 * RunDataAnalysisRunResult)
     */
    @Override
    public int register(RunDataAnalysisRunResult object) {
        this.register((AnalysisRunResult) object);

        try {
            int run_results_id = getRunResultId(object.getIdentifier());
            int run_results_analysis_id = getRunResultAnalysisId(run_results_id);
            int runResultRunAnalysisId = getRunResultRunAnalysisId(run_results_analysis_id);

            int run_results_anlysis_run_data_id = insert(
                    this.getTableRunResultsRunDataAnalysis(), new String[]{
                        "repository_id", "run_results_analysis_id"},
                    new String[]{"" + this.updateRepositoryId(),
                        "" + runResultRunAnalysisId});

            conn.commit();
            return run_results_anlysis_run_data_id;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#
	 * getTableRunsAnalysisRunDataDataIdentifiers()
     */
    @Override
    protected String getTableRunsAnalysisRunDataDataIdentifiers() {
        return "run_run_data_analysis_data_identifiers";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#
	 * getTableRunsAnalysisRunDataRunIdentifiers()
     */
    @Override
    protected String getTableRunsAnalysisRunDataRunIdentifiers() {
        return "run_run_data_analysis_run_identifiers";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#getTableProgramParameterType()
     */
    @Override
    protected String getTableProgramParameterType() {
        return "program_parameter_types";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#getProgramParameterTypeId(java.lang
	 * .String)
     */
    @Override
    protected int getProgramParameterTypeId(String typeName)
            throws SQLException {
        return this.select(this.getTableProgramParameterType(), "id",
                new String[]{"name"}, new String[]{typeName});

    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#unregister(program.
	 * ProgramParameter)
     */
    @Override
    protected int unregister(ProgramParameter<?> programParameter) {
        try {
            this.delete(this.getTableProgramParameter(),
                    this.getObjectId(programParameter) + "");
            return this.getObjectId(programParameter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    protected int getDataSetFormatId(final String dataSetFormatClassSimpleName)
            throws SQLException {
        return this.select(this.getTableDataSetFormats(), "id", new String[]{
            "repository_id", "name"},
                new String[]{this.updateRepositoryId() + "",
                    dataSetFormatClassSimpleName});
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.wiwie.wiutils.utils.SQLCommunicator#getTableDataSetFormats()
     */
    @Override
    protected String getTableDataSetTypes() {
        return "dataset_types";
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#unregister(run.Run)
     */
    @Override
    protected int unregister(Run object) {
        try {
            this.delete(this.getTableRuns(),
                    new String[]{object.getAbsolutePath()},
                    new String[]{"abs_path"});
            return this.getObjectId(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.clusteval.framework.repository.SQLCommunicator#unregister(de.clusteval
	 * .run.result.ParameterOptimizationResult)
     */
    @Override
    protected int unregister(ParameterOptimizationResult object) {
        try {
            this.delete(
                    this.getTableRunResultsParameterOptimization(),
                    new String[]{
                        this.getRepositoryId(this.repository.getBasePath())
                        + "", object.getAbsolutePath()},
                    new String[]{"repository_id", "abs_path"});

            try {
                this.delete(
                        this.getTableRepositories(),
                        new String[]{this.getRepositoryId(object
                                    .getRepository().getBasePath()) + ""},
                        new String[]{"id"});
            } catch (SQLException e) {
                // might have been deleted by another parameter optimization
                // run result contained in that folder
                e.printStackTrace();
            }
            this.unregister((RunResult) object);
            return this.getObjectId(object);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#unregister(run.result.RunResult)
     */
    @Override
    protected int unregister(RunResult object) {

        try {
            this.delete(
                    this.getTableRunResults(),
                    new String[]{
                        this.getRepositoryId(this.repository.getBasePath())
                        + "", object.getIdentifier()},
                    new String[]{"repository_id", "unique_run_identifier"});
            return this.getObjectId(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see framework.repository.SQLCommunicator#
	 * unregisterParameterOptimizationMethodClass(java.lang.Class)
     */
    @Override
    protected boolean unregisterParameterOptimizationMethodClass(
            Class<? extends ParameterOptimizationMethod> object) {
        try {
            this.delete(
                    this.getTableParameterOptimizationMethods(),
                    new String[]{this.updateRepositoryId() + "",
                        object.getSimpleName()}, new String[]{
                        "repository_id", "name"});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#unregisterClusteringQualityMeasureClass
	 * (java.lang.Class)
     */
    @Override
    protected boolean unregisterClusteringQualityMeasureClass(
            Class<? extends ClusteringQualityMeasure> object) {
        try {
            this.delete(this.getTableClusteringQualityMeasures(), new String[]{
                this.updateRepositoryId() + "", object.getSimpleName()},
                    new String[]{"repository_id", "name"});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#unregisterDataStatisticClass(java
	 * .lang.Class)
     */
    @Override
    protected boolean unregisterDataStatisticClass(
            Class<? extends DataStatistic> object) {
        try {
            int statistic_id = getStatisticId(object.getSimpleName());

            this.delete(this.getTableStatisticsData(),
                    new String[]{this.updateRepositoryId() + "",
                        statistic_id + ""}, new String[]{"repository_id",
                        "statistic_id"});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#unregisterRunStatisticClass(java
	 * .lang.Class)
     */
    @Override
    protected boolean unregisterRunStatisticClass(
            Class<? extends RunStatistic> object) {
        try {
            int statistic_id = getStatisticId(object.getSimpleName());

            this.delete(this.getTableStatisticsRun(),
                    new String[]{this.updateRepositoryId() + "",
                        statistic_id + ""}, new String[]{"repository_id",
                        "statistic_id"});

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#unregisterRunDataStatisticClass(
	 * java.lang.Class)
     */
    @Override
    protected boolean unregisterRunDataStatisticClass(
            Class<? extends RunDataStatistic> object) {
        try {
            int statistic_id = getStatisticId(object.getSimpleName());

            this.delete(this.getTableStatisticsRunData(),
                    new String[]{this.updateRepositoryId() + "",
                        statistic_id + ""}, new String[]{"repository_id",
                        "statistic_id"});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * framework.repository.SQLCommunicator#unregisterDataSetTypeClass(java.
	 * lang.Class)
     */
    @Override
    protected boolean unregisterDataSetTypeClass(
            Class<? extends DataSetType> object) {
        try {
            this.delete(
                    this.getTableDataSetTypes(),
                    new String[]{this.updateRepositoryId() + "",
                        object.getSimpleName()}, new String[]{
                        "repository_id", "name"});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see de.clusteval.framework.repository.SQLCommunicator#
	 * getTableRunResultsClusteringsQuality()
     */
    @Override
    protected String getTableRunResultsClusteringsQuality() {
        return "run_results_clustering_qualities";
    }

    @SuppressWarnings("unused")
    @Override
    protected boolean registerContextClass(Class<? extends Context> object) {
        // TODO think about how to integrate contexts into the website &
        // database
        return false;
    }

    @SuppressWarnings("unused")
    @Override
    protected boolean unregisterContextClass(Class<? extends Context> object) {
        // TODO think about how to integrate contexts into the website &
        // database
        return false;
    }

    @Override
    public boolean refreshMaterializedViews() {
        //TODO implement
        return true;
    }
}
