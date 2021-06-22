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

import static java.util.Objects.requireNonNull;

/**
 * A Setting of Enum type, see {@link Setting} <p>
 */
public class EnumSetting<T extends Enum<T>> implements Setting<T> {

	private final String name;
	private final Class<T> enumType;
	private final T defaultValue;

	public EnumSetting(String name, Class<T> enumType, T defaultValue) {
		this.name = requireNonNull(name);
		this.enumType = requireNonNull(enumType);
		this.defaultValue = defaultValue;
	}

	public static <T extends Enum<T>> EnumSetting<T> create(String name, Class<T> enumType, T defaultValue) {
		return new EnumSetting<>(name, enumType, defaultValue);
	}
	
	@Override
	public DynamicEnumProperty<T> getFrom(DynamicConfig config) {
		return config.getEnumProperty(name, enumType, defaultValue);
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public T defaultValue() {
		return defaultValue;
	}

}
