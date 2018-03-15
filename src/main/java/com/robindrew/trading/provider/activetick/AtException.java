package com.robindrew.trading.provider.activetick;

public class AtException extends RuntimeException {

	private static final long serialVersionUID = -4813398872671246225L;

	public AtException(String message) {
		super(message);
	}

	public AtException(Throwable cause) {
		super(cause);
	}

	public AtException(String message, Throwable cause) {
		super(message, cause);
	}

}
