package com.naturalmotion.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.naturalmotion.event.EventDetector;
import com.naturalmotion.event.EventUpdater;
import com.naturalmotion.webservice.configuration.Configuration;

public class EventTask implements Runnable {

	private Logger log = Logger.getLogger(EventTask.class);

	private Configuration configuration = new Configuration();

	private List<EventDetector> eventDetector = new ArrayList<>();

	private List<EventUpdater> eventUpdater = new ArrayList<>();

	private boolean running = true;

	public EventTask() {
		List<String> list = configuration.getList("token.task.crew");
		list.stream().forEach(x -> extracted(x));
	}

	private void extracted(String x) {
		eventDetector.add(new EventDetector(x));
		eventUpdater.add(new EventUpdater(x));
	}

	@Override
	public void run() {
		while (running) {
			eventDetector.stream().forEach(x -> x.detect());
			eventUpdater.stream().forEach(x -> x.update());

			waiting();
		}
		log.info("EventTask thread has been stopped");
	}

	private void waiting() {
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			log.error("Error waiting", e);
		}
	}

	public void stop() {
		running = false;
	}
}
