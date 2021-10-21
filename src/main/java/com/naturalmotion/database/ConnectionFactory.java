package com.naturalmotion.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.naturalmotion.webservice.configuration.Configuration;

public class ConnectionFactory {

	private static final Configuration configuration = new Configuration();

	public static Connection create() throws SQLException {
		return DriverManager.getConnection(configuration.getString("h2.database.file"),
		        configuration.getString("h2.database.user"), configuration.getString("h2.database.password"));
	}
}
