package com.naturalmotion.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.naturalmotion.database.ConnectionFactory;
import com.naturalmotion.database.usertoken.UserToken;

public class UserTokenDao {

	private static final String SELECT_USER_TOKEN = "SELECT * FROM USER_TOKEN WHERE id = ?";

	public UserToken readUserToken(String id) throws SQLException {
		UserToken userToken = null;
		try (Connection connection = ConnectionFactory.create();
		        PreparedStatement statement = connection.prepareStatement(SELECT_USER_TOKEN)) {
			statement.setString(1, id);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				userToken = new UserToken();
				userToken.setId(result.getString("id"));
				userToken.setUser(result.getString("user"));
				userToken.setRarity(result.getString("rarity"));
				userToken.setPaid(result.getInt("paid"));
			}
		}
		return userToken;
	}

}
