
package edu.cornell.cs.nlp.assignments.counting;

import java.io.Serializable;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;

/**
 * The MapFactory is a mechanism for specifying what kind of map is to be used
 * by some object. For example, if you want a Counter which is backed by an
 * IdentityHashMap instead of the defaul HashMap, you can pass in an
 * IdentityHashMapFactory.
 *
 * @author Dan Klein, Berkeley
 */

public abstract class MapFactory<K, V> implements Serializable {
	private static final long serialVersionUID = 5724671156522771657L;

	public abstract Map<K, V> buildMap();

	public static class HashMapFactory<K, V> extends MapFactory<K, V> {
		private static final long serialVersionUID = -6491616517914718240L;

		@Override
		public Map<K, V> buildMap() {
			return new HashMap<K, V>();
		}
	}

	public static class IdentityHashMapFactory<K, V> extends MapFactory<K, V> {
		private static final long serialVersionUID = 8005074728209927554L;

		@Override
		public Map<K, V> buildMap() {
			return new IdentityHashMap<K, V>();
		}
	}

	public static class TreeMapFactory<K, V> extends MapFactory<K, V> {
		private static final long serialVersionUID = -8397918717816870659L;

		@Override
		public Map<K, V> buildMap() {
			return new TreeMap<K, V>();
		}
	}

	public static class WeakHashMapFactory<K, V> extends MapFactory<K, V> {
		private static final long serialVersionUID = 5762127033258439199L;

		@Override
		public Map<K, V> buildMap() {
			return new WeakHashMap<K, V>();
		}
	}
}
