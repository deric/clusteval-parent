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

import de.clusteval.api.AbsContext;
import de.clusteval.api.IContext;
import de.clusteval.api.data.DataSetFormatFactory;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.factory.UnknownProviderException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.api.run.RunResultFormatFactory;
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
public class ClusteringContext extends AbsContext implements IContext {

    private IDataSetFormat df;
    private IRunResultFormat rf;

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
        return new HashSet<>(Arrays.asList(new String[]{
            "de.clusteval.data.dataset.format.SimMatrixDataSetFormat",
            "de.clusteval.run.result.format.TabSeparatedRunResultFormat"}));
    }

    @Override
    public IDataSetFormat getStandardInputFormat() {
        try {
            // take the newest version
            if (df == null) {
                df = DataSetFormatFactory.parseFromString("SimMatrixDataSetFormat");
                df.init(repository, System.currentTimeMillis(), new File(df.getName()));
            }
            return df;
        } catch (UnknownProviderException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public IRunResultFormat getStandardOutputFormat() {
        try {
            // take the newest version
            if (rf == null) {
                rf = RunResultFormatFactory.parseFromString("TabSeparatedRunResultFormat");
                rf.init(repository, System.currentTimeMillis(), new File(rf.getName()));
            }
            return rf;
        } catch (UnknownProviderException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

}
