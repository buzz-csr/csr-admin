package com.naturalmotion.event;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.line.api.MessageService;
import com.line.api.MessageServiceImpl;
import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.database.TokenDao;
import com.naturalmotion.database.token.Token;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.configuration.Configuration;
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.Card;

public class EventDetector {

	private static final String LINE_USER = "U34b21f21232f2c9134cbb741eedfa6d2";

	private Logger log = Logger.getLogger(EventDetector.class);

	private final String crew;

	private CrewResources crewResources = new CrewResources();

	private TokenDao dao = new TokenDao();

	private AuthorizationFactory authorizationFactory = new AuthorizationFactory();

	private MessageService messageService = new MessageServiceImpl();

	private MessageFactory messageFactory = new MessageFactory();

	private Configuration configuration = new Configuration();

	public EventDetector(String crew) {
		this.crew = crew;
	}

	public void detect() {
		Authorization authorization = authorizationFactory.get(crew);
		detectWilcards(authorization);
		// crewResources.getConversations(authorization, configuration.getString(crew +
		// ".crew-id"));
	}

	private void detectWilcards(Authorization authorization) {
		List<Card> wildcards = crewResources.getWildcards(authorization);
		try {
			Token read = dao.read(crew);

			if (read != null) {
				detectWilcardChanges(read.getGold(), wildcards, TOKEN_RARITY.GOLD);
				detectWilcardChanges(read.getSilver(), wildcards, TOKEN_RARITY.SILVER);
				detectWilcardChanges(read.getBronze(), wildcards, TOKEN_RARITY.BRONZE);
			}
		} catch (SQLException e) {
			log.error("Error read wilcards from database", e);
		}
	}

	private void detectWilcardChanges(com.naturalmotion.database.token.Card dbCard, List<Card> wildcards,
	        TOKEN_RARITY rarity) {
		Card actualGold = filterCard(rarity, wildcards);
		if (dbCard != null && isChanged(rarity, dbCard, actualGold)) {
			messageService.pushMessage(messageFactory.create(rarity, actualGold), LINE_USER);
		}
	}

	private boolean isChanged(TOKEN_RARITY rarity, com.naturalmotion.database.token.Card dbCard, Card actualCard) {
		return actualCard != null && !actualCard.getStatus().equals(dbCard.getStatus());
	}

	private Card filterCard(TOKEN_RARITY rarity, List<Card> wildcards) {
		return wildcards.stream().filter(x -> x.getRarity().equals(rarity.getNmValue())).findFirst().orElse(null);
	}

}
