/*
 * Copyright 2011 Stefan Partusch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.spartusch;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Simple implementation of a cache using {@link java.lang.ref.SoftReference
 * SoftReferences}. This class is thread-safe.
 * @author Stefan Partusch
 *
 * @param <K> Type of keys maintained by this cache
 * @param <V> Type of mapped values
 */
public class SoftCache<K, V> {
	/**
	 * A class for values in the cache. The cache is using a {@link
	 * java.util.WeakHashMap WeakHashMap} internally, which uses {@link
	 * java.lang.ref.WeakReference WeakReferences} to store its keys. In order
	 * to prevent the WeakHashMap from removing the entries immediately this
	 * class keeps a strong reference to the key of each value.
	 * @author Stefan Partusch
	 */
	private static class KeyValue<K, V> {
		/** Strong reference to the value's key */
		private K key;
		/** The actual value */
		private V value;

		/**
		 * Creates a new value for the cache with a strong reference
		 * to its key.
		 * @param key Key of the value
		 * @param value The value to store
		 */
		public KeyValue(final K key, final V value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * Returns the key of the value.
		 * @return Key of the value
		 */
		@SuppressWarnings("unused")
		public K getKey() {
			return key;
		}

		/**
		 * Returns the value.
		 * @return The value
		 */
		public V getValue() {
			return value;
		}
	}

	/** The synchronized WeakHashMap with the SoftReferences */
	private Map<K, SoftReference<KeyValue<K, V>>> map;

	/**
	 * Creates a new and empty SoftCache.
	 */
	public SoftCache() {
		map = Collections.synchronizedMap(
				new WeakHashMap<K, SoftReference<KeyValue<K, V>>>());
	}

	/**
	 * Stores the specified value with the specified key in this cache.
	 * If the cache previously contained a mapping for this key,
	 * the old value is replaced.
	 * @param key Key of the value
	 * @param value The value to store
	 * @return The previous value stored using key, or null if there
	 * was no such previous value.
	 */
	public V put(K key, V value) {
		KeyValue<K, V> newKV = new KeyValue<K, V>(key, value);
		SoftReference<KeyValue<K, V>> newRef =
			new SoftReference<KeyValue<K,V>>(newKV);

		SoftReference<KeyValue<K, V>> oldRef = map.put(key, newRef);

		if (oldRef != null) {
			KeyValue<K, V> oldKV = oldRef.get();
			
			if (oldKV != null) {
				return oldKV.getValue();
			}
		}

		return null;
	}

	/**
	 * Returns the value mapped to with key, or null
	 * if this cache contains no mapping for the key.
	 * @param key Key of the value to return
	 * @return The stored value
	 */
	public V get(K key) {
		SoftReference<KeyValue<K, V>> ref = map.get(key);

		if (ref != null) {
			KeyValue<K, V> kv = ref.get();
			
			if (kv != null) {
				return kv.getValue();
			}
		}

		return null;
	}
}
