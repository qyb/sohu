package com.bladefs.client.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bladefs.client.exception.BladeFSException;

public class TestladeFSConfig {

	@Before
	public void setUp() throws Exception {
		BladeFSConfigFactory.createBladeFSConfig("conf/client.properties");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws BladeFSException {
		System.out.println(BladeFSConfigFactory.getBladeFSConfig());
	}

}
