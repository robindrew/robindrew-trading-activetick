package com.robindrew.trading.provider.activetick;

import static at.shared.ATServerAPIDefines.ATBarHistoryResponseType.BarHistoryResponseDenied;
import static at.shared.ATServerAPIDefines.ATBarHistoryResponseType.BarHistoryResponseInvalidRequest;
import static at.shared.ATServerAPIDefines.ATBarHistoryResponseType.BarHistoryResponseMaxLimitReached;
import static at.shared.ATServerAPIDefines.ATBarHistoryResponseType.BarHistoryResponseSuccess;
import static at.shared.ATServerAPIDefines.ATBarHistoryType.BarHistoryIntraday;
import static at.shared.ATServerAPIDefines.ATLoginResponseType.LoginResponseInvalidPassword;
import static at.shared.ATServerAPIDefines.ATLoginResponseType.LoginResponseInvalidRequest;
import static at.shared.ATServerAPIDefines.ATLoginResponseType.LoginResponseInvalidUserid;
import static at.shared.ATServerAPIDefines.ATLoginResponseType.LoginResponseLoginDenied;
import static at.shared.ATServerAPIDefines.ATLoginResponseType.LoginResponseServerError;
import static at.shared.ATServerAPIDefines.ATLoginResponseType.LoginResponseSuccess;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionAdditionalInformation;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionAdditionalInformationDueToRelatedSecurity;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionAutomatedBidNoOfferNoBid;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionClosed;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionClosing;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionDueToRelatedSecurity;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionDueToRelatedSecurityNewsDissemination;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionDueToRelatedSecurityNewsPending;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionEquipmentChangeover;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionFastTrading;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionInViewOfCommon;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionManualAskAutomaticBid;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionManualBidAndAsk;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionManualBidAutomaticAsk;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionMarketMakerQuotesClosed;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionNewsDissemination;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionNoOpenNoResume;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionNonFirm;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionOpening;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionOrderImbalance;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionOrderInflux;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionRegular;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionRegularOneSidedOpen;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionRegularTwoSidedOpen;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionResume;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionSlowAsk;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionSlowBid;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionSlowBidAsk;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionSlowDueLRPAsk;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionSlowDueLRPBid;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionSlowDueSetSlowListBidAsk;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionSubPennyTrading;
import static at.shared.ATServerAPIDefines.ATQuoteConditionType.QuoteConditionTradingRangeIndication;
import static at.shared.ATServerAPIDefines.ATSessionStatusType.SessionStatusConnected;
import static at.shared.ATServerAPIDefines.ATSessionStatusType.SessionStatusDisconnected;
import static at.shared.ATServerAPIDefines.ATSessionStatusType.SessionStatusDisconnectedDuplicateLogin;
import static at.shared.ATServerAPIDefines.ATSessionStatusType.SessionStatusDisconnectedInactivity;
import static at.shared.ATServerAPIDefines.ATStreamUpdateType.StreamUpdateQuote;
import static at.shared.ATServerAPIDefines.ATStreamUpdateType.StreamUpdateRefresh;
import static at.shared.ATServerAPIDefines.ATStreamUpdateType.StreamUpdateTopMarketMovers;
import static at.shared.ATServerAPIDefines.ATStreamUpdateType.StreamUpdateTrade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.robindrew.common.date.Dates;
import com.robindrew.trading.IInstrument;
import com.robindrew.trading.provider.activetick.platform.AtInstrument;

import at.feedapi.Helpers;
import at.shared.ATServerAPIDefines;
import at.shared.ATServerAPIDefines.ATBarHistoryResponseType;
import at.shared.ATServerAPIDefines.ATBarHistoryType;
import at.shared.ATServerAPIDefines.ATExchangeType;
import at.shared.ATServerAPIDefines.ATLOGIN_RESPONSE;
import at.shared.ATServerAPIDefines.ATLoginResponseType;
import at.shared.ATServerAPIDefines.ATPRICE;
import at.shared.ATServerAPIDefines.ATQuoteConditionType;
import at.shared.ATServerAPIDefines.ATSYMBOL;
import at.shared.ATServerAPIDefines.ATSessionStatusType;
import at.shared.ATServerAPIDefines.ATStreamUpdateType;
import at.shared.ATServerAPIDefines.SYSTEMTIME;
import at.utils.jlib.Errors;

public class AtHelper {

	public static final ZoneId zoneId = ZoneId.of("America/New_York");

