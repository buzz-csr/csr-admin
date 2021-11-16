package com.naturalmotion.database;

public enum TOKEN_RARITY {

	BRONZE("bronze"), SILVER("silver"), GOLD("gold");

	private final String nmValue;

	private TOKEN_RARITY(String nmValue) {
		this.nmValue = nmValue;
	}

	public String getNmValue() {
		return nmValue;
	}

}
