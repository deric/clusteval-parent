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
package de.clusteval.data.dataset;

import de.clusteval.api.data.IDataSet;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.utils.FileFinder;
import de.clusteval.utils.SubDirectoryIterator;
import java.io.File;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Objects of this class look for new run-files in the run-directory defined in
 * the corresponding repository.
 *
 * @author Christian Wiwie
 *
 *
 */
public class DataSetFinder extends FileFinder<IDataSet> {

    private Logger LOG = LoggerFactory.getLogger(DataSetFinder.class);

    /**
     * Instantiates a new run finder.
     *
     * @param repository
     *                   the repository
     * @throws RegisterException
     */
    public DataSetFinder(final IRepository repository) throws RegisterException {
        super(repository, IDataSet.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Finder#checkFile(java.io.File)
     */
    @Override
    public boolean checkFile(File file) {
        try {
            DataSetAttributeParser p;
            if (file.isFile()
                    && !file.getParentFile().getName().equals("configs")) {
                p = new DataSetAttributeParser(file.getAbsolutePath());
                p.process();
                return p.getAttributeValues().size() > 0;
            }
            return false;
        } catch (Exception e) {
            LOG.warn("failed to load " + file.getPath(), e);
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.Finder#getIterator()
     */
    @Override
    public Iterator<File> getIterator() {
        return new SubDirectoryIterator(getBaseDir());
    }
}
