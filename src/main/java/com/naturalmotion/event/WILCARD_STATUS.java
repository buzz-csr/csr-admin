package com.naturalmotion.event;

public enum WILCARD_STATUS {

	COMPLETE("complete"), ACTIVE("active");

	private final String nmValue;

	private WILCARD_STATUS(String nmValue) {
		this.nmValue = nmValue;
	}

	public String getNmValue() {
		return nmValue;
	}

}
