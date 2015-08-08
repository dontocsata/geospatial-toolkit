package com.dontocsata.geospatial;


public class GeometryException extends Exception {

	private static final long serialVersionUID = 897024508283198089L;

	public GeometryException() {
		super();
	}

	public GeometryException(String message) {
		super(message);
	}

	public GeometryException(Throwable cause) {
		super(cause);
	}

	public GeometryException(String message, Throwable cause) {
		super(message, cause);
	}
}
