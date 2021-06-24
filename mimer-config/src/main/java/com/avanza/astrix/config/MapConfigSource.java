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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
		String stringRepresentation = value != null ? value.toString() : null;
		getProperty(setting.name()).set(stringRepresentation);
	}

	@Override
	public void set(LongSetting setting, long value) {
		getProperty(setting.name()).set(Long.toString(value));
	}

	@Override
	public void set(BooleanSetting setting, boolean value) {
		getProperty(setting.name()).set(Boolean.toString(value));
	}

	private ListenableStringProperty getProperty(String propertyName) {
		ListenableStringProperty result = propertyValues.get(propertyName);
		if (result != null) {
			return result;
		}
		propertyValues.putIfAbsent(propertyName, new ListenableStringProperty());
		return propertyValues.get(propertyName);
	}

	public void setAll(MapConfigSource config) {
		for (Map.Entry<String, ListenableStringProperty> entry : config.propertyValues.entrySet()) {
			set(entry.getKey(), entry.getValue().value);
		}
	}

	@Override
	public String toString() {
		return this.propertyValues.toString();
	}

	private static class ListenableStringProperty {

		final List<DynamicPropertyListener<String>> listeners = new CopyOnWriteArrayList<>();
		volatile String value;

		void propertyChanged(String newValue) {
			for (DynamicPropertyListener<String> l : listeners) {
				l.propertyChanged(newValue);
			}
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
