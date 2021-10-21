package com.naturalmotion.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

	public void init() throws SQLException {
		Connection connection = ConnectionFactory.create();
		try (Statement statement = connection.createStatement();) {
			statement.executeUpdate("CREATE SCHEMA IF NO EXISTS CSR");
		}
		try (Statement statement = connection.createStatement();) {
			statement.executeUpdate(
			        "CREATE TABLE IF NOT EXISTS TOKEN (crew VARCHAR2(10), 'rarity', 'status', 'paid', 'cost')");
		}

	}
}
