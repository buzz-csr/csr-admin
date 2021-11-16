package com.naturalmotion.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcDataSource;

import com.naturalmotion.webservice.configuration.Configuration;

public class ConnectionFactory {

	private static final Configuration configuration = new Configuration();

	public static Connection create() throws SQLException {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL(configuration.getString("h2.database.file"));
		dataSource.setUser(configuration.getString("h2.database.user"));
		dataSource.setPassword(configuration.getString("h2.database.password"));
		return dataSource.getConnection();
	}
}
