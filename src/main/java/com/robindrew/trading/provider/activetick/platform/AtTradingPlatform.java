package com.robindrew.trading.provider.activetick.platform;

import com.robindrew.common.util.Check;
import com.robindrew.trading.platform.TradingPlatform;
import com.robindrew.trading.provider.activetick.platform.history.AtHistoryService;
import com.robindrew.trading.provider.activetick.platform.streaming.AtStreamingService;

public class AtTradingPlatform extends TradingPlatform<IAtInstrument> implements IAtTradingPlatform {

	private final AtStreamingService streaming;
	private final AtHistoryService history;

	public AtTradingPlatform(AtStreamingService streaming, AtHistoryService history) {
		this.streaming = Check.notNull("streaming", streaming);
		this.history = Check.notNull("history", history);
	}

	@Override
	public AtHistoryService getHistoryService() {
		return history;
	}

	@Override
	public AtStreamingService getStreamingService() {
		return streaming;
	}
}
