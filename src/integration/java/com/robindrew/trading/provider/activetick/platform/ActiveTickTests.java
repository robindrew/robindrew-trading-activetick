package com.robindrew.trading.provider.activetick.platform;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robindrew.common.util.SystemProperties;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.price.candle.IPriceCandle;

public class ActiveTickTests {

	private static final Logger log = LoggerFactory.getLogger(ActiveTickTests.class);

	@Test
	public void historyTest() {

		String apiKey = SystemProperties.get("apiKey", false);
		String username = SystemProperties.get("username", false);
		String password = SystemProperties.get("password", false);

		AtCredentials credentials = new AtCredentials(apiKey, username, password);
		try (AtConnection connector = new AtConnection(credentials)) {
			connector.connect();
			connector.login();

			IInstrument instrument = AtInstrument.GBP_USD;;
			LocalDateTime from = LocalDateTime.of(2018, 01, 10, 13, 00, 00);
			LocalDateTime to = LocalDateTime.of(2018, 01, 10, 13, 05, 00);
			List<IPriceCandle> candles = connector.getPriceHistory(instrument, from, to);
			for (IPriceCandle candle : candles) {
				log.info(candle.toString());
			}
		}
	}
}
