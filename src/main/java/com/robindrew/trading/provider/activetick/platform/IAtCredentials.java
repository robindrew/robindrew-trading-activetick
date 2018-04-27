package com.robindrew.trading.provider.activetick.platform;

import at.shared.ATServerAPIDefines.ATGUID;

public interface IAtCredentials {

	String getApiKey();

	String getUsername();

	String getPassword();

	ATGUID getATGUID();
}
