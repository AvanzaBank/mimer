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

import java.util.function.BooleanSupplier;

/**
 * DynamicProperty of boolean type, see {@link DynamicProperty}. <p>
 * 
 * @author Elias Lindholm (elilin)
 *
 */
public final class DynamicBooleanProperty implements DynamicProperty<Boolean>, BooleanSupplier {
	
	private final ListenerSupport<DynamicPropertyListener<Boolean>> listenerSupport = new ListenerSupport<>();
	private volatile boolean value;
	
	public DynamicBooleanProperty() {
	}
	
	public DynamicBooleanProperty(boolean value) {
		this.value = value;
	}
	
	@Override
	public Boolean getCurrentValue() {
		return value;
	}
	
	public boolean get() {
		return value;
	}

	@Override
	public boolean getAsBoolean() {
		return value;
	}
	
	public void set(boolean value) {
		this.value = value;
		this.listenerSupport.dispatchEvent(l -> l.propertyChanged(value));
	}
	
	@Override
	public void setValue(Boolean value) {
		set(value);
	}
	
	@Override
	public String toString() {
		return Boolean.toString(value);
	}

	@Override
	public void addListener(DynamicPropertyListener<Boolean> listener) {
		listenerSupport.addListener(listener);
	}

	@Override
	public void removeListener(DynamicPropertyListener<Boolean> listener) {
		listenerSupport.removeListener(listener);
	}
}
