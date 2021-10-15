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

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.jupiter.api.Test;

class DynamicConfigTest {


	private final MapConfigSource firstSource = new MapConfigSource();
	private final MapConfigSource secondSource = new MapConfigSource();
	private final DynamicConfig dynamicConfig = new DynamicConfig(Arrays.asList(firstSource, secondSource));

	@Test
	void propertyIsResolvedToFirstOccurrenceInConfigSources() {
		DynamicStringProperty stringProperty = dynamicConfig.getStringProperty("foo", "defaultFoo");

		secondSource.set("foo", "secondValue");
		assertEquals("secondValue", stringProperty.get());

		firstSource.set("foo", "firstValue");
		assertEquals("firstValue", stringProperty.get());

		secondSource.set("foo", "secondNewValue");
		assertEquals("firstValue", stringProperty.get());
	}

	@Test
	void propertyValueReturnsToDefaultValueWhenConfigSourceValueIsRemoved() {
		DynamicStringProperty stringProperty = dynamicConfig.getStringProperty("foo", "defaultFoo");

		firstSource.set("foo", "firstValue");
		assertEquals("firstValue", stringProperty.get());

		firstSource.set("foo", null);
		assertEquals("defaultFoo", stringProperty.get());

	}

	@Test
	void booleanProperty() {
		DynamicBooleanProperty booleanProperty = dynamicConfig.getBooleanProperty("foo", false);

		secondSource.set("foo", "true");
		assertTrue(booleanProperty.get());

		firstSource.set("foo", "false");
		assertFalse(booleanProperty.get());
	}

	@Test
	void intProperty() {
		DynamicIntProperty intProperty = dynamicConfig.getIntProperty("foo", 0);
		assertEquals(0, intProperty.get());

		secondSource.set("foo", "2");
		assertEquals(2, intProperty.get());

		firstSource.set("foo", "1");
		assertEquals(1, intProperty.get());
	}

	@Test
	void enumProperty() {
		DynamicProperty<MyEnum> enumProperty = dynamicConfig.getEnumProperty("myEnum", MyEnum.class, MyEnum.FIRST);

		assertEquals(MyEnum.FIRST, enumProperty.getCurrentValue());

		secondSource.set("myEnum", "SECOND");
		assertEquals(MyEnum.SECOND, enumProperty.getCurrentValue());

		firstSource.set("myEnum", "third");
		assertEquals(MyEnum.THIRD, enumProperty.getCurrentValue());

		firstSource.set("myEnum", "MALFORMED");
		assertEquals(MyEnum.THIRD, enumProperty.getCurrentValue());
	}

	@Test
	void stringListProperty() {
		DynamicListProperty<String> property = dynamicConfig.getStringListProperty("foo", emptyList());
		assertThat(property.get(), empty());

		secondSource.set("foo", "1,2,   3   ,4");
		assertEquals(Arrays.asList("1", "2", "3", "4"), property.get());

		firstSource.set("foo", "1");
		assertEquals(singletonList("1"), property.get());

		firstSource.set("foo", "");
		assertThat(property.get(), empty());
	}

	@Test
	void intListProperty() {
		DynamicListProperty<Integer> property = dynamicConfig.getIntListProperty("foo", emptyList());
		assertThat(property.get(), empty());

		secondSource.set("foo", "1, 2    ,3,4,");
		assertEquals(Arrays.asList(1, 2, 3, 4), property.get());

		firstSource.set("foo", "1");
		assertEquals(singletonList(1), property.get());

		firstSource.set("foo", "");
		assertThat(property.get(), empty());

		firstSource.set("foo", "unparseable value,2,3,4");
		assertThat(property.get(), empty());
	}

	@Test
	void longListProperty() {
		DynamicListProperty<Long> property = dynamicConfig.getLongListProperty("foo", emptyList());
		assertThat(property.get(), empty());

		secondSource.set("foo", "1, 2    ,3,4,");
		assertEquals(Arrays.asList(1L, 2L, 3L, 4L), property.get());

		firstSource.set("foo", "1");
		assertEquals(singletonList(1L), property.get());

		firstSource.set("foo", "");
		assertThat(property.get(), empty());

		firstSource.set("foo", "unparseable value,2,3,4");
		assertThat(property.get(), empty());
	}

