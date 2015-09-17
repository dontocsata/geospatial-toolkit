package com.dontocsata.geospatial.layer;

import com.lynden.gmapsfx.javascript.event.UIEventType;

public enum MapLayerEventType {

	CLICK(UIEventType.click),
	DOUBLE_CLICK(UIEventType.dblclick),
	RIGHT_CLICK(UIEventType.rightclick);

	private UIEventType uiEventType;

	private MapLayerEventType(UIEventType uiEventType) {
		this.uiEventType = uiEventType;
	}

	public UIEventType getUiEventType() {
		return uiEventType;
	}

}
