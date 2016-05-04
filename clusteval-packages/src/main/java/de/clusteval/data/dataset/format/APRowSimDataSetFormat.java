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

import de.clusteval.api.data.DataSetFormatParser;
import de.clusteval.api.FormatVersion;
import de.clusteval.api.data.IDataSet;
import de.clusteval.api.data.IDataSetFormat;
import de.clusteval.api.program.RegisterException;
import java.io.File;
import java.io.IOException;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Christian Wiwie
 *
 */
@FormatVersion(version = 1)
@ServiceProvider(service = IDataSetFormat.class)
public class APRowSimDataSetFormat extends RowSimDataSetFormat implements IDataSetFormat {

    public APRowSimDataSetFormat() {

    }

    /**
     * The copy constructor for this format.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public APRowSimDataSetFormat(final APRowSimDataSetFormat other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.data.dataset.format.DataSetFormat#getName()
     */
    @Override
    public String getName() {
        return "Rowwise Similarity (Affinity Propagation)";
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * data.dataset.format.DataSetFormat#moveDataSetTo(data.dataset.DataSet,
     * java.io.File, boolean)
     */
    @Override
    public boolean moveDataSetTo(IDataSet dataSet, File moveDestination, boolean overwrite) {
        boolean result = super.moveDataSetTo(dataSet, moveDestination,
                overwrite);

        // move .map file
        File mapFile = new File(dataSet.getAbsolutePath() + ".map");
        if (!mapFile.exists()) {
            return result;
        }

        File mapFileTarget = new File(moveDestination.getAbsolutePath()
                + ".map");
        try {
            if (result && (!mapFileTarget.exists() || overwrite)) {
                org.apache.commons.io.FileUtils.moveFile(
                        new File(dataSet.getAbsolutePath() + ".map"),
                        mapFileTarget);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.clusteval.data.dataset.format.RowSimDataSetFormat#getDataSetFormatParser
     * ()
     */
    @Override
    public DataSetFormatParser getDataSetFormatParser() {
        return new APRowSimDataSetFormatParser();
    }
}
