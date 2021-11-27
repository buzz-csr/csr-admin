package com.naturalmotion.event;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.sql.SQLException;
import java.util.ArrayList;
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
import com.naturalmotion.database.token.Token;
import com.naturalmotion.database.usertoken.UserToken;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.api.Member;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.Card;
import com.naturalmotion.webservice.service.json.tchat.Message;
import com.naturalmotion.webservice.service.json.tchat.Metadata;

@RunWith(MockitoJUnitRunner.class)
public class EventDetectorTest {

	private static final String COMPLETE = "complete";

	private static final String BRONZE_STATUS = "status3";

	private static final String SILVER_STATUS = "status2";

	private static final String GOLD_STATUS = "status1";

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
	private EventDetector eventDetector = new EventDetector("rouge");

	@Captor
	private ArgumentCaptor<TextMessage> textMessage;

	@Before
	public void setup() throws SQLException {
		doReturn(realWilcards()).when(crewResources).getWildcards(any());
		doReturn(userToken("id5", "zid5")).when(userDao).readUserToken("id5");
	}

	private UserToken userToken(String id, String playerId) {
		UserToken userToken = new UserToken();
		userToken.setId(id);
		userToken.setUser(playerId);
		return userToken;
	}

	private Token dbWildcards(String goldStatus, String silverStatus, String bronzeStatus) {
		Token token = new Token();
		token.setBronze(dbCard(bronzeStatus));
		token.setSilver(dbCard(silverStatus));
		token.setGold(dbCard(goldStatus));
		return token;
	}

	private com.naturalmotion.database.token.Card dbCard(String status) {
		com.naturalmotion.database.token.Card bronze = new com.naturalmotion.database.token.Card();
		bronze.setCost(30);
		bronze.setPaid(10);
		bronze.setStatus(status);
		return bronze;
	}

	private List<Card> realWilcards() {
		return realWilcards(GOLD_STATUS, SILVER_STATUS, BRONZE_STATUS);
	}

	private List<Card> realWilcards(String goldStatus, String silverStatus, String bronzeStatus) {
		List<Card> arrayList = new ArrayList<>();
		arrayList.add(card(goldStatus, TOKEN_RARITY.GOLD));
		arrayList.add(card(silverStatus, TOKEN_RARITY.SILVER));
		arrayList.add(card(bronzeStatus, TOKEN_RARITY.BRONZE));
		return arrayList;
	}

	private Card card(String status, TOKEN_RARITY rarity) {
		Card card = new Card();
		card.setCost(150);
		card.setPaid(10);
		card.setRarity(rarity.getNmValue());
		card.setStatus(status);
		return card;
	}

	@Test
	public void testDetectNoNmData() throws Exception {
		doReturn(null).when(crewResources).getWildcards(any());
		eventDetector.detect();
		verifyZeroInteractions(messageService);
	}

	@Test
	public void testDetectNoChanges() throws Exception {
		doReturn(dbWildcards(GOLD_STATUS, SILVER_STATUS, BRONZE_STATUS)).when(dao).read(anyString());
		eventDetector.detect();
		verifyZeroInteractions(messageService);
	}

	@Test
	public void testDetectGoldChanges() throws Exception {
		doReturn(realWilcards(COMPLETE, SILVER_STATUS, BRONZE_STATUS)).when(crewResources).getWildcards(any());
		doReturn(dbWildcards(GOLD_STATUS, SILVER_STATUS, BRONZE_STATUS)).when(dao).read(anyString());
		eventDetector.detect();
		verify(messageService).pushMessage(textMessage.capture());
		Assertions.assertThat(textMessage.getValue().getText()).isEqualTo("$$$$ $$$$$ $");
		verifyZeroInteractions(messageService);
	}

	@Test
	public void testDetectSilverChanges() throws Exception {
		doReturn(realWilcards(GOLD_STATUS, COMPLETE, BRONZE_STATUS)).when(crewResources).getWildcards(any());
		doReturn(dbWildcards(GOLD_STATUS, SILVER_STATUS, BRONZE_STATUS)).when(dao).read(anyString());
		eventDetector.detect();
		verify(messageService).pushMessage(textMessage.capture());
		Assertions.assertThat(textMessage.getValue().getText()).isEqualTo("$$$ $$$$$ $");
		verifyZeroInteractions(messageService);
	}

	@Test
	public void testDetectBronzeChanges() throws Exception {
		doReturn(realWilcards(GOLD_STATUS, SILVER_STATUS, COMPLETE)).when(crewResources).getWildcards(any());

		doReturn(dbWildcards(GOLD_STATUS, SILVER_STATUS, BRONZE_STATUS)).when(dao).read(anyString());
		eventDetector.detect();
		verify(messageService).pushMessage(textMessage.capture());
		Assertions.assertThat(textMessage.getValue().getText()).isEqualTo("$$$ $$$$$ $");
		verifyZeroInteractions(messageService);
	}

	@Test
	public void testDetectAllChanges() throws Exception {
		doReturn(realWilcards(COMPLETE, COMPLETE, COMPLETE)).when(crewResources).getWildcards(any());
		doReturn(dbWildcards(GOLD_STATUS, SILVER_STATUS, BRONZE_STATUS)).when(dao).read(anyString());
		eventDetector.detect();
		verify(messageService, times(3)).pushMessage(textMessage.capture());
		Assertions.assertThat(textMessage.getAllValues()).hasSize(3);
		verifyZeroInteractions(messageService);
	}

	@Test
	public void testDetectTokenDonation() {
		doReturn(conversations()).when(crewResources).getConversations(any(), anyString());
		doReturn(members()).when(crewResources).getMembers(any());
		eventDetector.detect();
		verify(messageService, times(1)).pushMessage(textMessage.capture());
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
		serverConversations.add(message("id1", "zid1", null));
		serverConversations.add(message("id2", "zid2", metadata(10)));
		serverConversations.add(message("id3", "zid3", metadata(0)));
		serverConversations.add(message("id4", "zid4", metadata(5)));
		serverConversations.add(message("id5", "zid5", metadata(6)));
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

	private Metadata metadata(int paidDelta) {
		Metadata metadata = new Metadata();
		metadata.setCard(card(paidDelta));
		return metadata;
	}

	private com.naturalmotion.webservice.service.json.tchat.Card card(int paidDelta) {
		com.naturalmotion.webservice.service.json.tchat.Card card = new com.naturalmotion.webservice.service.json.tchat.Card();
		card.setPaidDelta(paidDelta);
		card.setRarity(TOKEN_RARITY.GOLD.getNmValue());
		return card;
	}

	private Message message(String id, String playerId, Metadata meta) {
		Message message = new Message();
		message.setZid(playerId);
		message.setId(id);
		message.setMeta(meta);
		return message;
	}
}
