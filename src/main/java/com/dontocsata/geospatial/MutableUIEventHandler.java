package com.dontocsata.geospatial;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import com.lynden.gmapsfx.javascript.event.UIEventHandler;

import netscape.javascript.JSObject;

@Component
public class MutableUIEventHandler implements UIEventHandler {

	private List<UIEventHandler> handlers = new CopyOnWriteArrayList<>();

	@Override
	public void handle(JSObject obj) {
		handlers.forEach(h -> h.handle(obj));
	}

	public void add(UIEventHandler handler) {
		if (handler == this) {
			throw new IllegalArgumentException();
		}
		handlers.add(handler);
	}

	public void remove(UIEventHandler handler) {
		handlers.remove(handler);
	}

}
