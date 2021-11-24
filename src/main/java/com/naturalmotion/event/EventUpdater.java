package com.naturalmotion.event;

import java.util.List;

import org.apache.log4j.Logger;

import com.naturalmotion.database.dao.TokenDao;
import com.naturalmotion.database.token.Converter;
import com.naturalmotion.database.token.Token;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.Card;

public class EventUpdater {

	private final Logger log = Logger.getLogger(EventUpdater.class);

	private final String crew;

	private TokenDao dao = new TokenDao();

	private CrewResources crewResources = new CrewResources();

	private AuthorizationFactory authorizationFactory = new AuthorizationFactory();

	private Converter converter = new Converter();

	public EventUpdater(String crew) {
		this.crew = crew;
	}

	public void update() {
		Authorization authorization = authorizationFactory.get(crew);
		try {
			List<Card> wildcards = crewResources.getWildcards(authorization);
			Token convert = converter.convert(wildcards, crew);

			if (dao.update(convert) == 0) {
				dao.insert(convert);
			}
		} catch (Exception e) {
			log.error("Error updating event data", e);
		}
	}
}
