package com.naturalmotion.line;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.naturalmotion.webservice.configuration.Configuration;

public class LineConfiguration {

	private final Properties properties;

	public LineConfiguration(Configuration configuration) {
		properties = new Properties();

		try {
			InputStream resourceAsStream = new FileInputStream(
			        configuration.getString("working.directory") + "/Line/config.properties");
			properties.load(resourceAsStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getString(String key) {
		return properties.getProperty(key);
	}

}
