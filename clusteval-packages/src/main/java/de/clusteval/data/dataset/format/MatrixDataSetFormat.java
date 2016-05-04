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

import de.clusteval.api.FormatVersion;
import de.clusteval.api.data.AbsoluteDataSetFormat;
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
public class MatrixDataSetFormat extends AbsoluteDataSetFormat {

    public static final String NAME = "MatrixDataSetFormat";

    public MatrixDataSetFormat() {
        super();
    }

    /**
     * The copy constructor for this format.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public MatrixDataSetFormat(final MatrixDataSetFormat other)
            throws RegisterException {
        super(other);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public IDataSetFormatParser getDataSetFormatParser() {
        return new MatrixDataSetFormatParser();
    }
}
