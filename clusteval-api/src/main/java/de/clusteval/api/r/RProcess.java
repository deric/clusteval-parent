/*
 * Copyright (C) 2016 deric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.clusteval.api.r;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author Christian Wiwie
 *
 */
public class RProcess extends Process {

    protected RProgramThread rProgramThread;

    public RProcess(RProgramThread rProgramThread) {
        super();
        this.rProgramThread = rProgramThread;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Process#getOutputStream()
     */
    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Process#getInputStream()
     */
    @Override
    public InputStream getInputStream() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Process#getErrorStream()
     */
    @Override
    public InputStream getErrorStream() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Process#waitFor()
     */
    @Override
    public int waitFor() throws InterruptedException {
        this.rProgramThread.join();
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Process#exitValue()
     */
    @Override
    public int exitValue() {
        // TODO: is this needed?
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Process#destroy()
     */
    @Override
    public void destroy() {
        this.rProgramThread.interrupt();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Process#destroyForcibly()
     */
    @Override
    public Process destroyForcibly() {
        this.destroy();
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Process#waitFor(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean waitFor(long timeout, TimeUnit unit)
            throws InterruptedException {
        long ms = TimeUnit.MILLISECONDS.convert(timeout, unit);
        if (ms > 0) {
            this.rProgramThread.join(ms);
        }
        return !this.rProgramThread.isAlive();
    }

}
