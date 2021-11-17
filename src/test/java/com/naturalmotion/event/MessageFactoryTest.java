package com.naturalmotion.event;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.webservice.service.json.Card;

public class MessageFactoryTest {

	private MessageFactory factory = new MessageFactory();

	@Test
	public void testCreateTokenFull() {
		Assertions.assertThat(factory.create(TOKEN_RARITY.GOLD, card(WILCARD_STATUS.COMPLETE)))
		        .isEqualTo("Joker 150 plein !");
		Assertions.assertThat(factory.create(TOKEN_RARITY.SILVER, card(WILCARD_STATUS.COMPLETE)))
		        .isEqualTo("Joker 70 plein !");
		Assertions.assertThat(factory.create(TOKEN_RARITY.BRONZE, card(WILCARD_STATUS.COMPLETE)))
		        .isEqualTo("Joker 30 plein !");
	}

	@Test
	public void testCreateTokenActive() {
		Assertions.assertThat(factory.create(TOKEN_RARITY.GOLD, card(WILCARD_STATUS.ACTIVE)))
		        .isEqualTo("/!\\ Joker 150 est démarré !");
		Assertions.assertThat(factory.create(TOKEN_RARITY.SILVER, card(WILCARD_STATUS.ACTIVE)))
		        .isEqualTo("/!\\ Joker 70 est démarré !");
		Assertions.assertThat(factory.create(TOKEN_RARITY.BRONZE, card(WILCARD_STATUS.ACTIVE)))
		        .isEqualTo("/!\\ Joker 30 est démarré !");
	}

	private Card card(WILCARD_STATUS status) {
		Card card = new Card();
		card.setStatus(status.getNmValue());
		return card;
	}

}
