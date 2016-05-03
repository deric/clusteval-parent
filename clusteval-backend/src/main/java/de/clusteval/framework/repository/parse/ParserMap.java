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
package de.clusteval.framework.repository.parse;

import java.util.HashMap;
import java.util.Map;

import de.clusteval.api.repository.RepositoryObject;

//TODO: rename
public class ParserMap {

    protected Map<Class<? extends RepositoryObject>, Parser<? extends RepositoryObject>> map;

    /**
     *
     */
    public ParserMap() {
        super();
        this.map = new HashMap<Class<? extends RepositoryObject>, Parser<? extends RepositoryObject>>();
    }

    @SuppressWarnings("unchecked")
    public <T extends RepositoryObject> Parser<T> put(final Class<T> c,
            final Parser<T> o) {
        return (Parser<T>) this.map.put(c, o);
    }

    @SuppressWarnings("unchecked")
    public <T extends RepositoryObject> Parser<T> get(final Class<T> c) {
        Object o = this.map.get(c);
        if (o != null) {
            return (Parser<T>) o;
        }
        return null;
    }

    public <T extends RepositoryObject> boolean containsKey(final Class<T> c) {
        return this.map.containsKey(c);
    }
}
