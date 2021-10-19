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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.Test;



class DynamicPropertyChainTest {
	
	
	private static final String DEFAULT_VALUE = "defaultFoo";
	
	private final Queue<String> propertyChanges = new LinkedList<>();
	private final DynamicPropertyChainListener<String> listener = propertyChanges::add;
	private final DynamicPropertyChain<String> propertyChain = DynamicPropertyChain.createWithDefaultValue(DEFAULT_VALUE, new PropertyParser.StringParser());
	
	@Test
	void listenerIsNotifiedWithDefaultValueWhenRegisteredIfNoPropertiesExistsInChain() {
		propertyChain.bindTo(listener);
		assertEquals(DEFAULT_VALUE, propertyChanges.poll());
	}
	
	@Test
	void listenerIsRegisteredWithNewValueWhenDynamicPropertyIsSet() {
		DynamicConfigProperty<String> dynamicProperty = propertyChain.appendValue();
		propertyChain.bindTo(listener);
		assertEquals(DEFAULT_VALUE, propertyChanges.poll());
		dynamicProperty.set("Foo");
		assertEquals("Foo", propertyChanges.poll());
	}
	
	@Test
	void chainReturnsToDefaultValueWhenAllPropertiesInChainAreNull() {
		DynamicConfigProperty<String> dynamicProperty = propertyChain.appendValue();
		propertyChain.bindTo(listener);
		assertEquals(DEFAULT_VALUE, propertyChanges.poll());
		
		dynamicProperty.set("Foo");
		assertEquals("Foo", propertyChanges.poll());
		
		dynamicProperty.set(null);
		assertEquals(DEFAULT_VALUE, propertyChanges.poll());
	}
	
	@Test
	void resolvesPropertyInOrder() {
		DynamicConfigProperty<String> firstPropertyInChain = propertyChain.appendValue();
		DynamicConfigProperty<String> secondPropertyInChain = propertyChain.appendValue();
		propertyChain.bindTo(listener);
		secondPropertyInChain.set("Foo");
		assertEquals(DEFAULT_VALUE, propertyChanges.poll());
		
		secondPropertyInChain.set("Foo");
		assertEquals("Foo", propertyChanges.poll());
		
		firstPropertyInChain.set("Bar");
		assertEquals("Bar", propertyChanges.poll());
		
		secondPropertyInChain.set("Foo2");
		assertNull(propertyChanges.poll());
	}
	
	@Test
	void notifiesListener() {
		DynamicConfigProperty<String> firstPropertyInChain = propertyChain.appendValue();
		DynamicConfigProperty<String> secondPropertyInChain = propertyChain.appendValue();
		propertyChain.bindTo(listener);
		assertEquals(DEFAULT_VALUE, propertyChanges.poll());
		
		secondPropertyInChain.set("Foo");
		assertEquals("Foo", propertyChanges.poll());
		firstPropertyInChain.set("Bar");
		assertEquals("Bar", propertyChanges.poll());
		
		secondPropertyInChain.set("Foo2");
		assertNull(propertyChanges.poll());
		assertTrue(propertyChanges.isEmpty());
	}
	
	@Test
	void listenerShouldOnlyBeNotifiedWhenUnderlyingPropertyActuallyChanges() {
		DynamicConfigProperty<String> firstPropertyInChain = propertyChain.appendValue();
		DynamicConfigProperty<String> secondPropertyInChain = propertyChain.appendValue();
		propertyChain.bindTo(listener);
		assertEquals(DEFAULT_VALUE, propertyChanges.poll());
		
		secondPropertyInChain.set("Foo");
		assertEquals("Foo", propertyChanges.poll());
		firstPropertyInChain.set("Bar");
		assertEquals("Bar", propertyChanges.poll());
		
		secondPropertyInChain.set("Bar");
		assertNull(propertyChanges.poll());
		assertTrue(propertyChanges.isEmpty());
	}
	
	@Test
	void listenerShouldBeNotifiedWithNewValueWhenCurrentResolvedConfigurationPropertyIsClearedAndPropertyWithLowerPrecedenceExists() {
		DynamicConfigProperty<String> firstPropertyInChain = propertyChain.appendValue();
		DynamicConfigProperty<String> secondPropertyInChain = propertyChain.appendValue();
		propertyChain.bindTo(listener);
		assertEquals(DEFAULT_VALUE, propertyChanges.poll());
		
		// Setting second property should trigger event with new value
		secondPropertyInChain.set("2");
		assertEquals("2", propertyChanges.poll());
		
		// Setting first property should trigger event with new value
		firstPropertyInChain.set("1");
		assertEquals("1", propertyChanges.poll());
		
		// Clearing first property should trigger event with value from second prop
		firstPropertyInChain.set(null);
		assertEquals("2", propertyChanges.poll());
	}
	
	@Test
	void getsNotifiedAboutResolvedValueWhenSetBeforeRegisteringListener() {
		DynamicConfigProperty<String> propertyInChain = propertyChain.appendValue();
		propertyInChain.set("2");
		
		propertyChain.bindTo(propertyChanges::add);
		assertEquals("2", propertyChanges.poll());
	}
	

}
