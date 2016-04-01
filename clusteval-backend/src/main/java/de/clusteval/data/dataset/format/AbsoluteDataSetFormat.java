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

import de.clusteval.api.Precision;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.data.dataset.DataMatrix;
import de.clusteval.data.dataset.DataSet;
import java.io.File;
import java.io.IOException;

/**
 * @author Christian Wiwie
 *
 */
public abstract class AbsoluteDataSetFormat extends DataSetFormat {

    /**
     * Instantiates a new absolute data set format.
     *
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     *
     * @param version
     * @throws RegisterException
     *
     */
    public AbsoluteDataSetFormat(final IRepository repo, final boolean register,
            final long changeDate, final File absPath, final int version)
            throws RegisterException {
        super(repo, register, changeDate, absPath, version);
    }

    /**
     * The copy constructor for this format.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public AbsoluteDataSetFormat(final AbsoluteDataSetFormat other)
            throws RegisterException {
        super(other);
    }

    @Override
    public final DataMatrix parse(final DataSet dataSet, Precision precision) throws IllegalArgumentException,
                                                                                     IOException, InvalidDataSetFormatVersionException {
        return (DataMatrix) super.parse(dataSet, precision);
    }
}
