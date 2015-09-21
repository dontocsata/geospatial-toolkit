package com.dontocsata.geospatial.plugin;

import com.dontocsata.geospatial.MenuItemDescriptor;

/**
 * Created by ray.douglass on 9/21/15.
 */
public interface MenuItemPluginRunner extends PluginRunner {

	public MenuItemDescriptor getMenuItemDescriptor();

	public void invoke() throws Exception;

}