	@Test
	void booleanListProperty() {
		DynamicListProperty<Boolean> property = dynamicConfig.getBooleanListProperty("foo", emptyList());
		assertThat(property.get(), empty());

		secondSource.set("foo", "true, false,  false, TRUE");
		assertEquals(Arrays.asList(true, false, false, true), property.get());

		firstSource.set("foo", "true");
		assertEquals(singletonList(true), property.get());

		firstSource.set("foo", "");
		assertThat(property.get(), empty());

		firstSource.set("foo", "unparseable value,false, true");
		assertThat(property.get(), empty());
	}

	@Test
	void enumSetProperty() {
		DynamicSetProperty<MyEnum> property = dynamicConfig.getEnumSetProperty("myEnumSet", MyEnum.class, emptySet());
		assertThat(property.get(), empty());

		secondSource.set("myEnumSet", "first,  tHiRd, SECOND  ");
		assertThat(property.get(), contains(MyEnum.FIRST, MyEnum.THIRD, MyEnum.SECOND));

		firstSource.set("myEnumSet", "FIRST");
		assertEquals(singleton(MyEnum.FIRST), property.get());

		firstSource.set("myEnumSet", "");
		assertThat(property.get(), empty());

		firstSource.set("myEnumSet", "unparseable value,second, FIRST");
		assertThat(property.get(), empty());
	}

	@Test
	void unparsableBooleanPropertiesAreIgnored() {
		DynamicBooleanProperty booleanProperty = dynamicConfig.getBooleanProperty("foo", false);

		secondSource.set("foo", "true");
		assertTrue(booleanProperty.get());

		firstSource.set("foo", "true[L]");
		assertTrue(booleanProperty.get());

		secondSource.set("foo", "MALFORMED");
		assertTrue(booleanProperty.get());

		secondSource.set("foo", "false");
		assertFalse(booleanProperty.get());

		firstSource.set("foo", "true");
		assertTrue(booleanProperty.get());

		firstSource.set("foo", "MALFORMED");
		assertTrue(booleanProperty.get());
	}

	@Test
	void unparsableIntPropertiesAreIgnored() {
		DynamicIntProperty intProperty = dynamicConfig.getIntProperty("foo", 0);

		secondSource.set("foo", "2d");
		assertEquals(0, intProperty.get());

		secondSource.set("foo", "1");
		assertEquals(1, intProperty.get());

		firstSource.set("foo", "1s");
		assertEquals(1, intProperty.get());
	}

	@Test
	void merge() {
		MapConfigSource thirdSource = new MapConfigSource();
		DynamicConfig dynamicConfigB = new DynamicConfig(singletonList(thirdSource));

		DynamicConfig merged = DynamicConfig.merged(dynamicConfig, dynamicConfigB);

		assertEquals("defaultValue", merged.getStringProperty("foo", "defaultValue").get());

		thirdSource.set("foo", "thirdValue");
		assertEquals("thirdValue", merged.getStringProperty("foo", "defaultValue").get());

		firstSource.set("foo", "firstValue");
		assertEquals("firstValue", merged.getStringProperty("foo", "defaultValue").get());

		secondSource.set("foo", "secondValue");
		assertEquals("firstValue", merged.getStringProperty("foo", "defaultValue").get());
	}

	@Test
	void propertyListenerSupport_StringType() {
		firstSource.set("foo", "1");
		secondSource.set("foo", "2");
		Queue<String> events = new LinkedBlockingQueue<>();
		DynamicStringProperty prop = dynamicConfig.getStringProperty("foo", "-");
		prop.addListener(events::add);

		assertEquals("1", prop.getCurrentValue());
		assertNull(events.poll(), "Listener should not be notified before a property actually changes");
		secondSource.set("foo", "22");
		assertNull(events.poll(), "Listener should not be notified when a property changes but resolved property has the same value");

		firstSource.set("foo", "11");
		assertEquals( "11", events.poll(), "Listener should be notified when property resolved changes");

		secondSource.set("foo", "11");
		assertNull(events.poll());
	}

	@Test
	void propertyListenerSupport_IntType() {
		firstSource.set("intProp", "1");
		DynamicIntProperty prop = dynamicConfig.getIntProperty("intProp", 0);
		Queue<Integer> events = new LinkedBlockingQueue<>();
		prop.addListener(events::add);

		assertEquals(1, prop.getCurrentValue().intValue());
		assertNull(events.poll(), "Listener should not be notified before a property actually changes");

		firstSource.set("intProp", "11");
		assertEquals(Integer.valueOf(11), events.poll(), "Listener should be notified when property resolved changes");
	}

