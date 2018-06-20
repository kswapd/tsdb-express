package com.dcits.tsdb.exceptions;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */
public class InfluxDBMapperException extends RuntimeException {

	private static final long serialVersionUID = -7328402653918756407L;

	public InfluxDBMapperException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InfluxDBMapperException(final String message) {
		super(message);
	}

	public InfluxDBMapperException(final Throwable cause) {
		super(cause);
	}
}
