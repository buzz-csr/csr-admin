package com.naturalmotion.database.token;

import java.util.List;

import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.webservice.service.json.Card;

public class Converter {

	public Token convert(List<Card> wildcards) {
		Token result = null;
		if (wildcards != null && !wildcards.isEmpty()) {
			result = new Token();

			convertGold(wildcards, result);
			convertSilver(wildcards, result);
			convertBronze(wildcards, result);
		}
		return result;
	}

	private void convertBronze(List<Card> wildcards, Token result) {
		Card actual = wildcards.stream().filter(x -> x.getRarity().equals(TOKEN_RARITY.BRONZE.getNmValue())).findFirst()
		        .orElse(null);
		if (actual != null) {
			result.setBronze(convertCard(actual));
		}
	}

	private void convertSilver(List<Card> wildcards, Token result) {
		Card actual = wildcards.stream().filter(x -> x.getRarity().equals(TOKEN_RARITY.SILVER.getNmValue())).findFirst()
		        .orElse(null);
		if (actual != null) {
			result.setSilver(convertCard(actual));
		}
	}

	private void convertGold(List<Card> wildcards, Token result) {
		Card actual = wildcards.stream().filter(x -> x.getRarity().equals(TOKEN_RARITY.GOLD.getNmValue())).findFirst()
		        .orElse(null);
		if (actual != null) {
			result.setGold(convertCard(actual));
		}
	}

	private com.naturalmotion.database.token.Card convertCard(Card actual) {
		com.naturalmotion.database.token.Card dbCard = new com.naturalmotion.database.token.Card();
		dbCard.setCost(actual.getCost());
		dbCard.setPaid(actual.getPaid());
		dbCard.setStatus(actual.getStatus());
		return dbCard;
	}

}
