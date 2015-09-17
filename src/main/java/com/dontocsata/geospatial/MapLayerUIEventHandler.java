package com.dontocsata.geospatial;

import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.vividsolutions.jts.geom.Geometry;

public interface MapLayerUIEventHandler {

	public void onClickMarker(GoogleMap map, Geometry geom, Marker marker);

	public void onClickMapShape(GoogleMap map, Geometry geom, MapShape mapShape);
}
