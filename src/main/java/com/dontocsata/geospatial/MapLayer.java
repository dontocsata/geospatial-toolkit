package com.dontocsata.geospatial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.shapes.PolygonOptions;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import javafx.scene.paint.Color;

public class MapLayer {

	private String name;
	private List<Geometry> geometries;
	private Color color;

	private Geometry bounds;
	private Map<GoogleMap, Collection<Object>> mappedObjects;

	public MapLayer(String name, Collection<Geometry> geometries) {
		this(name, geometries, Color.CORNFLOWERBLUE);
	}

	public MapLayer(String name, Collection<Geometry> geometries, Color color) {
		this.name = name;
		this.geometries = geometries.stream().map(StreamUtils.rethrow(g -> GeoUtils.transform(g, GeoUtils.WGS84_SRID)))
				.collect(Collectors.toList());
		bounds = new GeometryCollection(geometries.toArray(new Geometry[0]), GeoUtils.WGS84_GEOMETRY_FACTORY)
				.getEnvelope();
		this.color = color;
		mappedObjects = new ConcurrentHashMap<>();
	}

	public void addTo(GoogleMap map) throws GeometryException {
		Collection<Object> objects = new ArrayList<>();
		mappedObjects.put(map, objects);
		for (Geometry geom : geometries) {
			if (geom instanceof Point) {
				Marker marker = GeoUtils.convert((Point) geom);
				objects.add(marker);
				map.addMarker(marker);
			} else if (geom instanceof GeometryCollection) {
				GeometryCollection gc = (GeometryCollection) geom;
				for (int i = 0; i < gc.getNumGeometries(); i++) {
					Geometry g = gc.getGeometryN(i);
					if (g instanceof Point) {
						Marker marker = GeoUtils.convert((Point) g);
						objects.add(marker);
						map.addMarker(marker);
					} else {
						MapShape ms = convert(g);
						objects.add(ms);
						map.addMapShape(ms);
					}
				}
			} else {
				MapShape ms = convert(geom);
				objects.add(ms);
				map.addMapShape(ms);

			}
		}
	}

	public void removeFrom(GoogleMap map) {
		for (Object o : mappedObjects.remove(map)) {
			if (o instanceof Marker) {
				map.removeMarker((Marker) o);
			} else if (o instanceof MapShape) {
				map.removeMapShape((MapShape) o);
			} else {
				throw new IllegalStateException("Object is not a Marker or MapShape: " + o);
			}
		}
	}

	private MapShape convert(Geometry geom) throws GeometryException {
		geom = GeoUtils.transform(geom, GeoUtils.WGS84_SRID);
		if (geom instanceof LineString) {
			LineString ls = (LineString) geom;
			PolylineOptions opts = new PolylineOptions();
			opts.path(toMvcArray(ls));
			opts.strokeColor(toRGBCode(color));
			return new Polyline(opts);
		} else if (geom instanceof Polygon) {
			Polygon p = (Polygon) geom;
			PolygonOptions opts = new PolygonOptions();
			MVCArray array = new MVCArray();
			array.push(toMvcArray(p.getExteriorRing()));
			for (int i = 0; i < p.getNumInteriorRing(); i++) {
				array.push(toMvcArray(p.getInteriorRingN(i)));
			}
			// opts.paths(array);
			opts.strokeColor(toRGBCode(color));
			opts.fillColor(toRGBCode(color.brighter()));
			com.lynden.gmapsfx.shapes.Polygon polygon = new com.lynden.gmapsfx.shapes.Polygon(opts);
			polygon.setPaths(array);
			return polygon;
		} else {
			throw new RuntimeException();
		}
	}

	private MVCArray toMvcArray(LineString ls) {
		MVCArray array = new MVCArray();
		Stream.of(ls.getCoordinates()).map(GeoUtils::convert).forEach(array::push);
		return array;
	}

	private static String toRGBCode(Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}

	public Point getCenter() {
		return bounds.getCentroid();
	}

	public Geometry getBounds() {
		return bounds;
	}

	public Color getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

}
