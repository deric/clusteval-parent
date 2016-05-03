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
package de.clusteval.data.dataset.format;

import de.clusteval.api.data.RelativeDataSetFormat;
import de.clusteval.api.FormatVersion;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.program.RegisterException;
import de.clusteval.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Christian Wiwie
 *
 */
@FormatVersion(version = 1)
@ServiceProvider(service = IDataSetFormat.class)
public class BLASTDataSetFormat extends RelativeDataSetFormat {

    public BLASTDataSetFormat() {

    }

    /**
     * The copy constructor for this format.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public BLASTDataSetFormat(final BLASTDataSetFormat other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.dataset.format.DataSetFormat#getName()
     */
    @Override
    public String getName() {
        return "BLAST";
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * data.dataset.format.DataSetFormat#copyDataSetTo(data.dataset.DataSet,
     * java.io.File)
     */
    @Override
    public boolean copyDataSetTo(IDataSet dataSet, File copyDestination,
            final boolean overwrite) {
        boolean copied = super.copyDataSetTo(dataSet, copyDestination,
                overwrite);
        if (copied) {
            try {
                org.apache.commons.io.FileUtils.copyFile(
                        new File(dataSet.getAbsolutePath() + ".fasta"),
                        new File(copyDestination.getAbsolutePath() + ".fasta"));
            } catch (IOException e) {
                copied = false;
            }
            copied = true;
        }
        return copied;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * data.dataset.format.DataSetFormat#copyDataSetToFolder(data.dataset.DataSet
     * , java.io.File)
     */
    @Override
    public boolean copyDataSetToFolder(IDataSet dataSet,
            File copyFolderDestination, final boolean overwrite) {
        boolean copied = super.copyDataSetToFolder(dataSet,
                copyFolderDestination, overwrite);
        if (copied) {
            try {
                org.apache.commons.io.FileUtils.copyFile(
                        new File(dataSet.getAbsolutePath() + ".fasta"),
                        new File(FileUtils.buildPath(
                                copyFolderDestination.getAbsolutePath(),
                                new File(dataSet.getAbsolutePath()).getName()
                                + ".fasta")));
            } catch (IOException e) {
                copied = false;
            }
            copied = true;
        }
        return copied;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.dataset.format.DataSetFormat#getDataSetFormatParser()
     */
    @Override
    public DataSetFormatParser getDataSetFormatParser() {
        return new BLASTDataSetFormatParser();
    }

}
