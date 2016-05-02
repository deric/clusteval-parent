/** *****************************************************************************
 * Copyright (c) 2016 Mikkel Hansen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 ***************************************************************************** */
package de.clusteval.data.statistics;

import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RLibraryRequirement;
import de.clusteval.api.repository.IRepository;
import java.io.File;

/**
 * @author Christian Wiwie
 *
 */
@RLibraryRequirement(requiredRLibraries = {"igraph", "tnet"})
public class RichClubMaxDataStatistic
        extends
        DoubleValueDataStatistic {

    /*
     * (non-Javadoc)
     *
     * @see utils.Statistic#getAlias()
     */
    @Override
    public String getName() {
        return "Rich Club Max";
    }

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     *
     */
    public RichClubMaxDataStatistic(final IRepository repository,
            final boolean register, final long changeDate, final File absPath)
            throws RegisterException {
        super(repository, register, changeDate, absPath, 0.0);
    }

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @param value
     * @throws RegisterException
     */
    public RichClubMaxDataStatistic(final IRepository repository,
            final boolean register, final long changeDate, final File absPath,
            final double value) throws RegisterException {
        super(repository, register, changeDate, absPath, value);
    }

    /**
     * The copy constructor for this statistic.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public RichClubMaxDataStatistic(
            final RichClubMaxDataStatistic other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see data.statistics.DataStatistic#requiresGoldStandard()
     */
    @Override
    public boolean requiresGoldStandard() {
        return false;
    }

}
