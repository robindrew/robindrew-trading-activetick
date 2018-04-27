package com.robindrew.trading.provider.activetick.platform;

import com.robindrew.common.util.Check;
import com.robindrew.trading.platform.TradingPlatform;
import com.robindrew.trading.platform.account.IAccountService;
import com.robindrew.trading.provider.activetick.platform.history.AtHistoryService;
import com.robindrew.trading.provider.activetick.platform.streaming.AtStreamingService;

public class AtTradingPlatform extends TradingPlatform<IAtInstrument> implements IAtTradingPlatform {

	private final IAtCredentials credentials;
	private final AtAccountService account;
	private final AtStreamingService streaming;
	private final AtHistoryService history;

	public AtTradingPlatform(IAtCredentials credentials, AtStreamingService streaming, AtHistoryService history) {
		this.credentials = Check.notNull("credentials", credentials);;
		this.streaming = Check.notNull("streaming", streaming);
		this.history = Check.notNull("history", history);
		this.account = new AtAccountService(credentials);
	}

	@Override
	public AtHistoryService getHistoryService() {
		return history;
	}

	@Override
	public AtStreamingService getStreamingService() {
		return streaming;
	}

	@Override
	public IAccountService getAccountService() {
		return account;
	}

}
