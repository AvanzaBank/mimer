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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

interface PropertyParser<T> {
	
	public static PropertyParser<Boolean> BOOLEAN_PARSER = new BooleanParser();
	public static PropertyParser<String> STRING_PARSER = new StringParser();
	public static PropertyParser<Long> LONG_PARSER = new LongParser();
	public static PropertyParser<Integer> INT_PARSER = new IntParser();
	public static PropertyParser<List<String>> STRING_LIST_PARSER = new StringListParser();
	public static PropertyParser<List<Integer>> INT_LIST_PARSER = new IntListParser();

	T parse(String value);
	
	class BooleanParser implements PropertyParser<Boolean> {
		@Override
		public Boolean parse(String value) {
			if ("false".equalsIgnoreCase(value)) {
				return false;
			}
			if ("true".equalsIgnoreCase(value)) {
				return true;
			}
			throw new IllegalArgumentException("Cannot parse boolean value: \"" + value + "\"");
		}
	};
	
	class StringParser implements PropertyParser<String> {
		@Override
		public String parse(String value) {
			return value;
		}
	};

	class LongParser implements PropertyParser<Long> {
		@Override
		public Long parse(String value) {
			return Long.parseLong(value);
		}
	}
	
	class IntParser implements PropertyParser<Integer> {
		@Override
		public Integer parse(String value) {
			return Integer.parseInt(value);
		}
	}

	class StringListParser implements PropertyParser<List<String>> {
		@Override
		public List<String> parse(String value) {
			return value.isBlank() ? Collections.emptyList() : Arrays.stream(value.split(","))
					.map(String::trim)
					.collect(Collectors.toList());
		}
	}

	class IntListParser implements PropertyParser<List<Integer>> {
		@Override
		public List<Integer> parse(String value) {
			return value.isBlank() ? Collections.emptyList() : Arrays.stream(value.split(","))
					.map(String::trim)
					.map(Integer::valueOf)
					.collect(Collectors.toList());
		}
	}
}
