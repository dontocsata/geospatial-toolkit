package com.dontocsata.geospatial.handlers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.dontocsata.geospatial.MenuItemDescriptor;
import com.dontocsata.geospatial.plugin.MenuItemPluginRunner;
import com.dontocsata.geospatial.plugin.Plugin;
import com.vividsolutions.jts.geom.Geometry;

import javafx.scene.control.MenuItem;

@Plugin(name = "Create Geometry Handler", runners = CreateGeometryHandler.class, depends = PlaceMarkersHandler.class)
public class CreateGeometryHandler implements MenuItemPluginRunner {

	@Autowired
	private PlaceMarkersHandler placeMarkersHandler;

	@Override
	public void invoke() throws Exception {
		Geometry geometry = placeMarkersHandler.generate();
		System.out.println(geometry);
	}

	@Override
	public void init(MenuItem menuItem) {
		menuItem.disableProperty().bindBidirectional(placeMarkersHandler.statusProperty());
	}

	@Override
	public MenuItemDescriptor getMenuItemDescriptor() {
		return new MenuItemDescriptor("Generate", "Generate Geometry");
	}

	@Override
	public Map<String, Object> start() throws Exception {
		return null;
	}

	@Override
	public void stop() {

	}
}
