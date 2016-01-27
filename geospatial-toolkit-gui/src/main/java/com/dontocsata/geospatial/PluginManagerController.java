package com.dontocsata.geospatial;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dontocsata.geospatial.config.FxmlTemplateResolver;
import com.dontocsata.geospatial.plugin.MenuItemPluginRunner;
import com.dontocsata.geospatial.plugin.Plugin;
import com.dontocsata.geospatial.plugin.PluginManager;
import com.dontocsata.geospatial.plugin.PluginState;
import com.dontocsata.geospatial.plugin.PluginWrapper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

@Plugin(name = "Plugin Manager", runners = PluginManagerController.class)
public class PluginManagerController implements MenuItemPluginRunner {

	private static final Logger log = LoggerFactory.getLogger(PluginManagerController.class);

	@FXML
	private ListView<PluginWrapper> listView;

	private PluginManager pluginManager;

	@Autowired
	private FxmlTemplateResolver templates;

	@Override
	public void invoke() throws IOException {
		Dialog<Void> dialog = new Dialog<>();
		dialog.setTitle("Manage Plugins");
		DialogPane pane = templates.loadTemplate("Plugins.fxml", this);
		pane.getButtonTypes().add(ButtonType.CLOSE);
		List<PluginWrapper> plugins = pluginManager.getPlugins().stream()
				.filter(p -> !p.getPluginClass().equals(PluginManagerController.class)).collect(Collectors.toList());
		listView.getItems().addAll(plugins);
		Callback<ListView<PluginWrapper>, ListCell<PluginWrapper>> cellFactory = CheckBoxListCell
				.forListView(new Callback<PluginWrapper, ObservableValue<Boolean>>() {

					@Override
					public ObservableValue<Boolean> call(PluginWrapper pw) {
						BooleanProperty toRet = new SimpleBooleanProperty(pw.getState() == PluginState.STARTED);
						toRet.addListener((obs, wasSelected, isNowSelected) -> {
							if (isNowSelected) {
								log.debug("Enabling {}", pw.getPlugin().name());
								pluginManager.startPlugin(pw);
							} else {
								log.debug("Disabling {}", pw.getPlugin().name());
								pluginManager.stopPlugin(pw);
							}
						});
						return toRet;
					}
				}, new StringConverter<PluginWrapper>() {

					@Override
					public String toString(PluginWrapper object) {
						return object.getPlugin().name();
					}

					@Override
					public PluginWrapper fromString(String string) {
						throw new UnsupportedOperationException();
					}
				});
		listView.setCellFactory(cellFactory);
		dialog.setDialogPane(pane);
		dialog.show();
	}

	@Override
	public Map<String, Object> start() throws Exception {
		return StreamUtils.createMap("pluginManagerController", this);
	}

	@Override
	public void stop() {

	}

	@Override
	public MenuItemDescriptor getMenuItemDescriptor() {
		return new MenuItemDescriptor("Plugins", "Manage plugins");
	}

	void configure(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
}
