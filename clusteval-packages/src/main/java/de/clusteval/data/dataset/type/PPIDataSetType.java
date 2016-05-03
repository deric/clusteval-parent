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

import de.clusteval.api.data.AbsDataType;
import de.clusteval.api.program.RegisterException;

/**
 * @author Christian Wiwie
 *
 */
public class PPIDataSetType extends AbsDataType {

    /**
     * The copy constructor for this type.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public PPIDataSetType(final PPIDataSetType other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.type.DataSetType#getAlias()
     */
    @Override
    public String getName() {
        return "Protein-Protein-Interaction";
    }
}
