package com.naturalmotion.listener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.h2.tools.Server;

import com.naturalmotion.database.DatabaseInitializer;

public class ApplicationListener implements ServletContextListener {

	private final Logger log = Logger.getLogger(ApplicationListener.class);

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
			eventTask.start();

			accountHistoryTask = new AccountHistoryTask("rouge");
			accountHistoryTask.start();

			crewHistoryTask = new CrewHistoryTask("rouge");
			crewHistoryTask.start();

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

		closeJdbcDriver();

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			log.error("Error waiting shutdown");
		}
	}

	private void closeJdbcDriver() {
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				log.info(String.format("deregistering jdbc driver: %s", driver));
			} catch (SQLException e) {
				log.error(String.format("Error deregistering driver %s", driver), e);
			}
		}
	}

	private void stopAll() {
		stopTask(accountHistoryTask);
		stopTask(crewHistoryTask);
		stopTask(eventTask);

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
			task.stopTask();
			while (task.isRunning()) {
				log.info("Stopping " + task.getClass().getName() + "...");
			}
			log.info(task.getClass().getName() + " stopped");
		}
	}

}
