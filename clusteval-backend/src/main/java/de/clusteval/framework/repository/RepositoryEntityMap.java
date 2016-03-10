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
package de.clusteval.framework.repository;

import de.clusteval.api.repository.IRepositoryObject;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author deric
 * @param <E>
 * @param <R>
 */
public class RepositoryEntityMap<E extends IRepositoryObject, R extends RepositoryEntity<E>> {

    protected Map<Class<? extends E>, R> map;

    public RepositoryEntityMap() {
        super();
        this.map = new HashMap<>();
    }

    public R put(final Class<? extends E> c, R o) {
        return this.map.put(c, o);
    }

    public R get(final Class<? extends E> c) {
        R o = this.map.get(c);
        if (o != null) {
            return o;
        }
        return null;
    }

    public boolean containsKey(final Class<? extends E> c) {
        return this.map.containsKey(c);
    }

}
