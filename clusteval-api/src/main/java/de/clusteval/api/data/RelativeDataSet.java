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

import de.clusteval.api.Matrix;
import de.clusteval.api.Precision;
import de.clusteval.api.exceptions.InvalidDataSetFormatException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * A relative dataset contains data in terms of pairwise similarities or
 * distances between object pairs. From these no absolute coordinates of the
 * objects can be deduced. Thus a relative dataset can never be converted to an
 * absolute dataset (lossfree).
 *
 * @author Christian Wiwie
 *
 */
public class RelativeDataSet extends DataSet implements IDataSet {

    private Matrix similarities;

    /**
     *
     * @param repository
     *                          the repository this dataset should be registered at.
     * @param register
     *                          Whether this dataset should be registered in the repository.
     * @param changeDate
     *                          The change date of this dataset is used for equality checks.
     * @param absPath
     *                          The absolute path of this dataset.
     * @param alias
     *                          A short alias name for this data set.
     * @param dsFormat
     *                          The format of this dataset.
     * @param dsType
     *                          The type of this dataset
     * @param websiteVisibility
     * @throws RegisterException
     */
    public RelativeDataSet(IRepository repository, final boolean register,
            long changeDate, File absPath, final String alias,
            RelativeDataSetFormat dsFormat, IDataSetType dsType,
            final WEBSITE_VISIBILITY websiteVisibility)
            throws RegisterException {
        super(repository, register, changeDate, absPath, alias, dsFormat, dsType, websiteVisibility);
    }

    /**
     * @param dataset
     * @throws RegisterException
     */
    public RelativeDataSet(RelativeDataSet dataset) throws RegisterException {
        super(dataset);
    }

    @Override
    public RelativeDataSet clone() {
        try {
            return new RelativeDataSet(this);
        } catch (RegisterException e) {
            e.printStackTrace();
            // should not occur
        }
        return null;
    }

    @Override
    public RelativeDataSetFormat getDataSetFormat() {
        return (RelativeDataSetFormat) super.getDataSetFormat();
    }

    @Override
    public boolean loadIntoMemory(Precision precision) throws IllegalArgumentException, IOException,
                                                              InvalidDataSetFormatException {
        if (!isInMemory()) {
            this.similarities = getDataSetFormat().parse(this, precision);
        }
        return true;
    }

    @Override
    public boolean setDataSetContent(Object newContent) {
        if (!(newContent instanceof Matrix)) {
            return false;
        }

        this.similarities = (Matrix) newContent;
        return true;
    }

    @Override
    public boolean isInMemory() {
        return this.similarities != null;
    }

    @Override
    public Matrix getDataSetContent() {
        return similarities;
    }

    @Override
    public boolean unloadFromMemory() {
        this.similarities = null;
        return true;
    }

    @Override
    public List<String> getIds() {
        String[] result = new String[this.similarities.getIds().size()];
        for (String id : this.similarities.getIds().keySet()) {
            result[this.similarities.getIds().get(id)] = id;
        }
        return Arrays.asList(result);
    }

}
