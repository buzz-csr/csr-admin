package com.naturalmotion.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.naturalmotion.event.EventDetector;
import com.naturalmotion.event.EventUpdater;
import com.naturalmotion.webservice.configuration.Configuration;

public class EventTask extends Thread implements CsrTask {

	private static final int TIMEOUT = 30000;

	private Logger log = Logger.getLogger(EventTask.class);

	private Configuration configuration = new Configuration();

	private List<EventDetector> eventDetector = new ArrayList<>();

	private List<EventUpdater> eventUpdater = new ArrayList<>();

	private STATE state = STATE.RUNNING;

	private long chrono = System.currentTimeMillis();

	public EventTask() {
		List<String> list = configuration.getList("token.task.crew");
		list.stream().forEach(x -> extracted(x));
	}

	private void extracted(String x) {
		if (StringUtils.isNotBlank(x)) {
			eventDetector.add(new EventDetector(x));
			eventUpdater.add(new EventUpdater(x));
		}
	}

	@Override
	public void run() {
		log.info("EventTask started !");
		while (STATE.RUNNING.equals(state)) {
			if (System.currentTimeMillis() - chrono > TIMEOUT) {
				chrono = System.currentTimeMillis();
				eventDetector.stream().forEach(x -> x.detect());
				eventUpdater.stream().forEach(x -> x.update());
			}
		}
		state = STATE.STOP;
		log.info("EventTask thread has been stopped");
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
