package com.dontocsata.geospatial.handlers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dontocsata.geospatial.CommandHandler;
import com.dontocsata.geospatial.GeoUtils;
import com.dontocsata.geospatial.GeometryParser;
import com.dontocsata.geospatial.MenuItemDescriptor;
import com.dontocsata.geospatial.config.FxmlTemplateResolver;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Pair;

@Component
public class MapWktHandler implements CommandHandler {

	@Autowired
	private GeometryParser geometryParser;

	@Autowired
	private FxmlTemplateResolver templates;

	@FXML
	private TextArea textArea;

	@FXML
	private TextField sridInput;

	@Override
	public void invoke(GoogleMap map) throws Exception {
		Dialog<Pair<String, Integer>> dialog = new Dialog<>();
		dialog.setTitle("Map WKT");
		DialogPane pane = templates.loadTemplate("WktDialog.fxml", this);
		pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
		dialog.setDialogPane(pane);
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				return new Pair<String, Integer>(textArea.getText(), Integer.parseInt(sridInput.getText()));
			}
			return null;
		});
		Optional<Pair<String, Integer>> result = dialog.showAndWait();
		if(result.isPresent()) {
			String[] wkt = result.get().getKey().split("\n");
			for(String s:wkt) {
				Geometry geom = geometryParser.parse(s, result.get().getValue());
				if(geom instanceof Point) {
					map.addMarker(GeoUtils.convert((Point) geom));
				}else {
					map.addMapShape(GeoUtils.convert(geom));
				}
			}
		}

	}

	@Override
	public MenuItemDescriptor getMenuItemDescriptor() {
		return new MenuItemDescriptor("File", "Map WKT");
	}

}
