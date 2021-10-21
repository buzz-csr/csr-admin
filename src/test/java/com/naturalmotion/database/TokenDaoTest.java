package com.naturalmotion.database;

import java.sql.SQLException;

import org.assertj.core.api.Assertions;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.naturalmotion.database.token.Card;
import com.naturalmotion.database.token.Token;
import com.naturalmotion.webservice.configuration.Configuration;

public class TokenDaoTest {

	private static final Configuration configuration = new Configuration();

	private TokenDao dao = new TokenDao();

	@BeforeClass
	public static void setupClass() throws SQLException {
		Server.createTcpServer().start();
	}

	@AfterClass
	public static void afterClass() throws SQLException {
		Server.shutdownTcpServer(configuration.getString("h2.database.file"),
		        configuration.getString("h2.database.password"), false, false);
	}

	@Test
	public void testInsert() throws SQLException {
		Token token = new Token();
		token.setBronze(createCard(10, 5, "status"));
		token.setSilver(createCard(10, 5, "status"));
		token.setGold(createCard(10, 5, "status"));
		Assertions.assertThat(dao.insert(token)).isEqualTo(3);

	}

	private Card createCard(int cost, int paid, String status) {
		Card card = new Card();
		card.setCost(cost);
		card.setPaid(paid);
		card.setStatus(status);
		return card;
	}

	@Test
	public void testUpdateToken() {
		throw new RuntimeException("not yet implemented");
	}

	@Test
	public void testReadString() {
		throw new RuntimeException("not yet implemented");
	}

}
