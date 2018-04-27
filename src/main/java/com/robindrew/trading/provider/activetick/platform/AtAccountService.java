package com.robindrew.trading.provider.activetick.platform;

import com.robindrew.common.util.Check;
import com.robindrew.trading.platform.account.IAccountService;
import com.robindrew.trading.trade.cash.Cash;
import com.robindrew.trading.trade.cash.ICash;

public class AtAccountService implements IAccountService {

	private final IAtCredentials credentials;

	public AtAccountService(IAtCredentials credentials) {
		this.credentials = Check.notNull("credentials", credentials);
	}

	@Override
	public String getAccountId() {
		return credentials.getUsername();
	}

	@Override
	public ICash getBalance() {
		return new Cash(0);
	}

}
