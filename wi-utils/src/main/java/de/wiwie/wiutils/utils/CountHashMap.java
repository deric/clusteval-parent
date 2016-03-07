/**
 * 
 */
package de.wiwie.wiutils.utils;

/**
 * @author Christian Wiwie
 * @param <K>
 *            The key type
 * @param <V>
 *            The value type
 * 
 */
public class CountHashMap<K> extends TryHashMap<K, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6757076541208304287L;

	/**
	 * @param init
	 * @param initValue
	 */
	public CountHashMap(final int init) {
		super(Integer.valueOf(init));
	}

	/**
	 * @param key
	 * @return
	 */
	public Integer increase(final K key) {
		if (!this.containsKey(key))
			this.put(key, initValue);
		this.put(key, this.get(key) + 1);
		return this.get(key);
	}

	/**
	 * @param key
	 * @return
	 */
	public Integer decrease(final K key) {
		if (!this.containsKey(key))
			this.put(key, initValue);
		this.put(key, this.get(key) - 1);
		return this.get(key);
	}
}
