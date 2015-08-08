package com.dontocsata.geospatial.handlers;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.dontocsata.geospatial.CommandHandler;
import com.dontocsata.geospatial.MenuItemDescriptor;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;

import javafx.scene.control.TextInputDialog;

@Component
public class ZoomController implements CommandHandler {

	@Override
	public void invoke(GoogleMap map) throws Exception {
		System.out.println(map.fromLatLngToPoint(new LatLong(10, 0)));
		System.out.println(map.fromLatLngToPoint(new LatLong(11, 0)));
		Optional<String> result = new TextInputDialog(Integer.toString(map.getZoom())).showAndWait();
		if (result.isPresent()) {
			Integer i = Integer.parseInt(result.get());
			map.setZoom(i);
		}
	}

	@Override
	public MenuItemDescriptor getMenuItemDescriptor() {
		return new MenuItemDescriptor("File", "Zoom");
	}

}
