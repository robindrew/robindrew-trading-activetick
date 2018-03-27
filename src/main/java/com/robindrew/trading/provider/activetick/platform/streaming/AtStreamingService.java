package com.robindrew.trading.provider.activetick.platform.streaming;

import java.util.concurrent.atomic.AtomicBoolean;

import com.robindrew.trading.IInstrument;
import com.robindrew.trading.platform.streaming.IInstrumentPriceStream;
import com.robindrew.trading.platform.streaming.StreamingService;
import com.robindrew.trading.provider.activetick.platform.AtConnection;

public class AtStreamingService extends StreamingService {

	private final AtConnection connection;
	private final AtomicBoolean closed = new AtomicBoolean(false);

	public AtStreamingService(AtConnection connection) {
		this.connection = connection;
	}

	@Override
	public void register(IInstrumentPriceStream stream) {
		if (closed.get()) {
			throw new IllegalStateException("Service closed");
		}
		super.registerStream(stream);

		// Subscribe
		connection.getStreamListener().register(stream.getListener());
		connection.subscribe(stream.getInstrument());
	}

	@Override
	public void unregister(IInstrument instrument) {
		unregisterStream(instrument);
	}

	@Override
	public boolean isConnected() {
		return !closed.get() && connection.isConnected();
	}

	@Override
	public void connect() {
		// Nothing to do
	}

	@Override
	public void close() {
		closed.set(true);
	}

}
