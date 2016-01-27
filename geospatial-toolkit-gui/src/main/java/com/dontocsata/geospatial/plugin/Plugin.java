package com.dontocsata.geospatial.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * Mark a class as a plugin. This provides the meta data about the plugin. The
 * plugin will be identified by the fully qualified class name.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {

	/**
	 * Get the human readable name of the plugin
	 */
	public String name();

	/**
	 * Get the human readable description of the plugin
	 *
	 * @return
	 */
	public String description() default "";

	/**
	 * Get a version name for the plugin
	 */
	public String version() default "";

	/**
	 * Get the PluginRunners for this plugin. This is optional for non-system
	 * plugins. If this field is not set, then the every class that implements
	 * PluginRunner within the plugin's file will be loaded as part of this
	 * plugin.
	 *
	 * @return
	 */
	public Class<? extends PluginRunner>[] runners() default DEFAULT.class;

	/**
	 * Get the plugin classes that this plugin depends on
	 *
	 * @return
	 */
	public Class<?>[] depends() default DEFAULT.class;

	static final class DEFAULT implements PluginRunner {

		@Override
		public Map<String, Object> start() {
			return null;
		}

		@Override
		public void stop() {

		}
	}

}
