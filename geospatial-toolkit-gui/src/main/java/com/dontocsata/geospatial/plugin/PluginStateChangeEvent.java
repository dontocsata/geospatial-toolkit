package com.dontocsata.geospatial.plugin;

/**
 * Created by ray.douglass on 9/21/15.
 */
public class PluginStateChangeEvent {

	private PluginWrapper plugin;
	private PluginState oldState;
	private PluginState newState;

	public PluginStateChangeEvent(PluginWrapper plugin, PluginState oldState){
		this(plugin,oldState,plugin.getState());
	}

	public PluginStateChangeEvent(PluginWrapper plugin, PluginState oldState, PluginState newState) {
		this.plugin = plugin;
		this.oldState = oldState;
		this.newState = newState;
	}

	public PluginWrapper getPlugin() {
		return plugin;
	}

	public PluginState getOldState() {
		return oldState;
	}

	public PluginState getNewState() {
		return newState;
	}

	@Override
	public String toString() {
		return "PluginStateChangeEvent{" +
				"plugin=" + plugin.getPluginClass() +
				", oldState=" + oldState +
				", newState=" + newState +
				'}';
	}
}
