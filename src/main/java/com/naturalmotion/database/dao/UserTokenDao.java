package com.naturalmotion.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.naturalmotion.database.ConnectionFactory;
import com.naturalmotion.database.SqlLogBuilder;
import com.naturalmotion.database.usertoken.UserToken;

public class UserTokenDao {

	private final Logger log = Logger.getLogger("sql");

	private static final String SELECT_USER_TOKEN = "SELECT * FROM USER_TOKEN WHERE user = ? and token_date = ?";
	private static final String INSERT_USER_TOKEN = "INSERT INTO USER_TOKEN VALUES(?,?,?,?,?)";

	public UserToken readUserToken(String user, Timestamp tokenDate) throws SQLException {
		UserToken userToken = null;
		try (Connection connection = ConnectionFactory.create();
		        PreparedStatement statement = connection.prepareStatement(SELECT_USER_TOKEN)) {
			statement.setString(1, user);
			statement.setTimestamp(2, tokenDate);
			try (ResultSet result = statement.executeQuery();) {

				log.info(new SqlLogBuilder().build(SELECT_USER_TOKEN, Arrays.asList(user, tokenDate)));
				while (result.next()) {
					userToken = new UserToken();
					userToken.setId(result.getString("id"));
					userToken.setUser(result.getString("user"));
					userToken.setRarity(result.getString("rarity"));
					userToken.setPaid(result.getInt("paid"));
					userToken.setTokenDate(result.getTimestamp("token_date"));
				}
			}
			connection.commit();
		}
		return userToken;
	}

	public int insertUserToken(UserToken token) throws SQLException {
		int result = 0;
		if (token != null) {
			try (Connection connection = ConnectionFactory.create();
			        PreparedStatement statement = connection.prepareStatement(INSERT_USER_TOKEN)) {
				statement.setString(1, token.getId());
				statement.setTimestamp(2, token.getTokenDate());
				statement.setString(3, token.getUser());
				statement.setString(4, token.getRarity());
				statement.setInt(5, token.getPaid());
				result = statement.executeUpdate();

				log.info(new SqlLogBuilder().build(INSERT_USER_TOKEN, Arrays.asList(token.getId(), token.getTokenDate(),
				        token.getUser(), token.getRarity(), token.getPaid())) + ";" + result);

				connection.commit();
			}
		}
		return result;
	}

}
