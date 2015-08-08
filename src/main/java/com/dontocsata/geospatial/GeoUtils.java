package com.dontocsata.geospatial;

import java.util.stream.Stream;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.shapes.PolygonOptions;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public class GeoUtils {

	public static final int WGS84_SRID = 4326;
	public static final GeometryFactory WGS84_GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), WGS84_SRID);

	private GeoUtils() {

	}

	public static Geometry transform(Geometry geom, int sourceSrid, int targetSrid) throws GeometryException {
		geom.setSRID(sourceSrid);
		return transform(geom, targetSrid);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Geometry> T transform(T geom, int targetSrid) throws GeometryException {
		if (geom.getSRID() == targetSrid) {
			return geom;
		}
		try {
			CoordinateReferenceSystem sourceCrs = getCrs(geom.getSRID());
			CoordinateReferenceSystem targetCrs = getCrs(targetSrid);
			MathTransform transform = CRS.findMathTransform(sourceCrs, targetCrs);
			return (T) JTS.transform(geom, transform);
		} catch (FactoryException | MismatchedDimensionException | TransformException e) {
			throw new GeometryException(e);
		}
	}

	private static CoordinateReferenceSystem getCrs(int srid) throws GeometryException {
		try {
			return CRS.decode("EPSG:" + srid, true);
		} catch (FactoryException e) {
			throw new GeometryException(e);
		}
	}

	public static LatLong convert(Coordinate coor) {
		return new LatLong(coor.y, coor.x);
	}

	public static Marker convert(Point point) throws GeometryException {
		point = transform(point, WGS84_SRID);
		MarkerOptions opts = new MarkerOptions();
		opts.position(convert(point.getCoordinate()));
		return new Marker(opts);
	}

	public static MapShape convert(Geometry geom) throws GeometryException {
		geom = transform(geom, WGS84_SRID);
		if(geom instanceof Point) {
			throw new IllegalArgumentException("Cannot convert a Point to a MapShape.");
		}else if(geom instanceof LineString) {
			LineString ls = (LineString)geom;
			PolylineOptions opts = new PolylineOptions();
			MVCArray array = new MVCArray();
			Stream.of(ls.getCoordinates()).map(GeoUtils::convert).forEach(array::push);
			opts.path(array);
			return new Polyline(opts);
		} else if (geom instanceof Polygon) {
			Polygon p = (Polygon) geom;
			PolygonOptions opts = new PolygonOptions();
			MVCArray array = new MVCArray();
			Stream.of(p.getCoordinates()).map(GeoUtils::convert).forEach(array::push);
			opts.paths(array);
			return new com.lynden.gmapsfx.shapes.Polygon(opts);
		} else {
			throw new RuntimeException();
		}
	}

	public static LatLong fromPoint(Point point) throws GeometryException {
		point = transform(point, WGS84_SRID);
		return new LatLong(point.getY(), point.getX());
	}

}
