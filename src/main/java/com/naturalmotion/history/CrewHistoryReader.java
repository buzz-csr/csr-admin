package com.naturalmotion.history;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naturalmotion.api.CrewHistories;
import com.naturalmotion.webservice.configuration.Configuration;

public class CrewHistoryReader {

	private ObjectMapper objectMapper = new ObjectMapper();

	private Configuration configuration = new Configuration();

	public CrewHistories get(String id) {
		CrewHistories histories = null;

		File history = new File(configuration.getString("working.directory") + "/HISTORY/"
				+ configuration.getString(id + ".crew-id") + ".json");
		if (history.exists()) {
			try {
				histories = objectMapper.readValue(history, CrewHistories.class);
			} catch (IOException e) {
				Logger.getLogger(AccountHistoryReader.class).error("Impossible de lire l'historique du crew " + id, e);
			}

		}
		return histories;
	}

}
