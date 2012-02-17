package com.bladefs.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRepeatSmall {
	private int len = 0;
	private StringBuffer sb = new StringBuffer();
	private byte[] info = null;
	private byte[] data2Send = null;
	private long[] fnArr = null;
	private long[] writeFailedIDs = null;
	private int wfCount = 0;
	private int[] readFailedIDs = null;
	private int rfCount = 0;
	private int[] deleteFailedIDs = null;
	private int dfCount = 0;
	private int[] recoverFailedIDs = null;
	private int refCount = 0;
	private int fnID = 0;
	private BladeFSClient client = null;

	@Before
	public void setUp() throws Exception {
		PropertyConfigurator.configure(".\\conf\\log4j.properties");

		File file = new File("100k.jpg");
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[1024]; // buff用于存放循环读取的临时数据
		int rc = 0;
		while ((rc = in.read(buff, 0, 1024)) > 0) {
			swapStream.write(buff, 0, rc);
			len += rc;
		}

		data2Send = swapStream.toByteArray(); // in_b为转换之后的结果
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		client = new BladeFSClient(".\\conf\\client.properties");
//		testWrite();
//		testRead();
		getfn();
//		testDelete();
		testRecover();
	}

	public void testWrite() throws Exception {
		int count = 10000;
		int success = 0;
		int index = 0;
		fnArr = new long[10000];
		writeFailedIDs = new long[10000];
		long btime = System.currentTimeMillis();
		while (index < count) {
			long t1 = System.currentTimeMillis();
			System.out.println((index + 1) + ": begin: " + t1);
			long fn = client.write(data2Send, len);
			long t2 = System.currentTimeMillis();
			System.out.println("end: " + t2);
			System.out.println("last: " + (t2 - t1));
			if (fn > 0) {
				System.out.println("filename returned: " + fn + "\n");
				success++;
				fnArr[fnID++] = fn;
			} else {
				writeFailedIDs[wfCount] = fn;
				wfCount++;
			}
			
			index++;
		}
		long etime = System.currentTimeMillis();
		System.out.println("write smallfile totaltimes: " + count);
		System.out.println("write smallfile successtimes: " + success);
		System.out.println("total last: " + (etime - btime));
		writeNow(true, 1, count, success, (etime - btime));
		if (wfCount > 0)
			writeNow(false, 1, count, wfCount, (etime - btime));

		logFileName();
		logWriteErrCode();
	}

	public void logFileName() throws Exception {
		File file = new File(".\\logs\\filenames_small.log");
		BufferedWriter out = new BufferedWriter(new FileWriter(file, false));

		for (int i = 0; i < fnID; i++) {
			out.write(fnArr[i] + "\n");
		}

		out.close();
	}

	public void logWriteErrCode() throws Exception {
		File file = new File(".\\logs\\write_errcode.log");
		BufferedWriter out = new BufferedWriter(new FileWriter(file, false));

		for (int i = 0; i < wfCount; i++) {
			out.write(writeFailedIDs[i] + "\n");
		}

		out.close();
	}

	public void testRead() throws Exception {
		if (fnArr == null || fnArr.length < 1 || fnID < 1)
			return;
		readFailedIDs = new int[fnID];
		int success = 0;
		int index = 0;
		long btime = System.currentTimeMillis();
		while (index < fnID) {
			long t1 = System.currentTimeMillis();
			System.out.println((index + 1) + ": begin: " + t1);
			byte[] rev = client.read(fnArr[index]);
			long t2 = System.currentTimeMillis();
			System.out.println("end: " + t2);
			System.out.println("last: " + (t2 - t1));
			if (rev != null) {
				System.out.println("file received: " + rev.length + "\n");
				success++;
			} else {
				readFailedIDs[wfCount] = index + 1;
				wfCount++;
			}
			
			index++;
		}
		long etime = System.currentTimeMillis();
		System.out.println("read smallfile totaltimes: " + fnID);
		System.out.println("read smallfile successtimes: " + success);
		System.out.println("total last: " + (etime - btime));
		writeNow(true, 2, index, success, (etime - btime));
		if (rfCount > 0)
			writeNow(false, 2, index, rfCount, (etime - btime));
	}

	public void testDelete() throws Exception {
		if (fnArr == null || fnArr.length < 1 || fnID < 1)
			return;

		deleteFailedIDs = new int[fnID];
		int success = 0;
		int index = 0;
		long btime = System.currentTimeMillis();
		while  (index < fnID) {
			long t1 = System.currentTimeMillis();
			System.out.println((index + 1) + ": begin: " + t1);
			boolean ret = client.delete(fnArr[index]);
			long t2 = System.currentTimeMillis();
			System.out.println("end: " + t2);
			System.out.println("last: " + (t2 - t1));
			if (ret) {
				System.out.println(fnArr[index] + "file delete success:");
				success++;
			} else {
				deleteFailedIDs[dfCount] = index + 1;
				dfCount++;
			}
			
			index++;
		}
		long etime = System.currentTimeMillis();
		System.out.println("delete smallfile totaltimes: " + fnID);
		System.out.println("delete smallfile successtimes: " + success);
		System.out.println("delete last: " + (etime - btime));
		writeNow(true, 3, index, success, (etime - btime));
		if (dfCount > 0)
			writeNow(false, 3, index, dfCount, (etime - btime));
	}

	public void testRecover() throws Exception {
		if (fnArr == null || fnArr.length < 1 || fnID < 1)
			return;

		recoverFailedIDs = new int[fnID];
		int count = 10000;
		int success = 0;
		int index = 0;
		long btime = System.currentTimeMillis();
		while (index < fnID) {
			long t1 = System.currentTimeMillis();
			System.out.println((index + 1) + ": begin: " + t1);
			boolean ret = client.recover(fnArr[index]);
			long t2 = System.currentTimeMillis();
			System.out.println("end: " + t2);
			System.out.println("last: " + (t2 - t1));
			if (ret) {
				success++;
			} else {
				System.out.println("file recover failed:"+fnArr[index] );
				recoverFailedIDs[dfCount] = index + 1;
				refCount++;
			}
			
			index++;
		}
		long etime = System.currentTimeMillis();
		System.out.println("recover smallfile totaltimes: " + count);
		System.out.println("recover smallfile successtimes: " + success);
		System.out.println("recover last: " + (etime - btime));
		writeNow(true, 4, index, success, (etime - btime));
		if (refCount > 0)
			writeNow(false, 4, index, refCount, (etime - btime));
	}

	public void writeNow(boolean isASuccess, int taskID, int times,
			int succtimes, long last) throws Exception {
		String taskname = null;
		switch (taskID) {
		case 1:
			taskname = "write";
			break;
		case 2:
			taskname = "read";
			break;
		case 3:
			taskname = "delete";
			break;
		case 4:
			taskname = "recover";
			break;
		default:
			return;
		}

		String logname = null;
		if (isASuccess)
			logname = ".\\logs\\" + taskname + "_smallfile" + "_success" + ".log";
		else
			logname = ".\\logs\\" + taskname + "_smallfile" + "_failed"	+ ".log";

		File file = new File(logname);
		BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
		out.write("total Last: " + last + "\n");
		out.write("total count: " + times + "\n");
		out.write("success count: " + succtimes + "\n");

		if (taskID == 0) {
			for (int i = 0; i < wfCount; i++)
				out.write(writeFailedIDs[i] + "\n");
		} else if (taskID == 1) {
			for (int i = 0; i < rfCount; i++)
				out.write(readFailedIDs[i] + "\n");
		} else if (taskID == 2) {
			for (int i = 0; i < dfCount; i++)
				out.write(deleteFailedIDs[i] + "\n");
		} else {
			for (int i = 0; i < refCount; i++)
				out.write(recoverFailedIDs[i] + "\n");
		}

		out.close();
	}
	
	@SuppressWarnings("deprecation")
	public void getfn() throws Exception {
		String fnDir = ".\\logs\\filenames_big.log";
		FileInputStream fin = null;
		DataInputStream in = null;
		fin = new FileInputStream(fnDir);
		in = new DataInputStream(fin);
		fnArr = new long[10000];
		String fn = null;
		while ((fn =in.readLine()) != null) {
			fnArr[fnID++] = Long.parseLong(fn);
		}
	}

}
