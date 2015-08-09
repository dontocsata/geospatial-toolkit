package com.dontocsata.geospatial;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

import com.dontocsata.geospatial.config.FxmlTemplateResolver;
import com.dontocsata.geospatial.setup.CommandHandler;
import com.dontocsata.geospatial.setup.MenuCommandHandler;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application implements MapComponentInitializedListener {

	private AnchorPane parent;

	@FXML
	private GoogleMapView mapComponent;

	private GoogleMap map;
	private AnnotationConfigApplicationContext context;

	private MutableUIEventHandler eventHandler;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		context = new AnnotationConfigApplicationContext();
		parent = new FxmlTemplateResolver(new DefaultResourceLoader()).loadTemplate("MainApp.fxml", this);
		mapComponent.addMapInializedListener(this);

		Scene scene = new Scene(parent);
		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();
	}

	@Override
	public void mapInitialized() {
		// Configure map
		LatLong center = new LatLong(39, -76);
		MapOptions options = new MapOptions();
		options.center(center).mapMarker(true).zoom(9).overviewMapControl(false).panControl(true).rotateControl(false)
		.scaleControl(true).streetViewControl(false).zoomControl(true).mapType(MapTypeIdEnum.ROADMAP);
		map = mapComponent.createMap(options);

		// Register and setup the context
		context.getBeanFactory().registerSingleton("googleMap", map);
		context.scan("com.dontocsata.geospatial");
		context.refresh();

		eventHandler = context.getBean(MutableUIEventHandler.class);
		map.addUIEventHandler(UIEventType.click, eventHandler);

		Collection<CommandHandler> commandHandlers = context.getBeansOfType(CommandHandler.class).values();
		MenuBar menuBar = setupMenu(commandHandlers);
		parent.getChildren().add(menuBar);
	}

	private MenuBar setupMenu(Collection<CommandHandler> handlers) {
		MenuBar menuBar = new MenuBar();
		Map<String, Menu> menus = new LinkedHashMap<>();
		menus.put("File", new Menu("File"));
		menuBar.getMenus().add(menus.get("File"));
		for (CommandHandler ch : handlers) {
			if (ch instanceof MenuCommandHandler) {
				MenuItemDescriptor mi = ((MenuCommandHandler) ch).getMenuItemDescriptor();
				Menu menu = menus.get(mi.getMenuName());
				if (menu == null) {
					menu = new Menu(mi.getMenuName());
					menus.put(mi.getMenuName(), menu);
					menuBar.getMenus().add(menu);
				}
				MenuItem item = new MenuItem(mi.getItemName());
				menu.getItems().add(item);
				item.setOnAction(ae -> {
					try {
						ch.invoke(map);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		}
		menuBar.setUseSystemMenuBar(true);
		return menuBar;
	}

}
