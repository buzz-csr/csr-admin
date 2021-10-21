package com.naturalmotion.listener;

import org.apache.log4j.Logger;

public class EventTask implements Runnable {

	private Logger log = Logger.getLogger(EventTask.class);

	@Override
	public void run() {
		while (true) {

			waiting();
		}
	}

	private void waiting() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			log.error("Error waiting", e);
		}
	}

}
