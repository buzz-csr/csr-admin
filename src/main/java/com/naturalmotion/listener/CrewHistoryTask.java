package com.naturalmotion.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naturalmotion.api.CrewHistories;
import com.naturalmotion.api.CrewHistory;
import com.naturalmotion.webservice.api.Crew;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.configuration.Configuration;
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;

public class CrewHistoryTask implements Runnable {

	private static final int TIMEOUT = 10 * 60 * 1000; // 10 min

	private final Logger log = Logger.getLogger(CrewHistoryTask.class);

	private CrewResources crewResources = new CrewResources();

	private ObjectMapper objectMapper = new ObjectMapper();

	private String team;

	public CrewHistoryTask(String team) {
		this.team = team;
	}

	@Override
	public void run() {
		try {
			Configuration configuration = new Configuration();
			File backup = new File(configuration.getString("working.directory") + "/HISTORY/");
			if (!backup.exists()) {
				backup.mkdirs();
			}

			AuthorizationFactory authorizationFactory = new AuthorizationFactory();
			Authorization authorization = authorizationFactory.get(team);
			while (true) {
				Date date = new Date();
				Crew crew = crewResources.getCrew(authorization);

				updateCrewHistory(backup, date, crew);
				Thread.sleep(TIMEOUT);
			}
		} catch (InterruptedException e) {
			log.error("Error into AccountHistoryTask", e);
		}
	}

	private void updateCrewHistory(File backup, Date date, Crew crew) {
		try {
			CrewHistories histories;

			File crewFile = new File(backup.getPath() + "/" + crew.getId() + ".json");
			if (!crewFile.exists()) {
				crewFile.createNewFile();
				histories = new CrewHistories();
				histories.setHistories(new ArrayList<CrewHistory>());
			} else {
				histories = objectMapper.readValue(crewFile, CrewHistories.class);
			}
			CrewHistory crewHistory = new CrewHistory();
			crewHistory.setSnapshotDate(date);
			crewHistory.setRank(crew.getRank());
			crewHistory.setTotal(crew.getPoints());

			List<CrewHistory> lastHistories = histories.getHistories();

			if (lastHistories.size() > 0) {
				CrewHistory lastSnapshot = lastHistories.stream()
						.max((x, y) -> x.getSnapshotDate().compareTo(y.getSnapshotDate())).get();
				if (lastSnapshot != null && lastSnapshot.getTotal() < crewHistory.getTotal()) {
					crewHistory.setDiff(crewHistory.getTotal() - lastSnapshot.getTotal());
				} else {
					crewHistory.setDiff(crewHistory.getTotal());
				}
			} else {
				crewHistory.setDiff(crewHistory.getTotal());
			}
			lastHistories.add(crewHistory);

			// Réduction de l'historique à 10j glissants
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, -10);
			histories.setHistories(lastHistories.stream().filter(x -> x.getSnapshotDate().after(calendar.getTime()))
					.collect(Collectors.toList()));

			write(histories, crewFile);
		} catch (IOException e) {
			log.error("Error into CrewHistoryTask", e);
		}
	}

	private void write(CrewHistories json, File memberFile) throws IOException {
		try (FileWriter myWriter = new FileWriter(memberFile)) {
			myWriter.write(objectMapper.writeValueAsString(json));
		}
	}

}
