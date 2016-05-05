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

import de.clusteval.api.program.RegisterException;
import de.clusteval.api.r.RException;
import de.clusteval.api.r.RLibraryInferior;
import de.clusteval.api.repository.RepositoryObject;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 *
 * @author deric
 */
public abstract class DataPreprocessor extends RepositoryObject implements RLibraryInferior {

    public DataPreprocessor() {
        super();
    }

    /**
     * The copy constructor of data preprocessors.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public DataPreprocessor(DataPreprocessor other) throws RegisterException {
        super(other);
    }

    /** *
     *
     * @return preprocessing method identifier
     */
    public abstract String getName();

    /**
     * This method is reponsible for preprocessing the passed data and creating
     * a new dataset object corresponding to the newly created preprocessed
     * dataset.
     *
     * @param dataSet
     *                The dataset to be preprocessed.
     * @return The preprocessed dataset.
     * @throws InterruptedException
     * @throws de.clusteval.api.r.RException
     */
    public abstract IDataSet preprocess(final IDataSet dataSet) throws InterruptedException, RException;

    /**
     * @return A set with simple names of all classes, this preprocessor is
     *         compatible to.
     */
    public abstract Set<String> getCompatibleDataSetFormats();

    @Override
    public DataPreprocessor clone() {
        try {
            return this.getClass().getConstructor(this.getClass())
                    .newInstance(this);
        } catch (IllegalArgumentException | SecurityException | InstantiationException |
                 IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.log.warn("Cloning instance of class "
                + this.getClass().getSimpleName() + " failed");
        return null;
    }

}
