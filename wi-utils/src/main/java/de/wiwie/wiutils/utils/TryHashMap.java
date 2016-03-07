/**
 * 
 */
package de.wiwie.wiutils.utils;

import java.util.HashMap;

/**
 * @author Christian Wiwie
 * @param <K>
 * @param <V>
 * 
 */
public abstract class TryHashMap<K, V> extends HashMap<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5268585235021191215L;
	protected V initValue;

	/**
	 * @param initValue
	 */
	public TryHashMap(V initValue) {
		super();
		this.initValue = initValue;
	}

	@Override
	public V put(K key, V value) {
		if (!this.containsKey(key))
			super.put(key, initValue);
		return super.put(key, value);
	}
}
