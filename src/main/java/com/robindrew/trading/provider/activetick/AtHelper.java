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
import static at.shared.ATServerAPIDefines.ATSessionStatusType.SessionStatusConnected;
import static at.shared.ATServerAPIDefines.ATSessionStatusType.SessionStatusDisconnected;
import static at.shared.ATServerAPIDefines.ATSessionStatusType.SessionStatusDisconnectedDuplicateLogin;
import static at.shared.ATServerAPIDefines.ATSessionStatusType.SessionStatusDisconnectedInactivity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import at.shared.ATServerAPIDefines;
import at.shared.ATServerAPIDefines.ATBarHistoryResponseType;
import at.shared.ATServerAPIDefines.ATBarHistoryType;
import at.shared.ATServerAPIDefines.ATLOGIN_RESPONSE;
import at.shared.ATServerAPIDefines.ATLoginResponseType;
import at.shared.ATServerAPIDefines.ATSessionStatusType;
import at.shared.ATServerAPIDefines.SYSTEMTIME;
import at.utils.jlib.Errors;

public class AtHelper {

	public static int toBigInt(double price, int precision) {
		BigDecimal decimal = new BigDecimal(price);
		decimal = decimal.setScale(precision, BigDecimal.ROUND_HALF_UP);
		decimal = decimal.movePointRight(precision);
		return decimal.intValue();
	}

	public static LocalDateTime toLocalDateTime(SYSTEMTIME time) {
		return LocalDateTime.of(time.year, time.month, time.day, time.hour, time.minute, time.second, time.milliseconds);
	}

	public static ATBarHistoryType newBarHistoryType() {
		return new ATServerAPIDefines().new ATBarHistoryType(BarHistoryIntraday);
	}

	public static SYSTEMTIME toSystemTime(LocalDate date) {
		return toSystemTime(LocalDateTime.of(date, LocalTime.of(0, 0)));
	}

	public static SYSTEMTIME toSystemTime(LocalDateTime date) {
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
