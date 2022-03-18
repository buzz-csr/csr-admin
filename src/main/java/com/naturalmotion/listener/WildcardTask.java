package com.naturalmotion.listener;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.naturalmotion.event.WildcardDetector;
import com.naturalmotion.event.WildcardUpdater;
import com.naturalmotion.webservice.configuration.Configuration;

public class WildcardTask extends Thread implements CsrTask {

	private static final int TIMEOUT = 10 * 1000; // 30s

	private Logger log = Logger.getLogger(WildcardTask.class);

	private Configuration configuration = new Configuration();

	private List<WildcardDetector> wildcardDetector = new ArrayList<>();

	private List<WildcardUpdater> wildcardUpdater = new ArrayList<>();

	private STATE state = STATE.RUNNING;

	private long chrono = System.currentTimeMillis();

	public WildcardTask() {
		List<String> list = configuration.getList("crew.list");
		list.stream().forEach(x -> extracted(x));
	}

	private void extracted(String x) {
		if (StringUtils.isNotBlank(x)) {
			wildcardDetector.add(new WildcardDetector(x));
			wildcardUpdater.add(new WildcardUpdater(x));
		}
	}

	@Override
	public void run() {
		log.info("EventTask started !");
		try {
			while (STATE.RUNNING.equals(state)) {
				if (System.currentTimeMillis() - chrono > TIMEOUT) {
					chrono = System.currentTimeMillis();
					wildcardDetector.stream().forEach(x -> x.detect());
					wildcardUpdater.stream().forEach(x -> x.update());
				}
				Thread.sleep(10000);
			}
		} catch (InterruptedException e) {
			log.error("Error sleeping", e);
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
