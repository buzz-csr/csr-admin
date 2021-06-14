package com.naturalmotion.history;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naturalmotion.webservice.api.history.AccountHistory;
import com.naturalmotion.webservice.configuration.Configuration;

public class AccountHistoryReader {

	private ObjectMapper objectMapper = new ObjectMapper();

	private Configuration configuration = new Configuration();

	public AccountHistory get(String id) {
		AccountHistory accountHistory = null;

		File history = new File(configuration.getString("working.directory") + "/HISTORY/" + id + ".json");
		if (history.exists()) {
			try {
				accountHistory = objectMapper.readValue(history, AccountHistory.class);
			} catch (IOException e) {
				Logger.getLogger(AccountHistoryReader.class).error("Impossible de lire l'historique du compte " + id,
						e);
			}

		}
		return accountHistory;
	}

}
