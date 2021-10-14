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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

class MapConfigSourceTest {

	@Test
	void shouldCreateFromMap() {
		MapConfigSource source = MapConfigSource.of(new HashMap<String, Object>() {{
			put("property1", "value1");
			put("property2", "value2");
		}});

		assertThat(source.get("property1"), equalTo("value1"));
		assertThat(source.get("property2"), equalTo("value2"));
	}

	@Test
	void shouldCreateFromSingleKeyValuePair() {
		MapConfigSource source = MapConfigSource.of("property1", "value1");

		assertThat(source.get("property1"), equalTo("value1"));
		assertThat(source.get("property2"), nullValue());
	}

	@Test
	void shouldCreateFromTwoKeyValuePairs() {
		MapConfigSource source = MapConfigSource.of("property1", "value1",
													"property2", "value2");

		assertThat(source.get("property1"), equalTo("value1"));
		assertThat(source.get("property2"), equalTo("value2"));
	}

	@Test
	void shouldCreateFromThreeKeyValuePairs() {
		MapConfigSource source = MapConfigSource.of("property1", "value1",
													"property2", "value2",
													"property3", "value3");

		assertThat(source.get("property1"), equalTo("value1"));
		assertThat(source.get("property2"), equalTo("value2"));
		assertThat(source.get("property3"), equalTo("value3"));
	}

	@Test
	void shouldGetNewDefaultValuesEachTime() {
		// Arrange
		final DynamicConfig dynamicConfig = DynamicConfig.create(new MapConfigSource());

		// Act
		final String v1 = dynamicConfig.getStringProperty("key", "first-default").get();
		final String v2 = dynamicConfig.getStringProperty("key", "second-default").get();

		// Assert
		assertThat(v1, equalTo("first-default"));
		assertThat(v2, equalTo("second-default"));
	}

	@Test
	void shouldGetNewDefaultValuesEachTimeForIntListProperties() {
		// Arrange
		final DynamicConfig dynamicConfig = DynamicConfig.create(new MapConfigSource());

		// Act
		final DynamicListProperty<Integer> emptyList1 = dynamicConfig.getIntListProperty("key", Collections.emptyList());
		final DynamicListProperty<Integer> emptyList2 = dynamicConfig.getIntListProperty("key", new ArrayList<>());
		final DynamicListProperty<Integer> list = dynamicConfig.getIntListProperty("key", Arrays.asList(1, 2));

		// Assert
		assertThat(emptyList1, sameInstance(emptyList2));
		assertThat(emptyList1.get(), equalTo(Collections.emptyList()));
		assertThat(list.get(), containsInAnyOrder(1, 2));
	}
}
