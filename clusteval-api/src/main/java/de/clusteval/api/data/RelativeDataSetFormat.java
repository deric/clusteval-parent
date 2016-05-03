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

import de.clusteval.api.Matrix;
import de.clusteval.api.Precision;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.program.RegisterException;
import java.io.IOException;

/**
 * @author Christian Wiwie
 *
 */
public abstract class RelativeDataSetFormat extends DataSetFormat {

    public RelativeDataSetFormat() {

    }

    /**
     * The copy constructor for this format.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public RelativeDataSetFormat(final RelativeDataSetFormat other)
            throws RegisterException {
        super(other);
    }

    @Override
    public final Matrix parse(final IDataSet dataSet, Precision precision)
            throws IllegalArgumentException, IOException, InvalidDataSetFormatVersionException {
        return (Matrix) super.parse(dataSet, precision);
    }
}
