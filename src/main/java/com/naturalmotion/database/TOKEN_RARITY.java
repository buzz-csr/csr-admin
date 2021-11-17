package com.naturalmotion.database;

public enum TOKEN_RARITY {

	BRONZE("bronze", "30"), SILVER("silver", "70"), GOLD("gold", "150");

	private final String nmValue;

	private final String name;

	private TOKEN_RARITY(String nmValue, String name) {
		this.nmValue = nmValue;
		this.name = name;
	}

	public String getNmValue() {
		return nmValue;
	}

	public String getName() {
		return name;
	}

}
