package com.dontocsata.geospatial.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javafx.fxml.FXMLLoader;

/**
 * Resolves FXML Templates stored relative to classpath:/layouts/
 */
@Component
public class FxmlTemplateResolver {

	@Autowired
	private ResourceLoader resourceLoader;

	public FxmlTemplateResolver() {

	}

	public FxmlTemplateResolver(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
	 * Loads a template given the name and controller. The provided controller will be set on the FXML.
	 */
	public <T> T loadTemplate(String templateName, Object controller) throws IOException {
		Resource resource = resourceLoader.getResource("classpath:/layouts/" + templateName);
		if (!resource.exists()) {
			throw new IllegalArgumentException("Resource not found: " + templateName);
		}
		FXMLLoader fxmlLoader = new FXMLLoader(resource.getURL());
		fxmlLoader.setController(controller);
		return fxmlLoader.load();
	}
}
