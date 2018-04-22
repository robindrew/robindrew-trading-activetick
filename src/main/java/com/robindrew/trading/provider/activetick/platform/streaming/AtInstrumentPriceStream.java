package com.robindrew.trading.provider.activetick.platform.streaming;

import com.robindrew.trading.platform.streaming.InstrumentPriceStream;
import com.robindrew.trading.provider.activetick.platform.IAtInstrument;

public class AtInstrumentPriceStream extends InstrumentPriceStream<IAtInstrument> {

	public AtInstrumentPriceStream(IAtInstrument instrument) {
		super(instrument);
	}

	@Override
	public void close() {
	}

}
