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

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This is an abstraction for a hierarchical set of configuration sources. Each property is resolved
 * by querying each ConfigurationSource in turn until the property is resolved. <p>
 * 
 * Each {@link DynamicProperty} read is cached in the {@link DynamicConfig} instance. The first time a property
 * with a given name is read, an instance of the given {@link DynamicProperty} type is created, and its value is
 * bound to the underlying configuration sources.
 * 
 * @author Elias Lindholm (elilin)
 *
 */
public final class DynamicConfig {

	private final ConcurrentMap<CacheKey<? extends DynamicProperty<?>>, DynamicProperty<?>> configCache = new ConcurrentHashMap<>();
	private final List<DynamicConfigSource> configSources;
	private final ListenerSupport<DynamicConfigListener> dynamicConfigListenerSupport = new ListenerSupport<>();

	public DynamicConfig(ConfigSource configSource) {
		this(singletonList(configSource));
	}

	public DynamicConfig(List<? extends ConfigSource> configSources) {
		this.configSources = configSources.stream()
				.map(configSource -> configSource instanceof DynamicConfigSource ? (DynamicConfigSource) configSource : new DynamicConfigSourceAdapter(configSource))
				.collect(toList());
	}

	/**
	 * Creates a {@link DynamicConfig} instance resolving configuration properties using
	 * the defined set of {@link ConfigSource}'s (possibly {@link DynamicConfigSource}). <p>
	 */
	public static DynamicConfig create(ConfigSource first, ConfigSource... other) {
		List<ConfigSource> sources = new LinkedList<>();
		sources.add(first);
		sources.addAll(Arrays.asList(other));
		return new DynamicConfig(sources);
	}

	public static DynamicConfig create(List<? extends ConfigSource> sources) {
		return new DynamicConfig(sources);
	}

	private static class DynamicConfigSourceAdapter extends AbstractDynamicConfigSource {
		private final ConfigSource configSource;
		public DynamicConfigSourceAdapter(ConfigSource configSource) {
			this.configSource = configSource;
		}
		@Override
		public String get(String propertyName, DynamicPropertyListener<String> propertyChangeListener) {
			return configSource.get(propertyName);
		}

		@Override
		public String toString() {
			return this.configSource.toString();
		}

	}

	/**
	 * Reads a property of String type.
	 */
	public DynamicStringProperty getStringProperty(String name, String defaultValue) {
		return getProperty(name, DynamicStringProperty.class, defaultValue, PropertyParser.STRING_PARSER);
	}

	public DynamicOptionalProperty<String> getOptionalStringProperty(String name) {
		return new DynamicOptionalProperty<>(getStringProperty(name, null));
	}

	public DynamicBooleanProperty getBooleanProperty(String name, boolean defaultValue) {
		return getProperty(name, DynamicBooleanProperty.class, defaultValue, PropertyParser.BOOLEAN_PARSER);
	}

	public DynamicLongProperty getLongProperty(String name, long defaultValue) {
		return getProperty(name, DynamicLongProperty.class, defaultValue, PropertyParser.LONG_PARSER);
	}

	public DynamicOptionalProperty<Long> getOptionalLongProperty(String name) {
		return new DynamicOptionalProperty<>(getProperty(name, DynamicNullableLongProperty.class, null, PropertyParser.LONG_PARSER));
	}

	public DynamicIntProperty getIntProperty(String name, int defaultValue) {
		return getProperty(name, DynamicIntProperty.class, defaultValue, PropertyParser.INT_PARSER);
	}

	public DynamicOptionalProperty<Integer> getOptionalIntProperty(String name) {
		return new DynamicOptionalProperty<>(getProperty(name, DynamicNullableIntProperty.class, null, PropertyParser.INT_PARSER));
	}

	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> DynamicEnumProperty<T> getEnumProperty(String name, Class<T> enumClass, T defaultValue) {
		return getProperty(name, DynamicEnumProperty.class, defaultValue, PropertyParser.enumParser(enumClass));
	}

	public <T extends Enum<T>> DynamicOptionalProperty<T> getOptionalEnumProperty(String name, Class<T> enumClass) {
		return new DynamicOptionalProperty<>(getEnumProperty(name, enumClass, null));
	}

	@SuppressWarnings("unchecked")
	public DynamicListProperty<String> getStringListProperty(String name, List<String> defaultValue) {
		return getProperty(name, DynamicListProperty.class, defaultValue, PropertyParser.STRING_LIST_PARSER);
	}

	@SuppressWarnings("unchecked")
	public DynamicListProperty<Integer> getIntListProperty(String name, List<Integer> defaultValue) {
		return getProperty(name, DynamicListProperty.class, defaultValue, PropertyParser.INT_LIST_PARSER);
	}

