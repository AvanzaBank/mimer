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
 * DynamicProperty of List<String> type, see {@link DynamicProperty}. <p>
 * 
 * Property values are parsed as a comma separated string 
 *
 */
public final class DynamicStringListProperty implements DynamicProperty<List<String>> {

	private final ListenerSupport<DynamicPropertyListener<List<String>>> listenerSupport = new ListenerSupport<>();
	private volatile List<String> value;
	
	public DynamicStringListProperty(List<String> initialValue) {
		this.value = initialValue;
	}
	
	public DynamicStringListProperty() {
		this.value = Collections.emptyList();
	}
	
	@Override
	public List<String> getCurrentValue() {
		return this.value;
	}
	
	public List<String> get() {
		return this.value;
	}
	
	public void set(List<String> value) {
		this.value = value;
		this.listenerSupport.dispatchEvent(l -> l.propertyChanged(value));
	}
	
	@Override
	public void setValue(List<String> value) {
		set(value);
	}
	
	@Override
	public String toString() {
		return this.value.stream().collect(joining(","));
	}

	@Override
	public void addListener(DynamicPropertyListener<List<String>> listener) {
		listenerSupport.addListener(listener);
	}

	@Override
	public void removeListener(DynamicPropertyListener<List<String>> listener) {
		listenerSupport.removeListener(listener);
	}
	
}