	private static final BigDecimal TWO = new BigDecimal(2);

	public static LocalDateTime toUTC(LocalDateTime date) {
		return Dates.convertDateTime(date, zoneId, Dates.UTC_ZONE);
	}

	public static LocalDateTime fromUTC(LocalDateTime date) {
		return Dates.convertDateTime(date, Dates.UTC_ZONE, zoneId);
	}

	public static String getSymbol(ATSYMBOL symbol) {
		return new String(symbol.symbol).trim();
	}

	public static String getSymbol(ATServerAPIDefines.ATQUOTESTREAM_QUOTE_UPDATE update) {
		return getSymbol(update.symbol);
	}

	public static AtInstrument getInstrument(ATSYMBOL symbol) {
		return AtInstrument.valueOf(getSymbol(symbol));
	}

	public static AtInstrument getInstrument(ATServerAPIDefines.ATQUOTESTREAM_QUOTE_UPDATE update) {
		return getInstrument(update.symbol);
	}

	public static BigDecimal getMid(ATServerAPIDefines.ATQUOTESTREAM_QUOTE_UPDATE update) {
		return getMid(update.bidPrice, update.askPrice);
	}

	public static BigDecimal getMid(ATPRICE bidPrice, ATPRICE askPrice) {
		BigDecimal bid = toBigDecimal(bidPrice);
		BigDecimal ask = toBigDecimal(askPrice);
		return ask.subtract(bid).divide(TWO).add(bid);
	}

	public static String toString(ATServerAPIDefines.ATQUOTESTREAM_QUOTE_UPDATE update) {
		ToStringBuilder text = new ToStringBuilder(update, ToStringStyle.SHORT_PREFIX_STYLE);
		String symbol = getSymbol(update);
		text.append("symbol", symbol);
		text.append("date", toLocalDateTime(update.quoteDateTime));
		if (update.bidSize > 0) {
			text.append("bidSize", update.bidSize);
		}
		if (update.askSize > 0) {
			text.append("askSize", update.askSize);
		}
		if (update.askExchange.m_atExchangeType == update.bidExchange.m_atExchangeType) {
			text.append("exchange", toString(update.askExchange));
		} else {
			text.append("askExchange", toString(update.askExchange));
			text.append("bidExchange", toString(update.bidExchange));
		}
		text.append("condition", toString(update.condition));
		text.append("type", toString(update.updateType));
		text.append("mid", getMid(update));
		text.append("bid", toBigDecimal(update.bidPrice));
		text.append("ask", toBigDecimal(update.askPrice));
		return text.toString();
	}

	private static String toString(ATQuoteConditionType condition) {
		byte type = condition.m_quoteConditionType;
		switch (type) {
			case QuoteConditionAdditionalInformation:
				return "AdditionalInformation";
			case QuoteConditionAdditionalInformationDueToRelatedSecurity:
				return "AdditionalInformationDueToRelatedSecurity";
			case QuoteConditionAutomatedBidNoOfferNoBid:
				return "AutomatedBidNoOfferNoBid";
			case QuoteConditionClosed:
				return "Closed";
			case QuoteConditionClosing:
				return "Closing";
			case QuoteConditionDueToRelatedSecurity:
				return "DueToRelatedSecurity";
			case QuoteConditionDueToRelatedSecurityNewsDissemination:
				return "DueToRelatedSecurityNewsDissemination";
			case QuoteConditionDueToRelatedSecurityNewsPending:
				return "DueToRelatedSecurityNewsPending";
			case QuoteConditionEquipmentChangeover:
				return "EquipmentChangeover";
			case QuoteConditionFastTrading:
				return "FastTrading";
			case QuoteConditionInViewOfCommon:
				return "InViewOfCommon";
			case QuoteConditionManualAskAutomaticBid:
				return "ManualAskAutomaticBid";
			case QuoteConditionManualBidAndAsk:
				return "ManualBidAndAsk";
			case QuoteConditionManualBidAutomaticAsk:
				return "ManualBidAutomaticAsk";
			case QuoteConditionMarketMakerQuotesClosed:
				return "MarketMakerQuotesClosed";
			case QuoteConditionNewsDissemination:
				return "NewsDissemination";
			case QuoteConditionNonFirm:
				return "NonFirm";
			case QuoteConditionNoOpenNoResume:
				return "NoOpenNoResume";
			case QuoteConditionOpening:
				return "Opening";
			case QuoteConditionOrderImbalance:
				return "OrderImbalance";
			case QuoteConditionOrderInflux:
				return "OrderInflux";
			case QuoteConditionRegular:
				return "Regular";
			case QuoteConditionRegularOneSidedOpen:
				return "RegularOneSidedOpen";
			case QuoteConditionRegularTwoSidedOpen:
				return "RegularTwoSidedOpen";
			case QuoteConditionResume:
				return "Resume";
			case QuoteConditionSlowAsk:
				return "SlowAsk";
			case QuoteConditionSlowBid:
				return "SlowBid";
			case QuoteConditionSlowBidAsk:
				return "SlowBidAsk";
			case QuoteConditionSlowDueLRPAsk:
				return "SlowDueLRPAsk";
			case QuoteConditionSlowDueLRPBid:
				return "SlowDueLRPBid";
			case QuoteConditionSlowDueSetSlowListBidAsk:
				return "SlowDueSetSlowListBidAsk";
			case QuoteConditionSubPennyTrading:
				return "SubPennyTrading";
			case QuoteConditionTradingRangeIndication:
				return "QuoteConditionTradingRangeIndication";
			default:
				throw new IllegalArgumentException("type not supported: " + type);
		}
	}

