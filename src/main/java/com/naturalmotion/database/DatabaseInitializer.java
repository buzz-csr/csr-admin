package com.naturalmotion.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS TOKEN(crew VARCHAR2(20),rarity VARCHAR(20),"
	        + "status VARCHAR2(20),paid INTEGER,cost INTEGER);";

	public void init() throws SQLException {
		try (Connection connection = ConnectionFactory.create();) {
			try (Statement statement = connection.createStatement();) {
				statement.executeUpdate(CREATE_TABLE);
			}
			connection.commit();
		}

	}
}
