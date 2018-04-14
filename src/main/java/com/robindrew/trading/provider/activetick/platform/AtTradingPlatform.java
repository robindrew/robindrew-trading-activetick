package com.robindrew.trading.provider.activetick.platform;

import com.robindrew.common.util.Check;
import com.robindrew.trading.platform.positions.IPositionService;
import com.robindrew.trading.platform.positions.UnavailablePositionService;
import com.robindrew.trading.platform.streaming.IStreamingService;
import com.robindrew.trading.price.history.IHistoryService;
import com.robindrew.trading.provider.activetick.platform.history.AtHistoryService;
import com.robindrew.trading.provider.activetick.platform.streaming.AtStreamingService;

public class AtTradingPlatform implements IAtTradingPlatform {

	private final AtStreamingService streaming;
	private final AtHistoryService history;
	private final IPositionService position = new UnavailablePositionService();

	public AtTradingPlatform(AtStreamingService streaming, AtHistoryService history) {
		this.streaming = Check.notNull("streaming", streaming);
		this.history = Check.notNull("history", history);
	}

	@Override
	public IHistoryService getHistoryService() {
		return history;
	}

	@Override
	public IStreamingService getStreamingService() {
		return streaming;
	}

	@Override
	public IPositionService getPositionService() {
		return position;
	}

}
