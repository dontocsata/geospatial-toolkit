package com.dontocsata.geospatial.handlers;

import com.dontocsata.geospatial.MenuItemDescriptor;
import com.dontocsata.geospatial.plugin.MenuItemPluginRunner;
import com.dontocsata.geospatial.plugin.Plugin;
import com.vividsolutions.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;

@Plugin(name="Create Geometry Handler", runners=CreateGeometryHandler.class)
public class CreateGeometryHandler implements MenuItemPluginRunner {

	@Autowired
	private PlaceMarkersHandler placeMarkersHandler;

	@Override
	public void invoke() throws Exception {
		Geometry geometry = placeMarkersHandler.generate();
		System.out.println(geometry);
	}

	@Override
	public MenuItemDescriptor getMenuItemDescriptor() {
		return new MenuItemDescriptor("Generate", "Generate Geometry");
	}

	@Override
	public void start() throws Exception {

	}

	@Override
	public void stop() {

	}
}