	private static String toString(ATExchangeType exchange) {
		// TODO: Why are there overlapping types??
		byte type = exchange.m_atExchangeType;
		switch (type) {
			case ATExchangeType.AMEX:
				return "AMEX";
			case ATExchangeType.BatsExchange:
				return "BatsExchange";
			case ATExchangeType.BatsYExchange:
				return "BatsYExchange";
			case ATExchangeType.CTANasdaqOMX:
				// case ATExchangeType.CanadaToronto:
				return "CanadaToronto";
			case ATExchangeType.CanadaVenture:
				return "CanadaVenture";
			case ATExchangeType.ExchangeOptionC2:
				// case ATExchangeType.ChicagoBoardOptionsExchange:
				return "ExchangeOptionC2";
			case ATExchangeType.ChicagoStockExchange:
				return "ChicagoStockExchange";
			case ATExchangeType.Composite:
				return "Composite";
			case ATExchangeType.CQS:
				return "CQS";
			case ATExchangeType.CTS:
				return "CTS";
			case ATExchangeType.EdgaExchange:
				return "EdgaExchange";
			case ATExchangeType.EdgxExchange:
				return "EdgxExchange";
			case ATExchangeType.ExchangeOptionBoston:
				// case ATExchangeType.NasdaqOmxBx:
				return "ExchangeOptionBoston";
			case ATExchangeType.Forex:
				return "Forex";
			case ATExchangeType.FinraAdf:
				return "FinraAdf";
			case ATExchangeType.NNOTC:
				return "NNOTC";
			case ATExchangeType.NyseArcaExchange:
				return "NyseArcaExchange";
			case ATExchangeType.NyseEuronext:
				return "NyseEuronext";
			case ATExchangeType.InternationalSecuritiesExchange:
				return "InternationalSecuritiesExchange";
			case ATExchangeType.NasdaqOmx:
				return "NasdaqOmx";
			case ATExchangeType.NasdaqOmxPhlx:
				return "NasdaqOmxPhlx";
			case ATExchangeType.OTCBB:
				return "OTCBB";
			default:
				return "UnknownExchange(" + type + ")";
		}
	}

	public static String toString(ATStreamUpdateType update) {
		int type = update.m_nUpdateType;
		switch (type) {
			case StreamUpdateTrade:
				return "Trade";
			case StreamUpdateQuote:
				return "Quote";
			case StreamUpdateRefresh:
				return "Refresh";
			case StreamUpdateTopMarketMovers:
				return "TopMarketMovers";
			default:
				throw new IllegalArgumentException("type not supported: " + type);
		}
	}

	public static ATSYMBOL toSymbol(IInstrument instrument) {
		return toSymbol(instrument.getName());
	}

	public static ATSYMBOL toSymbol(String symbol) {
		return Helpers.StringToSymbol(symbol);
	}

	public static List<ATSYMBOL> toSymbolList(Collection<? extends IInstrument> instruments) {
		List<ATSYMBOL> symbols = new ArrayList<ATSYMBOL>();
		for (IInstrument instrument : instruments) {
			symbols.add(toSymbol(instrument));
		}
		return symbols;
	}

	public static BigDecimal toBigDecimal(ATPRICE price) {
		return new BigDecimal(price.price).setScale(price.precision, RoundingMode.HALF_UP);
	}

	public static int toBigInt(ATPRICE price) {
		return toBigInt(price.price, price.precision);
	}

