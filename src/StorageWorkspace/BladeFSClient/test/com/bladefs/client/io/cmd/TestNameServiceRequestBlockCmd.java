package com.bladefs.client.io.cmd;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import com.bladefs.client.de.DecisionEngine;
import com.bladefs.client.util.io.imp.TCPClient;

public class TestNameServiceRequestBlockCmd {

	@Test
	public void test() throws IOException {
		TCPClient tcpClient = new TCPClient("192.168.0.153", 991, 1, 1000, 3);
//		NameServiceRequestBlockCmd cmd = new NameServiceRequestBlockCmd(new DecisionEngine());
		NameServiceRequestBlockCmd cmd = new NameServiceRequestBlockCmd(DecisionEngine.DECISIONENGINEINST);
		tcpClient.serve(cmd);
	}

}
