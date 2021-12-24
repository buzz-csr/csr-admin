package com.naturalmotion.event;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.naturalmotion.database.dao.TokenDao;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.Card;

@RunWith(MockitoJUnitRunner.class)
public class WildcardUpdaterTest {

	@Mock
	private TokenDao dao;

	@Mock
	private CrewResources crewResources;

	@Mock
	private AuthorizationFactory authorizationFactory;

	@InjectMocks
	private WildcardUpdater eventUpdater = new WildcardUpdater("crew");

	@Test
	public void testUpdate() throws SQLException {
		List<Card> wilcards = new ArrayList<>();
		doReturn(wilcards).when(crewResources).getWildcards(Mockito.any());

		eventUpdater.update();
		verify(dao).update(Mockito.any());
	}

}
