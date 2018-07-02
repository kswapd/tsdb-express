package com.dcits.tsdb.exceptions;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */
public class MethodInvocationException extends RuntimeException {

	private static final long serialVersionUID = -7328402653918756407L;

	public MethodInvocationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MethodInvocationException(final String message) {
		super(message);
	}

	public MethodInvocationException(final Throwable cause) {
		super(cause);
	}
}
