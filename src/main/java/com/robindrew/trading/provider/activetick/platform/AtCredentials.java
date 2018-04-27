package com.robindrew.trading.provider.activetick.platform;

import com.robindrew.common.util.Check;

import at.shared.ATServerAPIDefines;
import at.shared.ATServerAPIDefines.ATGUID;

public class AtCredentials implements IAtCredentials {

	private final String apiKey;
	private final String username;
	private final String password;

	private final ATGUID guid;

	public AtCredentials(String apiKey, String username, String password) {
		this.apiKey = Check.notEmpty("apiKey", apiKey);
		this.username = Check.notEmpty("username", username);
		this.password = Check.notEmpty("password", password);

		this.guid = (new ATServerAPIDefines()).new ATGUID();
		this.guid.SetGuid(apiKey);
	}

	@Override
	public String getApiKey() {
		return apiKey;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public ATGUID getATGUID() {
		return guid;
	}

	@Override
	public String toString() {
		return username + "/" + apiKey;
	}

}
