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
package de.clusteval.data.goldstandard;

import de.clusteval.api.data.IGoldStandardConfig;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.utils.FileFinder;
import de.wiwie.wiutils.utils.ArrayIterator;
import java.io.File;
import java.util.Iterator;

/**
 * Objects of this class look for new run-files in the run-directory defined in
 * the corresponding repository.
 *
 * @author Christian Wiwie
 *
 *
 */
public class GoldStandardConfigFinder extends FileFinder<IGoldStandardConfig> {

    /**
     * Instantiates a new run finder.
     *
     * @param repository
     *                   The repository to register the new goldstandard configurations
     *                   at.
     * @throws RegisterException
     */
    public GoldStandardConfigFinder(final IRepository repository) throws RegisterException {
        super(repository, IGoldStandardConfig.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Finder#checkFile(java.io.File)
     */
    @Override
    public boolean checkFile(File file) {
        return file.isFile() && file.getName().endsWith(".gsconfig");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Finder#getIterator()
     */
    @Override
    public Iterator<File> getIterator() {
        return new ArrayIterator<>(getBaseDir().listFiles());
    }
}
