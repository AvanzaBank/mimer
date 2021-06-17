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

/**
 * DynamicProperty of List<Integer> type, see {@link DynamicProperty}. <p>
 * 
 * Property values are parsed as a comma separated string of integers
 */
public final class DynamicIntListProperty implements DynamicProperty<List<Integer>> {

	private final ListenerSupport<DynamicPropertyListener<List<Integer>>> listenerSupport = new ListenerSupport<>();
	private volatile List<Integer> value;
	
	public DynamicIntListProperty(List<Integer> initialValue) {
		this.value = initialValue;
	}
	
	public DynamicIntListProperty() {
		this.value = Collections.emptyList();
	}
	
	@Override
	public List<Integer> getCurrentValue() {
		return this.value;
	}
	
	public List<Integer> get() {
		return this.value;
	}
	
	public void set(List<Integer> value) {
		this.value = value;
		this.listenerSupport.dispatchEvent(l -> l.propertyChanged(value));
	}
	
	@Override
	public void setValue(List<Integer> value) {
		set(value);
	}
	
	@Override
	public String toString() {
		return this.value.stream().map(String::valueOf).collect(joining(","));
	}

	@Override
	public void addListener(DynamicPropertyListener<List<Integer>> listener) {
		listenerSupport.addListener(listener);
	}

	@Override
	public void removeListener(DynamicPropertyListener<List<Integer>> listener) {
		listenerSupport.removeListener(listener);
	}
	
}
