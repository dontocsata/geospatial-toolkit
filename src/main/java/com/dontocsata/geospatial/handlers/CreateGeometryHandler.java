package com.dontocsata.geospatial.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dontocsata.geospatial.MenuItemDescriptor;
import com.dontocsata.geospatial.setup.MenuCommandHandler;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.vividsolutions.jts.geom.Geometry;

@Component
public class CreateGeometryHandler implements MenuCommandHandler {

	@Autowired
	private PlaceMarkersHandler placeMarkersHandler;

	@Override
	public void invoke(GoogleMap map) throws Exception {
		Geometry geometry = placeMarkersHandler.generate();
		System.out.println(geometry);
	}

	@Override
	public MenuItemDescriptor getMenuItemDescriptor() {
		return new MenuItemDescriptor("Generate", "Generate Geometry");
	}

}
