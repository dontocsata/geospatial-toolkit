package com.dontocsata.geospatial.config;

import com.google.common.eventbus.EventBus;
import org.geotools.geometry.jts.WKTReader2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vividsolutions.jts.geom.GeometryFactory;

@Configuration
public class MainConfig {

	@Bean
	public GeometryFactory geometryFactory() {
		return new GeometryFactory();
	}

	@Bean
	public WKTReader2 wkt2Reader() {
		return new WKTReader2(geometryFactory());
	}

	@Bean
	public EventBus eventBus(){
		return new EventBus();
	}

}
