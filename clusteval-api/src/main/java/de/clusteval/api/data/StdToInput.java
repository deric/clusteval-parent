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

/**
 * @author Christian Wiwie
 *
 */
public class StdToInput implements IConversionStandardToInputConfiguration {

    public StdToInput() {
        super();
    }

    /**
     * The copy constructor of this class.
     *
     * @param other
     *              The object to clone.
     */
    @SuppressWarnings("unused")
    public StdToInput(final StdToInput other) {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public StdToInput clone() {
        return new StdToInput(this);
    }

}
