/*
 * Copyright (C) 2013-2016 Christian Wiwie
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
package de.clusteval.api.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//TODO: rename
public class StaticRepositoryEntityMap {

    protected Map<Class<? extends IRepositoryObject>, StaticRepositoryEntity<? extends IRepositoryObject>> map;

    public StaticRepositoryEntityMap() {
        super();
        this.map = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends IRepositoryObject> StaticRepositoryEntity<T> put(
            final Class<? extends T> c, final StaticRepositoryEntity<T> o) {
        return (StaticRepositoryEntity<T>) this.map.put(c, o);
    }

    @SuppressWarnings("unchecked")
    public <T extends IRepositoryObject> StaticRepositoryEntity<T> get(
            final Class<T> c) {
        Object o = this.map.get(c);
        if (o != null) {
            return (StaticRepositoryEntity<T>) o;
        }
        return null;
    }

    public <T extends IRepositoryObject> boolean containsKey(final Class<T> c) {
        return this.map.containsKey(c);
    }

    public Set<Entry<Class<? extends IRepositoryObject>, StaticRepositoryEntity<? extends IRepositoryObject>>> entrySet() {
        return map.entrySet();
    }
}
