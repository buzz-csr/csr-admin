package com.naturalmotion.database;

import java.util.List;

public class SqlLogBuilder {

	public String build(String statment, List<Object> params) {
		String result = statment;
		int index = 0;
		while (result.indexOf('?') > 0) {
			if (index < params.size()) {
				String valueOf = null;
				Object object = params.get(index++);
				if (object instanceof Integer || object instanceof Long || object instanceof Double) {
					valueOf = String.valueOf(object);
				} else {
					valueOf = "'" + String.valueOf(object) + "'";
				}
				result = result.replaceFirst("\\?", valueOf);
			}
		}

		return result;
	}
}
