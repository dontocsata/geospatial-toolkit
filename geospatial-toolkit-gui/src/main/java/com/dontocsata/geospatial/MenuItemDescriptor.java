package com.dontocsata.geospatial;

import com.google.common.base.Preconditions;

/**
 * Describes a menu and item
 */
public class MenuItemDescriptor {

	private String menuName;
	private String itemName;

	public MenuItemDescriptor(String menuName, String itemName) {
		this.menuName = Preconditions.checkNotNull(menuName);
		this.itemName = Preconditions.checkNotNull(itemName);
	}

	public String getMenuName() {
		return menuName;
	}

	public String getItemName() {
		return itemName;
	}

	public String toId() {
		return menuName + "_" + itemName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		MenuItemDescriptor that = (MenuItemDescriptor) o;

		if (!menuName.equals(that.menuName)) {
			return false;
		}
		return itemName.equals(that.itemName);

	}

	@Override
	public int hashCode() {
		int result = menuName.hashCode();
		result = 31 * result + itemName.hashCode();
		return result;
	}
}
