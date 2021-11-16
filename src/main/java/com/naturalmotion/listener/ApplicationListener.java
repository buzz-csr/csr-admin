package com.naturalmotion.listener;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.naturalmotion.database.DatabaseInitializer;

public class ApplicationListener implements ServletContextListener {

	private Thread threadChrisMembers;
	private Thread threadRedMembers;
//	private Thread threadOrangeMembers;
	private Thread threadChrisCrew;
	private Thread threadRedCrew;
//	private Thread threadOrangeCrew;

	private Thread eventThread;
	private EventTask eventTask;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			new DatabaseInitializer().init();

			eventTask = new EventTask();
			eventThread = new Thread(eventTask);
			eventThread.start();

			threadChrisMembers = new Thread(new AccountHistoryTask("chris"));
			threadChrisMembers.start();
			threadRedMembers = new Thread(new AccountHistoryTask("rouge"));
			threadRedMembers.start();
//			threadOrangeMembers = new Thread(new AccountHistoryTask("orange"));
//			threadOrangeMembers.start();
			threadChrisCrew = new Thread(new CrewHistoryTask("chris"));
			threadChrisCrew.start();
			threadRedCrew = new Thread(new CrewHistoryTask("rouge"));
			threadRedCrew.start();
//			threadOrangeCrew = new Thread(new CrewHistoryTask("orange"));
//			threadOrangeCrew.start();

		} catch (SQLException e) {
			Logger.getLogger(ApplicationListener.class).error("Error initializing database");
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		threadChrisMembers.interrupt();
		threadChrisCrew.interrupt();
		threadRedMembers.interrupt();
		threadRedCrew.interrupt();
//		threadOrangeMembers.interrupt();
//		threadOrangeCrew.interrupt();

		if (eventTask != null) {
			eventTask.stop();
			eventThread.interrupt();
		}
	}

}
