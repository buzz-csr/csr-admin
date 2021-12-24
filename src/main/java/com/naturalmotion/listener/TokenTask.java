package com.naturalmotion.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.naturalmotion.event.TokenDetector;
import com.naturalmotion.webservice.configuration.Configuration;

public class TokenTask extends Thread implements CsrTask {

	private static final int TIMEOUT = 60 * 60 * 1000; // 1h

	private Logger log = Logger.getLogger(TokenTask.class);

	private Configuration configuration = new Configuration();

	private List<TokenDetector> tokenDetector = new ArrayList<>();

	private STATE state = STATE.RUNNING;

	private long chrono = System.currentTimeMillis();

	public TokenTask() {
		List<String> list = configuration.getList("token.task.crew");
		list.stream().forEach(x -> extracted(x));
	}

	private void extracted(String x) {
		if (StringUtils.isNotBlank(x)) {
			tokenDetector.add(new TokenDetector(x));
		}
	}

	@Override
	public void run() {
		log.info("EventTask started !");
		while (STATE.RUNNING.equals(state)) {
			if (System.currentTimeMillis() - chrono > TIMEOUT) {
				chrono = System.currentTimeMillis();
				tokenDetector.stream().forEach(x -> x.detect());
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
