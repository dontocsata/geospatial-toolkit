package com.dontocsata.geospatial.plugin;

import java.util.Map;

/**
 * Created by ray.douglass on 9/18/15.
 */
public interface PluginRunner {

	/**
	 * Start this {@link PluginRunner} returning a mapping of bean names to
	 * objects for dependency injection. Return null if no beans should be
	 * registered.
	 */
	public Map<String, Object> start() throws Exception;

	public void stop();

}
