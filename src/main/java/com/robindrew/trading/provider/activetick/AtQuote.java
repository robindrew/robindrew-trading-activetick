package com.robindrew.trading.provider.activetick;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robindrew.common.date.Dates;
import com.robindrew.common.util.Check;
import com.robindrew.trading.price.tick.PriceTick;
import com.robindrew.trading.provider.activetick.platform.AtInstrument;

import at.shared.ATServerAPIDefines.ATExchangeType;
import at.shared.ATServerAPIDefines.ATQUOTESTREAM_QUOTE_UPDATE;
import at.shared.ATServerAPIDefines.ATQuoteConditionType;

public class AtQuote {

	private static final Logger log = LoggerFactory.getLogger(AtQuote.class);

	public static AtQuote create(ATQUOTESTREAM_QUOTE_UPDATE update) {

		// Sanity Checks
		if (update.bidExchange.m_atExchangeType != ATExchangeType.Forex) {
			log.warn("[Invalid Quote Exchange] {}", AtHelper.toString(update));
			return null;
		}
		if (update.askExchange.m_atExchangeType != ATExchangeType.Forex) {
			log.warn("[Invalid Quote Exchange] {}", AtHelper.toString(update));
			return null;
		}
		if (update.condition.m_quoteConditionType != ATQuoteConditionType.QuoteConditionRegular) {
			log.warn("[Invalid Quote Condition] {}", AtHelper.toString(update));
			return null;
		}

		// Parse instrument
		AtInstrument instrument = AtHelper.getInstrument(update.symbol);

		// Parse timestamp
		LocalDateTime date = AtHelper.toLocalDateTime(update.quoteDateTime);
		long timestamp = Dates.toMillis(date);

		// Calculate mid
		double bid = update.bidPrice.price;
		double ask = update.askPrice.price;

		// Calculate precision
		int bidPrecision = update.bidPrice.precision;
		int askPrecision = update.askPrice.precision;
		int decimalPlaces = (bidPrecision > askPrecision ? bidPrecision : askPrecision) + 1;

		// Convert price
		int bidPrice = AtHelper.toBigInt(bid, decimalPlaces);
		int askPrice = AtHelper.toBigInt(ask, decimalPlaces);
		PriceTick candle = new PriceTick(bidPrice, askPrice, timestamp, decimalPlaces);

		return new AtQuote(instrument, candle);
	}

	private final AtInstrument instrument;
	private final PriceTick tick;

	public AtQuote(AtInstrument instrument, PriceTick tick) {
		this.instrument = Check.notNull("instrument", instrument);
		this.tick = Check.notNull("tick", tick);
	}

	public AtInstrument getInstrument() {
		return instrument;
	}

	public PriceTick getTick() {
		return tick;
	}
}
