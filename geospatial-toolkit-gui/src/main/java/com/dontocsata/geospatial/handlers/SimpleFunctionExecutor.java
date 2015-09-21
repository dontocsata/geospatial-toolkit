package com.dontocsata.geospatial.handlers;

import com.dontocsata.geospatial.GeometryException;
import com.dontocsata.geospatial.GeometryParser;
import com.dontocsata.geospatial.MenuItemDescriptor;
import com.dontocsata.geospatial.config.FxmlTemplateResolver;
import com.dontocsata.geospatial.plugin.MenuItemPluginRunner;
import com.dontocsata.geospatial.plugin.Plugin;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Plugin(name = "Simple Function Executor", runners = SimpleFunctionExecutor.class)
public class SimpleFunctionExecutor implements MenuItemPluginRunner {

	@Autowired
	private GeometryFactory gf;

	@Autowired
	private FxmlTemplateResolver templates;

	@Autowired
	private GeometryParser geomParser;

	@FXML
	private TextArea firstTextArea;

	@FXML
	private TextArea secondTextArea;

	@FXML
	private Label firstLabel;

	@FXML
	private Label secondLabel;

	@FXML
	private ComboBox<String> comboBox;

	@Override
	public void invoke() throws IOException {
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("Simple Geospatial Functions");
		DialogPane pane = templates.loadTemplate("SimpleFunctionDialog.fxml", this);
		pane.getButtonTypes().add(ButtonType.CLOSE);
		dialog.setDialogPane(pane);
		dialog.show();
	}

	@Override
	public MenuItemDescriptor getMenuItemDescriptor() {
		return new MenuItemDescriptor("File", "Simple Functions");
	}

	@FXML
	public void executeFunctionCombo(ActionEvent ae) {
		executeFunction();
	}

	@FXML
	public void executeFunction(KeyEvent ke) {
		executeFunction();
	}

	private void executeFunction() {
		Geometry first = null;
		Geometry second = null;

		try {
			if (firstTextArea.getText() != null && !firstTextArea.getText().isEmpty()) {
				first = geomParser.parse(firstTextArea.getText());
				firstLabel.setText("Valid Geometry");
			} else {
				firstLabel.setText(null);
			}
		} catch (GeometryException e) {
			firstLabel.setText("Invalid Geometry");
		}
		try {
			if (secondTextArea.getText() != null && !secondTextArea.getText().isEmpty()) {
				second = geomParser.parse(secondTextArea.getText());
				secondLabel.setText("Valid Geometry");
			} else {
				secondLabel.setText(null);
			}
		} catch (GeometryException e) {
			secondLabel.setText("Invalid Geometry");
		}

		if (first != null && second != null) {
			System.out.println(check(first, second));
		}

	}

	private boolean check(Geometry first, Geometry second) {
		switch (comboBox.getSelectionModel().getSelectedItem()) {
			case "contains":
				return first.contains(second);
			case "coveredBy":
				return first.coveredBy(second);
			case "covers":
				return first.covers(second);
			case "crosses":
				return first.crosses(second);
			case "disjoint":
				return first.disjoint(second);
			case "intersects":
				return first.intersects(second);
			case "overlaps":
				return first.overlaps(second);
			case "touches":
				return first.touches(second);
			case "within":
				return first.within(second);
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public void start() throws Exception {

	}

	@Override
	public void stop() {

	}
}
