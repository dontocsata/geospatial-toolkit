package com.dontocsata.geospatial.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows a {@link PluginRunner} to bind itself to a Menu Item.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MenuItemBinding {

	public String menu();
	public String name();
}
