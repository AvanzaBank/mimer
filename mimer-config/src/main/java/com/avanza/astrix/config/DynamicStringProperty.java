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

/**
 * DynamicProperty of String type, see {@link DynamicProperty}. <p>
 *
 * @author Elias Lindholm (elilin)
 */
public final class DynamicStringProperty extends AbstractDynamicProperty<String> {

	public DynamicStringProperty(String initialValue) {
		super(initialValue);
	}

	public DynamicStringProperty() {
		super(null);
	}

	@Override
	public String get() {
		return super.get();
	}

	public void set(String value) {
		setValue(value);
	}

	@Override
	public String toString() {
		return getCurrentValue();
	}

}
