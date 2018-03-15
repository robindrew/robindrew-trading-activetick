package com.robindrew.trading.provider.activetick.platform;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robindrew.common.util.SystemProperties;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.format.pcf.PcfFormat;
import com.robindrew.trading.price.candle.format.pcf.source.file.IPcfFile;
import com.robindrew.trading.price.candle.format.pcf.source.file.PcfFile;

public class ActiveTickTests {

	private static final Logger log = LoggerFactory.getLogger(ActiveTickTests.class);

	@Test
	public void historyDownloadTest() {

		String apiKey = SystemProperties.get("apiKey", false);
		String username = SystemProperties.get("username", false);
		String password = SystemProperties.get("password", false);

		File directory = new File("C:\\development\\repository\\git\\robindrew-public\\robindrew-trading-data\\data\\pcf\\ACTIVETICK\\CURRENCIES\\USDJPY");

		AtCredentials credentials = new AtCredentials(apiKey, username, password);
		try (AtConnection connector = new AtConnection(credentials)) {
			connector.connect();
			connector.login();

			IInstrument instrument = AtInstrument.USD_JPY;

			LocalDateTime date = getDate();

			while (true) {
				List<IPriceCandle> candles = getPriceHistory(connector, instrument, date);
				if (candles.isEmpty()) {
					break;
				}

				LocalDate month = date.toLocalDate();
				String filename = PcfFormat.getFilename(month);

				IPcfFile file = new PcfFile(new File(directory, filename), month);
				file.write(candles);

				date = date.minusMonths(1);
			}
		}
	}

	private List<IPriceCandle> getPriceHistory(AtConnection connector, IInstrument instrument, LocalDateTime date) {
		List<IPriceCandle> monthList = new ArrayList<>();

		LocalDateTime fromMonth = date;
		LocalDateTime toMonth = date.plusMonths(1);

		LocalDateTime fromDay = date;
		while (!fromDay.equals(toMonth)) {
			LocalDateTime toDay = fromDay.plusDays(1);
			if (toDay.isAfter(toMonth)) {
				break;
			}

			List<IPriceCandle> dayList = connector.getPriceHistory(instrument, fromDay, toDay);
			monthList.addAll(dayList);
			fromDay = toDay;
		}

		return monthList;
	}

	private LocalDateTime getDate() {
		LocalDate now = LocalDate.now();
		LocalDateTime date = LocalDateTime.of(now, LocalTime.of(0, 0));
		date = date.withDayOfMonth(1);
		date = date.minusMonths(1);
		return date;
	}
}
