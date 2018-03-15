package com.robindrew.trading.provider.activetick.platform.history;

import java.util.Set;

import com.robindrew.common.util.Check;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.price.history.IHistoryService;
import com.robindrew.trading.price.history.IInstrumentPriceHistory;
import com.robindrew.trading.provider.activetick.platform.AtConnection;
import com.robindrew.trading.provider.activetick.platform.AtInstrument;

public class AtHistoryService implements IHistoryService {

	private final AtConnection connection;

	public AtHistoryService(AtConnection connection) {
		this.connection = Check.notNull("connection", connection);
	}

	@Override
	public Set<IInstrument> getInstruments() {
		return AtInstrument.getAll();
	}

	@Override
	public IInstrumentPriceHistory getPriceHistory(IInstrument instrument) {
		return new AtInstrumentPriceHistory(instrument, connection);
	}

}
