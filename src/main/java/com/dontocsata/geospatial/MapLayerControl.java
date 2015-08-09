package com.dontocsata.geospatial;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lynden.gmapsfx.javascript.object.GoogleMap;

import javafx.collections.ListChangeListener;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;

/**
 * Handles adding and removing {@link MapLayer} objects from a {@link GoogleMap}. It also handles the layer list for the
 * UI.
 */
@Component
public class MapLayerControl {

	@Autowired
	private GoogleMap map;

	private List<MapLayer> realLayers = new ArrayList<>();
	private ModifiableObservableListBase<MapLayer> layers = new ModifiableObservableListBase<MapLayer>() {

		@Override
		public MapLayer get(int index) {
			return realLayers.get(index);
		}

		@Override
		public int size() {
			return realLayers.size();
		}

		@Override
		protected void doAdd(int index, MapLayer element) {
			realLayers.add(index, element);
		}

		@Override
		protected MapLayer doSet(int index, MapLayer element) {
			return realLayers.set(index, element);
		}

		@Override
		protected MapLayer doRemove(int index) {
			return realLayers.remove(index);
		}
	};

	public MapLayerControl() {
		layers.addListener(new ListChangeListener<MapLayer>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends MapLayer> c) {
				try {
					while (c.next()) {
						for (MapLayer layer : c.getAddedSubList()) {
							layer.addTo(map);
						}
						for (MapLayer layer : c.getRemoved()) {
							layer.removeFrom(map);
						}
					}
				} catch (GeometryException ex) {
					throw new RuntimeException(ex);
				}
			}

		});

	}

	/**
	 * Gets the current layers
	 */
	public ObservableList<MapLayer> getLayers() {
		return layers;
	}

	public void remove(MapLayer layer) {
		layers.remove(layer);
	}

	public void add(MapLayer layer) {
		layers.add(layer);
	}

	/**
	 * Adds the {@link MapLayer} and centers the map over the center of {@link MapLayer}
	 *
	 * @param layer
	 * @throws GeometryException
	 *         the center point of the {@link MapLayer} has to be transformed which may throw this
	 */
	public void addAndCenter(MapLayer layer) throws GeometryException {
		add(layer);
		map.setCenter(GeoUtils.fromPoint(layer.getCenter()));
	}

	public GoogleMap getMap() {
		return map;
	}

	public void setMap(GoogleMap map) {
		this.map = map;
	}

}
