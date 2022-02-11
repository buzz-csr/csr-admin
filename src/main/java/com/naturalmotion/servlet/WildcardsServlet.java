package com.naturalmotion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.configuration.Configuration;
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.Card;

public class WildcardsServlet extends HttpServlet {

	private static final long serialVersionUID = -618859554938428225L;

	private final Logger log = LoggerFactory.getLogger(WildcardsServlet.class);

	private CrewResources crewResources = new CrewResources();

	private AuthorizationFactory authorizationFactory = new AuthorizationFactory();

	private Configuration configuration = new Configuration();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String crew = req.getParameter("crew");
		String crewPlayerId = configuration.getString(crew + ".player-id");

		Authorization authorization = authorizationFactory.get(crewPlayerId);
		List<Card> wildcards = crewResources.getWildcards(authorization);

		resp.setContentType("application/json; charset=UTF-8");
		try (PrintWriter writer = resp.getWriter();) {
			ObjectMapper mapper = new ObjectMapper();
			writer.write(mapper.writeValueAsString(wildcards));
		} catch (Exception e) {
			log.error("Error loading crew informations", e);
		}

	}

}
