package com.dontocsata.geospatial;

import java.util.function.Function;

public class StreamUtils {

	public static interface ExceptionFunction<T, R> {

		public R apply(T t) throws Exception;
	}

	public static <T, R> Function<T, R> rethrow(ExceptionFunction<T, R> function) {
		return new Function<T, R>() {

			@Override
			public R apply(T t) {
				try {
					return function.apply(t);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};

	}
}
