package com.bladefs.client.io;

import java.io.IOException;

import org.junit.Test;

import com.bladefs.client.exception.BladeFSException;
import com.bladefs.client.exception.NameServiceException;

public class TestNameServiceClient {

	@Test
	public void test() throws IOException, BladeFSException, NameServiceException, InterruptedException {
		NameServiceClient client = NameServiceClient.getInstance();
		for(int i = 0; i < 100; i++){
			System.out.println(client.getDecisionEngine());
			Thread.sleep(1000);
		}
	}

}
