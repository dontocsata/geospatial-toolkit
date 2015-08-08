package com.dontocsata.geospatial;

import com.lynden.gmapsfx.javascript.object.GoogleMap;

public interface CommandHandler {

	public void invoke(GoogleMap map) throws Exception;

	public MenuItemDescriptor getMenuItemDescriptor();

}
