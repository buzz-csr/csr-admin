package com.naturalmotion.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

	private static final String CREATE_TOKEN_TABLE = "CREATE TABLE IF NOT EXISTS TOKEN(crew VARCHAR2(20),"
	        + "rarity VARCHAR(20),status VARCHAR2(20),paid INTEGER,cost INTEGER);";
//	private static final String CREATE_USER_TOKEN_TABLE = "CREATE TABLE IF NOT EXISTS USER_TOKEN(id VARCHAR2(20),"
//	        + "user VARCHAR(20),rarity VARCHAR2(20),paid INTEGER);";

	public void init() throws SQLException {
		try (Connection connection = ConnectionFactory.create();) {
			try (Statement statement = connection.createStatement();) {
				statement.executeUpdate(CREATE_TOKEN_TABLE);
//				statement.executeUpdate(CREATE_USER_TOKEN_TABLE);
			}
			connection.commit();
		}

	}
}
