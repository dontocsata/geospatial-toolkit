package com.dontocsata.geospatial.plugin;

import com.dontocsata.geospatial.MenuItemDescriptor;

import javafx.scene.control.MenuItem;

/**
 * A {@link PluginRunner} that is invoked by a {@link MenuItem}.
 */
public interface MenuItemPluginRunner extends PluginRunner {

	public MenuItemDescriptor getMenuItemDescriptor();

	public default void init(MenuItem menuItem) {
	}

	public void invoke() throws Exception;

}
