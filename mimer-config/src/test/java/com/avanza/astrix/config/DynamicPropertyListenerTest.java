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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.Test;

class DynamicPropertyListenerTest {
	
	@Test
	void intPropertyListenersAreNotifiedWhenPropertySet() {
		PropertySpy<Integer> propertySpy = new PropertySpy<>();
		DynamicIntProperty prop = new DynamicIntProperty(1);
		prop.addListener(propertySpy);
		
		propertySpy.receivesNoChange();
		prop.set(2);
		propertySpy.receivesPropertyChangeWithValue(2);
	}
	
	@Test
	void intPropertyDoesNotNotifyUnsubscribedListeners() {
		DynamicIntProperty prop = new DynamicIntProperty(1);
		PropertySpy<Integer> propertySpy = new PropertySpy<>();
		prop.addListener(propertySpy);
		prop.set(2);
		propertySpy.receivesPropertyChangeWithValue(2);

		prop.removeListener(propertySpy);
		prop.set(3);
		propertySpy.receivesNoChange();
		
	}
	
	@Test
	void booleanPropertyListenersAreNotifiedWhenPropertySet() {
		PropertySpy<Boolean> propertySpy = new PropertySpy<>();
		DynamicBooleanProperty prop = new DynamicBooleanProperty(false);
		prop.addListener(propertySpy);
		
		propertySpy.receivesNoChange();
		prop.set(true);
		propertySpy.receivesPropertyChangeWithValue(true);
	}
	
	@Test
	void booleanPropertyDoesNotNotifyUnsubscribedListeners() {
		PropertySpy<Boolean> propertySpy = new PropertySpy<>();
		DynamicBooleanProperty prop = new DynamicBooleanProperty(false);
		prop.addListener(propertySpy);
		prop.set(false);
		propertySpy.receivesPropertyChangeWithValue(false);
		
		prop.removeListener(propertySpy);
		prop.set(true);
		propertySpy.receivesNoChange();
	}
	
	@Test
	void longPropertyListenersAreNotifiedWhenPropertySet() {
		PropertySpy<Long> propertySpy = new PropertySpy<>();
		DynamicLongProperty prop = new DynamicLongProperty(1);
		prop.addListener(propertySpy);
		
		propertySpy.receivesNoChange();
		prop.set(2);
		propertySpy.receivesPropertyChangeWithValue(2L);
	}
	
	@Test
	void longPropertyDoesNotNotifyUnsubscribedListeners() {
		PropertySpy<Long> propertySpy = new PropertySpy<>();
		DynamicLongProperty prop = new DynamicLongProperty(1);
		prop.addListener(propertySpy);
		prop.set(2);
		propertySpy.receivesPropertyChangeWithValue(2L);
		
		prop.removeListener(propertySpy);
		prop.set(3);
		propertySpy.receivesNoChange();
	}
	
	@Test
	void stringPropertyListenersAreNotifiedWhenPropertySet() {
		PropertySpy<String> propertySpy = new PropertySpy<>();
		DynamicStringProperty prop = new DynamicStringProperty("1");
		prop.addListener(propertySpy);
		
		propertySpy.receivesNoChange();
		prop.set("2");
		propertySpy.receivesPropertyChangeWithValue("2");
	}
	
	@Test
	void stringPropertyDoesNotNotifyUnsubscribedListeners() {
		PropertySpy<String> propertySpy = new PropertySpy<>();
		DynamicStringProperty prop = new DynamicStringProperty("1");
		prop.addListener(propertySpy);
		prop.set("2");
		propertySpy.receivesPropertyChangeWithValue("2");
		
		prop.removeListener(propertySpy);
		prop.set("3");
		propertySpy.receivesNoChange();
	}

	@Test
	void propertyListenerErrorDoesNotPropagate() {
		DynamicIntProperty prop = new DynamicIntProperty(1);
		prop.addListener(new ThrowingListener<>());
		prop.set(2);
	}

	@Test
	void propertyListenerNotifiedWhenFirstListenerFails() {
		PropertySpy<Integer> propertySpy = new PropertySpy<>();
		DynamicIntProperty prop = new DynamicIntProperty(1);
		prop.addListener(new ThrowingListener<>());
		prop.addListener(propertySpy);

		prop.set(2);
		propertySpy.receivesPropertyChangeWithValue(2);
	}
	
	private static class PropertySpy<T> implements DynamicPropertyListener<T> {
		final Queue<T> notifiedChanges = new LinkedBlockingQueue<>();

		@Override
		public void propertyChanged(T newValue) {
			notifiedChanges.add(newValue);
		}

		void receivesPropertyChangeWithValue(T expected) {
			assertEquals(expected, notifiedChanges.poll(), "Last propertyChangedEvent\n");
		}

		void receivesNoChange() {
			T value = notifiedChanges.poll();
			assertNull(value, "Expected no propertyChanged event, but event with value:\n" + value);
		}
		
	}
	
	private static class ThrowingListener<T> implements DynamicPropertyListener<T> {

		@Override
		public void propertyChanged(T newValue) {
			throw new RuntimeException();
		}
	}

}
