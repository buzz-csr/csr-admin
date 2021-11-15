package com.naturalmotion.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.naturalmotion.event.EventDetector;
import com.naturalmotion.webservice.configuration.Configuration;

public class EventTask implements Runnable {

	private Logger log = Logger.getLogger(EventTask.class);

	private Configuration configuration = new Configuration();

	private List<EventDetector> eventDetector;

	private boolean running = true;

	public EventTask() {
		List<String> list = configuration.getList("token.task.crew");
		eventDetector = new ArrayList<>();
		list.stream().forEach(x -> eventDetector.add(new EventDetector(x)));
	}

	@Override
	public void run() {
		while (running) {
			for (EventDetector event : eventDetector) {
				event.detect();
			}
			waiting();
		}
	}

	private void waiting() {
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			log.error("Error waiting", e);
		}
	}

	public void stop() {
		running = false;
	}
}
