package com.robindrew.trading.provider.activetick.platform;

import static com.robindrew.common.date.Dates.toMillis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.robindrew.common.util.Threads;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.price.candle.MidPriceCandle;
import com.robindrew.trading.provider.activetick.AtHelper;

import at.feedapi.ActiveTickServerAPI;
import at.feedapi.ActiveTickServerRequester;
import at.feedapi.Session;
import at.shared.ATServerAPIDefines;
import at.shared.ATServerAPIDefines.ATBarHistoryResponseType;

public class AtRequestor extends ActiveTickServerRequester {

	private final Map<Long, Object> responseMap = new ConcurrentHashMap<>();

	public AtRequestor(ActiveTickServerAPI api, Session session, AtStreamListener streamListener) {
		super(api, session, streamListener);
	}

	@SuppressWarnings("unchecked")
	public <V> V awaitResponse(long requestId) {
		while (true) {
			Object response = responseMap.remove(requestId);
			if (response != null) {
				return (V) response;
			}
			Threads.sleep(10);
		}
	}

	@Override
	public void OnBarHistoryDbResponse(long requestId, ATBarHistoryResponseType responseType, Vector<ATServerAPIDefines.ATBARHISTORY_RECORD> recordList) {

		// Assumption: all candles are 1 minute bars

		List<IPriceCandle> candles = new ArrayList<>();
		for (ATServerAPIDefines.ATBARHISTORY_RECORD record : recordList) {

			int open = AtHelper.toBigInt(record.open.price, record.open.precision);
			int high = AtHelper.toBigInt(record.high.price, record.high.precision);
			int low = AtHelper.toBigInt(record.low.price, record.low.precision);
			int close = AtHelper.toBigInt(record.close.price, record.close.precision);

			LocalDateTime openTime = AtHelper.toLocalDateTime(record.barTime);
			LocalDateTime closeTime = openTime.plusSeconds(59);

			IPriceCandle candle = new MidPriceCandle(open, high, low, close, toMillis(openTime), toMillis(closeTime), 0);
			candles.add(candle);
		}

		responseMap.put(requestId, candles);
	}
}
