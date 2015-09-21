package com.dontocsata.geospatial.handlers;

import com.dontocsata.geospatial.GeometryParser;
import com.dontocsata.geospatial.GeometryWrapper;
import com.dontocsata.geospatial.MapLayerControl;
import com.dontocsata.geospatial.MenuItemDescriptor;
import com.dontocsata.geospatial.StreamUtils;
import com.dontocsata.geospatial.config.FxmlTemplateResolver;
import com.dontocsata.geospatial.layer.MapLayer;
import com.dontocsata.geospatial.setup.MenuCommandHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MapWktHandler implements MenuCommandHandler {

	private int count = 1;

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

	@FXML
	private TextField nameTextField;

	@Override
	public void invoke() throws Exception {
		Dialog<MapWktResult> dialog = new Dialog<>();
		dialog.setTitle("Map WKT");
		DialogPane pane = templates.loadTemplate("WktDialog.fxml", this);
		nameTextField.setText("WKT #" + count);
		pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
		dialog.setDialogPane(pane);
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				String name = nameTextField.getText();
				String wkts = textArea.getText();
				int srid = Integer.parseInt(sridInput.getText());
				Color color = colorPicker.getValue();
				return new MapWktResult(name, wkts, color, srid);
			}
			return null;
		});
		Optional<MapWktResult> result = dialog.showAndWait();
		if (result.isPresent()) {
			MapWktResult mwr = result.get();
			if (mwr.wkts != null) {
				List<GeometryWrapper> geometries = Stream.of(mwr.wkts)
						.map(StreamUtils.rethrow(w -> geometryParser.parse(w, mwr.srid)))
						.map(GeometryWrapper::new)
						.collect(Collectors.toList());
				MapLayer layer = new MapLayer.Builder().setName(mwr.name).setGeometries(geometries).setColor(mwr.color)
						.build();
				mapLayerControl.addAndCenter(layer);
				count++;
			}

		}
	}

	@Override
	public MenuItemDescriptor getMenuItemDescriptor() {
		return new MenuItemDescriptor("Map", "Map WKT");
	}

	private static class MapWktResult {

		private String name;
		private String[] wkts;
		private Color color;
		private int srid;

		public MapWktResult(String name, String wkts, Color color, int srid) {
			this.name = name;
			this.wkts = wkts != null && !wkts.isEmpty() ? wkts.split("\n") : null;
			this.color = color;
			this.srid = srid;
		}

	}

}