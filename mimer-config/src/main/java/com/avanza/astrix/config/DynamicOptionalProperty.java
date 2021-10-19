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

import java.util.Optional;
import java.util.function.Supplier;

public final class DynamicOptionalProperty<T> implements DynamicProperty<T>, Supplier<Optional<T>> {
	private final DynamicProperty<T> delegate;

	public DynamicOptionalProperty(DynamicProperty<T> delegate) {
		this.delegate = requireNonNull(delegate);
	}

	@Override
	public void addListener(DynamicPropertyListener<T> listener) {
		delegate.addListener(listener);
	}

	@Override
	public void removeListener(DynamicPropertyListener<T> listener) {
		delegate.removeListener(listener);
	}

	@Override
	public T getCurrentValue() {
		return delegate.getCurrentValue();
	}

	@Override
	public void setValue(T value) {
		delegate.setValue(value);
	}

	@Override
	public Optional<T> get() {
		return Optional.ofNullable(getCurrentValue());
	}

}
