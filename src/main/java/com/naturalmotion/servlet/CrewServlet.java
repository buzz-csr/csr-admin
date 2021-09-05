package com.naturalmotion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.naturalmotion.api.CrewHistories;
import com.naturalmotion.api.CsrMember;
import com.naturalmotion.history.AccountHistoryReader;
import com.naturalmotion.history.CrewHistoryReader;
import com.naturalmotion.webservice.api.Crew;
import com.naturalmotion.webservice.api.CrewResources;
import com.naturalmotion.webservice.api.Member;
import com.naturalmotion.webservice.api.history.AccountHistory;
import com.naturalmotion.webservice.api.history.DayHistory;
import com.naturalmotion.webservice.configuration.Configuration;
import com.naturalmotion.webservice.service.auth.Authorization;
import com.naturalmotion.webservice.service.auth.AuthorizationFactory;
import com.naturalmotion.webservice.service.history.HistoryUpdater;

public class CrewServlet extends HttpServlet {

	private static final long serialVersionUID = 3399327358276622634L;

	private final Logger log = LoggerFactory.getLogger(CrewServlet.class);

	private CrewResources crewService = new CrewResources();

	private AuthorizationFactory authorizationFactory = new AuthorizationFactory();

	private Configuration configuration = new Configuration();

	private AccountHistoryReader accountHistoryReader = new AccountHistoryReader();

	private CrewHistoryReader crewHistoryReader = new CrewHistoryReader();

	private HistoryUpdater historyUpdater = new HistoryUpdater();

	private SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String page = req.getParameter("page");
		String crew = req.getParameter("crew");
		if ("crews".equals(page)) {
			getCrewLeaderboard(resp, crew);
		} else if ("graph".equals(page)) {
			getCrewGraph(resp, crew);
		} else {
			getMembersList(resp, crew);
		}
	}

	private void getCrewGraph(HttpServletResponse resp, String crew) {
		CrewHistories crewHistories = crewHistoryReader.get(crew);
		try (PrintWriter writer = resp.getWriter();) {
			ObjectMapper mapper = new ObjectMapper();
			writer.write(mapper.writeValueAsString(crewHistories));
		} catch (Exception e) {
			log.error("Error loading crew informations", e);
		}
	}

	private void getCrewLeaderboard(HttpServletResponse resp, String crew) {
		Authorization authorization = authorizationFactory.get(configuration.getString(crew + ".player-id"));
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
		Date date = new Date();
		Authorization authorization = authorizationFactory.get(configuration.getString(crew + ".player-id"));
		List<Member> members = crewService.getMembers(authorization);
		List<CsrMember> csrMember = new ArrayList<>();
		members.forEach(x -> {
			CsrMember from = CsrMember.from(x);
			AccountHistory accountHistories = accountHistoryReader.get(from.getId());
			if (accountHistories == null) {
				accountHistories = new AccountHistory();
			}
			historyUpdater.update(x, date, accountHistories);

			DayHistory dayHistory = accountHistories.getDayHistories().get(dFormat.format(date));

			if (dayHistory != null) {
				from.setDayPerformance(dayHistory.getCumul());
			} else {
				from.setDayPerformance(BigDecimal.ZERO);
			}
			csrMember.add(from);
		});

		JsonObjectBuilder build = Json.createObjectBuilder();
		csrMember.forEach(x -> build.add(x.getId(), x.getName()));

		resp.setContentType("application/json; charset=UTF-8");
		try (PrintWriter writer = resp.getWriter();) {
			ObjectMapper mapper = new ObjectMapper();
			writer.write("{ \"list\" : " + mapper.writeValueAsString(csrMember) + ", \"names\" : "
					+ build.build().toString() + "}");
		} catch (Exception e) {
			log.error("Error loading crew informations", e);
		}
	}

}
