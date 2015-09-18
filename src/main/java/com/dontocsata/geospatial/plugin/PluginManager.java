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
import java.util.Set;

/**
 * Created by ray.douglass on 9/18/15.
 */
public class PluginManager {

	private static final Logger log = LoggerFactory.getLogger(PluginManager.class);

	private GenericApplicationContext context;
	private File pluginDirectory = new File("plugins");

	private Collection<PluginWrapper> allPlugins = new ArrayList<>();

	public PluginManager(GenericApplicationContext context) {
		this.context = context;
	}

	public void loadPlugins() throws IOException {
		//System plugins
		allPlugins.addAll(findPlugins(getClass().getClassLoader(), true));
		//Other plugins
		if (pluginDirectory.exists() && pluginDirectory.isDirectory()) {
			for (File file : pluginDirectory.listFiles()) {
				URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
				allPlugins.addAll(findPlugins(classLoader, false));
			}
		}
	}

	public void startPlugins() {
		allPlugins.forEach(PluginWrapper::start);
	}

	public Collection<PluginWrapper> getAllPlugins() {
		return allPlugins;
	}

	private Collection<PluginWrapper> findPlugins(ClassLoader classLoader, boolean systemPlugins) {
		Collection<PluginWrapper> result = new ArrayList<>();
		Reflections reflections = systemPlugins ? new Reflections("com.dontocsata.geospatial") : new Reflections(classLoader);
		Set<Class<?>> klasses = reflections.getTypesAnnotatedWith(Plugin.class);
		for (Class<?> klass : klasses) {
			Plugin plugin = klass.getAnnotation(Plugin.class);
			Collection<PluginRunner> runners = new ArrayList<>();
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
		}
		return result;
	}

}
