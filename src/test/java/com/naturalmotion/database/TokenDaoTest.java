package com.naturalmotion.database;

import java.sql.SQLException;

import org.assertj.core.api.Assertions;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.naturalmotion.database.token.Card;
import com.naturalmotion.database.token.Token;

public class TokenDaoTest {

	private TokenDao dao = new TokenDao();
 
	private static Server server;

	@BeforeClass
	public static void setupClass() throws SQLException, ClassNotFoundException {
		server = org.h2.tools.Server.createTcpServer("-tcpPort", "9090", "-tcpAllowOthers").start();
		new DatabaseInitializer().init();
	}

	@AfterClass
	public static void afterClass() throws SQLException {
		if (server != null) {
			server.stop();
		}
	}

	@Test
	public void testInsert() throws SQLException {
		Token token = createToken();
		Assertions.assertThat(dao.insert(token)).isEqualTo(3);

	}

	@Test
	public void testUpdateTokenAndRead() throws SQLException {
		Token token = createToken();
		dao.insert(token);

		token.setBronze(createCard(1, 2, "newStatus1"));
		token.setSilver(createCard(11, 21, "newStatus2"));
		token.setGold(createCard(111, 211, "newStatus3"));
		Assertions.assertThat(dao.update(token)).isEqualTo(3);

		Token actual = dao.read(token.getCrew());
		Assertions.assertThat(actual.getCrew()).isEqualTo(token.getCrew());
		assertCard("newStatus1", 1, 2, actual.getBronze());
		assertCard("newStatus2", 11, 21, actual.getSilver());
		assertCard("newStatus3", 111, 211, actual.getGold());
	}

	private void assertCard(String status, int cost, int paid, Card actualCard) {
		Assertions.assertThat(actualCard.getStatus()).isEqualTo(status);
		Assertions.assertThat(actualCard.getCost()).isEqualTo(cost);
		Assertions.assertThat(actualCard.getPaid()).isEqualTo(paid);
	}

	private Token createToken() {
		Token token = new Token();
		token.setBronze(createCard(2, 5, "status1"));
		token.setSilver(createCard(3, 6, "status2"));
		token.setGold(createCard(4, 7, "status3"));
		token.setCrew("crew");
		return token;
	}

	private Card createCard(int cost, int paid, String status) {
		Card card = new Card();
		card.setCost(cost);
		card.setPaid(paid);
		card.setStatus(status);
		return card;
	}
}
