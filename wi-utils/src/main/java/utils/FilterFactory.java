/**
 * 
 */
package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wiwie
 * 
 */
public abstract class FilterFactory {

	protected static Map<FILTER_TYPE, Map<Integer, Filter>> filters = new HashMap<FILTER_TYPE, Map<Integer, Filter>>();

	public enum FILTER_TYPE {
		LARGER_THAN
	}

	public static Filter createInstance(FILTER_TYPE fType, int x) {
		Filter filter = null;
		if (filters.containsKey(fType) && filters.get(fType).containsKey(x)) {
			filter = filters.get(fType).get(x);
		} else if (fType.equals(FILTER_TYPE.LARGER_THAN)) {
			filter = new LargerFilter(x);
			if (!filters.containsKey(fType))
				filters.put(fType, new HashMap<Integer, Filter>());
			filters.get(fType).put(x, filter);
		}
		return filter;
	}
}

interface Filter {

	public boolean filter(int x);
}

class LargerFilter implements Filter {

	protected int x;

	public LargerFilter(int x) {
		super();
		this.x = x;
	}

	public boolean filter(int x) {
		return x >= this.x;
	}
}