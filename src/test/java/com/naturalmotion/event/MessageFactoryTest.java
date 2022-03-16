package com.naturalmotion.event;

import java.net.URISyntaxException;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.webservice.service.json.Card;

public class MessageFactoryTest {

	private static final String CREW = "team1";
	private MessageFactory factory = new MessageFactory();

	@Test
	public void testCreateTokenActive() throws URISyntaxException {
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, TOKEN_RARITY.GOLD), CREW)
		                .getOriginalContentUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/150_team1.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, TOKEN_RARITY.GOLD), CREW)
		                .getPreviewImageUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/150_team1.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, TOKEN_RARITY.SILVER), CREW)
		                .getOriginalContentUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/70_team1.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, TOKEN_RARITY.SILVER), CREW)
		                .getPreviewImageUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/70_team1.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, TOKEN_RARITY.BRONZE), CREW)
		                .getOriginalContentUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/30_team1.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, TOKEN_RARITY.BRONZE), CREW)
		                .getPreviewImageUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/30_team1.jpg");
	}

	private Card card(WILCARD_STATUS status, TOKEN_RARITY rarity) {
		Card card = new Card();
		card.setStatus(status.getNmValue());
		card.setRarity(rarity.getNmValue());
		return card;
	}

}
