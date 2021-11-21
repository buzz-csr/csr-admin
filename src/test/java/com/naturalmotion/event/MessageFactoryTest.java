package com.naturalmotion.event;

import java.net.URISyntaxException;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.naturalmotion.webservice.service.json.Card;

public class MessageFactoryTest {

	private static final String CREW = "rouge";
	private MessageFactory factory = new MessageFactory();

	@Test
	public void testCreateTokenFull() {
		Assertions.assertThat(factory.createGoldFull().getText()).isEqualTo("$$$$$ $$$$ $$$$$ $");
		Assertions.assertThat(factory.createGoldFull().getEmojis().size()).isEqualTo(15);
		Assertions.assertThat(factory.createSilverFull().getText()).isEqualTo("$$$$$ $$$ $$$$$ $");
		Assertions.assertThat(factory.createSilverFull().getEmojis().size()).isEqualTo(14);
		Assertions.assertThat(factory.createBronzeFull().getText()).isEqualTo("$$$$$ $$$ $$$$$ $");
		Assertions.assertThat(factory.createBronzeFull().getEmojis().size()).isEqualTo(14);
	}

	@Test
	public void testCreateTokenActive() throws URISyntaxException {
		Assertions
		        .assertThat(
		                factory.createImage(card(WILCARD_STATUS.ACTIVE, 150), CREW).getOriginalContentUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/150_rouge.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, 150), CREW).getPreviewImageUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/150_rouge.jpg");
		Assertions
		        .assertThat(
		                factory.createImage(card(WILCARD_STATUS.ACTIVE, 70), CREW).getOriginalContentUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/70_rouge.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, 70), CREW).getPreviewImageUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/70_rouge.jpg");
		Assertions
		        .assertThat(
		                factory.createImage(card(WILCARD_STATUS.ACTIVE, 30), CREW).getOriginalContentUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/30_rouge.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, 30), CREW).getPreviewImageUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/30_rouge.jpg");
	}

	private Card card(WILCARD_STATUS status, int cost) {
		Card card = new Card();
		card.setStatus(status.getNmValue());
		card.setCost(cost);
		return card;
	}

}
