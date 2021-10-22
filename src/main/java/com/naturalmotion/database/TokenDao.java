package com.naturalmotion.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.naturalmotion.database.token.Card;
import com.naturalmotion.database.token.Token;

public class TokenDao {

	private static final String INSERT_TOKEN = "INSERT INTO TOKEN VALUES (?,?,?,?,?)";
	private static final String UPDATE_TOKEN = "UPDATE TOKEN SET status=?, paid=?, cost=? WHERE crew=? AND rarity=?";
	private static final String SELECT_TOKEN = "SELECT * FROM TOKEN WHERE CREW = ? AND RARITY = ?";

	public int insert(Token token) throws SQLException {
		int executeUpdate = 0;
		try (Connection connection = ConnectionFactory.create();) {
			executeUpdate += insertToken(token.getCrew(), token.getBronze(), TOKEN_RARITY.BRONZE, connection);
			executeUpdate += insertToken(token.getCrew(), token.getSilver(), TOKEN_RARITY.SILVER, connection);
			executeUpdate += insertToken(token.getCrew(), token.getGold(), TOKEN_RARITY.GOLD, connection);
		}
		return executeUpdate;
	}

	public int update(Token token) throws SQLException {
		int executeUpdate = 0;
		try (Connection connection = ConnectionFactory.create();) {
			executeUpdate += update(token.getCrew(), token.getBronze(), TOKEN_RARITY.BRONZE, connection);
			executeUpdate += update(token.getCrew(), token.getSilver(), TOKEN_RARITY.SILVER, connection);
			executeUpdate += update(token.getCrew(), token.getGold(), TOKEN_RARITY.GOLD, connection);
		}
		return executeUpdate;
	}

	private int update(String crew, Card card, TOKEN_RARITY rarity, Connection connection) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(UPDATE_TOKEN);) {
			statement.setString(1, card.getStatus());
			statement.setInt(2, card.getPaid());
			statement.setInt(3, card.getCost());
			statement.setString(4, crew);
			statement.setString(5, rarity.getNmValue());
			return statement.executeUpdate();
		}
	}

	private int insertToken(String crew, Card card, TOKEN_RARITY rarity, Connection connection) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(INSERT_TOKEN);) {
			statement.setString(1, crew);
			statement.setString(2, rarity.getNmValue());
			statement.setString(3, card.getStatus());
			statement.setInt(4, card.getPaid());
			statement.setInt(5, card.getCost());
			return statement.executeUpdate();
		}
	}

	public Token read(String crew) throws SQLException {
		try (Connection connection = ConnectionFactory.create();) {
			Token token = new Token();
			token.setCrew(crew);
			token.setBronze(read(crew, TOKEN_RARITY.BRONZE, connection));
			token.setSilver(read(crew, TOKEN_RARITY.SILVER, connection));
			token.setGold(read(crew, TOKEN_RARITY.GOLD, connection));
			return token;
		}
	}

	public Card read(String crew, TOKEN_RARITY rarity, Connection connection) throws SQLException {

		Card card = null;
		try (PreparedStatement statement = connection.prepareStatement(SELECT_TOKEN);) {
			statement.setString(1, crew);
			statement.setString(2, rarity.getNmValue());
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				card = new Card();
				card.setStatus(result.getString("status"));
				card.setPaid(result.getInt("paid"));
				card.setCost(result.getInt("cost"));
			}
		}

		return card;
	}
}
