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
package de.clusteval.data.preprocessing;

import de.clusteval.api.data.AbsoluteDataSet;
import de.clusteval.api.data.DataMatrix;
import de.clusteval.api.data.DataPreprocessor;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.RelativeDataSet;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.exceptions.UnknownDataSetFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.IRengine;
import de.clusteval.api.r.RException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Christian Wiwie
 *
 */
@ServiceProvider(service = DataPreprocessor.class)
public class EigenvectorTransDataPreprocessor extends DataPreprocessor {

    public EigenvectorTransDataPreprocessor() {
        super();
    }

    /**
     * @param other
     * @throws RegisterException
     */
    public EigenvectorTransDataPreprocessor(DataPreprocessor other)
            throws RegisterException {
        super(other);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.preprocessing.DataPreprocessor#preprocess(de.clusteval
     * .data.DataConfig)
     */
    @Override
    public IDataSet preprocess(IDataSet data) throws InterruptedException, RException {
        if (data instanceof RelativeDataSet) {
            throw new IllegalArgumentException(
                    "The eigenvector transformation preprocessor is only applicable to absolute coordinates");
        }
        final AbsoluteDataSet dataSet = (AbsoluteDataSet) data;
        try {
            dataSet.loadIntoMemory();
            DataMatrix matrix = dataSet.getDataSetContent();
            IRengine rEngine = repository.getRengineForCurrentThread();
            rEngine.assign("matrix", matrix.getData());
            rEngine.clear();
        } catch (InvalidDataSetFormatVersionException | IllegalArgumentException |
                 IOException | UnknownDataSetFormatException e1) {
            e1.printStackTrace();
        } finally {
            dataSet.unloadFromMemory();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.preprocessing.DataPreprocessor#getCompatibleDataSetFormats
     * ()
     */
    @Override
    public Set<String> getCompatibleDataSetFormats() {
        return new HashSet<>(
                Arrays.asList(new String[]{"MatrixDataSetFormat"}));
    }
}
