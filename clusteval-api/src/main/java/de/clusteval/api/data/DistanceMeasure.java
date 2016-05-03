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
package de.clusteval.api.data;

import de.clusteval.api.IDistanceMeasure;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RLibraryInferior;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.repository.AbsRepoObject;
import de.clusteval.api.repository.IRepository;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public abstract class DistanceMeasure extends AbsRepoObject implements RLibraryInferior, IDistanceMeasure {

    private static final Logger LOG = LoggerFactory.getLogger(DistanceMeasure.class);

    /**
     * The copy constructor of this distance measures.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public DistanceMeasure(final DistanceMeasure other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public final DistanceMeasure clone() {
        try {
            return this.getClass().getConstructor(this.getClass())
                    .newInstance(this);
        } catch (IllegalArgumentException | SecurityException |
                 InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class "
                + this.getClass().getSimpleName() + " failed");
        return null;
    }

    /**
     * Parses the from string.
     *
     * @param repository      the repository
     * @param distanceMeasure the distance measure
     * @return the distance measure
     * @throws UnknownDistanceMeasureException
     */
    public static IDistanceMeasure parseFromString(final IRepository repository,
            String distanceMeasure) throws UnknownDistanceMeasureException {
        Class<? extends IDistanceMeasure> c = repository.getRegisteredClass(
                IDistanceMeasure.class, "de.clusteval.data.distance."
                + distanceMeasure);
        try {
            IDistanceMeasure measure = c.getConstructor(IRepository.class,
                    boolean.class, long.class, File.class).newInstance(
                            repository, false, System.currentTimeMillis(),
                            new File(distanceMeasure));

            return measure;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                 SecurityException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            LOG.warn("failed parsing", e);
        }
        throw new UnknownDistanceMeasureException("\"" + distanceMeasure
                + "\" is not a known distance measure.");
    }

    /**
     * @param point1 A point with double valued coordinates.
     * @param point2 A point with double valued coordinates.
     * @return Distance between point1 and point2.
     * @throws RNotAvailableException
     * @throws InterruptedException
     */
    public abstract double getDistance(double[] point1, double[] point2)
            throws RNotAvailableException, InterruptedException;


    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

}
