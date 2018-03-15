package com.robindrew.trading.provider.activetick.platform;

import at.feedapi.ActiveTickStreamListener;
import at.feedapi.Session;

public class AtStreamListener extends ActiveTickStreamListener {

	public AtStreamListener(Session session) {
		super(session, false);
	}

}
