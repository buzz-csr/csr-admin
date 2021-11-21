package com.naturalmotion.database.usertoken;

public class UserToken {

	private String id;
	private String user;
	private String rarity;
	private int paid;

	public void setId(String id) {
		this.id = id;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	public void setPaid(int paid) {
		this.paid = paid;
	}

	public String getId() {
		return id;
	}

	public String getUser() {
		return user;
	}

	public String getRarity() {
		return rarity;
	}

	public int getPaid() {
		return paid;
	}

}
