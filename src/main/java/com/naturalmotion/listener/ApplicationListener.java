package com.naturalmotion.listener;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.h2.tools.Server;

import com.naturalmotion.database.DatabaseInitializer;

public class ApplicationListener implements ServletContextListener {

	private final Logger log = Logger.getLogger(ApplicationListener.class);

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

			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {
					stopAll();
				}
			});
		} catch (SQLException e) {
			log.error("Error initializing database", e);
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		stopAll();
	}

	private void stopAll() {
		stopTask(accountHistoryTask);
		stopTask(crewHistoryTask);
		stopTask(eventTask);

		stopThread(threadRedMembers);
		stopThread(threadRedCrew);
		stopThread(eventThread);

		if (server != null) {
			server.stop();
			while (server.isRunning(false)) {
				log.info("Stopping h2...");
			}
			log.info("h2 stopped");
		}
	}

	private void stopTask(CsrTask task) {
		if (task != null) {
			task.stop();
			while (task.isRunning()) {
				log.info("Stopping " + task.getClass().getCanonicalName() + "...");
			}
			log.info(task.getClass().getCanonicalName() + " stopped");
		}
	}

	private void stopThread(Thread thread) {
		thread.interrupt();
		while (!thread.isInterrupted()) {
			log.info("Stopping " + thread.getClass().getCanonicalName() + "...");
		}
		log.info(thread.getClass().getCanonicalName() + " stopped");
	}

}
