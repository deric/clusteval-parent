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

import de.clusteval.utils.StringExt;
import java.io.IOException;
import java.util.Map;

/**
 * @author Christian Wiwie
 *
 */
public class MCLRunResultFormatParser extends RunResultFormatParser {

    /**
     * @param internalParams
     * @param params
     * @param absFilePath
     * @throws IOException
     */
    public MCLRunResultFormatParser(final Map<String, String> internalParams,
            final Map<String, String> params, final String absFilePath)
            throws IOException {
        super(internalParams, params, absFilePath);
        this.countLines();
    }

    @Override
    protected void processLine(String[] key, String[] value) {
    }

    @Override
    protected String getLineOutput(String[] key, String[] value) {
        StringBuilder sb = new StringBuilder();
        if (currentLine == 0) {
            String I = null;
            for (String pa : params.keySet()) {
                if (pa.equals("I")) {
                    I = params.get(pa);
                    break;
                }
            }

            sb.append("I");
            sb.append("\t");
            sb.append("Clustering");
            sb.append(System.getProperty("line.separator"));

            sb.append(I);
            sb.append("\t");
        }
        sb.append(StringExt.paste(",", StringExt.append(value, ":1.0")));
        if (currentLine < getTotalLineCount() - 1) {
            sb.append(";");
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * run.result.format.RunResultFormatParser#convertToStandardFormat(run.result
     * .RunResult)
     */
    @Override
    public void convertToStandardFormat() throws IOException {
        this.process();
    }
}
