package com.dontocsata.geospatial;

import com.google.common.base.Preconditions;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * This class simply wraps a {@link Geometry} object. It is intended to be extended to store additional information
 * about the specified Geometry.
 */
public class GeometryWrapper {

	protected Geometry geometry;

	public GeometryWrapper(Geometry geometry) {
		this.geometry = Preconditions.checkNotNull(geometry);
	}

	public boolean isPoint(){
		return geometry instanceof Point;
	}

	public boolean isGeometryCollection(){
		return geometry instanceof GeometryCollection;
	}

	public boolean isLineString(){
		return geometry instanceof LineString;
	}

	public boolean isPolygon(){
		return geometry instanceof Polygon;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	/**
	 * Destructively transforms the geometry inside of this object
	 * @param targetSrid
	 * @throws GeometryException
	 */
	public GeometryWrapper transformGeometry(int targetSrid) throws GeometryException {
		this.geometry=GeoUtils.transform(geometry,targetSrid);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GeometryWrapper that = (GeometryWrapper) o;
		return geometry.equals(that.geometry);

	}

	@Override
	public int hashCode() {
		return geometry.hashCode();
	}
}
