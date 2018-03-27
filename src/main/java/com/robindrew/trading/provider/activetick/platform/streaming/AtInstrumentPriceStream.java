package com.robindrew.trading.provider.activetick.platform.streaming;

import com.robindrew.common.util.Check;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.platform.streaming.InstrumentPriceStream;
import com.robindrew.trading.price.candle.io.stream.sink.subscriber.IInstrumentPriceStreamListener;

public class AtInstrumentPriceStream extends InstrumentPriceStream {

	private final AtInstrumentPriceStreamListener listener;

	public AtInstrumentPriceStream(IInstrument instrument, AtInstrumentPriceStreamListener listener) {
		super(instrument, listener.getLatestPrice());
		this.listener = Check.notNull("listener", listener);
	}

	public AtInstrumentPriceStream(IInstrument instrument) {
		this(instrument, new AtInstrumentPriceStreamListener(instrument));
	}

	@Override
	public IInstrumentPriceStreamListener getListener() {
		return listener;
	}

	@Override
	public void close() {
	}

}
