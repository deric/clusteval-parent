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
package de.clusteval.context;

import de.clusteval.api.IContext;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.exceptions.UnknownRunResultFormatException;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.data.dataset.format.DataSetFormat;
import de.clusteval.run.result.format.RunResultFormat;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.openide.util.Exceptions;

/**
 * This is the default context of the framework, concerning clustering tasks.
 *
 * @author Christian Wiwie
 *
 */
public class ClusteringContext extends Context implements IContext {

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public ClusteringContext(IRepository repository, boolean register,
            long changeDate, File absPath) throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * @param other
     * @throws RegisterException
     */
    public ClusteringContext(final ClusteringContext other)
            throws RegisterException {
        super(other);
    }

    @Override
    public String getName() {
        return "Clustering context";
    }

    @Override
    public Set<String> getRequiredJavaClassFullNames() {
        return new HashSet<String>(Arrays.asList(new String[]{
            "de.clusteval.data.dataset.format.SimMatrixDataSetFormat",
            "de.clusteval.run.result.format.TabSeparatedRunResultFormat"}));
    }

    @Override
    public IDataSetFormat getStandardInputFormat() {
        try {
            // take the newest version
            return DataSetFormat.parseFromString(repository,
                    "SimMatrixDataSetFormat");
        } catch (UnknownDataSetFormatException e) {
            e.printStackTrace();
            // should not occur, because we checked this in the repository using
            // #getRequiredJavaClassFullNames()
        } catch (UnknownProviderException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public IRunResultFormat getStandardOutputFormat() {
        try {
            // take the newest version
            return RunResultFormat.parseFromString(repository,
                    "TabSeparatedRunResultFormat");
        } catch (UnknownRunResultFormatException e) {
            e.printStackTrace();
            // should not occur, because we checked this in the repository using
            // #getRequiredJavaClassFullNames()
        }
        return null;
    }

}
