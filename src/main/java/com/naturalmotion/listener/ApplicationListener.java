package com.naturalmotion.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationListener implements ServletContextListener {

	private Thread threadRouge;
	private Thread threadOrange;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		threadRouge = new Thread(new AccountHistoryTask("rouge"));
		threadRouge.start();
		threadOrange = new Thread(new AccountHistoryTask("orange"));
		threadOrange.start();
//		new Thread(new AccountHistoryTask("verte")).start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		threadRouge.interrupt();
		threadOrange.interrupt();
	}

}
