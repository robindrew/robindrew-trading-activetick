package com.robindrew.trading.provider.activetick.platform.streaming;

import com.robindrew.trading.IInstrument;
import com.robindrew.trading.platform.streaming.InstrumentPriceStream;

public class AtInstrumentPriceStream extends InstrumentPriceStream {

	public AtInstrumentPriceStream(IInstrument instrument) {
		super(instrument);
	}

	@Override
	public void close() {
	}

}
