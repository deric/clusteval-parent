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
package de.clusteval.api.run;

/**
 * @author Christian Wiwie
 *
 */
public abstract class ExecutionIterationRunnable<IW extends IterationWrapper> extends IterationRunnable<IW> {

    protected Process proc;

    protected NoRunResultFormatParserException noRunResultException;

    /**
     * @param iterationWrapper
     */
    public ExecutionIterationRunnable(final IW iterationWrapper) {
        super(iterationWrapper);
    }

    /**
     * @return the noRunResultException
     */
    public NoRunResultFormatParserException getNoRunResultException() {
        return noRunResultException;
    }

    public int getIterationNumber() {
        return this.iterationWrapper.getOptId();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.clusteval.run.runnable.IterationRunnable#getRun()
     */
    @Override
    public ExecutionRun getRun() {
        return (ExecutionRun) super.getRun();
    }

    public Process getProcess() {
        return this.proc;
    }

}
