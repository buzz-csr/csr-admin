package com.naturalmotion.listener;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.naturalmotion.database.DatabaseInitializer;

public class ApplicationListener implements ServletContextListener {

	private Thread threadRedMembers;
	private Thread threadRedCrew;

	private Thread eventThread;
	private EventTask eventTask;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			new DatabaseInitializer().init();

			eventTask = new EventTask();
			eventThread = new Thread(eventTask);
			eventThread.start();

			threadRedMembers = new Thread(new AccountHistoryTask("rouge"));
			threadRedMembers.start();
			threadRedCrew = new Thread(new CrewHistoryTask("rouge"));
			threadRedCrew.start();

		} catch (SQLException e) {
			Logger.getLogger(ApplicationListener.class).error("Error initializing database", e);
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		threadRedMembers.interrupt();
		threadRedCrew.interrupt();

		if (eventTask != null) {
			eventTask.stop();
			eventThread.interrupt();
		}
	}

}
