package com.dontocsata.geospatial.handlers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dontocsata.geospatial.GeometryParser;
import com.dontocsata.geospatial.MapLayer;
import com.dontocsata.geospatial.MapLayerControl;
import com.dontocsata.geospatial.MenuItemDescriptor;
import com.dontocsata.geospatial.StreamUtils;
import com.dontocsata.geospatial.config.FxmlTemplateResolver;
import com.dontocsata.geospatial.setup.MenuCommandHandler;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.vividsolutions.jts.geom.Geometry;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

@Component
public class MapWktHandler implements MenuCommandHandler {

	@Autowired
	private MapLayerControl mapLayerControl;

	@Autowired
	private GeometryParser geometryParser;

	@Autowired
	private FxmlTemplateResolver templates;

	@FXML
	private TextArea textArea;

	@FXML
	private TextField sridInput;

	@FXML
	private ColorPicker colorPicker;

	@Override
	public void invoke(GoogleMap map) throws Exception {
		Dialog<MapWktResult> dialog = new Dialog<>();
		dialog.setTitle("Map WKT");
		DialogPane pane = templates.loadTemplate("WktDialog.fxml", this);
		pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
		dialog.setDialogPane(pane);
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				String wkts = textArea.getText();
				int srid = Integer.parseInt(sridInput.getText());
				Color color = colorPicker.getValue();
				return new MapWktResult(wkts, color, srid);
			}
			return null;
		});
		Optional<MapWktResult> result = dialog.showAndWait();
		if(result.isPresent()) {
			MapWktResult mwr = result.get();
			List<Geometry> geometries = Stream.of(mwr.wkts)
					.map(StreamUtils.rethrow(w -> geometryParser.parse(w, mwr.srid)))
					.collect(Collectors.toList());
			MapLayer layer = new MapLayer(geometries, mwr.color);
			mapLayerControl.addAndCenter(layer);
		}
	}

	@Override
	public MenuItemDescriptor getMenuItemDescriptor() {
		return new MenuItemDescriptor("File", "Map WKT");
	}

	private static class MapWktResult {

		private String[] wkts;
		private Color color;
		private int srid;

		public MapWktResult(String wkts, Color color, int srid) {
			super();
			this.wkts = wkts.split("\n");
			this.color = color;
			this.srid = srid;
		}

	}

}