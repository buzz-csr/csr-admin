package com.naturalmotion.event;

import java.util.List;

import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.Card;

public class EventDetector {

	private final String crew;

	private CrewResources crewResources = new CrewResources();

	private AuthorizationFactory authorizationFactory = new AuthorizationFactory();

	public EventDetector(String crew) {
		this.crew = crew;
	}

	public void detect() {
		Authorization authorization = authorizationFactory.get(crew);
		List<Card> wildcards = crewResources.getWildcards(authorization);

	}

}
