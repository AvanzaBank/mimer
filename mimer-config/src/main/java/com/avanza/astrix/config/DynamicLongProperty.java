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

import java.util.function.LongSupplier;

/**
 * DynamicProperty of int type, see {@link DynamicProperty}. <p>
 *  
 * @author Elias Lindholm (elilin)
 *
 */
public final class DynamicLongProperty implements DynamicProperty<Long>, LongSupplier {

	private final ListenerSupport<DynamicPropertyListener<Long>> listenerSupport = new ListenerSupport<>();
	private volatile long value;
	
	public DynamicLongProperty() {
	}
	
	public DynamicLongProperty(long initialValue) {
		this.value = initialValue;
	}
	
	@Override
	public Long getCurrentValue() {
		return value;
	}
	
	public long get() {
		return value;
	}

	@Override
	public long getAsLong() {
		return value;
	}
	
	public void set(long value) {
		this.value = value;
		this.listenerSupport.dispatchEvent(l -> l.propertyChanged(value));
	}
	
	@Override
	public void setValue(Long value) {
		set(value);
	}
	
	@Override
	public String toString() {
		return Long.toString(value);
	}

	@Override
	public void addListener(DynamicPropertyListener<Long> listener) {
		listenerSupport.addListener(listener);
	}

	@Override
	public void removeListener(DynamicPropertyListener<Long> listener) {
		listenerSupport.removeListener(listener);
	}
}
