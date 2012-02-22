package com.bladefs.client.io.cmd;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.bladefs.client.util.io.imp.TCPShortClient;

public class TestDataServiceWriteCmd {
	
	StringBuffer sb = new StringBuffer();
	int len = 0;
	@Before
	public void setUp() throws Exception {
		PropertyConfigurator.configure(".\\conf\\log4j.properties");
		File file = new File("src.jpg");
		FileInputStream in = new FileInputStream(file);
		byte[] buf = new byte[1024];
		int tmplen = 0;
		while ((tmplen = (in.read(buf))) != -1) {
			len += tmplen;
			if (tmplen == 1024)
				sb.append(new String(buf));
			else {
				byte[] tmpbuf = new byte[tmplen];
				System.arraycopy(buf, 0, tmpbuf, 0, tmplen);
				sb.append(new String(tmpbuf));
				break;
			}
		}
		System.out.println(sb.toString().length());
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void test() throws Exception{
//		byte[] d = new byte[2];
//		d[0] = 1;
//		d[1] = 2;
		
		TCPShortClient tcpShortClient = new TCPShortClient("192.168.0.151", 12331, 60*1000, 5);
		DataServiceWriteCmd cmd = new DataServiceWriteCmd();
		cmd.setFileData(sb.toString().getBytes());
		cmd.setFileLen(len);
		tcpShortClient.serve(cmd);
		long fileName = cmd.getFileName();
		if(fileName > 0){
			System.out.println("fileName:"+fileName);
		}
		
//		TCPShortClient tcpShortClient = new TCPShortClient("192.168.0.151", 12331, 60*1000, 5);
//		DataServiceReadCmd cmd = new DataServiceReadCmd();
//		cmd.setFileName(47244640257L);
//		tcpShortClient.serve(cmd);
//		byte info[] = cmd.getFile();
//		System.out.println("data:"+Arrays.toString(info));
	}
	
}
