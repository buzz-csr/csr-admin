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
import com.naturalmotion.webservice.api.Profile;
import com.naturalmotion.webservice.configuration.Configuration;
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.event.Player;

public class EventServlet extends HttpServlet {

	private static final long serialVersionUID = 5652721432844490519L;

	private Logger log = LoggerFactory.getLogger(EventServlet.class);

	private CrewResources resources = new CrewResources();

	private AuthorizationFactory authorizationFactory = new AuthorizationFactory();

	private Configuration configuration = new Configuration();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String action = req.getParameter("action");
		String crew = req.getParameter("crew");
		Authorization authorization = authorizationFactory.get(configuration.getString(crew + ".player-id"));

		if ("list".equals(action)) {
			getEventsList(resp, authorization);
		} else {
			getEventScore(req, resp, authorization);
		}
	}

	private void getEventScore(HttpServletRequest req, HttpServletResponse resp, Authorization authorization) {
		String event = req.getParameter("eventName");
		List<Player> score = resources.getEventCrewScore(authorization, event);
		resp.setContentType("application/json; charset=UTF-8");
		try (PrintWriter writer = resp.getWriter();) {
			ObjectMapper mapper = new ObjectMapper();
			writer.write(mapper.writeValueAsString(score));
		} catch (Exception e) {
			log.error("Error loading crew informations", e);
		}
	}

	private void getEventsList(HttpServletResponse resp, Authorization authorization) throws IOException {
		Profile profile = resources.getProfile(authorization);
		List<String> events = profile.getEvents();
		resp.setContentType("application/json; charset=UTF-8");
		try (PrintWriter writer = resp.getWriter();) {
			ObjectMapper mapper = new ObjectMapper();
			writer.write(mapper.writeValueAsString(events));
		} catch (Exception e) {
			log.error("Error loading crew informations", e);
		}
	}

}
