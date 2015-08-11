package com.dontocsata.geospatial;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lynden.gmapsfx.javascript.object.GoogleMap;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Handles adding and removing {@link MapLayer} objects from a {@link GoogleMap}. It also handles the layer list for the
 * UI.
 */
@Component
public class MapLayerControl {

	@Autowired
	private GoogleMap map;

	private ListView<MapLayer> layerList;

	private List<MapLayer> realLayers = new ArrayList<>();
	private ObservableList<MapLayer> layers = FXCollections.observableArrayList();

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
	 * Configure the {@link MapLayerControl} to update the specified {@link ListView} with changes to the map layers
	 */
	void configure(ListView<MapLayer> layerList) {
		this.layerList = layerList;
		this.layerList.setItems(layers);
		Callback<ListView<MapLayer>, ListCell<MapLayer>> cellFactory = CheckBoxListCell
				.forListView(new Callback<MapLayer, ObservableValue<Boolean>>() {

					@Override
					public ObservableValue<Boolean> call(MapLayer param) {
						BooleanProperty toRet = new SimpleBooleanProperty(true);
						toRet.addListener((obs, wasSelected, isNowSelected) -> {
							if (isNowSelected) {
								try {
									param.addTo(map);
								} catch (Exception e) {
									throw new RuntimeException(e);
								}
							} else {
								param.removeFrom(map);
							}
						});
						return toRet;
					}
				}, new StringConverter<MapLayer>() {

					@Override
					public String toString(MapLayer object) {
						return object.toString();
					}

					@Override
					public MapLayer fromString(String string) {
						throw new UnsupportedOperationException();
					}
				});
		this.layerList.setCellFactory(cellFactory);
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
