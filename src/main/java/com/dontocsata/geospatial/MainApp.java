package com.dontocsata.geospatial;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application implements MapComponentInitializedListener {

	protected GoogleMapView mapComponent;
	protected GoogleMap map;
	private ApplicationContext context;

	private MutableUIEventHandler eventHandler;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		context = new AnnotationConfigApplicationContext("com.dontocsata.geospatial");
		eventHandler = context.getBean(MutableUIEventHandler.class);

		mapComponent = new GoogleMapView();
		mapComponent.addMapInializedListener(this);
		BorderPane bp = new BorderPane();
		bp.setCenter(mapComponent);

		Collection<CommandHandler> commandHandlers = context.getBeansOfType(CommandHandler.class).values();

		MenuBar menuBar = setupMenu(commandHandlers);
		bp.setTop(menuBar);

		Scene scene = new Scene(bp);
		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();
	}

	@Override
	public void mapInitialized() {
		LatLong center = new LatLong(39, -76);
		mapComponent.addMapReadyListener(() -> {

		});
		MapOptions options = new MapOptions();
		options.center(center).mapMarker(true).zoom(9).overviewMapControl(false).panControl(false).rotateControl(false)
		.scaleControl(true).streetViewControl(false).zoomControl(true).mapType(MapTypeIdEnum.ROADMAP);

		map = mapComponent.createMap(options);
		map.addUIEventHandler(UIEventType.click, eventHandler);
	}

	private MenuBar setupMenu(Collection<CommandHandler> handlers) {
		MenuBar menuBar = new MenuBar();
		Map<String, Menu> menus = new LinkedHashMap<>();
		menus.put("File", new Menu("File"));
		menuBar.getMenus().add(menus.get("File"));
		for (CommandHandler ch : handlers) {
			MenuItemDescriptor mi = ch.getMenuItemDescriptor();
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
		menuBar.setUseSystemMenuBar(true);
		return menuBar;
	}

}
