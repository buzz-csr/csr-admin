package com.naturalmotion.listener;

import org.apache.log4j.Logger;

import csr.session.SessionService;

public class SessionUpdateTask extends Thread implements CsrTask {

	private final Logger log = Logger.getLogger(SessionUpdateTask.class);

	private static final int TIMEOUT = 15 * 1000; // 15 sec

	private SessionService sessionService = new SessionService();

	private STATE state = STATE.RUNNING;

	private long chrono = System.currentTimeMillis();

	@Override
	public void run() {
		while (STATE.RUNNING.equals(state)) {
			if (System.currentTimeMillis() - chrono > TIMEOUT) {
				chrono = System.currentTimeMillis();
				try {
					sessionService.updateAuth();

					Thread.sleep(TIMEOUT);
				} catch (Exception e) {
					log.error("Error into SessionUpdateTask", e);
				}
			}
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
