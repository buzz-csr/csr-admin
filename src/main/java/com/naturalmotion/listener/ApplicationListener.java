package com.naturalmotion.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationListener implements ServletContextListener {

	private Thread threadRedMembers;
	private Thread threadOrangeMembers;
	private Thread threadRedCrew;
	private Thread threadOrangeCrew;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		threadRedMembers = new Thread(new AccountHistoryTask("rouge"));
		threadRedMembers.start();
		threadOrangeMembers = new Thread(new AccountHistoryTask("orange"));
		threadOrangeMembers.start();
		threadRedCrew = new Thread(new CrewHistoryTask("rouge"));
		threadRedCrew.start();
		threadOrangeCrew = new Thread(new CrewHistoryTask("orange"));
		threadOrangeCrew.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		threadRedMembers.interrupt();
		threadOrangeMembers.interrupt();
		threadRedCrew.interrupt();
		threadOrangeCrew.interrupt();
	}

}
