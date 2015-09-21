package com.dontocsata.geospatial.plugin;

import java.util.Collection;

/**
 * Created by ray.douglass on 9/18/15.
 */
public class PluginWrapper {

	private PluginManager manager;

	private Plugin plugin;
	private Class<?> pluginClass;
	private Collection<PluginRunner> runners;
	private ClassLoader loader;

	private PluginState state = PluginState.LOADED;

	public PluginWrapper(PluginManager manager, Plugin plugin, Class<?> pluginClass, Collection<PluginRunner> runners) {
		this.manager = manager;
		this.plugin = plugin;
		this.pluginClass = pluginClass;
		this.runners = runners;
	}

	/**
	 * Construct the wrapper with the specified PluginState. This is typically used to mark errors.
	 */
	public PluginWrapper(PluginManager manager, Plugin plugin, Class<?> pluginClass, PluginState state) {
		this.manager = manager;
		this.plugin = plugin;
		this.pluginClass = pluginClass;
		this.state = state;
	}

	public void start() throws Exception {
		for (PluginRunner runner : runners) {
			runner.start();
		}
		setState(PluginState.STARTED);
	}

	public void stop() throws Exception {
		runners.forEach(PluginRunner::stop);
		setState(PluginState.STOPPED);
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

	void setState(PluginState state) {
		PluginState current = getState();
		this.state = state;
		manager.getEventBus().post(new PluginStateChangeEvent(this, current, state));
	}

	@Override
	public String toString() {
		return "PluginWrapper{" +
				", pluginClass=" + pluginClass +
				", runners=" + runners +
				", loader=" + loader +
				'}';
	}
}
