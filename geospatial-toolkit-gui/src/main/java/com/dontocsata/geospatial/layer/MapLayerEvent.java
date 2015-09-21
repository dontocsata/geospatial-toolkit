package com.dontocsata.geospatial.layer;

import com.dontocsata.geospatial.GeometryWrapper;
import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.javascript.object.Marker;

public class MapLayerEvent {

	private GeometryWrapper geometry;
	private Marker marker;
	private MapShape shape;

	public MapLayerEvent(Marker marker, GeometryWrapper geometry) {
		this.marker = marker;
		this.geometry = geometry;
	}

	public MapLayerEvent(MapShape shape, GeometryWrapper geometry) {
		this.shape = shape;
		this.geometry = geometry;
	}

	public GeometryWrapper getGeometry() {
		return geometry;
	}

	public Marker getMarker() {
		return marker;
	}

	public MapShape getShape() {
		return shape;
	}

}
