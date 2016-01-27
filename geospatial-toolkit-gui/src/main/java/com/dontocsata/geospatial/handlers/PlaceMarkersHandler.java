package com.dontocsata.geospatial.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.dontocsata.geospatial.MenuItemDescriptor;
import com.dontocsata.geospatial.MutableUIEventHandler;
import com.dontocsata.geospatial.StreamUtils;
import com.dontocsata.geospatial.plugin.MenuItemPluginRunner;
import com.dontocsata.geospatial.plugin.Plugin;
import com.lynden.gmapsfx.javascript.event.UIEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.Animation;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import netscape.javascript.JSObject;

@Plugin(name = "Place Markers Handler", runners = PlaceMarkersHandler.class)
public class PlaceMarkersHandler implements MenuItemPluginRunner {

	@Autowired
	private MutableUIEventHandler eventHandler;

	@Autowired
	private GeometryFactory gf;

	@Autowired
	private GoogleMap map;

	private List<LatLong> points = new ArrayList<>();
	private List<Marker> markers = new ArrayList<>();
	private BooleanProperty statusProperty = new SimpleBooleanProperty(true);

	private UIEventHandler handler = new UIEventHandler() {

		@Override
		public void handle(JSObject obj) {
			LatLong latLong = new LatLong((JSObject) obj.getMember("latLng"));
			points.add(latLong);
			double lat = latLong.getLatitude();
			double lon = latLong.getLongitude();
			MarkerOptions opts = new MarkerOptions();
			opts.position(latLong);
			opts.title(lat + " " + lon);
			opts.animation(Animation.BOUNCE);
			Marker marker = new Marker(opts);
			map.addMarker(marker);
			map.addUIEventHandler(marker, UIEventType.click, jsObj -> {
				InfoWindowOptions iwo = new InfoWindowOptions();
				iwo.position(latLong);
				iwo.content("(" + lat + ", " + lon + ")");
				new InfoWindow(iwo).open(map, marker);
			});
			markers.add(marker);
			statusProperty.set(false);
		}
	};

	@Override
	public void invoke() throws Exception {
		points = new ArrayList<>();
		eventHandler.add(handler);
	}

	@Override
	public MenuItemDescriptor getMenuItemDescriptor() {
		return new MenuItemDescriptor("Generate", "Set Points");
	}

	public Geometry generate() {
		eventHandler.remove(handler);
		statusProperty.set(true);
		markers.forEach(map::removeMarker);
		List<Coordinate> coords = points.stream().map(ll -> new Coordinate(ll.getLongitude(), ll.getLatitude()))
				.collect(Collectors.toList());
		if (coords.size() > 2) {
			Coordinate[] array = coords.toArray(new Coordinate[coords.size() + 1]);
			array[array.length - 1] = array[0];
			return gf.createPolygon(array);
		}
		return null;
	}

	@Override
	public Map<String, Object> start() throws Exception {
		return StreamUtils.createMap("placeMarkersHandler", this);
	}

	@Override
	public void stop() {

	}

	BooleanProperty statusProperty() {
		return statusProperty;
	}
}
