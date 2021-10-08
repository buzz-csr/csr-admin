package com.naturalmotion.servlet.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/line")
public class LineWebHook {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/hook")
	public String sayPlainTextHello(String content) {
		System.out.println(content);
		return "{ \"test\": \"coucou\" }";
	}
}
