package com.robindrew.trading.provider.activetick.platform;

import static at.feedapi.ActiveTickServerAPI.DEFAULT_REQUEST_TIMEOUT;
import static at.shared.ATServerAPIDefines.ATStreamRequestType.StreamRequestSubscribe;
import static com.robindrew.common.util.Check.notNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.robindrew.common.util.Check;
import com.robindrew.common.util.Java;
import com.robindrew.common.util.Threads;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.price.candle.IPriceCandle;
import com.robindrew.trading.provider.activetick.AtException;
import com.robindrew.trading.provider.activetick.AtHelper;

import at.feedapi.ATCallback;
import at.feedapi.ATCallback.ATLoginResponseCallback;
import at.feedapi.ATCallback.ATRequestTimeoutCallback;
import at.feedapi.ATCallback.ATSessionStatusChangeCallback;
import at.feedapi.ActiveTickServerAPI;
import at.feedapi.Helpers;
import at.feedapi.Session;
import at.shared.ATServerAPIDefines;
import at.shared.ATServerAPIDefines.ATBarHistoryType;
import at.shared.ATServerAPIDefines.ATLOGIN_RESPONSE;
import at.shared.ATServerAPIDefines.ATSYMBOL;
import at.shared.ATServerAPIDefines.ATSessionStatusType;
import at.shared.ATServerAPIDefines.ATStreamRequestType;
import at.shared.ATServerAPIDefines.SYSTEMTIME;
import at.utils.jlib.Errors;

public class AtConnection implements AutoCloseable {

	private static final Logger log = LoggerFactory.getLogger(AtConnection.class);

	public static final String DEFAULT_HOST = "activetick1.activetick.com";
	public static final int DEFAULT_PORT = 443;

	private final AtCredentials credentials;
	private final ActiveTickServerAPI api;
	private final ConnectionStatus status = new ConnectionStatus();
	private Session session;

	private AtStreamListener streamListener;
	private AtRequestor requestor;

	public AtConnection(AtCredentials credentials) {
		this.credentials = notNull("credentials", credentials);
		this.api = new ActiveTickServerAPI();
	}

	public void connect() {
		connect(DEFAULT_HOST, DEFAULT_PORT);
	}

	public void connect(String host, int port) {
		log.info("[Connect] {}:{}", host, port);

		// Initialise the API
		api.ATInitAPI();

		// Create the session
		if (session != null) {
			api.ATShutdownSession(session);
		}
		session = api.ATCreateSession();
		streamListener = new AtStreamListener(session);
		requestor = new AtRequestor(api, session, streamListener);

		// Set the API key
		long code = api.ATSetAPIKey(session, credentials.getATGUID());
		if (code != Errors.ERROR_SUCCESS) {
			throw new AtException("ATSetAPIKey failed");
		}

		// Initialise the session
		api.ATInitSession(session, DEFAULT_HOST, DEFAULT_HOST, DEFAULT_PORT, status);
		if (!status.isConnected()) {
			throw new AtException("Failed to connect: " + status);
		}

		log.info("[Version] {}", api.GetAPIVersionInformation());
	}

	public boolean isConnected() {
		return session.IsConnected();
	}

	@Override
	public void close() {
		if (session != null) {
			api.ATShutdownSession(session);
			session = null;
		}
		api.ATShutdownAPI();
	}

	public void subscribe(IInstrument... instruments) {
		subscribe(Arrays.asList(instruments));
	}

	public void subscribe(Collection<? extends IInstrument> instruments) {

		for (IInstrument instrument : instruments) {
			log.info("[Subscribe] Instrument: {}", instrument);
		}

		// Instruments to subscribe for
		List<ATSYMBOL> symbols = AtHelper.toSymbolList(instruments);

		// Subscribe
		ATStreamRequestType requestType = (new ATServerAPIDefines()).new ATStreamRequestType();
		requestType.m_streamRequestType = StreamRequestSubscribe;

		long requestId = requestor.SendATQuoteStreamRequest(symbols, requestType, DEFAULT_REQUEST_TIMEOUT);
		AtHelper.throwError(requestId);
		log.info("[Subscribe] RequestId: {}", requestId);
	}

