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
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import java.io.File;

/**
 * @author Christian Wiwie
 *
 */
@FormatVersion(version = 1)
public class RowSimDataSetFormat extends RelativeDataSetFormat {

    public static final String NAME = "Rowwise Similarity";

    @Override
    public String getName() {
        return NAME;
    }

    public RowSimDataSetFormat() {

    }
    /**
     * Instantiates a new row sim data set format.
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
    public RowSimDataSetFormat(final IRepository repo, final boolean register,
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
    public RowSimDataSetFormat(final RowSimDataSetFormat other)
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
    public DataSetFormatParser getDataSetFormatParser() {
        return new RowSimDataSetFormatParser();
    }
}
