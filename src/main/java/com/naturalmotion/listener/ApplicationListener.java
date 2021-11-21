package com.naturalmotion.listener;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.h2.tools.Server;

import com.naturalmotion.database.DatabaseInitializer;

public class ApplicationListener implements ServletContextListener {

	private Thread threadRedMembers;
	private Thread threadRedCrew;

	private Thread eventThread;
	private EventTask eventTask;
	private Server server;
	private AccountHistoryTask accountHistoryTask;
	private CrewHistoryTask crewHistoryTask;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			server = Server.createTcpServer("-tcpAllowOthers").start();

			new DatabaseInitializer().init();

			eventTask = new EventTask();
			eventThread = new Thread(eventTask);
			eventThread.start();

			accountHistoryTask = new AccountHistoryTask("rouge");
			threadRedMembers = new Thread(accountHistoryTask);
			threadRedMembers.start();
			crewHistoryTask = new CrewHistoryTask("rouge");
			threadRedCrew = new Thread(crewHistoryTask);
			threadRedCrew.start();

		} catch (SQLException e) {
			Logger.getLogger(ApplicationListener.class).error("Error initializing database", e);
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (accountHistoryTask != null) {
			accountHistoryTask.stop();
		}
		if (crewHistoryTask != null) {
			crewHistoryTask.stop();
		}

		if (eventTask != null) {
			eventTask.stop();
		}
		threadRedMembers.interrupt();
		threadRedCrew.interrupt();
		eventThread.interrupt();

		if (server != null) {
			server.stop();
		}
	}

}
