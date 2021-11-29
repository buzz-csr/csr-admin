package com.naturalmotion.database;

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class SqlLogBuilderTest {

	private SqlLogBuilder builder = new SqlLogBuilder();

	@Test
	public void testBuild() {
		Assertions
		        .assertThat(builder.build("INSERT INTO TABLE VALUES(?,?,?,?)",
		                Arrays.asList(0, "toto", "13/10/1995", Integer.valueOf(2), Double.valueOf(3))))
		        .isEqualTo("INSERT INTO TABLE VALUES(0,'toto','13/10/1995',2)");
	}

}
