package com.naturalmotion.servlet.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

@Path("/line")
public class LineWebHook {

	private Logger log = Logger.getLogger(LineWebHook.class);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/hook")
	public String sayPlainTextHello(String content) {
		log.info("Received Line Hook: \n" + content);
		return "{ \"receivedHook\": \"" + content + "\" }";
	}
}
