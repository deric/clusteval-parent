/**
 * 
 */
package de.clusteval.framework.repository;

import java.util.HashMap;
import java.util.Map;

//TODO: rename
public class DynamicRepositoryEntityMap {

	protected Map<Class<? extends RepositoryObject>, DynamicRepositoryEntity<? extends RepositoryObject>> map;

	/**
	 * 
	 */
	public DynamicRepositoryEntityMap() {
		super();
		this.map = new HashMap<Class<? extends RepositoryObject>, DynamicRepositoryEntity<? extends RepositoryObject>>();
	}

	@SuppressWarnings("unchecked")
	public <T extends RepositoryObject> DynamicRepositoryEntity<T> put(
			final Class<? extends T> c, final DynamicRepositoryEntity<T> o) {
		return (DynamicRepositoryEntity<T>) this.map.put(c, o);
	}

	@SuppressWarnings("unchecked")
	public <T extends RepositoryObject> DynamicRepositoryEntity<T> get(
			final Class<T> c) {
		Object o = this.map.get(c);
		if (o != null)
			return (DynamicRepositoryEntity<T>) o;
		return null;
	}

	public <T extends RepositoryObject> boolean containsKey(final Class<T> c) {
		return this.map.containsKey(c);
	}
}