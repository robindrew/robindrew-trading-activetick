package com.robindrew.trading.provider.activetick.platform;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.robindrew.common.util.Check;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.platform.streaming.IStreamingService;
import com.robindrew.trading.position.IPosition;
import com.robindrew.trading.position.closed.IClosedPosition;
import com.robindrew.trading.position.order.IPositionOrder;
import com.robindrew.trading.price.history.IHistoryService;
import com.robindrew.trading.price.precision.IPricePrecision;
import com.robindrew.trading.provider.activetick.platform.history.AtHistoryService;
import com.robindrew.trading.provider.activetick.platform.streaming.AtStreamingService;
import com.robindrew.trading.trade.funds.AccountFunds;

public class AtTradingPlatform implements IAtTradingPlatform {

	private final AtStreamingService streaming;
	private final AtHistoryService history;

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
	public IPricePrecision getPrecision(IInstrument instrument) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<IPosition> getAllPositions() {
		return Collections.emptyList();
	}

	@Override
	public List<IPosition> getPositions(IInstrument instrument) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IClosedPosition closePosition(IPosition position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<IPosition, IClosedPosition> closePositions(Collection<? extends IPosition> positions) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AccountFunds getAvailableFunds() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IPosition openPosition(IPositionOrder order) {
		throw new UnsupportedOperationException();
	}

}
