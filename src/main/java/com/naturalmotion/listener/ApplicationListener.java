package com.naturalmotion.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationListener implements ServletContextListener {

	private Thread threadChrisMembers;
	private Thread threadRedMembers;
	private Thread threadOrangeMembers;
	private Thread threadChrisCrew;
	private Thread threadRedCrew;
	private Thread threadOrangeCrew;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		threadChrisMembers = new Thread(new AccountHistoryTask("rouge"));
		threadChrisMembers.start();
		threadRedMembers = new Thread(new AccountHistoryTask("rouge"));
		threadRedMembers.start();
		threadOrangeMembers = new Thread(new AccountHistoryTask("orange"));
		threadOrangeMembers.start();
		threadChrisCrew = new Thread(new CrewHistoryTask("rouge"));
		threadChrisCrew.start();
		threadRedCrew = new Thread(new CrewHistoryTask("rouge"));
		threadRedCrew.start();
		threadOrangeCrew = new Thread(new CrewHistoryTask("orange"));
		threadOrangeCrew.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		threadChrisMembers.interrupt();
		threadChrisCrew.interrupt();
		threadRedMembers.interrupt();
		threadOrangeMembers.interrupt();
		threadRedCrew.interrupt();
		threadOrangeCrew.interrupt();
	}

}
