package com.dontocsata.geospatial.plugin;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ray.douglass on 9/18/15.
 */
public class PluginManager {

	private static final Logger log = LoggerFactory.getLogger(PluginManager.class);

	private GenericApplicationContext context;
	private File pluginDirectory = new File("plugins");

	private Map<Class<?>, PluginWrapper> plugins = new LinkedHashMap<>();

	public PluginManager(GenericApplicationContext context) {
		this.context = context;
	}

	/**
	 * Load all of the plugins. Returns the plugins that failed to load.
	 */
	public Collection<PluginWrapper> loadPlugins() throws IOException {
		//System plugins
		findPlugins(getClass().getClassLoader(), true).stream().forEach(pw -> plugins.put(pw.getPluginClass(), pw));
		//Other plugins
		if (pluginDirectory.exists() && pluginDirectory.isDirectory()) {
			for (File file : pluginDirectory.listFiles()) {
				URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
				findPlugins(classLoader, false).stream().forEach(pw -> plugins.put(pw.getPluginClass(), pw));
			}
		}
		return plugins.values().stream().filter(pw -> pw.getState() == PluginState.ERROR_LOADING).collect(Collectors.toList());
	}

	/**
	 * Start all of the loaded plugins. Returns the plugins that failed to started
	 */
	public Collection<PluginWrapper> startPlugins() {
		Collection<PluginWrapper> toRet = new ArrayList<>();
		for (PluginWrapper pw : plugins.values()) {
			if (pw.getState() == PluginState.LOADED) {
				try {
					pw.start();
					pw.setState(PluginState.STARTED);
				} catch (Exception ex) {
					log.warn("Failed to start plugin=" + pw.getPluginClass(), ex);
					pw.setState(PluginState.ERROR_STARTING);
					toRet.add(pw);
				}
			}
		}
		return toRet;
	}

	public void stopPlugins() {
		for (PluginWrapper pw : plugins.values()) {
			if (pw.getState() == PluginState.STARTED) {
				try {
					pw.stop();
				} catch (Exception ex) {
					log.warn("Failed to start plugin=" + pw.getPluginClass(), ex);
				}
				pw.setState(PluginState.STOPPED);
			}
		}
	}

	private Collection<PluginWrapper> findPlugins(ClassLoader classLoader, boolean systemPlugins) {
		Collection<PluginWrapper> result = new ArrayList<>();
		Reflections reflections = systemPlugins ? new Reflections("com.dontocsata.geospatial") : new Reflections(classLoader);
		Set<Class<?>> klasses = reflections.getTypesAnnotatedWith(Plugin.class);
		if (!systemPlugins && klasses.size() > 1) {
			log.warn("Non-system plugin declares multiple @Plugin annotation. Offending class={}", klasses);
			return klasses.stream().map(klass -> {
				Plugin plugin = klass.getAnnotation(Plugin.class);
				return new PluginWrapper(plugin, klass, PluginState.ERROR_LOADING);
			}).collect(Collectors.toList());
		}
		for (Class<?> klass : klasses) {
			Plugin plugin = klass.getAnnotation(Plugin.class);
			Collection<PluginRunner> runners = new ArrayList<>();
			try {
				for (Class<? extends PluginRunner> runnerClass : plugin.runners()) {
					if (!runnerClass.equals(Plugin.DEFAULT.class)) {
						PluginRunner runner = context.getBeanFactory().createBean(runnerClass);
						runners.add(runner);
					}
				}
				if (systemPlugins && runners.isEmpty()) {
					throw new IllegalStateException("System plugins must declare their PluginRunners. Offending Plugin: " + klass.getName());
				} else if (runners.isEmpty()) {
					reflections = new Reflections(klass.getPackage().getName());
					Set<Class<? extends PluginRunner>> runnerClasses = reflections.getSubTypesOf(PluginRunner.class);
					for (Class<? extends PluginRunner> runnerClass : runnerClasses) {
						if (!runnerClass.equals(Plugin.DEFAULT.class)) {
							PluginRunner runner = context.getBeanFactory().createBean(runnerClass);
							runners.add(runner);
						}
					}
				}
				PluginWrapper wrapper = new PluginWrapper(plugin, klass, runners);
				log.info("Found plugin {}", wrapper);
				result.add(wrapper);
			} catch (Exception ex) {
				log.error("Error loading Plugin=" + klass, ex);
				result.add(new PluginWrapper(plugin, klass, PluginState.ERROR_LOADING));
			}
		}

		return result;
	}

}
