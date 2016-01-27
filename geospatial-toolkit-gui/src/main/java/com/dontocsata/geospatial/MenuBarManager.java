package com.dontocsata.geospatial;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dontocsata.geospatial.plugin.MenuItemPluginRunner;
import com.dontocsata.geospatial.plugin.PluginRunner;
import com.dontocsata.geospatial.plugin.PluginStateChangeEvent;
import com.google.common.eventbus.Subscribe;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * Created by ray.douglass on 9/21/15.
 */
public class MenuBarManager {

	private static final Logger log = LoggerFactory.getLogger(MenuBarManager.class);

	private MenuBar menuBar = new MenuBar();
	private Map<String, Menu> menus = new ConcurrentHashMap<>();
	private Map<MenuItemDescriptor, Menu> descriptorMenuMap = new ConcurrentHashMap<>();

	public MenuBarManager() {
		menuBar.setUseSystemMenuBar(true);
		for (String name : new String[] { "File", "Edit", "View", "Plugins" }) {
			menus.put(name, new Menu(name));
			menuBar.getMenus().add(menus.get(name));
		}
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}

	public MenuItem add(MenuItemDescriptor mi, EventHandler<ActionEvent> action) {
		MenuItem item = new MenuItem(mi.getItemName());
		item.setId(mi.toId());
		item.setOnAction(action);
		Menu menu = getMenu(mi.getMenuName());
		descriptorMenuMap.put(mi, menu);
		menu.getItems().add(item);
		return item;
	}

	public MenuItem add(String menuName, MenuItem item) {
		MenuItemDescriptor mi = new MenuItemDescriptor(menuName, item.getText());
		item.setId(mi.toId());
		Menu menu = getMenu(menuName);
		descriptorMenuMap.put(mi, menu);
		menu.getItems().add(item);
		return item;
	}

	public void remove(MenuItemDescriptor mi) {
		Menu menu = descriptorMenuMap.get(mi);
		Optional<MenuItem> item = menu.getItems().stream().filter(i -> i.getId().equals(mi.toId())).findFirst();
		if (item.isPresent()) {
			menu.getItems().remove(item.get());
		}
	}

	private synchronized Menu getMenu(String name) {
		Menu menu = menus.get(name);
		if (menu == null) {
			menu = new Menu(name);
			menus.put(name, menu);
			menuBar.getMenus().add(menu);
		}
		return menu;
	}

	@Subscribe
	public void handlePluginChanged(PluginStateChangeEvent event) {
		switch (event.getNewState()) {
			case NEW:
			case ERROR_LOADING:
			case ERROR_STARTING:
			case LOADED:
				// no op
				break;
			case STOPPED:
				for (PluginRunner r : event.getPlugin().getRunners()) {
					if (r instanceof MenuItemPluginRunner) {
						remove(((MenuItemPluginRunner) r).getMenuItemDescriptor());
					}
				}
				break;
			case STARTED:
				for (PluginRunner r : event.getPlugin().getRunners()) {
					if (r instanceof MenuItemPluginRunner) {
						log.debug("Adding menu item for {}", event.getPlugin().getPlugin().name());
						MenuItemPluginRunner pr = (MenuItemPluginRunner) r;
						MenuItem item = add(pr.getMenuItemDescriptor(), ae -> {
							try {
								pr.invoke();
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
						pr.init(item);
					}
				}
				break;
			default:
				throw new IllegalStateException();
		}
	}

}
