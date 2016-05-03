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

import de.clusteval.api.run.RunResultFormat;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import java.io.File;

/**
 * @author Christian Wiwie
 *
 */
public class clusterONERunResultFormat extends RunResultFormat {

    /**
     * @param repo
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public clusterONERunResultFormat(IRepository repo, boolean register,
            long changeDate, File absPath) throws RegisterException {
        super(repo, register, changeDate, absPath);
    }

    /**
     * The copy constructor for this format.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public clusterONERunResultFormat(final clusterONERunResultFormat other)
            throws RegisterException {
        super(other);
    }

}
