package com.bladefs.client.util.io;

import java.io.IOException;

public interface IoCmd {
	void process(IoSession session) throws IOException;
}
