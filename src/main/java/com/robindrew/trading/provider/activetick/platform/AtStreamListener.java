package com.robindrew.trading.provider.activetick.platform;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robindrew.trading.IInstrument;
import com.robindrew.trading.platform.streaming.IInstrumentPriceStream;
import com.robindrew.trading.price.candle.ITickPriceCandle;
import com.robindrew.trading.provider.activetick.AtHelper;
import com.robindrew.trading.provider.activetick.AtQuote;

import at.feedapi.ActiveTickStreamListener;
import at.feedapi.Session;
import at.shared.ATServerAPIDefines;
import at.shared.ATServerAPIDefines.ATQUOTESTREAM_TRADE_UPDATE;

public class AtStreamListener extends ActiveTickStreamListener implements Runnable, AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(AtStreamListener.class);

	private final Map<IInstrument, IInstrumentPriceStream<IAtInstrument>> listenerMap = new ConcurrentHashMap<>();
	private final BlockingDeque<AtQuote> quoteQueue = new LinkedBlockingDeque<>();
	private final Thread thread;
	private final AtomicBoolean closed = new AtomicBoolean(false);

	public AtStreamListener(Session session) {
		super(session, false);

		this.thread = new Thread(this, "AtStreamListener");
	}

	@Override
	public void OnATStreamRefreshUpdate(at.shared.ATServerAPIDefines.ATQUOTESTREAM_REFRESH_UPDATE update) {
		// How do we want to handle refresh updates?
	};

	@Override
	public void OnATStreamTradeUpdate(ATQUOTESTREAM_TRADE_UPDATE update) {
		// How do we want to handle trade updates?
	}

	@Override
	public void OnATStreamQuoteUpdate(ATServerAPIDefines.ATQUOTESTREAM_QUOTE_UPDATE update) {
		if (log.isDebugEnabled()) {
			log.debug("[Tick] {}", AtHelper.toString(update));
		}

		try {
			AtQuote quote = AtQuote.create(update);
			if (quote != null) {
				quoteQueue.addLast(quote);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void register(IInstrumentPriceStream<IAtInstrument> stream) {
		IInstrument instrument = stream.getInstrument();
		instrument = instrument.getUnderlying(true);
		listenerMap.put(instrument, stream);
	}

	public boolean isClosed() {
		return closed.get();
	}

	@Override
	public void run() {
		try {
			while (!isClosed()) {
				AtQuote quote = quoteQueue.takeFirst();

				IInstrument instrument = quote.getInstrument();
				instrument = instrument.getUnderlying(true);
				IInstrumentPriceStream<IAtInstrument> priceStream = listenerMap.get(instrument);
				if (priceStream != null) {
					ITickPriceCandle tick = quote.getTick();
					priceStream.putNextCandle(tick);
				}
			}
		} catch (InterruptedException e) {
			log.info("Stream listener interrupted");
		} catch (Exception e) {
			log.error("Stream listener crashed", e);
		} finally {
			closed.set(true);
		}
	}

	public void start() {
		thread.start();
	}

	@Override
	public void close() {
		closed.set(true);
		thread.interrupt();
	}
}
