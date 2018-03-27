package com.robindrew.trading.provider.activetick.platform.streaming;

import com.robindrew.trading.IInstrument;
import com.robindrew.trading.platform.streaming.latest.LatestPrice;
import com.robindrew.trading.price.candle.io.stream.sink.subscriber.PriceCandleSubscriberStreamSink;

public class AtInstrumentPriceStreamListener extends PriceCandleSubscriberStreamSink {

	private final LatestPrice latest = new LatestPrice();

	public AtInstrumentPriceStreamListener(IInstrument instrument) {
		super(instrument);
	}

	public LatestPrice getLatestPrice() {
		return latest;
	}

}
