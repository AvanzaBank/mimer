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
 * A {@link ConfigSource} that resolves a property by reading system properties. <p>
 * 
 * @author Elias Lindholm (elilin)
 *
 */
public class SystemPropertiesConfigSource implements ConfigSource {
	
	@Override
	public String get(String propertyName) {
		return System.getProperty(propertyName);
	}
}
