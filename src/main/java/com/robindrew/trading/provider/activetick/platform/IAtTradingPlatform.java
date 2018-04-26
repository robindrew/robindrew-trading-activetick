package com.robindrew.trading.provider.activetick.platform;

import com.robindrew.trading.platform.ITradingPlatform;
import com.robindrew.trading.provider.activetick.platform.streaming.IAtStreamingService;

public interface IAtTradingPlatform extends ITradingPlatform<IAtInstrument> {

	@Override
	IAtStreamingService getStreamingService();

}
