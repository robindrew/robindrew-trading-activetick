package com.robindrew.trading.provider.activetick.platform.history;

import java.time.LocalDateTime;
import java.util.List;

import com.robindrew.common.util.Check;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.interval.IPriceCandleInterval;
import com.robindrew.trading.price.candle.io.stream.source.IPriceCandleStreamSource;
import com.robindrew.trading.price.candle.io.stream.source.PriceCandleListBackedStreamSource;
import com.robindrew.trading.price.history.IInstrumentPriceHistory;
import com.robindrew.trading.provider.activetick.platform.AtConnection;

public class AtInstrumentPriceHistory implements IInstrumentPriceHistory {

	private static final LocalDateTime HISTORY_FROM_DATE = LocalDateTime.of(2013, 10, 01, 0, 0);

	private final IInstrument instrument;
	private final AtConnection connection;

	public AtInstrumentPriceHistory(IInstrument instrument, AtConnection connection) {
		this.instrument = Check.notNull("instrument", instrument);
		this.connection = Check.notNull("connection", connection);
	}

	@Override
	public IInstrument getInstrument() {
		return instrument;
	}

	@Override
	public List<IPriceCandle> getPriceCandles(LocalDateTime from, LocalDateTime to) {
		return connection.getPriceHistory(instrument, from, to);
	}

	@Override
	public IPriceCandleStreamSource getStreamSource(LocalDateTime from, LocalDateTime to) {
		return new PriceCandleListBackedStreamSource(getPriceCandles(from, to));
	}

	@Override
	public IPriceCandleStreamSource getStreamSource() {
		return getStreamSource(HISTORY_FROM_DATE, LocalDateTime.now());
	}

	@Override
	public List<IPriceCandle> getLatestPrices(IPriceCandleInterval interval, int count) {
		throw new UnsupportedOperationException();
	}

}
