package com.naturalmotion.event;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.line.api.MessageService;
import com.line.api.MessageServiceImpl;
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

	private MessageService messageService;

	private CrewResources crewResources = new CrewResources();

	private UserTokenDao userTokenDao = new UserTokenDao();

	private AuthorizationFactory authorizationFactory = new AuthorizationFactory();

	private MessageFactory messageFactory = new MessageFactory();

	private Configuration configuration = new Configuration();

	private String lineReplyId;

	private SimpleDateFormat sdfDay = new SimpleDateFormat("EEEEE", Locale.FRANCE);
	private SimpleDateFormat sdfHours = new SimpleDateFormat("HH:mm", Locale.FRANCE);

	public TokenDetector(String crew) {
		this.crew = crew;
		lineReplyId = configuration.getString("line.user.reply." + crew);
		messageService = new MessageServiceImpl(configuration.getString("line.access_token"));
	}

	public void detect() {
		if (isTokenDetectionEnable()) {
			Authorization authorization = authorizationFactory.get(crew);
			detectUserToken(authorization);
		}
	}

	private void detectUserToken(Authorization authorization) {
		try {
			List<List<Message>> conversations = crewResources.getConversations(authorization,
			        configuration.getString(crew + ".crew-id"));
			if (hasTokenConversations(conversations)) {
				List<Message> conv = conversations.get(1);

				List<Member> members = crewResources.getMembers(authorization);

				Map<String, List<List<String>>> data = new HashMap<>();
				for (Message message : conv) {
					if (isTokenDonationMessage(message)) {
						String day = sdfDay.format(message.getCreationTime());
						if (data.get(day) == null) {
							List<List<String>> arrayList = new ArrayList<>();
							arrayList.add(new ArrayList<>()); // [0] = Username
							arrayList.add(new ArrayList<>()); // [1] = Token
							arrayList.add(new ArrayList<>()); // [2] = Hour of the day
							data.put(day, arrayList);
						}
						detectTokenChange(message, members, data.get(day));
					}
				}
				messageService.pushMessage(messageFactory.createFlexMessage(crew, data), lineReplyId);
			}
		} catch (Exception e) {
			log.error("Error detecting user token donation", e);
		}
	}

	private void detectTokenChange(Message message, List<Member> members, List<List<String>> list) throws SQLException {
		UserToken dbMessage = userTokenDao.readUserToken(message.getZid(),
		        new Timestamp(message.getCreationTime().getTime()));
		if (isUserDonationToSend(dbMessage)) {
			userTokenDao.insertUserToken(createToken(message));
			com.naturalmotion.webservice.service.json.tchat.Card card = message.getMeta().getCard();
			Member actualMember = members.stream().filter(x -> message.getZid().equals(x.getId())).findFirst()
			        .orElse(null);
			list.get(0).add(getUserName(message, actualMember));
			list.get(1).add(card.getPaidDelta() + "/" + TOKEN_RARITY.from(card.getRarity()).getName());
			list.get(2).add(sdfHours.format(message.getCreationTime()));
		}
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
		return configuration.getList("crew.list").contains(crew);
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
