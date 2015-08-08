package com.dontocsata.geospatial;

/**
 * Describes a menu and item
 */
public class MenuItemDescriptor {

	private String menuName;
	private String itemName;

	public MenuItemDescriptor(String menuName, String itemName) {
		this.menuName = menuName;
		this.itemName = itemName;
	}

	public String getMenuName() {
		return menuName;
	}

	public String getItemName() {
		return itemName;
	}

}
