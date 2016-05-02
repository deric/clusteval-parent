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
package de.clusteval.data.dataset.type;

import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import java.io.File;

/**
 * @author Christian Wiwie
 *
 */
public class OtherDataSetType extends DataSetType {

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     *
     */
    public OtherDataSetType(final IRepository repository,
            final boolean register, final long changeDate, final File absPath)
            throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * The copy constructor for this type.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public OtherDataSetType(
            final OtherDataSetType other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.type.DataSetType#getAlias()
     */
    @Override
    public String getAlias() {
        return "Other";
    }

}
