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
package de.clusteval.data.dataset.type;

import de.clusteval.api.program.RegisterException;
import de.clusteval.api.repository.IRepository;
import de.clusteval.framework.repository.RepositoryObject;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Dataset types are used to classify datasets into different thematic groups.
 * <p>
 *
 * {@code
 *
 * Data set types can be added to ClustEval by
 *
 * 1. extending this class with your own class MyDataSetType. You have to provide your own implementations for the following methods, otherwise the framework will not be able to load your class.
 *
 *   * :java:ref:`DataSetType(Repository, boolean,long, File)`: The constructor of your class. This constructor has to be implemented and public, otherwise the framework will not be able to load your class.
 *   * :java:ref:`DataSetType(MyDataSetType)`: The copy constructor of your class taking another instance of your class. This constructor has to be im- plemented and public.
 *   * :java:ref:`getAlias()`: This alias is used whenever this program is visually represented and a readable name is needed. This is used to represent your program on the website for example.
 *
 * 2. Creating a jar file named MyDataSetType.jar containing the MyDataSetType.class compiled on your machine in the correct folder structure corresponding to the packages:
 *
 *   * de/clusteval/data/dataset/type/MyDataSetType.class
 *
 * 3. Putting the MyDataSetType.jar into the dataset types folder of the repository:
 *
 *   * <REPOSITORY ROOT>/supp/types/dataset
 *   * The backend server will recognize and try to load the new dataset type automatically the next time, the :java:ref:`DataSetTypeFinderThread` checks the filesystem.
 *
 * }
 *
 * @author Christian Wiwie
 *
 */
public abstract class DataSetType extends RepositoryObject {

    /**
     * @param repository
     * @param register
     * @param changeDate
     * @param absPath
     * @throws RegisterException
     *
     */
    public DataSetType(final IRepository repository, final boolean register,
            final long changeDate, final File absPath) throws RegisterException {
        super(repository, register, changeDate, absPath);
    }

    /**
     * The copy constructor for dataset types.
     *
     * @param other
     *              The object to clone.
     * @throws RegisterException
     */
    public DataSetType(final DataSetType other) throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.RepositoryObject#clone()
     */
    @Override
    public final DataSetType clone() {
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


    /**
     * This alias is used whenever this dataset type is visually represented and
     * a readable name is needed.
     *
     * @return The alias of this dataset type.
     */
    public abstract String getName();
}
