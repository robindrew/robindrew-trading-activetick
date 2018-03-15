package com.robindrew.trading.provider.activetick.platform;

import com.robindrew.trading.IInstrument;
import com.robindrew.trading.Instrument;
import com.robindrew.trading.Instruments;

public class AtInstrument extends Instrument {

	/** AUD/USD. */
	public static final AtInstrument AUD_USD = new AtInstrument("#AUD/USD", Instruments.AUD_USD);
	/** EUR/JPY. */
	public static final AtInstrument EUR_JPY = new AtInstrument("#EUR/JPY", Instruments.EUR_JPY);
	/** EUR/USD. */
	public static final AtInstrument EUR_USD = new AtInstrument("#EUR/USD", Instruments.EUR_USD);
	/** GBP/USD. */
	public static final AtInstrument GBP_USD = new AtInstrument("#GBP/USD", Instruments.GBP_USD);
	/** USD/CHF. */
	public static final AtInstrument USD_CHF = new AtInstrument("#USD/CHF", Instruments.USD_CHF);
	/** USD/JPY. */
	public static final AtInstrument USD_JPY = new AtInstrument("#USD/JPY", Instruments.USD_JPY);

	public AtInstrument(String name, IInstrument underlying) {
		super(name, underlying);
	}

}
