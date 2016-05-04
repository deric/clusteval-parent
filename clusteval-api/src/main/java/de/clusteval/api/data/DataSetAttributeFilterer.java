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

import de.clusteval.utils.TextFileParser;
import java.io.IOException;

/**
 * @author Christian Wiwie
 *
 */
public class DataSetAttributeFilterer extends TextFileParser {

    /**
     * @param absFilePath
     * @throws IOException
     */
    public DataSetAttributeFilterer(String absFilePath) throws IOException {
        super(absFilePath, new int[0], new int[0], false, null, absFilePath
                + ".strip", OUTPUT_MODE.STREAM);
        this.setLockTargetFile(true);
    }

    @Override
    public boolean isLockingTargetFile() {
        return super.isLockingTargetFile();
    }

    @Override
    protected void processLine(String[] key, String[] value) {
        // do nothing
    }

    @Override
    protected boolean checkLine(String line) {
        return !DataSetAttributeParser.attributeLinePrefixPattern.matcher(line)
                .matches();
    }

    @Override
    protected String getLineOutput(String[] key, String[] value) {
        return value[0] + System.getProperty("line.separator");
    }

}
