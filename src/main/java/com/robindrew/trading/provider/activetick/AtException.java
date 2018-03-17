package com.robindrew.trading.provider.activetick;

import com.robindrew.trading.TradingException;

public class AtException extends TradingException {

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
