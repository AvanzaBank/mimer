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
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collector;

interface PropertyParser<T> {

	PropertyParser<Boolean> BOOLEAN_PARSER = new BooleanParser();
	PropertyParser<String> STRING_PARSER = new StringParser();
	PropertyParser<Long> LONG_PARSER = new LongParser();
	PropertyParser<Integer> INT_PARSER = new IntParser();
	PropertyParser<List<Boolean>> BOOLEAN_LIST_PARSER = new ListParser<>(BOOLEAN_PARSER);
	PropertyParser<List<String>> STRING_LIST_PARSER = new ListParser<>(STRING_PARSER);
	PropertyParser<List<Long>> LONG_LIST_PARSER = new ListParser<>(LONG_PARSER);
	PropertyParser<List<Integer>> INT_LIST_PARSER = new ListParser<>(INT_PARSER);

	static <T extends Enum<T>> PropertyParser<T> enumParser(Class<T> enumClass) {
		return new EnumParser<>(enumClass);
	}

	static <T extends Enum<T>> PropertyParser<Set<T>> enumSetParser(Class<T> enumClass) {
		return new SetParser<>(enumParser(enumClass));
	}

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
	}

	class StringParser implements PropertyParser<String> {
		@Override
		public String parse(String value) {
			return value;
		}
	}

	class LongParser implements PropertyParser<Long> {
		@Override
		public Long parse(String value) {
			return Long.valueOf(value);
		}
	}

	class IntParser implements PropertyParser<Integer> {
		@Override
		public Integer parse(String value) {
			return Integer.valueOf(value);
		}
	}

	class EnumParser<T extends Enum<T>> implements PropertyParser<T> {
		private final Class<T> enumClass;

		public EnumParser(Class<T> enumClass) {
			this.enumClass = requireNonNull(enumClass);
		}

		@Override
		public T parse(String value) {
			return Arrays.stream(enumClass.getEnumConstants())
					.filter(it -> it.name().equalsIgnoreCase(value))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException("Unknown " + enumClass.getSimpleName() + " value " + value));
		}
	}

	abstract class CollectionParser<T, C extends Collection<T>> implements PropertyParser<C> {
		private final PropertyParser<T> singleValueParser;
		private final Supplier<C> emptyCollection;
		private final Collector<T, ?, C> toCollection;

		protected CollectionParser(PropertyParser<T> singleValueParser, Supplier<C> emptyCollection, Collector<T, ?, C> toCollection) {
			this.singleValueParser = requireNonNull(singleValueParser);
			this.emptyCollection = requireNonNull(emptyCollection);
			this.toCollection = requireNonNull(toCollection);
		}

		@Override
		public final C parse(String value) {
			String trimmed = value == null ? "" : value.trim();
			if (trimmed.isEmpty()) {
				return emptyCollection.get();
			} else {
				return Arrays.stream(trimmed.split(","))
						.map(String::trim)
						.map(singleValueParser::parse)
						.collect(toCollection);
			}
		}

	}

	class ListParser<T> extends CollectionParser<T, List<T>> {

		ListParser(PropertyParser<T> singleValueParser) {
			super(singleValueParser, Collections::emptyList, toList());
		}

	}

	class SetParser<T> extends CollectionParser<T, Set<T>> {

		SetParser(PropertyParser<T> singleValueParser) {
			super(singleValueParser, Collections::emptySet, toCollection(LinkedHashSet::new));
		}

	}

}
