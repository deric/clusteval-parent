/**
 * 
 */
package de.clusteval.framework.repository.parse;

import java.util.HashMap;
import java.util.Map;

import de.clusteval.framework.repository.RepositoryObject;

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
		if (o != null)
			return (Parser<T>) o;
		return null;
	}

	public <T extends RepositoryObject> boolean containsKey(final Class<T> c) {
		return this.map.containsKey(c);
	}
}