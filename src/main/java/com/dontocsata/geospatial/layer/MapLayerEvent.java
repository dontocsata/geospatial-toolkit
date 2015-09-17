package com.dontocsata.geospatial.layer;

import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.vividsolutions.jts.geom.Geometry;

public class MapLayerEvent {

	private Geometry geometry;
	private Marker marker;
	private MapShape shape;

	public MapLayerEvent(Marker marker, Geometry geometry) {
		this.marker = marker;
		this.geometry = geometry;
	}

	public MapLayerEvent(MapShape shape, Geometry geometry) {
		this.shape = shape;
		this.geometry = geometry;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public Marker getMarker() {
		return marker;
	}

	public MapShape getShape() {
		return shape;
	}

}
