package com.dontocsata.geospatial.setup;

import com.dontocsata.geospatial.MenuItemDescriptor;

import javafx.scene.control.MenuItem;

/**
 * A {@link CommandHandler} invoked by a {@link MenuItem}.
 */
public interface MenuCommandHandler extends CommandHandler {

	public MenuItemDescriptor getMenuItemDescriptor();

}
