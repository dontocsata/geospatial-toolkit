package com.dontocsata.geospatial;

import org.geotools.geometry.jts.WKTReader2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;

@Component
public class GeometryParser {

	@Autowired
	private WKTReader2 wktReader;

	public Geometry parse(String wkt) throws GeometryException {
		try {
			return wktReader.read(wkt);
		} catch (ParseException e) {
			throw new GeometryException(e);
		}
	}

	public Geometry parse(String wkt, int srid) throws GeometryException {
		try {
			WKTReader2 reader = new WKTReader2(new GeometryFactory(new PrecisionModel(), srid));
			return reader.read(wkt);
		} catch (ParseException e) {
			throw new GeometryException(e);
		}
	}
}
