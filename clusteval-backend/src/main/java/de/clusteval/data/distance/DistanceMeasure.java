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
package de.clusteval.data.distance;

import de.clusteval.api.IDistanceMeasure;
import de.clusteval.api.r.RNotAvailableException;
import de.clusteval.api.exceptions.UnknownDistanceMeasureException;
import de.clusteval.api.r.RLibraryInferior;
import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;
import de.clusteval.framework.repository.RepositoryObject;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * {@code
 *
 *
 * A distance measure MyDistanceMeasure can be added to ClustEval by
 *
 * 1. extending this class with your own class MyDistanceMeasure . You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your distance measure:
 *
 *   * :java:ref:`DistanceMeasure(Repository, boolean, long, File)` : The constructor for your distance measure. This constructor has to be implemented and public, otherwise the framework will not be able to load your distance measure.
 *   * :java:ref:`DistanceMeasure(MyDistanceMeasure)` : The copy constructor for your distance measure. This constructor has to be implemented and public, otherwise the framework will not be able to load your distance measure.
 *   * :java:ref:`getDistance(double[],double[])` : This method is the core of your distance measure. It returns the distance of the two points specified by the absolute coordinates in the two double arrays.
 *   * :java:ref:`supportsMatrix()` : This method indicates, whether your distance measure can calculate distances of a whole set of point-pairs, i.e. your distance measure implements the method getDistances(double[][]).
 *   * :java:ref:`getDistances(double[][])` : The absolute coordinates of the points are stored row-wise in the given matrix and distances are calculated between every pair of rows. Position [i][j] of the returned double[][] matrix contains the distance between the i-th and j-th row of the input matrix.
 *
 * 2. Creating a jar file named MyDistanceMeasure.jar containing the MyDistanceMeasure.class compiled on your machine in the correct folder structure corresponding to the packages:
 *
 *   * de/clusteval/data/distance/MyDistanceMeasure.class
 *
 * 3. Putting the MyDistanceMeasure.jar into the distance measure folder of the repository:
 *
 *   * <REPOSITORY ROOT>/supp/distanceMeasures
 *
 * The backend server will recognize and try to load the new distance measure au- tomatically the
 *
 * }
 *
 * @author Christian Wiwie
 *
 */
public abstract class DistanceMeasure extends RepositoryObject implements RLibraryInferior, IDistanceMeasure {

    private static final Logger LOG = LoggerFactory.getLogger(DistanceMeasure.class);

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     */
    public DistanceMeasure(IRepository repository, boolean register,
            long changeDate, File absPath) throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * The copy constructor of this distance measures.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public DistanceMeasure(final DistanceMeasure other)
            throws RegisterException {
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
