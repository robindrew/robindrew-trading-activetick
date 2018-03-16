package com.robindrew.trading.provider.activetick.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robindrew.trading.provider.activetick.AtHelper;

import at.feedapi.ActiveTickStreamListener;
import at.feedapi.Session;
import at.shared.ATServerAPIDefines;

public class AtStreamListener extends ActiveTickStreamListener {

	private static final Logger log = LoggerFactory.getLogger(AtStreamListener.class);

	public AtStreamListener(Session session) {
		super(session, false);
	}

	@Override
	public void OnATStreamQuoteUpdate(ATServerAPIDefines.ATQUOTESTREAM_QUOTE_UPDATE update) {
		log.info("[Tick] {}", AtHelper.toString(update));
	}
}
