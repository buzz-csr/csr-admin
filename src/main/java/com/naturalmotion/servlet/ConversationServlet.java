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
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.json.tchat.Message;

public class ConversationServlet extends HttpServlet {

	private static final long serialVersionUID = -6651799132773064663L;

	private Logger log = LoggerFactory.getLogger(ConversationServlet.class);

	private AuthorizationFactory authorizationFactory = new AuthorizationFactory();

	private Configuration configuration = new Configuration();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String crew = req.getParameter("crew");
		String crewId = configuration.getString(crew + ".crew-id");

		String crewPlayerId = configuration.getString(crew + ".player-id");
		List<List<Message>> conversations = new CrewResources().getConversations(authorizationFactory.get(crewPlayerId),
		        crewId);

		resp.setContentType("application/json; charset=UTF-8");
		try (PrintWriter writer = resp.getWriter();) {
			ObjectMapper mapper = new ObjectMapper();
			writer.write("{ \"chat_events\" : " + mapper.writeValueAsString(conversations.get(0))
			        + ", \"server_events\": " + mapper.writeValueAsString(conversations.get(1)) + "}");
		} catch (Exception e) {
			log.error("Error loading crew conversations", e);
		}
	}

}
