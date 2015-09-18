package com.dontocsata.geospatial.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ray.douglass on 9/18/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {

	public String name();

	public String description() default "";

	public String version() default "";

	public Class<? extends PluginRunner>[] runners() default DEFAULT.class;

	public static final class DEFAULT implements PluginRunner{

		@Override
		public void start() {

		}

		@Override
		public void stop() {

		}
	}

}
