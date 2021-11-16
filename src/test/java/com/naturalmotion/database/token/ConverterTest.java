package com.naturalmotion.database.token;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.webservice.service.json.Card;

public class ConverterTest {

	private Converter converter = new Converter();

	@Test
	public void testConvertNull() {
		Token actual = converter.convert(null, "crew");
		Assertions.assertThat(actual).isNull();
	}

	@Test
	public void testConvert() {
		List<Card> wildcards = new ArrayList<>();
		wildcards.add(card(TOKEN_RARITY.BRONZE, 1, 10, "status1"));
		wildcards.add(card(TOKEN_RARITY.GOLD, 2, 11, "status2"));
		wildcards.add(card(TOKEN_RARITY.SILVER, 3, 12, "status3"));
		Token actual = converter.convert(wildcards, "crew");
		Assertions.assertThat(actual.getBronze().getCost()).isEqualTo(1);
		Assertions.assertThat(actual.getBronze().getPaid()).isEqualTo(10);
		Assertions.assertThat(actual.getBronze().getStatus()).isEqualTo("status1");

		Assertions.assertThat(actual.getGold().getCost()).isEqualTo(2);
		Assertions.assertThat(actual.getGold().getPaid()).isEqualTo(11);
		Assertions.assertThat(actual.getGold().getStatus()).isEqualTo("status2");

		Assertions.assertThat(actual.getSilver().getCost()).isEqualTo(3);
		Assertions.assertThat(actual.getSilver().getPaid()).isEqualTo(12);
		Assertions.assertThat(actual.getSilver().getStatus()).isEqualTo("status3");

	}

	private Card card(TOKEN_RARITY rarity, int cost, int paid, String status) {
		Card card = new Card();
		card.setCost(cost);
		card.setPaid(paid);
		card.setRarity(rarity.getNmValue());
		card.setStatus(status);
		return card;
	}

}
