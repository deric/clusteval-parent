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
 * The Class InternalRunResultFormat.
 *
 * @author Christian Wiwie
 */
@ServiceProvider(service = IRunResultFormat.class)
public class TabSeparatedRunResultFormat extends RunResultFormat {

    public static final String NAME = "tab separated";

    public TabSeparatedRunResultFormat() {
        super();
    }

    /**
     * The copy constructor for this format.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public TabSeparatedRunResultFormat(final TabSeparatedRunResultFormat other)
            throws RegisterException {
        super(other);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
