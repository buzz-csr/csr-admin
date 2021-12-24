package com.naturalmotion.event;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.line.api.MessageService;
import com.linecorp.bot.model.message.TextMessage;
import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.database.dao.TokenDao;
import com.naturalmotion.database.dao.UserTokenDao;
import com.naturalmotion.database.usertoken.UserToken;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.api.Member;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.tchat.Message;
import com.naturalmotion.webservice.service.json.tchat.Metadata;

@RunWith(MockitoJUnitRunner.class)
public class TokenDetectorTest {

	private static final Timestamp TOKEN_DATE = new Timestamp(System.currentTimeMillis());

	@Mock
	private CrewResources crewResources;

	@Mock
	private TokenDao dao;

	@Mock
	private UserTokenDao userDao;

	@Mock
	private AuthorizationFactory authorizationFactory;

	@Mock
	private MessageService messageService;

	@InjectMocks
	private TokenDetector wildcardDetector = new TokenDetector("rouge");

	@Captor
	private ArgumentCaptor<TextMessage> textMessage;

	@Before
	public void setup() throws SQLException {
		doReturn(userToken("id5", "zid5")).when(userDao).readUserToken(org.mockito.Matchers.eq("zid5"), any());
	}

	private UserToken userToken(String id, String playerId) {
		UserToken userToken = new UserToken();
		userToken.setId(id);
		userToken.setUser(playerId);
		userToken.setTokenDate(TOKEN_DATE);
		return userToken;
	}

	@Test
	public void testDetectTokenDonation() {
		doReturn(conversations()).when(crewResources).getConversations(any(), anyString());
		doReturn(members()).when(crewResources).getMembers(any());
		wildcardDetector.detect();
		verify(messageService, times(1)).pushMessage(textMessage.capture(), anyString());
		Assertions.assertThat(textMessage.getValue().getText())
		        .isEqualTo("zid2 a posé 10 sur le 150%\nname4 a posé 5 sur le 150%");
	}

	private List<Member> members() {
		List<Member> members = new ArrayList<>();
		members.add(member("zid4", "name4"));
		return members;
	}

	private List<List<Message>> conversations() {
		List<List<Message>> conversations = new ArrayList<>();
		List<Message> serverConversations = new ArrayList<>();
		serverConversations.add(message("id1", "zid1", null, new Date()));
		serverConversations.add(message("id2", "zid2", metadata(10, "WCARD_STATUS"), new Date()));
		serverConversations.add(message("id3", "zid3", metadata(0, "WCARD_STATUS"), new Date()));
		serverConversations.add(message("id4", "zid4", metadata(5, "WCARD_STATUS"), new Date()));
		serverConversations.add(message("id5", "zid5", metadata(6, "WCARD_STATUS"), TOKEN_DATE));
		serverConversations.add(message("id6", "zid5", metadata(6, "toto"), new Date()));
		conversations.add(new ArrayList<>());
		conversations.add(serverConversations);
		return conversations;
	}

	private Member member(String id, String name) {
		Member member = new Member();
		member.setId(id);
		member.setName(name);
		return member;
	}

	private Metadata metadata(int paidDelta, String eventId) {
		Metadata metadata = new Metadata();
		metadata.setCard(card(paidDelta));
		metadata.setEventID(eventId);
		return metadata;
	}

	private com.naturalmotion.webservice.service.json.tchat.Card card(int paidDelta) {
		com.naturalmotion.webservice.service.json.tchat.Card card = new com.naturalmotion.webservice.service.json.tchat.Card();
		card.setPaidDelta(paidDelta);
		card.setRarity(TOKEN_RARITY.GOLD.getNmValue());
		return card;
	}

	private Message message(String id, String playerId, Metadata meta, Date date) {
		Message message = new Message();
		message.setZid(playerId);
		message.setId(id);
		message.setMeta(meta);
		message.setCreationTime(date);
		return message;
	}
}
