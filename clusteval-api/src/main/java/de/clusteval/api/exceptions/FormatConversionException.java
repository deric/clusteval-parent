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
package de.clusteval.api.exceptions;

/**
 * @author Christian Wiwie
 */
public class FormatConversionException extends Exception {

    private static final long serialVersionUID = 734817160402227505L;

    /**
     * Instantiates a new format conversion exception.
     *
     * @param s the s
     */
    public FormatConversionException(String s) {
        super(s);
    }
}