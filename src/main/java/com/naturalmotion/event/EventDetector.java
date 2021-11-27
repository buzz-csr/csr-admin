package com.naturalmotion.event;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.line.api.MessageService;
import com.line.api.MessageServiceImpl;
import com.linecorp.bot.model.message.TextMessage;
import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.database.dao.TokenDao;
import com.naturalmotion.database.dao.UserTokenDao;
import com.naturalmotion.database.token.Token;
import com.naturalmotion.database.usertoken.UserToken;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.api.Member;
import com.naturalmotion.webservice.configuration.Configuration;
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.Card;
import com.naturalmotion.webservice.service.json.tchat.Message;

public class EventDetector {

	private Logger log = Logger.getLogger(EventDetector.class);

	private final String crew;

	private CrewResources crewResources = new CrewResources();

	private TokenDao dao = new TokenDao();

	private UserTokenDao userTokenDao = new UserTokenDao();

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

		if (isTokenDetectionEnable()) {
			detectUserToken(authorization);
		}
	}

	private void detectUserToken(Authorization authorization) {
		List<TextMessage> tMessages = new ArrayList<>();
		try {
			List<List<Message>> conversations = crewResources.getConversations(authorization,
			        configuration.getString(crew + ".crew-id"));
			if (hasTokenConversations(conversations)) {
				List<Message> conv = conversations.get(1);

				List<Member> members = crewResources.getMembers(authorization);

				for (Message message : conv) {
					if (isTokenDonationMessage(message)) {
						TextMessage tMessage = detectTokenChange(message, members);
						if (tMessage != null) {
							tMessages.add(tMessage);
						}
					}
				}
			}

			if (!tMessages.isEmpty()) {
				messageService.pushMessage(messageFactory.join(tMessages));
			}
		} catch (Exception e) {
			log.error("Error detecting user token donation", e);
		}
	}

	private TextMessage detectTokenChange(Message message, List<Member> members) throws SQLException {
		TextMessage textMessage = null;
		UserToken dbMessage = userTokenDao.readUserToken(message.getId());
		if (isUserDonationToSend(dbMessage)) {
			userTokenDao.insertUserToken(createToken(message));
			com.naturalmotion.webservice.service.json.tchat.Card card = message.getMeta().getCard();
			Member actualMember = members.stream().filter(x -> message.getZid().equals(x.getId())).findFirst()
			        .orElse(null);
			textMessage = messageFactory.createUserTokenDonation(getUserName(message, actualMember),
			        TOKEN_RARITY.from(card.getRarity()).getName(), card.getPaidDelta());
		}
		return textMessage;
	}

	private String getUserName(Message message, Member actualMember) {
		String name = null;
		if (actualMember != null && StringUtils.isNoneBlank(actualMember.getName())) {
			name = actualMember.getName();
		} else {
			name = message.getZid();
		}
		return name;
	}

	private boolean hasTokenConversations(List<List<Message>> conversations) {
		return conversations != null && conversations.size() > 1;
	}

	private boolean isTokenDetectionEnable() {
		return configuration.getList("token.task.crew").contains(crew);
	}

	private UserToken createToken(Message message) {
		UserToken token = new UserToken();
		token.setId(message.getId());
		token.setUser(message.getZid());
		token.setRarity(message.getMeta().getCard().getRarity());
		return token;
	}

	private boolean isTokenDonationMessage(Message message) {
		return message.getMeta() != null && message.getMeta().getCard() != null
		        && message.getMeta().getCard().getPaidDelta() > 0;
	}

	private boolean isUserDonationToSend(UserToken dbMessage) {
		return dbMessage == null;
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
				messageService.pushMessage(textMessage);
			}
			if (WILCARD_STATUS.ACTIVE.getNmValue().equals(actualCard.getStatus())) {
				try {
					messageService.pushImage(messageFactory.createImage(actualCard, crew));
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
