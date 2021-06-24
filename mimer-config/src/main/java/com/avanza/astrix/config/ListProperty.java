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

import static java.util.stream.Collectors.joining;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ListProperty<T> implements DynamicProperty<List<T>>, Supplier<List<T>> {

	private final ListenerSupport<DynamicPropertyListener<List<T>>> listenerSupport = new ListenerSupport<>();
	private volatile List<T> value;

	public ListProperty() {
		this(Collections.emptyList());
	}

	public ListProperty(List<T> initialValue) {
		this.value = Collections.unmodifiableList(initialValue);
	}

	@Override
	public List<T> getCurrentValue() {
		return get();
	}

	@Override
	public List<T> get() {
		return this.value;
	}

	public void set(List<T> value) {
		this.value = Collections.unmodifiableList(value);
		this.listenerSupport.dispatchEvent(l -> l.propertyChanged(this.value));
	}

	@Override
	public void setValue(List<T> value) {
		set(value);
	}

	@Override
	public String toString() {
		return this.value.stream().map(String::valueOf).collect(joining(","));
	}

	@Override
	public void addListener(DynamicPropertyListener<List<T>> listener) {
		listenerSupport.addListener(listener);
	}

	@Override
	public void removeListener(DynamicPropertyListener<List<T>> listener) {
		listenerSupport.removeListener(listener);
	}

}