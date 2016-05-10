/** *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 ***************************************************************************** */
package de.clusteval.api.data;

import de.clusteval.api.Precision;
import de.clusteval.api.exceptions.RepositoryObjectDumpException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RepositoryObject;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;

/**
 * Subclasses of this abstract class correspond to objects, that provide in some
 * way data sets to the framework. For example by generating new ones
 * (generators) or by deriving them from existing ones (e.g. randomizer).
 *
 * This class encapsulate the logic that is common to all these data set
 * providers, e.g. the logic to write configuration files for the newly
 * generated data set.
 *
 * @author Christian Wiwie
 *
 */
public abstract class AbstractDataSetProvider extends RepositoryObject {

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public AbstractDataSetProvider(IRepository repository, boolean register, long changeDate, File absPath)
            throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * The copy constructor of dataset generators.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public AbstractDataSetProvider(AbstractDataSetProvider other) throws RegisterException {
        super(other);
    }

    public AbstractDataSetProvider() {
        super();
    }

    protected IDataConfig writeConfigFiles(final IDataSet newDataSet,
            final IGoldStandard newGoldStandard, final String configFileName)
            throws RepositoryObjectDumpException, RegisterException, UnknownProviderException {
        // write dataset config file
        File dsConfigFile = new File(
                FileUtils.buildPath(repository.getBasePath(IDataSetConfig.class), configFileName + ".dsconfig"));
        IDataSetConfig dsConfig = new DataSetConfig(this.repository, System.currentTimeMillis(), dsConfigFile,
                newDataSet,
                new InputToStd(
                        DistanceMeasureFactory.parseFromString(repository, "EuclidianDistanceMeasure"),
                        Precision.DOUBLE, new ArrayList<>(), new ArrayList<>()),
                new StdToInput());

        dsConfig.dumpToFile();

        GoldStandardConfig gsConfig = null;

        if (newGoldStandard != null) {

            File gsConfigFile = new File(FileUtils.buildPath(repository.getBasePath(IGoldStandardConfig.class),
                    configFileName + ".gsconfig"));

            // write goldstandard config file
            gsConfig = new GoldStandardConfig(this.repository, System.currentTimeMillis(), gsConfigFile,
                    newGoldStandard);

            gsConfig.dumpToFile();
        }

        // write data config file
        File dataConfigFile = new File(
                FileUtils.buildPath(repository.getBasePath(IDataConfig.class), configFileName + ".dataconfig"));
        DataConfig dataConfig = new DataConfig(this.repository, System.currentTimeMillis(), dataConfigFile, dsConfig,
                gsConfig);

        dataConfig.dumpToFile();

        return dataConfig;
    }
}
