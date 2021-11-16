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
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.Card;

public class EventDetector {

	private static final String COMPLETE = "complete";

	private static final String LINE_USER = "U34b21f21232f2c9134cbb741eedfa6d2";

	private Logger log = Logger.getLogger(EventDetector.class);

	private final String crew;

	private CrewResources crewResources = new CrewResources();

	private TokenDao dao = new TokenDao();

	private AuthorizationFactory authorizationFactory = new AuthorizationFactory();

	private MessageService messageService = new MessageServiceImpl();

	public EventDetector(String crew) {
		this.crew = crew;
	}

	public void detect() {
		Authorization authorization = authorizationFactory.get(crew);
		List<Card> wildcards = crewResources.getWildcards(authorization);
		try {
			Token read = dao.read(crew);

			if (read != null && read.getGold() != null && isCompleted(TOKEN_RARITY.GOLD, read.getGold(), wildcards)) {
				messageService.pushMessage("Joker 150 plein !", LINE_USER);
			}
			if (read != null && read.getSilver() != null
			        && isCompleted(TOKEN_RARITY.SILVER, read.getSilver(), wildcards)) {
				messageService.pushMessage("Joker 70 plein !", LINE_USER);
			}
			if (read != null && read.getBronze() != null
			        && isCompleted(TOKEN_RARITY.BRONZE, read.getBronze(), wildcards)) {
				messageService.pushMessage("Joker 30 plein !", LINE_USER);
			}
		} catch (SQLException e) {
			log.error("Error read wilcards from database", e);
		}

	}

	private boolean isCompleted(TOKEN_RARITY rarity, com.naturalmotion.database.token.Card dbCard,
	        List<Card> wildcards) {
		Card actualCard = filterCard(rarity, wildcards);
		return actualCard != null && COMPLETE.equals(actualCard.getStatus())
		        && !actualCard.getStatus().equals(dbCard.getStatus());
	}

	private Card filterCard(TOKEN_RARITY rarity, List<Card> wildcards) {
		return wildcards.stream().filter(x -> x.getRarity().equals(rarity.getNmValue())).findFirst().orElse(null);
	}

}
