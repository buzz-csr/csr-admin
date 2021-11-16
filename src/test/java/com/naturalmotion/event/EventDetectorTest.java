package com.naturalmotion.event;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.line.api.MessageService;
import com.naturalmotion.database.TOKEN_RARITY;
import com.naturalmotion.database.TokenDao;
import com.naturalmotion.database.token.Token;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.Card;

@RunWith(MockitoJUnitRunner.class)
public class EventDetectorTest {

	private static final String BRONZE_STATUS = "status3";

	private static final String SILVER_STATUS = "status2";

	private static final String GOLD_STATUS = "status1";

	@Mock
	private CrewResources crewResources;

	@Mock
	private TokenDao dao;

	@Mock
	private AuthorizationFactory authorizationFactory;

	@Mock
	private MessageService messageService;

	@InjectMocks
	private EventDetector eventDetector = new EventDetector("rouge");

	@Before
	public void setup() throws SQLException {
		doReturn(realWilcards()).when(crewResources).getWildcards(any());
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
		List<Card> arrayList = new ArrayList<>();
		arrayList.add(card(GOLD_STATUS, TOKEN_RARITY.GOLD));
		arrayList.add(card(SILVER_STATUS, TOKEN_RARITY.SILVER));
		arrayList.add(card(BRONZE_STATUS, TOKEN_RARITY.BRONZE));
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
		doReturn(dbWildcards("status11", SILVER_STATUS, BRONZE_STATUS)).when(dao).read(anyString());
		eventDetector.detect();
		verify(messageService).pushMessage(eq("Joker 150 plein !"), anyString());
		verifyZeroInteractions(messageService);
	}

	@Test
	public void testDetectSilverChanges() throws Exception {
		doReturn(dbWildcards(GOLD_STATUS, "status21", BRONZE_STATUS)).when(dao).read(anyString());
		eventDetector.detect();
		verify(messageService).pushMessage(eq("Joker 70 plein !"), anyString());
		verifyZeroInteractions(messageService);
	}

	@Test
	public void testDetectBronzeChanges() throws Exception {
		doReturn(dbWildcards(GOLD_STATUS, SILVER_STATUS, "status31")).when(dao).read(anyString());
		eventDetector.detect();
		verify(messageService).pushMessage(eq("Joker 30 plein !"), anyString());
		verifyZeroInteractions(messageService);
	}

	@Test
	public void testDetectAllChanges() throws Exception {
		doReturn(dbWildcards("status11", "status21", "status31")).when(dao).read(anyString());
		eventDetector.detect();
		verify(messageService).pushMessage(eq("Joker 150 plein !"), anyString());
		verify(messageService).pushMessage(eq("Joker 70 plein !"), anyString());
		verify(messageService).pushMessage(eq("Joker 30 plein !"), anyString());
		verifyZeroInteractions(messageService);
	}
}
