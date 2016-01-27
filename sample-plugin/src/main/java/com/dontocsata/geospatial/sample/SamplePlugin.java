package com.dontocsata.geospatial.sample;

import java.util.Map;

import com.dontocsata.geospatial.MenuItemDescriptor;
import com.dontocsata.geospatial.plugin.MenuItemPluginRunner;
import com.dontocsata.geospatial.plugin.Plugin;
import com.dontocsata.geospatial.plugin.PluginRunner;

/**
 * Created by ray.douglass on 9/21/15.
 */
@Plugin(name = "Sample Plugin")
public class SamplePlugin implements PluginRunner, MenuItemPluginRunner {

	@Override
	public MenuItemDescriptor getMenuItemDescriptor() {
		return new MenuItemDescriptor("Sample", "Sample Plugin");
	}

	@Override
	public void invoke() throws Exception {
		System.out.println("SamplePlugin invoked");
	}

	@Override
	public Map<String, Object> start() throws Exception {
		return null;
	}

	@Override
	public void stop() {

	}
}
