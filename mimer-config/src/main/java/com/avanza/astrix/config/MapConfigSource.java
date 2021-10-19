/*
 * Copyright 2020 Avanza Bank AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.avanza.astrix.config;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Map backed {@link DynamicConfigSource} useful in testing. <p>
 *
 * @author Elias Lindholm (elilin)
 */
public class MapConfigSource extends AbstractDynamicConfigSource implements MutableConfigSource {

	private final ConcurrentMap<String, ListenableStringProperty> propertyValues = new ConcurrentHashMap<>();

	public MapConfigSource() {
	}

	public static MapConfigSource of(Map<String, ?> source) {
		MapConfigSource configSource = new MapConfigSource();
		source.forEach((key, value) -> configSource.set(key, value.toString()));
		return configSource;
	}

	public static MapConfigSource of(String key, Object value) {
		MapConfigSource configSource = new MapConfigSource();
		configSource.set(key, value.toString());
		return configSource;
	}

	public static MapConfigSource of(String key1, Object value1,
									 String key2, Object value2) {
		MapConfigSource configSource = new MapConfigSource();
		configSource.set(key1, value1.toString());
		configSource.set(key2, value2.toString());
		return configSource;
	}

	public static MapConfigSource of(String key1, Object value1,
									 String key2, Object value2,
									 String key3, Object value3) {
		MapConfigSource configSource = new MapConfigSource();
		configSource.set(key1, value1.toString());
		configSource.set(key2, value2.toString());
		configSource.set(key3, value3.toString());
		return configSource;
	}

	@Override
	public String get(String propertyName, DynamicPropertyListener<String> propertyChangeListener) {
		ListenableStringProperty dynamicProperty = getProperty(propertyName);
		dynamicProperty.listeners.add(propertyChangeListener);
		return dynamicProperty.value;
	}

	public void set(String propertyName, String value) {
		getProperty(propertyName).set(value);
	}

	@Override
	public <T> void set(Setting<T> setting, T value) {
		set(setting.name(), value == null ? null : value.toString());
	}

	@Override
	public void set(IntSetting setting, int value) {
		set(setting.name(), Integer.toString(value));
	}

	@Override
	public void set(LongSetting setting, long value) {
		set(setting.name(), Long.toString(value));
	}

	@Override
	public void set(BooleanSetting setting, boolean value) {
		set(setting.name(), Boolean.toString(value));
	}

	@Override
	public <T extends Enum<T>> void set(EnumSetting<T> setting, T value) {
		set(setting.name(), value == null ? null : value.name());
	}

	private ListenableStringProperty getProperty(String propertyName) {
		return propertyValues.computeIfAbsent(propertyName, key -> new ListenableStringProperty());
	}

	public void setAll(MapConfigSource config) {
		config.propertyValues.forEach((key, value) -> set(key, value.value));
	}

	@Override
	public String toString() {
		return this.propertyValues.toString();
	}

	private static class ListenableStringProperty {

		private final Queue<DynamicPropertyListener<String>> listeners = new ConcurrentLinkedQueue<>();
		private volatile String value;
		void propertyChanged(String newValue) {
			listeners.forEach(l -> l.propertyChanged(newValue));
		}

		void set(String value) {
			this.value = value;
			propertyChanged(value);
		}

		@Override
		public String toString() {
			return value;
		}
	}

}