	@SuppressWarnings("unchecked")
	public DynamicListProperty<Long> getLongListProperty(String name, List<Long> defaultValue) {
		return getProperty(name, DynamicListProperty.class, defaultValue, PropertyParser.LONG_LIST_PARSER);
	}

	@SuppressWarnings("unchecked")
	public DynamicListProperty<Boolean> getBooleanListProperty(String name, List<Boolean> defaultValue) {
		return getProperty(name, DynamicListProperty.class, defaultValue, PropertyParser.BOOLEAN_LIST_PARSER);
	}

	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> DynamicListProperty<T> getEnumListProperty(String name, Class<T> enumClass, List<T> defaultValue) {
		return getProperty(name, DynamicListProperty.class, defaultValue, PropertyParser.enumListParser(enumClass));
	}

	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> DynamicSetProperty<T> getEnumSetProperty(String name, Class<T> enumClass, Set<T> defaultValue) {
		return getProperty(name, DynamicSetProperty.class, defaultValue, PropertyParser.enumSetParser(enumClass));
	}

	private <T, P extends DynamicProperty<T>> P getProperty(String name, Class<P> propertyType, T defaultValue, PropertyParser<T> propertyParser) {
		return getOrCreate(propertyType, name, () ->
				bindPropertyToConfigurationSources(name, propertyType.getDeclaredConstructor().newInstance(), defaultValue, propertyParser));
	}

	private <T, P extends DynamicProperty<T>> P bindPropertyToConfigurationSources(String name, P property, T defaultValue, PropertyParser<T> propertyParser) {
		DynamicPropertyChain<T> chain = createPropertyChain(name, defaultValue, propertyParser);
		chain.bindTo(property::setValue);
		notifyPropertyCreated(name, property.getCurrentValue());
		property.addListener(newValue -> notifyPropertyChanged(name, newValue));
		return property;
	}

	private <T> void notifyPropertyCreated(String propertyName, T initialValue) {
		dynamicConfigListenerSupport.dispatchEvent(listener -> listener.propertyCreated(propertyName, initialValue));
	}

	private <T> void notifyPropertyChanged(String propertyNAme, T newValue) {
		dynamicConfigListenerSupport.dispatchEvent(listener -> listener.propertyChanged(propertyNAme, newValue));
	}

	private <T> DynamicPropertyChain<T> createPropertyChain(String name, T defaultValue, PropertyParser<T> propertyParser) {
		DynamicPropertyChain<T> chain = DynamicPropertyChain.createWithDefaultValue(defaultValue, propertyParser);
		for (DynamicConfigSource configSource : configSources) {
			DynamicConfigProperty<T> newValueInChain = chain.appendValue();
			// bind newValueInChain to configuration property in source
			String propertyValue = configSource.get(name, newValueInChain);
			newValueInChain.set(propertyValue);
		}
		return chain;
	}

	public static DynamicConfig merged(DynamicConfig dynamicConfigA, DynamicConfig dynamicConfigB) {
		List<ConfigSource> merged = new ArrayList<>(dynamicConfigA.configSources.size() + dynamicConfigB.configSources.size());
		merged.addAll(dynamicConfigA.configSources);
		merged.addAll(dynamicConfigB.configSources);
		return new DynamicConfig(merged);
	}

	@Override
	public String toString() {
		return this.configSources.toString();
	}

	/**
	 * Adds a listener to this DynamicConfig instance.
	 * 
	 * The listener receives a "propertyChanged" event each time the resolved
	 * value of a DynamicProperty read
	 * from this instance changes.
	 * 
	 * The listener receives a "propertyCreated" each time a new property
	 * is created in this {@link DynamicConfig} instance (i.e. the first time
	 * a property with a given name is read).
	 */
	public void addListener(DynamicConfigListener l) {
		this.dynamicConfigListenerSupport.addListener(l);
	}

	@SuppressWarnings("unchecked")
	private <T extends DynamicProperty<?>> T getOrCreate(Class<T> type, String name, Callable<T> objectFactory) {
		return (T) configCache.computeIfAbsent(new CacheKey<>(type, name), key -> {
			try {
				return objectFactory.call();
			} catch (RuntimeException exception) {
				throw exception;
			} catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		});
	}

	private static final class CacheKey<T> {
		private final Class<T> type;
		private final String name;

		public CacheKey(Class<T> type, String name) {
			this.type = requireNonNull(type);
			this.name = name.intern();
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (other == null || getClass() != other.getClass()) {
				return false;
			}
			CacheKey<?> cacheKey = (CacheKey<?>) other;
			return type.equals(cacheKey.type) && name.equals(cacheKey.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(type, name);
		}
	}

}