	public List<IPriceCandle> getPriceHistory(IInstrument instrument, LocalDateTime from, LocalDateTime to) {
		return getPriceHistory(instrument, from, to, 1);
	}

	public List<IPriceCandle> getPriceHistory(IInstrument instrument, LocalDateTime from, LocalDateTime to, int intervalInMinutes) {
		if (intervalInMinutes < 1) {
			throw new IllegalArgumentException("intervalInMinutes=" + intervalInMinutes);
		}

		Check.notNull("instrument", instrument);
		Check.notNull("from", from);
		Check.notNull("to", to);

		log.info("[GetHistory] Instrument: " + instrument);
		log.info("[GetHistory] FromDate: " + from);
		log.info("[GetHistory] ToDate: " + to);

		// Instrument
		ATSYMBOL symbol = Helpers.StringToSymbol(instrument.getName());

		// Dates
		SYSTEMTIME fromDate = AtHelper.toSystemTime(from);
		SYSTEMTIME toDate = AtHelper.toSystemTime(to.minusSeconds(1));

		ATBarHistoryType barHistoryType = AtHelper.newBarHistoryType();

		long requestId = requestor.SendATBarHistoryDbRequest(symbol, barHistoryType, (short) intervalInMinutes, fromDate, toDate, DEFAULT_REQUEST_TIMEOUT);
		AtHelper.throwError(requestId);
		log.info("[GetHistory] RequestId: {}", requestId);

		List<IPriceCandle> candles = requestor.getResponse(requestId);
		log.info("[GetHistory] {} Price Candles", candles.size());
		if (!candles.isEmpty()) {
			log.info("[GetHistory] First: {}", candles.get(0));
			log.info("[GetHistory] Last: {}", candles.get(candles.size() - 1));
		}
		return candles;
	}

	public void login() {
		LoginResponseCallback response = new LoginResponseCallback();
		log.info("[Login] User: {}", credentials.getUsername());
		log.info("[Login] Key: {}", credentials.getApiKey());

		long id = api.ATCreateLoginRequest(session, credentials.getUsername(), credentials.getPassword(), response);
		log.info("[Login] RequestId: {}", id);

		boolean success = api.ATSendRequest(session, id, ActiveTickServerAPI.DEFAULT_REQUEST_TIMEOUT, response);
		if (!success) {
			throw new IllegalArgumentException("Failed to send login request");
		}

		if (!response.isLoggedIn()) {
			throw new IllegalArgumentException("Login failed on server");
		}
		log.info("[Login] Success");
	}

	private class LoginResponseCallback extends ATCallback implements ATLoginResponseCallback, ATRequestTimeoutCallback {

		private final CompletableFuture<ATLOGIN_RESPONSE> futureResponse = new CompletableFuture<>();

		@Override
		public void process(Session session, long id, ATLOGIN_RESPONSE response) {
			futureResponse.complete(response);
		}

		public boolean isLoggedIn() {
			try {
				return AtHelper.isLoggedIn(futureResponse.get());
			} catch (ExecutionException ee) {
				throw Java.propagate(ee.getCause());
			} catch (Exception e) {
				throw new AtException(e);
			}
		}

		@Override
		public void process(long id) {
			log.warn("Login request {} timed out", id);
			futureResponse.completeExceptionally(new AtException("Login request " + id + " timed out"));
		}
	}

	private class ConnectionStatus implements ATSessionStatusChangeCallback {

		private final AtomicReference<ATSessionStatusType> type = new AtomicReference<>();

		@Override
		public void process(Session session, ATSessionStatusType type) {
			this.type.set(type);
			log.info("[Status] " + AtHelper.toString(type));
		}

		public ATSessionStatusType getType() {
			waitUntilSet();
			return type.get();
		}

		public boolean isConnected() {
			return AtHelper.isConnected(getType());
		}

		private void waitUntilSet() {
			while (type.get() == null) {
				Threads.sleep(10);
			}
		}

		@Override
		public String toString() {
			return AtHelper.toString(getType());
		}

	}
}
