package com.naturalmotion.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.api.Member;
import com.naturalmotion.webservice.api.history.AccountHistory;
import com.naturalmotion.webservice.configuration.Configuration;
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.history.HistoryCleaner;
import com.naturalmotion.webservice.service.history.HistoryUpdater;

public class AccountHistoryTask implements CsrTask {

	private static final int TIMEOUT = 30 * 60 * 1000; // 30 min

	private final Logger log = Logger.getLogger(AccountHistoryTask.class);

	private CrewResources crewResources = new CrewResources();

	private ObjectMapper objectMapper = new ObjectMapper();

	private HistoryUpdater historyUpdater = new HistoryUpdater();

	private HistoryCleaner historyCleaner = new HistoryCleaner();

	private String team;

	private STATE state = STATE.RUNNING;

	public AccountHistoryTask(String team) {
		this.team = team;
	}

	@Override
	public void run() {
		Configuration configuration = new Configuration();
		File backup = new File(configuration.getString("working.directory") + "/HISTORY/");
		if (!backup.exists()) {
			backup.mkdirs();
		}

		AuthorizationFactory authorizationFactory = new AuthorizationFactory();
		Authorization authorization = authorizationFactory.get(team);
		while (STATE.RUNNING.equals(state)) {
			try {
				Date date = new Date();
				List<Member> members = crewResources.getMembers(authorization);
				members.forEach(x -> updateHistory(x, backup, date));

				waiting();
			} catch (Exception e) {
				log.error("Error into AccountHistoryTask for team " + team, e);
				waiting();
			}
		}

		state = STATE.STOP;
	}

	private void waiting() {
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			log.error("Error waiting for team " + team, e);
		}
	}

	private void updateHistory(Member member, File backup, Date date) {
		try {
			AccountHistory accountJson = null;

			File memberFile = new File(backup.getPath() + "/" + member.getId() + ".json");
			if (!memberFile.exists()) {
				memberFile.createNewFile();
				accountJson = new AccountHistory();
			} else {
				accountJson = objectMapper.readValue(memberFile, AccountHistory.class);
			}

			historyUpdater.update(member, date, accountJson);
			accountJson = historyCleaner.clean(accountJson, date);
			write(accountJson, memberFile);
		} catch (IOException | ParseException e) {
			log.error("Error into AccountHistoryTask", e);
		}
	}

	private void write(AccountHistory accountJson, File memberFile) throws IOException {
		try (FileWriter myWriter = new FileWriter(memberFile)) {
			myWriter.write(objectMapper.writeValueAsString(accountJson));
		}
	}

	@Override
	public void stop() {
		state = STATE.STOPPING;
	}

	@Override
	public boolean isRunning() {
		return STATE.STOP.equals(state);
	}
}
