package com.naturalmotion.api;

import java.math.BigDecimal;

import com.naturalmotion.webservice.api.Member;

public class CsrMember {

	private String name;

	private String id;

	private int points;

	private int rank;

	private int token;

	private int level;

	private String role;

	private BigDecimal dayPerformance;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getToken() {
		return token;
	}

	public void setToken(int token) {
		this.token = token;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getDayPerformance() {
		return dayPerformance;
	}

	public void setDayPerformance(BigDecimal dayPerformance) {
		this.dayPerformance = dayPerformance;
	}

	public static CsrMember from(Member member) {
		CsrMember csrMember = new CsrMember();
		csrMember.setId(member.getId());
		csrMember.setLevel(member.getLevel());
		csrMember.setName(member.getName());
		csrMember.setPoints(member.getPoints());
		csrMember.setRank(member.getRank());
		csrMember.setRole(member.getRole());
		csrMember.setToken(member.getToken());
		return csrMember;
	}
}
