package com.dontocsata.geospatial;

import com.dontocsata.geospatial.config.FxmlTemplateResolver;
import com.dontocsata.geospatial.layer.MapLayer;
import com.dontocsata.geospatial.layer.MapLayerEventType;
import com.dontocsata.geospatial.plugin.MenuItemBinding;
import com.dontocsata.geospatial.plugin.PluginManager;
import com.dontocsata.geospatial.plugin.PluginRunner;
import com.dontocsata.geospatial.plugin.PluginWrapper;
import com.dontocsata.geospatial.setup.CommandHandler;
import com.dontocsata.geospatial.setup.MenuCommandHandler;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.MapStateEventType;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.vividsolutions.jts.geom.Point;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainApp extends Application implements MapComponentInitializedListener {

	private static final Logger log = LoggerFactory.getLogger(MainApp.class);

	private AnchorPane parent;

	@FXML
	private GoogleMapView mapComponent;
	@FXML
	private ListView<MapLayer> layerList;
	@FXML
	private VBox vbox;
	@FXML
	private Label latLongLabel;

	private GoogleMap map;
	private AnnotationConfigApplicationContext context;

	private MutableUIEventHandler eventHandler;

	private ContextMenu contextMenu;

	private MapLayerControl mapLayerControl;

	private MapLayer droppedMarkersLayer;

	private PluginManager pluginManager;

	private static final int SPLASH_WIDTH = 450;
	private static final int SPLASH_HEIGHT = 225;
	private Pane splashLayout;
	private ProgressBar loadProgress;
	private Label progressText;
	private Stage initalStage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() throws Exception {
		//ImageView splash = new ImageView(new Image("http://fxexperience.com/wp-content/uploads/2010/06/logo.png"));
		loadProgress = new ProgressBar();
		loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
		progressText = new Label();
		splashLayout = new VBox();
		//splashLayout.getChildren().add(splash);
		splashLayout.getChildren().addAll(loadProgress, progressText);
		progressText.setAlignment(Pos.CENTER);
		splashLayout.setStyle("-fx-padding: 5; -fx-background-color: cornsilk; -fx-border-width:5; -fx-border-color: linear-gradient(to bottom, chocolate, derive(chocolate, 50%));");
		splashLayout.setEffect(new DropShadow());
	}

	@Override
	public void start(Stage stage) throws Exception {
		showSplash(stage);
		progressText.setText("Initializing Spring Context");
		context = new AnnotationConfigApplicationContext();
		progressText.setText("Initializing Map");
		parent = new FxmlTemplateResolver(new DefaultResourceLoader()).loadTemplate("MainApp.fxml", this);
		mapComponent.addMapInializedListener(this);

		//stage.setMaximized(true);
	}

	private void showSplash(Stage initStage) {
		this.initalStage = initStage;
		Scene splashScene = new Scene(splashLayout);
		initStage.initStyle(StageStyle.UNDECORATED);
		final Rectangle2D bounds = Screen.getPrimary().getBounds();
		initStage.setScene(splashScene);
		initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
		initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
		initStage.show();
	}

	@Override
	public void mapInitialized() {
		log.debug("Map initialized");
		progressText.setText("Configuring Map Components");
		// Configure map
		LatLong center = new LatLong(39, -76);
		MapOptions options = new MapOptions();
		options.center(center).mapMarker(true).zoom(9).overviewMapControl(false).panControl(true).rotateControl(false)
				.scaleControl(true).streetViewControl(false).zoomControl(true).mapType(MapTypeIdEnum.ROADMAP);
		map = mapComponent.createMap(options);
		map.addUIEventHandler(UIEventType.mousemove, jObj -> {
			LatLong latLong = new LatLong((JSObject) jObj.getMember("latLng"));
			latLongLabel.setText("(" + latLong.getLatitude() + ", " + latLong.getLongitude() + ")");
		});
		map.addUIEventHandler(UIEventType.rightclick, jObj -> {
			LatLong latLong = new LatLong((JSObject) jObj.getMember("latLng"));
			Point2D point = map.fromLatLngToPoint(latLong);
			contextMenu = new ContextMenu();
			MenuItem item = new MenuItem("Drop Marker");
			contextMenu.getItems().add(item);
			item.setOnAction(ae -> {
				Point p = GeoUtils.latLongToPoint(latLong);
				MapLayer.Builder builder = null;
				boolean addToControl = false;
				if (droppedMarkersLayer == null) {
					builder = new MapLayer.Builder();
					addToControl = true;
				} else {
					builder = new MapLayer.Builder(droppedMarkersLayer);
				}
				droppedMarkersLayer = builder.setName("Dropped Markers").addGeometry(new GeometryWrapper(p))
						.addListener(MapLayerEventType.CLICK, event -> {
							InfoWindowOptions iwo = new InfoWindowOptions();
							iwo.position(latLong);
							iwo.content("(" + latLong.getLatitude() + ", " + latLong.getLongitude() + ")");
							new InfoWindow(iwo).open(map, event.getMarker());
						}).addListener(MapLayerEventType.RIGHT_CLICK, event -> {
							ContextMenu menu = new ContextMenu();
							MenuItem remove = new MenuItem("Remove Marker");
							remove.setOnAction(actionEvent -> {
								System.out.println("Remove!");
							});
							menu.getItems().add(item);
							menu.show(mapComponent, point.getX(), point.getY());
						}).build();
				if (addToControl) {
					mapLayerControl.add(droppedMarkersLayer);
				}
			});
			contextMenu.show(mapComponent, point.getX(), point.getY());
		});
		map.addStateEventHandler(MapStateEventType.dragstart, () -> contextMenu.hide());
		EnumSet<UIEventType> of = EnumSet.of(UIEventType.click, UIEventType.dblclick);
		for (UIEventType uiet : of) {
			if (uiet != UIEventType.rightclick) {
				map.addUIEventHandler(uiet, jObj -> {
					contextMenu.hide();
				});
			}
		}

		progressText.setText("Configuring Spring Components");
		// Register and setup the context
		context.getBeanFactory().registerSingleton("googleMap", map);
		context.scan("com.dontocsata.geospatial");
		context.refresh();

		setupContextMenu();

		mapLayerControl = context.getBean(MapLayerControl.class);
		mapLayerControl.configure(layerList);

		eventHandler = context.getBean(MutableUIEventHandler.class);
		map.addUIEventHandler(UIEventType.click, eventHandler);

		progressText.setText("Detecting Handlers");
		Collection<CommandHandler> commandHandlers = context.getBeansOfType(CommandHandler.class).values();
		MenuBar menuBar = setupMenu(commandHandlers);
		parent.getChildren().add(menuBar);

		progressText.setText("Loading Plugins");
		pluginManager = new PluginManager(context);
		try {
			pluginManager.loadPlugins();
		} catch (IOException e) {
			log.error("Exception loading plugins", e);
			//TODO show some dialog here?
		}

		progressText.setText("Initializing Main UI");
		Stage stage = new Stage(StageStyle.DECORATED);
		stage.setTitle("Geospatial Toolkit");
		Scene scene = new Scene(parent);
		scene.getStylesheets().add("/css/style.css");
		stage.setScene(scene);
		stage.show();
		initalStage.hide();
	}

	private void setupContextMenu() {

	}

	private MenuBar setupMenu(Collection<CommandHandler> handlers) {
		MenuBar menuBar = new MenuBar();
		Map<String, Menu> menus = new LinkedHashMap<>();
		for (String name : new String[]{"File", "Edit", "View"}) {
			menus.put(name, new Menu(name));
			menuBar.getMenus().add(menus.get(name));
		}

		MenuItem showHide = new MenuItem("Hide Layer List");
		showHide.setOnAction(ae -> {
			synchronized (this) {
				if (vbox.getChildren().isEmpty()) {
					vbox.getChildren().add(layerList);
					showHide.setText("Hide Layer List");
				} else {
					vbox.getChildren().remove(layerList);
					showHide.setText("Show Layer List");
				}
			}
		});
		menus.get("View").getItems().add(showHide);

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
						ch.invoke();
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
