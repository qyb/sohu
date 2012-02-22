package com.bladefs.client.util.io;

import java.io.IOException;

public interface IoClient {
	boolean serve(IoCmd cmd) throws IOException;
	void close();
}
