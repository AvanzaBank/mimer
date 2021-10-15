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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import java.util.AbstractMap.SimpleImmutableEntry;
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
	void shouldCreateFromKeyValuePair() {
		MapConfigSource source = MapConfigSource.of("property1", "value1");

		assertThat(source.get("property1"), equalTo("value1"));
		assertThat(source.get("property2"), nullValue());
	}

	@Test
	void shouldCreateFromMapEntries() {
		MapConfigSource source = MapConfigSource.of(new SimpleImmutableEntry<>("property1", "value1"), new SimpleImmutableEntry<>("property2", "value2"));

		assertThat(source.get("property1"), equalTo("value1"));
		assertThat(source.get("property2"), equalTo("value2"));
	}

}
