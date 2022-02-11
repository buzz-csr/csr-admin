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

public class AccountHistoryTask extends Thread implements CsrTask {

	private static final int TIMEOUT = 60 * 60 * 1000; // 1h

	private final Logger log = Logger.getLogger(AccountHistoryTask.class);

	private CrewResources crewResources = new CrewResources();

	private ObjectMapper objectMapper = new ObjectMapper();

	private HistoryUpdater historyUpdater = new HistoryUpdater();

	private HistoryCleaner historyCleaner = new HistoryCleaner();

	private STATE state = STATE.RUNNING;

	private long chrono = System.currentTimeMillis();

	@Override
	public void run() {
		log.info(AccountHistoryTask.class.getName() + " started !");
		Configuration configuration = new Configuration();
		File backup = new File(configuration.getString("working.directory") + "/HISTORY/");
		if (!backup.exists()) {
			backup.mkdirs();
		}

		while (STATE.RUNNING.equals(state)) {
			if (System.currentTimeMillis() - chrono > TIMEOUT) {
				chrono = System.currentTimeMillis();
				doTask(backup, configuration);
			}
		}

		state = STATE.STOP;
	}

	private void doTask(File backup, Configuration configuration) {
		List<String> list = configuration.getList("crew.list");
		list.stream().forEach(team -> {
			try {
				Date date = new Date();
				String crewPlayerId = configuration.getString(team + ".player-id");
				Authorization authorization = new AuthorizationFactory().get(crewPlayerId);
				List<Member> members = crewResources.getMembers(authorization);
				members.forEach(m -> updateHistory(m, backup, date));
			} catch (Exception e) {
				log.error("Error into AccountHistoryTask for team " + team, e);
			}
		});

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
	public void stopTask() {
		state = STATE.STOPPING;
	}

	@Override
	public boolean isRunning() {
		return STATE.STOP.equals(state);
	}
}
