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
package de.clusteval.utils;

import de.clusteval.api.repository.IRepository;
import de.clusteval.api.program.RegisterException;

/**
 * @author Christian Wiwie
 *
 */
public class NamedStringAttribute extends NamedAttribute<String> {

    /**
     * @param repository
     * @param name
     * @param value
     * @throws RegisterException
     */
    public NamedStringAttribute(final IRepository repository, final String name,
            final String value) throws RegisterException {
        super(repository, name, value);
        this.register();
    }

    /**
     * The copy constructor of named double attributes.
     *
     * @param other The object to clone.
     * @throws RegisterException
     */
    public NamedStringAttribute(final NamedStringAttribute other)
            throws RegisterException {
        super(other);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.wiwie.wiutils.utils.NamedAttribute#cloneValue(java.lang.Object)
     */
    @Override
    public String cloneValue(String value) {
        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see framework.repository.RepositoryObject#clone()
     */
    @Override
    public NamedStringAttribute clone() {
        try {
            return new NamedStringAttribute(this);
        } catch (RegisterException e) {
            // should not occur
            e.printStackTrace();
        }
        return null;
    }


    /* (non-Javadoc)
     * @see de.clusteval.framework.repository.RepositoryObject#register()
     */
    @Override
    public boolean register() throws RegisterException {
        return this.repository.register(this);
    }

    /* (non-Javadoc)
     * @see de.clusteval.framework.repository.RepositoryObject#unregister()
     */
    @Override
    public boolean unregister() {
        return this.repository.unregister(this);
    }
}
