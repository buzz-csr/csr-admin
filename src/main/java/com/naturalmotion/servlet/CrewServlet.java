package com.naturalmotion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naturalmotion.webservice.api.Crew;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.api.Member;
import com.naturalmotion.webservice.configuration.Configuration;
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;

public class CrewServlet extends HttpServlet {

	private static final long serialVersionUID = 3399327358276622634L;

	private final Logger log = LoggerFactory.getLogger(CrewServlet.class);

	private CrewResources crewService = new CrewResources();

	private AuthorizationFactory authorizationFactory = new AuthorizationFactory();

	private Configuration configuration = new Configuration();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String page = req.getParameter("page");
		String crew = req.getParameter("crew");
		if ("crews".equals(page)) {
			getCrewLeaderboard(resp, crew);
		} else {
			getMembersList(resp, crew);
		}
	}

	private void getCrewLeaderboard(HttpServletResponse resp, String crew) {
		Authorization authorization = authorizationFactory.get(crew);
		List<Crew> crews = crewService.getLeaderCrews(authorization, crew);
		crews.stream().forEach(x -> x.setActive(x.getId().equals(configuration.getString(crew + ".crew-id"))));
		resp.setContentType("application/json; charset=UTF-8");
		try (PrintWriter writer = resp.getWriter();) {
			ObjectMapper mapper = new ObjectMapper();
			writer.write(mapper.writeValueAsString(crews));
		} catch (Exception e) {
			log.error("Error loading crew informations", e);
		}
	}

	private void getMembersList(HttpServletResponse resp, String crew) {
		Authorization authorization = authorizationFactory.get(crew);
		List<Member> members = crewService.getMembers(authorization);

		JsonObjectBuilder build = Json.createObjectBuilder();
		members.forEach(x -> build.add(x.getId(), x.getName()));

		resp.setContentType("application/json; charset=UTF-8");
		try (PrintWriter writer = resp.getWriter();) {
			ObjectMapper mapper = new ObjectMapper();
			writer.write("{ \"list\" : " + mapper.writeValueAsString(members) + ", \"names\" : "
					+ build.build().toString() + "}");
		} catch (Exception e) {
			log.error("Error loading crew informations", e);
		}
	}

}
