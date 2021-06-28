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

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.joining;

import java.util.Collections;
import java.util.Set;

/**
 * DynamicProperty of Set<T> type, see {@link DynamicProperty}.
 * <p>
 *
 * Property values are parsed as a comma separated string of T
 */
public final class DynamicSetProperty<T> extends AbstractDynamicProperty<Set<T>> {

	public DynamicSetProperty() {
		super(Collections.emptySet());
	}

	public DynamicSetProperty(Set<T> initialValue) {
		super(unmodifiableSet(initialValue));
	}

	public void set(Set<T> value) {
		setValue(value);
	}

	@Override
	public void setValue(Set<T> value) {
		super.setValue(unmodifiableSet(value));
	}

	@Override
	public String toString() {
		return getCurrentValue().stream().map(String::valueOf).collect(joining(","));
	}

}