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
import com.robindrew.common.util.Threads;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.format.pcf.PcfFormat;
import com.robindrew.trading.price.candle.format.pcf.source.file.IPcfFile;
import com.robindrew.trading.price.candle.format.pcf.source.file.PcfFile;
import com.robindrew.trading.provider.activetick.platform.streaming.AtInstrumentPriceStream;
import com.robindrew.trading.provider.activetick.platform.streaming.AtStreamingService;

public class ActiveTickTests {

	private static final Logger log = LoggerFactory.getLogger(ActiveTickTests.class);

	@Test
	public void subscribeTest() {

		String apiKey = SystemProperties.get("apiKey", false);
		String username = SystemProperties.get("username", false);
		String password = SystemProperties.get("password", false);

		IInstrument instrument = AtInstrument.valueOf(SystemProperties.get("instrument", false));

		AtCredentials credentials = new AtCredentials(apiKey, username, password);
		try (AtConnection connection = new AtConnection(credentials)) {
			connection.connect();
			connection.login();

			try (AtStreamingService service = new AtStreamingService(connection)) {
				AtInstrumentPriceStream stream = new AtInstrumentPriceStream(instrument);
				service.register(stream);

				Threads.sleepForever();
			}
		}
	}

	public void historyDownloadTest() {

		String apiKey = SystemProperties.get("apiKey", false);
		String username = SystemProperties.get("username", false);
		String password = SystemProperties.get("password", false);

		File directory = new File(SystemProperties.get("directory", false));

		AtCredentials credentials = new AtCredentials(apiKey, username, password);
		try (AtConnection connector = new AtConnection(credentials)) {
			connector.connect();
			connector.login();

			for (IInstrument instrument : AtInstrument.values()) {
				File instrumentDir = new File(directory, instrument.getUnderlying(true).getName());
				instrumentDir.mkdir();
				LocalDateTime date = getDate();

				while (true) {

					LocalDate month = date.toLocalDate();
					String filename = PcfFormat.getFilename(month);
					IPcfFile file = new PcfFile(new File(instrumentDir, filename), month);
					if (file.exists()) {
						log.warn("Skipping existing file: " + file.getMonth());
					} else {

						List<IPriceCandle> candles = getPriceHistory(connector, instrument, date);
						if (candles.isEmpty()) {
							break;
						}
						file.write(candles);
					}
					date = date.minusMonths(1);
				}
			}
		}
	}

	private List<IPriceCandle> getPriceHistory(AtConnection connector, IInstrument instrument, LocalDateTime date) {
		List<IPriceCandle> monthList = new ArrayList<>();

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
