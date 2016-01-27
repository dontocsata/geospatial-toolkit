package com.dontocsata.geospatial.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.GenericApplicationContext;

/**
 * Created by ray.douglass on 9/18/15.
 */
public class PluginWrapper {

	private PluginManager manager;

	private Plugin plugin;
	private Class<?> pluginClass;
	private Collection<Class<? extends PluginRunner>> runnerClasses;

	private Collection<PluginRunner> runners = new ArrayList<>();
	private ClassLoader loader;

	private PluginState state = PluginState.LOADED;
	private Exception reason;

	public PluginWrapper(PluginManager manager, Plugin plugin, Class<?> pluginClass,
			Collection<Class<? extends PluginRunner>> runnerClasses) {
		this.manager = manager;
		this.plugin = plugin;
		this.pluginClass = pluginClass;
		this.runnerClasses = runnerClasses;
	}

	/**
	 * Construct the wrapper with the specified PluginState. This is typically
	 * used to mark errors.
	 */
	public PluginWrapper(PluginManager manager, Plugin plugin, Class<?> pluginClass, PluginState state) {
		this.manager = manager;
		this.plugin = plugin;
		this.pluginClass = pluginClass;
		this.state = state;
	}

	public PluginWrapper(PluginManager manager, Plugin plugin, Class<?> pluginClass, PluginState state,
			Exception reason) {
		this(manager, plugin, pluginClass, state);
		this.reason = reason;
	}

	Map<String, Object> start(GenericApplicationContext context) throws Exception {
		Map<String, Object> beans = new HashMap<>();
		for (Class<? extends PluginRunner> klass : runnerClasses) {
			PluginRunner runner = context.getBeanFactory().createBean(klass);
			runners.add(runner);
		}
		for (PluginRunner runner : runners) {
			Map<String, Object> map = runner.start();
			if (map != null) {
				beans.putAll(map);
			}
		}
		setState(PluginState.STARTED);
		return beans;
	}

	void stop() throws Exception {
		runners.forEach(PluginRunner::stop);
		setState(PluginState.STOPPED);
		runners.clear();
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

	void setState(PluginState state, Exception reason) {
		if (state == PluginState.ERROR_LOADING || state == PluginState.ERROR_STARTING) {
			setState(state);
			this.reason = reason;
		} else {
			throw new IllegalStateException("Can only set a reason when there is an error.");
		}
	}

	public Exception getReason() {
		if (state == PluginState.ERROR_LOADING || state == PluginState.ERROR_STARTING) {
			return reason;
		} else {
			throw new IllegalStateException("Can only get tge reason when there is an error.");
		}
	}

	@Override
	public String toString() {
		return "PluginWrapper{" + ", pluginClass=" + pluginClass + ", runners=" + runners + ", loader=" + loader + '}';
	}
}
