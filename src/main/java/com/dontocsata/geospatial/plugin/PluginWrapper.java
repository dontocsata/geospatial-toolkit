package com.dontocsata.geospatial.plugin;

import java.util.Collection;

/**
 * Created by ray.douglass on 9/18/15.
 */
public class PluginWrapper {

	private Plugin plugin;
	private Class<?> pluginClass;
	private Collection<PluginRunner> runners;
	private ClassLoader loader;

	private PluginState state = PluginState.LOADED;

	public PluginWrapper(Plugin plugin, Class<?> pluginClass, Collection<PluginRunner> runners) {
		this.plugin = plugin;
		this.pluginClass = pluginClass;
		this.runners = runners;
	}

	public void start() {
		runners.forEach(PluginRunner::start);
		state=PluginState.STARTED;
	}

	public void stop() {
		runners.forEach(PluginRunner::stop);
		state=PluginState.STOPPED;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public Class<?> getPluginClass() {
		return pluginClass;
	}

	public Collection<PluginRunner> getRunners() {
		return runners;
	}

	public PluginState getState() {
		return state;
	}

	@Override
	public String toString() {
		return "PluginWrapper{" +
				"plugin=" + plugin +
				", pluginClass=" + pluginClass +
				", runners=" + runners +
				", loader=" + loader +
				'}';
	}
}
