/**
 * *****************************************************************************
 * Copyright (c) 2013 Christian Wiwie.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Christian Wiwie - initial API and implementation
 *****************************************************************************
 */
package de.clusteval.program.r;

import de.clusteval.api.data.IDataConfig;
import de.clusteval.api.program.IProgramConfig;
import de.clusteval.api.r.IRProgram;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RLibraryNotLoadedException;
import de.clusteval.api.r.RNotAvailableException;
import java.io.IOException;
import java.util.Map;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Wiwie
 *
 */
public class RProgramThread extends Thread {

    protected Thread poolThread;
    protected Exception ex;
    protected IRProgram rProgram;
    protected IDataConfig dataConfig;
    protected IProgramConfig programConfig;
    protected String[] invocationLine;
    protected Map<String, String> effectiveParams;
    protected Map<String, String> internalParams;
    protected Logger log;

    /**
     * @param t
     * @param rProgram
     * @param dataConfig
     * @param programConfig
     * @param invocationLine
     * @param effectiveParams
     * @param internalParams
     * @throws RserveException
     */
    public RProgramThread(final Thread t, final IRProgram rProgram,
            final IDataConfig dataConfig, final IProgramConfig programConfig,
            final String[] invocationLine,
            final Map<String, String> effectiveParams,
            final Map<String, String> internalParams) throws RserveException {
        super();

        this.poolThread = t;
        // we need to clone here, as each thread needs its own object
        this.rProgram = rProgram.clone();
        this.dataConfig = dataConfig;
        this.programConfig = programConfig;
        this.invocationLine = invocationLine;
        this.effectiveParams = effectiveParams;
        this.internalParams = internalParams;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void run() {
        try {
            this.rProgram.setEngine(this.rProgram.getRepository().getRengine(this.poolThread));
            try {
                this.rProgram.beforeExec(dataConfig, programConfig,
                        invocationLine, effectiveParams, internalParams);
                if (this.isInterrupted()) {
                    throw new InterruptedException();
                }
                this.rProgram.doExec(dataConfig, programConfig, invocationLine,
                        effectiveParams, internalParams);
                if (this.isInterrupted()) {
                    throw new InterruptedException();
                }
                this.rProgram.afterExec(dataConfig, programConfig,
                        invocationLine, effectiveParams, internalParams);
            } catch (RLibraryNotLoadedException |
                    RNotAvailableException | InterruptedException | IOException e) {
                ex = e;
            }
        } catch (RException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Thread#interrupt()
     */
    @Override
    public void interrupt() {
        super.interrupt();
        try {
            if (this.rProgram.getEngine() != null) {
                this.rProgram.getEngine().interrupt();
            }
        } finally {
            this.rProgram.getRepository().clearRengine(poolThread);
        }
    }

    public Exception getException() {
        return this.ex;
    }
}
