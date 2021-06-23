package com.avanza.astrix.config;

import static org.junit.Assert.*;

import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Test;

public class MapConfigSourceTest {

	@Test
	public void shouldCreateFromMap() throws Exception {		
		MapConfigSource source = MapConfigSource.of(Map.of("property1", "value1", "property2", "value2"));
		assertThat(source.get("property1"), Matchers.equalTo("value1"));
		assertThat(source.get("property2"), Matchers.equalTo("value2"));
	}
	
}
