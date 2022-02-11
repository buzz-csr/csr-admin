package com.naturalmotion.event;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.linecorp.bot.model.message.TextMessage;
import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.webservice.service.json.Card;

public class MessageFactoryTest {

	private static final String CREW = "rouge";
	private MessageFactory factory = new MessageFactory();

	@Test
	public void testCreateTokenFull() {
		Assertions.assertThat(factory.createGoldFull().getText()).isEqualTo("$$$$ $$$$$ $");
		Assertions.assertThat(factory.createGoldFull().getEmojis().size()).isEqualTo(10);
		Assertions.assertThat(factory.createSilverFull().getText()).isEqualTo("$$$ $$$$$ $");
		Assertions.assertThat(factory.createSilverFull().getEmojis().size()).isEqualTo(9);
		Assertions.assertThat(factory.createBronzeFull().getText()).isEqualTo("$$$ $$$$$ $");
		Assertions.assertThat(factory.createBronzeFull().getEmojis().size()).isEqualTo(9);
	}

	@Test
	public void testCreateTokenActive() throws URISyntaxException {
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, TOKEN_RARITY.GOLD), CREW)
		                .getOriginalContentUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/150_rouge.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, TOKEN_RARITY.GOLD), CREW)
		                .getPreviewImageUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/150_rouge.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, TOKEN_RARITY.SILVER), CREW)
		                .getOriginalContentUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/70_rouge.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, TOKEN_RARITY.SILVER), CREW)
		                .getPreviewImageUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/70_rouge.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, TOKEN_RARITY.BRONZE), CREW)
		                .getOriginalContentUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/30_rouge.jpg");
		Assertions
		        .assertThat(factory.createImage(card(WILCARD_STATUS.ACTIVE, TOKEN_RARITY.BRONZE), CREW)
		                .getPreviewImageUrl().toString())
		        .isEqualTo("https://mod.csr-lesnains.fr/csr-admin/images/line/30_rouge.jpg");
	}

	private Card card(WILCARD_STATUS status, TOKEN_RARITY rarity) {
		Card card = new Card();
		card.setStatus(status.getNmValue());
		card.setRarity(rarity.getNmValue());
		return card;
	}

	@Test
	public void testCreateUserTokenDonation() {
		Assertions.assertThat(factory.createUserTokenDonation("playerName", "20", 10, createDate()).getText())
		        .isEqualTo("lundi 11 à 22:30:00 - playerName a posé 10 sur le 20%");
	}

	private Date createDate() {
		Calendar instance = Calendar.getInstance(Locale.FRANCE);
		instance.set(Calendar.YEAR, 2022);
		instance.set(Calendar.MONTH, 03);
		instance.set(Calendar.DAY_OF_MONTH, 11);
		instance.set(Calendar.HOUR_OF_DAY, 22);
		instance.set(Calendar.MINUTE, 30);
		instance.set(Calendar.SECOND, 0);
		instance.set(Calendar.MILLISECOND, 0);
		return instance.getTime();
	}

	@Test
	public void testJoin() throws Exception {
		List<TextMessage> list = new ArrayList<>();
		list.add(new TextMessage("text1"));
		list.add(new TextMessage("text2"));
		TextMessage join = factory.join(list);
		Assertions.assertThat(join.getText()).isEqualTo("text1\ntext2");
	}

}
