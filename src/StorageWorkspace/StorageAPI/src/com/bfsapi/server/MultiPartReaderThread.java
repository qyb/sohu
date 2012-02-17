/*
 * Copyright (c) Sohu Inc. 2012
 * 
 */
package com.bfsapi.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Samuel
 *
 */
public class MultiPartReaderThread extends Thread {

	private OutputStream output;

	public MultiPartReaderThread(OutputStream output) {
		this.output = output;
	}

	public void run() {
		// lock
		try {
			for (int i = 0; i < 3; i++) {
				System.out.printf("thread round %d\n", i);
				java.io.FileInputStream fs = new java.io.FileInputStream(
						"./Sunset.jpg");
				int size = 0;
				byte[] buffer = new byte[256];
				while ((size = fs.read(buffer)) != -1) {
					this.output.write(buffer, 0, size);
					System.out.printf("thread wrote %d\n", size);
				}
				fs.close();
			}
			this.output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// unlock
	}
}
