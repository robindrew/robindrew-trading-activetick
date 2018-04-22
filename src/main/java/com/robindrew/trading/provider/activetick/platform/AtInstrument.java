package com.robindrew.trading.provider.activetick.platform;

import java.util.LinkedHashSet;
import java.util.Set;

import com.robindrew.trading.IInstrument;
import com.robindrew.trading.Instrument;
import com.robindrew.trading.Instruments;

public class AtInstrument extends Instrument implements IAtInstrument {

	/** AUD/USD. */
	public static final AtInstrument AUD_USD = new AtInstrument("#AUD/USD", Instruments.AUD_USD);
	/** EUR/JPY. */
	public static final AtInstrument EUR_JPY = new AtInstrument("#EUR/JPY", Instruments.EUR_JPY);
	/** EUR/USD. */
	public static final AtInstrument EUR_USD = new AtInstrument("#EUR/USD", Instruments.EUR_USD);
	/** GBP/USD. */
	public static final AtInstrument GBP_USD = new AtInstrument("#GBP/USD", Instruments.GBP_USD);
	/** USD/JPY. */
	public static final AtInstrument NZD_USD = new AtInstrument("#NZD/USD", Instruments.NZD_USD);
	/** USD/CAD. */
	public static final AtInstrument USD_CAD = new AtInstrument("#USD/CHF", Instruments.USD_CAD);
	/** USD/CHF. */
	public static final AtInstrument USD_CHF = new AtInstrument("#USD/CHF", Instruments.USD_CHF);
	/** USD/JPY. */
	public static final AtInstrument USD_JPY = new AtInstrument("#USD/JPY", Instruments.USD_JPY);

	public static final Set<AtInstrument> values() {
		Set<AtInstrument> set = new LinkedHashSet<>();
		set.add(AUD_USD);
		set.add(EUR_JPY);
		set.add(EUR_USD);
		set.add(GBP_USD);
		set.add(NZD_USD);
		set.add(USD_CAD);
		set.add(USD_CHF);
		set.add(USD_JPY);
		return set;
	}

	public static final AtInstrument valueOf(String text) {
		for (AtInstrument instrument : values()) {
			if (instrument.getName().equals(text) || instrument.getUnderlying(true).getName().equals(text)) {
				return instrument;
			}
		}
		if (!text.startsWith("#")) {
			return valueOf("#" + text);
		}
		throw new IllegalArgumentException("text: " + text);
	}

	public AtInstrument(String name, IInstrument underlying) {
		super(name, underlying);
	}

}
