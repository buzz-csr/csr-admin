package com.naturalmotion.event;

import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;

import com.line.api.MessageService;
import com.line.api.MessageServiceImpl;
import com.linecorp.bot.model.message.TextMessage;
import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.database.dao.TokenDao;
import com.naturalmotion.database.token.Token;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.configuration.Configuration;
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.Card;

public class WildcardDetector {

	private Logger log = Logger.getLogger(WildcardDetector.class);

	private final String crew;

	private CrewResources crewResources = new CrewResources();

	private TokenDao dao = new TokenDao();

	private AuthorizationFactory authorizationFactory = new AuthorizationFactory();

	private MessageService messageService = new MessageServiceImpl();

	private MessageFactory messageFactory = new MessageFactory();

	private Configuration configuration = new Configuration();

	private String lineReplyId;

	public WildcardDetector(String crew) {
		this.crew = crew;
		lineReplyId = configuration.getString("line.user.reply." + crew);
	}

	public void detect() {
		Authorization authorization = authorizationFactory.get(crew);
		detectWilcards(authorization);
	}

	private void detectWilcards(Authorization authorization) {
		try {
			List<Card> wildcards = crewResources.getWildcards(authorization);
			Token read = dao.read(crew);

			if (read != null) {
				detectWilcardChanges(read.getGold(), wildcards, TOKEN_RARITY.GOLD);
				detectWilcardChanges(read.getSilver(), wildcards, TOKEN_RARITY.SILVER);
				detectWilcardChanges(read.getBronze(), wildcards, TOKEN_RARITY.BRONZE);
			}
		} catch (Exception e) {
			log.error("Error detecting wilcard changes", e);
		}
	}

	private void detectWilcardChanges(com.naturalmotion.database.token.Card dbCard, List<Card> wildcards,
	        TOKEN_RARITY rarity) {
		Card actualCard = filterCard(rarity, wildcards);
		if (dbCard != null && isChanged(rarity, dbCard, actualCard)) {
			if (WILCARD_STATUS.COMPLETE.getNmValue().equals(actualCard.getStatus())) {
				TextMessage textMessage = null;
				switch (rarity) {
				case GOLD:
					textMessage = messageFactory.createGoldFull();
					break;
				case SILVER:
					textMessage = messageFactory.createSilverFull();
					break;
				case BRONZE:
					textMessage = messageFactory.createBronzeFull();
					break;
				default:
					break;
				}
				messageService.pushMessage(textMessage, lineReplyId);
			}
			if (WILCARD_STATUS.ACTIVE.getNmValue().equals(actualCard.getStatus())) {
				try {
					messageService.pushImage(messageFactory.createImage(actualCard, crew), lineReplyId);
				} catch (URISyntaxException e) {
					log.error("Impossible d'envoyer l'image");
				}
			}
		}
	}

	private boolean isChanged(TOKEN_RARITY rarity, com.naturalmotion.database.token.Card dbCard, Card actualCard) {
		return actualCard != null && !actualCard.getStatus().equals(dbCard.getStatus());
	}

	private Card filterCard(TOKEN_RARITY rarity, List<Card> wildcards) {
		return wildcards.stream().filter(x -> x.getRarity().equals(rarity.getNmValue())).findFirst().orElse(null);
	}

}
