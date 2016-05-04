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
package de.clusteval.run.result.format;

import de.clusteval.api.program.RegisterException;
import de.clusteval.api.run.IRunResultFormat;
import de.clusteval.api.run.RunResultFormat;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Christian Wiwie
 *
 */
@ServiceProvider(service = IRunResultFormat.class)
public class RRWRunResultFormat extends RunResultFormat {

    public static final String NAME = "RRW result";

    public RRWRunResultFormat() {
        super();
    }

    /**
     * The copy constructor for this format.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public RRWRunResultFormat(final RRWRunResultFormat other)
            throws RegisterException {
        super(other);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
