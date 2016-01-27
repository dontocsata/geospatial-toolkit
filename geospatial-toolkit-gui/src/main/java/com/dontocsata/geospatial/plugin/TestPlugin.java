package com.dontocsata.geospatial.plugin;

import java.util.Map;

/**
 * Created by ray.douglass on 9/18/15.
 */
@Plugin(name = "TestPlugin", runners = TestPlugin.TestPluginRunner.class)
public class TestPlugin {

	public static class TestPluginRunner implements PluginRunner {

		@Override
		public Map<String, Object> start() {
			return null;
		}

		@Override
		public void stop() {

		}
	}
}
