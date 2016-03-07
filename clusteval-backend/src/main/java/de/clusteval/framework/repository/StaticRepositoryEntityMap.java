/**
 * 
 */
package de.clusteval.framework.repository;

import java.util.HashMap;
import java.util.Map;

//TODO: rename
public class StaticRepositoryEntityMap {

	protected Map<Class<? extends RepositoryObject>, StaticRepositoryEntity<? extends RepositoryObject>> map;

	/**
	 * 
	 */
	public StaticRepositoryEntityMap() {
		super();
		this.map = new HashMap<Class<? extends RepositoryObject>, StaticRepositoryEntity<? extends RepositoryObject>>();
	}

	@SuppressWarnings("unchecked")
	public <T extends RepositoryObject> StaticRepositoryEntity<T> put(
			final Class<? extends T> c, final StaticRepositoryEntity<T> o) {
		return (StaticRepositoryEntity<T>) this.map.put(c, o);
	}

	@SuppressWarnings("unchecked")
	public <T extends RepositoryObject> StaticRepositoryEntity<T> get(
			final Class<T> c) {
		Object o = this.map.get(c);
		if (o != null)
			return (StaticRepositoryEntity<T>) o;
		return null;
	}

	public <T extends RepositoryObject> boolean containsKey(final Class<T> c) {
		return this.map.containsKey(c);
	}
}