	@Test
	void propertyListenerSupport_LongType() {
		firstSource.set("longProp", "1");
		DynamicLongProperty prop = dynamicConfig.getLongProperty("longProp", 0);
		Queue<Long> events = new LinkedBlockingQueue<>();
		prop.addListener(events::add);

		assertEquals(1, prop.getCurrentValue().longValue());
		assertNull(events.poll(), "Listener should not be notified before a property actually changes");

		firstSource.set("longProp", "11");
		assertEquals(Long.valueOf(11), events.poll(), "Listener should be notified when property resolved changes");
	}

	@Test
	void propertyListenerSupport_BooleanType() {
		firstSource.set("booleanProp", "true");
		DynamicBooleanProperty prop = dynamicConfig.getBooleanProperty("booleanProp", false);
		Queue<Boolean> events = new LinkedBlockingQueue<>();
		prop.addListener(events::add);

		assertEquals(Boolean.TRUE, prop.getCurrentValue());
		assertNull(events.poll(), "Listener should not be notified before a property actually changes");

		firstSource.set("booleanProp", "false");
		assertEquals(Boolean.FALSE, events.poll(), "Listener should be notified when property resolved changes");
	}

	@Test
	void globalPropertyListenerIsNotifiedWhenNewPropertyIsCreated() {
		firstSource.set("booleanProp", "true");
		DynamicBooleanProperty prop = dynamicConfig.getBooleanProperty("booleanProp", false);
		Queue<Boolean> events = new LinkedBlockingQueue<>();
		prop.addListener(events::add);

		assertEquals(Boolean.TRUE, prop.getCurrentValue());
		assertNull( events.poll(), "Listener should not be notified before a property actually changes");

		firstSource.set("booleanProp", "false");
		assertEquals(Boolean.FALSE, events.poll(), "Listener should be notified when property resolved changes");
	}

	@Test
	void globalConfigListenerSupport_PropertyCreatedEvents() {
		class Prop {
			private final String name;
			private final Object val;
			Prop(String name, Object val) {
				this.name = name;
				this.val = val;
			}
			@Override
			public String toString() {
				return name + "=" + val;
			}
		}
		MapConfigSource firstSource = new MapConfigSource();
		firstSource.set("foo", "1");
		MapConfigSource secondSource = new MapConfigSource();
		secondSource.set("foo", "2");

		DynamicConfig config = DynamicConfig.create(firstSource, secondSource);
		Queue<Prop> propertyCreatedEvents = new LinkedBlockingQueue<>();
		config.addListener(new DynamicConfigListener() {
			@Override
			public void propertyCreated(String propertyName, Object initialValue) {
				propertyCreatedEvents.add(new Prop(propertyName, initialValue));
			}
		});

		assertNull(propertyCreatedEvents.poll());
		config.getStringProperty("foo", "default");
		assertEquals("foo", propertyCreatedEvents.poll().name);

		// Should return cached value and not fire created event
		config.getStringProperty("foo", "default");
		assertNull(propertyCreatedEvents.poll());
	}

	@Test
	void globalConfigListenerSupport_PropertyUpdateEvents() {
		class Prop {
			private final String name;
			private final Object val;
			Prop(String name, Object val) {
				this.name = name;
				this.val = val;
			}
			@Override
			public String toString() {
				return name + "=" + val;
			}
		}
		MapConfigSource firstSource = new MapConfigSource();
		firstSource.set("foo", "1");
		MapConfigSource secondSource = new MapConfigSource();
		secondSource.set("foo", "2");

		DynamicConfig config = DynamicConfig.create(firstSource, secondSource);
		Queue<Prop> propertyChangeEvents = new LinkedBlockingQueue<>();
		config.addListener(new DynamicConfigListener() {
			@Override
			public void propertyChanged(String propertyName, Object newValue) {
				propertyChangeEvents.add(new Prop(propertyName, newValue));
			}
		});

		assertNull(propertyChangeEvents.poll());
		config.getStringProperty("foo", "default");
		assertNull(propertyChangeEvents.poll());

		firstSource.set("foo", "11");
		Prop prop = propertyChangeEvents.poll();
		assertNotNull(prop);
		assertEquals("foo", prop.name);
		assertEquals("11", prop.val);

		secondSource.set("foo", "11");
		assertNull(propertyChangeEvents.poll());
	}


	private enum MyEnum {
		FIRST, SECOND, THIRD
	}

}
