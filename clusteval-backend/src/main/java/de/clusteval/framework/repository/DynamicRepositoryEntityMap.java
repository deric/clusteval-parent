/**
 *
 */
package de.clusteval.framework.repository;

import de.clusteval.api.repository.IRepositoryObject;
import java.util.HashMap;
import java.util.Map;

//TODO: rename
public class DynamicRepositoryEntityMap {

    protected Map<Class<? extends IRepositoryObject>, DynamicRepositoryEntity<? extends IRepositoryObject>> map;

    /**
     *
     */
    public DynamicRepositoryEntityMap() {
        super();
        this.map = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends IRepositoryObject> DynamicRepositoryEntity<T> put(
            final Class<? extends T> c, final DynamicRepositoryEntity<T> o) {
        return (DynamicRepositoryEntity<T>) this.map.put(c, o);
    }

    @SuppressWarnings("unchecked")
    public <T extends IRepositoryObject> DynamicRepositoryEntity<T> get(final Class<T> c) {
        Object o = this.map.get(c);
        if (o != null) {
            return (DynamicRepositoryEntity<T>) o;
        }
        return null;
    }

    public <T extends IRepositoryObject> boolean containsKey(final Class<T> c) {
        return this.map.containsKey(c);
    }
}
