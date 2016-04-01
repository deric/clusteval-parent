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

import de.clusteval.api.Precision;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetType;
import de.clusteval.api.data.WEBSITE_VISIBILITY;
import de.clusteval.api.exceptions.InvalidDataSetFormatVersionException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.repository.RegisterException;
import de.clusteval.data.dataset.format.RelativeDataSetFormat;
import de.wiwie.wiutils.utils.SimilarityMatrix;
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

    private SimilarityMatrix similarities;

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

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.DataSet#clone()
     */
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

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.DataSet#getDataSetFormat()
     */
    @Override
    public RelativeDataSetFormat getDataSetFormat() {
        return (RelativeDataSetFormat) super.getDataSetFormat();
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.DataSet#loadIntoMemory()
     */
    @Override
    public boolean loadIntoMemory(Precision precision) throws IllegalArgumentException, IOException,
                                                              InvalidDataSetFormatVersionException {
        if (!isInMemory()) {
            this.similarities = this.getDataSetFormat().parse(this, precision);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.dataset.DataSet#setDataSetContent(java.lang.Object)
     */
    @Override
    public boolean setDataSetContent(Object newContent) {
        if (!(newContent instanceof SimilarityMatrix)) {
            return false;
        }

        this.similarities = (SimilarityMatrix) newContent;
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.DataSet#isInMemory()
     */
    @Override
    public boolean isInMemory() {
        return this.similarities != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.DataSet#getDataSetContent()
     */
    @Override
    public SimilarityMatrix getDataSetContent() {
        return this.similarities;
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.DataSet#unloadFromMemory()
     */
    @Override
    public boolean unloadFromMemory() {
        this.similarities = null;
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.DataSet#getIds()
     */
    @Override
    public List<String> getIds() {
        String[] result = new String[this.similarities.getIds().size()];
        for (String id : this.similarities.getIds().keySet()) {
            result[this.similarities.getIds().get(id)] = id;
        }
        return Arrays.asList(result);
    }

}
