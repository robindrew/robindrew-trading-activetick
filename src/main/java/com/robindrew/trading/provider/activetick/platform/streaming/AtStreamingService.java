package com.robindrew.trading.provider.activetick.platform.streaming;

import java.util.concurrent.atomic.AtomicBoolean;

import com.robindrew.trading.platform.streaming.StreamingService;
import com.robindrew.trading.provider.activetick.platform.AtConnection;
import com.robindrew.trading.provider.activetick.platform.IAtInstrument;

public class AtStreamingService extends StreamingService<IAtInstrument> implements IAtStreamingService {

	private final AtConnection connection;
	private final AtomicBoolean closed = new AtomicBoolean(false);

	public AtStreamingService(AtConnection connection) {
		this.connection = connection;
	}

	@Override
	public boolean subscribe(IAtInstrument instrument) {
		if (closed.get()) {
			throw new IllegalStateException("Service closed");
		}
		if (isSubscribed(instrument)) {
			return true;
		}

		// Create the underlying stream
		AtInstrumentPriceStream stream = new AtInstrumentPriceStream(instrument);
		super.registerStream(stream);

		// Subscribe
		connection.getStreamListener().register(stream);
		connection.subscribe(stream.getInstrument());

		return false;
	}

	@Override
	public boolean unsubscribe(IAtInstrument instrument) {
		if (isSubscribed(instrument)) {
			unregisterStream(instrument);
			return true;
		}
		return false;
	}
}
