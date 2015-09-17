package com.dontocsata.geospatial.layer;

import com.lynden.gmapsfx.javascript.event.UIEventType;

public enum MapLayerEventType {

	CLICK(UIEventType.click);

	private UIEventType uiEventType;

	private MapLayerEventType(UIEventType uiEventType) {
		this.uiEventType = uiEventType;
	}

	public UIEventType getUiEventType() {
		return uiEventType;
	}

}
