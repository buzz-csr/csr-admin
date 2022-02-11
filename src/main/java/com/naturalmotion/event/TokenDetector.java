package com.naturalmotion.event;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.line.api.MessageService;
import com.line.api.MessageServiceImpl;
import com.linecorp.bot.model.message.TextMessage;
import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.database.dao.UserTokenDao;
import com.naturalmotion.database.usertoken.UserToken;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.api.Member;
import com.naturalmotion.webservice.configuration.Configuration;
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.tchat.Message;

public class TokenDetector {

	private Logger log = Logger.getLogger(TokenDetector.class);

	private final String crew;

	private CrewResources crewResources = new CrewResources();

	private UserTokenDao userTokenDao = new UserTokenDao();

	private AuthorizationFactory authorizationFactory = new AuthorizationFactory();

	private MessageService messageService = new MessageServiceImpl();

	private MessageFactory messageFactory = new MessageFactory();

	private Configuration configuration = new Configuration();

	private String lineReplyId;

	public TokenDetector(String crew) {
		this.crew = crew;
		lineReplyId = configuration.getString("line.user.reply");
	}

	public void detect() {
		if (isTokenDetectionEnable()) {
			Authorization authorization = authorizationFactory.get(crew);
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
				messageService.pushMessage(messageFactory.join(tMessages), lineReplyId);
			}
		} catch (Exception e) {
			log.error("Error detecting user token donation", e);
		}
	}

	private TextMessage detectTokenChange(Message message, List<Member> members) throws SQLException {
		TextMessage textMessage = null;
		UserToken dbMessage = userTokenDao.readUserToken(message.getZid(),
		        new Timestamp(message.getCreationTime().getTime()));
		if (isUserDonationToSend(dbMessage)) {
			userTokenDao.insertUserToken(createToken(message));
			com.naturalmotion.webservice.service.json.tchat.Card card = message.getMeta().getCard();
			Member actualMember = members.stream().filter(x -> message.getZid().equals(x.getId())).findFirst()
			        .orElse(null);
			textMessage = messageFactory.createUserTokenDonation(getUserName(message, actualMember),
			        TOKEN_RARITY.from(card.getRarity()).getName(), card.getPaidDelta(), message.getCreationTime());
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
		token.setTokenDate(new Timestamp(message.getCreationTime().getTime()));
		com.naturalmotion.webservice.service.json.tchat.Card card = message.getMeta().getCard();
		token.setRarity(card.getRarity());
		token.setPaid(card.getPaidDelta());
		return token;
	}

	private boolean isTokenDonationMessage(Message message) {
		return message.getMeta() != null && "WCARD_STATUS".equals(message.getMeta().getEventID())
		        && message.getMeta().getCard() != null && message.getMeta().getCard().getPaidDelta() > 0;
	}

	private boolean isUserDonationToSend(UserToken dbMessage) {
		return dbMessage == null;
	}
}