	public static int toBigInt(double price, int precision) {
		BigDecimal decimal = new BigDecimal(price);
		decimal = decimal.setScale(precision, BigDecimal.ROUND_HALF_UP);
		decimal = decimal.movePointRight(precision);
		return decimal.intValue();
	}

	public static LocalDateTime toLocalDateTime(SYSTEMTIME time) {
		LocalDateTime date = LocalDateTime.of(time.year, time.month, time.day, time.hour, time.minute, time.second, time.milliseconds);
		return toUTC(date);
	}

	public static ATBarHistoryType newBarHistoryType() {
		return new ATServerAPIDefines().new ATBarHistoryType(BarHistoryIntraday);
	}

	public static SYSTEMTIME toSystemTime(LocalDate date) {
		return toSystemTime(LocalDateTime.of(date, LocalTime.of(0, 0)));
	}

	public static SYSTEMTIME toSystemTime(LocalDateTime date) {
		date = fromUTC(date);

		SYSTEMTIME time = new ATServerAPIDefines().new SYSTEMTIME();
		time.year = (short) date.getYear();
		time.month = (short) date.getMonthValue();
		time.day = (short) date.getDayOfMonth();
		time.hour = (short) date.getHour();
		time.minute = (short) date.getMinute();
		time.second = (short) date.getSecond();
		return time;
	}

	public static boolean isSuccess(long errorCode) {
		return errorCode >= 0;
	}

	public static void throwError(long errorCode) {
		if (!isSuccess(errorCode)) {
			throw new AtException(toString((int) errorCode));
		}
	}

	public static String toString(int errorCode) {
		switch (errorCode) {
			case Errors.ERROR_SUCCESS:
				return "Success";
			case Errors.ERROR_ALREADY_INITIALIZED:
				return "AlreadyInitialized";
			case Errors.ERROR_BUFFER_OVERFLOW:
				return "BufferOverflow";
			case Errors.ERROR_INVALID_STATE:
				return "InvalidState";
			case Errors.ERROR_NOT_CONNECTED:
				return "NotConnected";
			case Errors.ERROR_NOT_FOUND:
				return "NotFound";
			case Errors.ERROR_NOT_SOCKET:
				return "NotSocket";
			case Errors.ERROR_UNKNOWN:
				return "Unknown";
			default:
				throw new IllegalArgumentException("errorCode not supported: " + errorCode);
		}
	}

	public static String toString(ATLOGIN_RESPONSE response) {
		return toString(response.loginResponse);
	}

	private static String toString(ATLoginResponseType response) {
		int type = response.m_atLoginResponseType;
		switch (type) {
			case LoginResponseSuccess:
				return "Success";
			case LoginResponseInvalidUserid:
				return "InvalidUserid";
			case LoginResponseInvalidPassword:
				return "InvalidPassword";
			case LoginResponseInvalidRequest:
				return "InvalidRequest";
			case LoginResponseLoginDenied:
				return "LoginDenied";
			case LoginResponseServerError:
				return "ServerError";
			default:
				throw new IllegalArgumentException("type not supported: " + type);
		}
	}

	public static boolean isLoggedIn(ATLOGIN_RESPONSE response) {
		return isLoggedIn(response.loginResponse);
	}

	public static boolean isLoggedIn(ATLoginResponseType response) {
		return response.m_atLoginResponseType == LoginResponseSuccess;
	}

	public static String toString(ATBarHistoryResponseType response) {
		int type = response.m_responseType;
		switch (type) {
			case BarHistoryResponseSuccess:
				return "Success";
			case BarHistoryResponseInvalidRequest:
				return "InvalidRequest";
			case BarHistoryResponseMaxLimitReached:
				return "MaxLimitReached";
			case BarHistoryResponseDenied:
				return "Denied";
			default:
				throw new IllegalArgumentException("type not supported: " + type);
		}
	}

	public static boolean isConnected(ATSessionStatusType status) {
		return status.m_atSessionStatusType == SessionStatusConnected;
	}

	public static String toString(ATSessionStatusType status) {
		int type = status.m_atSessionStatusType;
		switch (type) {
			case SessionStatusConnected:
				return "Connected";
			case SessionStatusDisconnected:
				return "Disconnected";
			case SessionStatusDisconnectedDuplicateLogin:
				return "DisconnectedDuplicateLogin";
			case SessionStatusDisconnectedInactivity:
				return "DisconnectedInactivity";
			default:
				throw new IllegalArgumentException("type not supported: " + type);
		}
	}

}
