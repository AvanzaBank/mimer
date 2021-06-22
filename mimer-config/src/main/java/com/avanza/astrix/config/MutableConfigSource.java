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
 * Most used by test-utility classes to allow setting configuration programmatically.
 * 
 * 
 * @author Elias Lindholm (elilin)
 *
 */
public interface MutableConfigSource {

	<T> void set(Setting<T> setting, T value);

	default void set(IntSetting setting, int value) {
		set((Setting<Integer>) setting, value);
	}

	default void set(LongSetting setting, long value) {
		set((Setting<Long>) setting, value);
	}

	default void set(BooleanSetting setting, boolean value) {
		set((Setting<Boolean>) setting, value);
	}

	default <T extends Enum<T>> void set(EnumSetting<T> setting, T value) {
		set((Setting<T>) setting, value);
	}

}
