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
package de.clusteval.data.dataset.format;

import de.clusteval.api.data.RelativeDataSetFormat;
import de.clusteval.api.FormatVersion;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.data.IDataSetFormatParser;
import de.clusteval.api.program.RegisterException;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Christian Wiwie
 *
 */
@FormatVersion(version = 1)
@ServiceProvider(service = IDataSetFormat.class)
public class SimMatrixDataSetFormat extends RelativeDataSetFormat {

    public SimMatrixDataSetFormat() {

    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.format.DataSetFormat#getName()
     */
    @Override
    public String getName() {
        return "Similarity Matrix";
    }


    /**
     * The copy constructor for this format.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public SimMatrixDataSetFormat(final SimMatrixDataSetFormat other)
            throws RegisterException {
        super(other);
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.dataset.format.DataSetFormat#getDataSetFormatParser()
     */
    @Override
    public IDataSetFormatParser getDataSetFormatParser() {
        return new SimMatrixDataSetFormatParser();
    }
}
