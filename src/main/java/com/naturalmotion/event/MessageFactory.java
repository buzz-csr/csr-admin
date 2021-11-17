package com.naturalmotion.event;

import com.naturalmotion.database.TOKEN_RARITY;

public class MessageFactory {

	public String create(TOKEN_RARITY tokenType, com.naturalmotion.webservice.service.json.Card actualCard) {
		StringBuilder message = new StringBuilder();
		if (WILCARD_STATUS.COMPLETE.getNmValue().equals(actualCard.getStatus())) {
			message.append("Joker " + tokenType.getName() + " plein !");
		} else if (WILCARD_STATUS.ACTIVE.getNmValue().equals(actualCard.getStatus())) {
			message.append("/!\\ Joker " + tokenType.getName() + " est démarré !");
		}
		return message.toString();
	}

}
