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
import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RLibraryInferior;
import de.clusteval.api.repository.RepositoryObject;
import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deric
 */
public abstract class DistanceMeasure extends RepositoryObject implements RLibraryInferior, IDistanceMeasure {

    protected static final Logger LOG = LoggerFactory.getLogger(DistanceMeasure.class);

    public DistanceMeasure() {
        super();
    }

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

}
