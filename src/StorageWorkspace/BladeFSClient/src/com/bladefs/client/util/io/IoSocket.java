package com.bladefs.client.util.io;

import java.io.IOException;
import java.net.Socket;

public interface IoSocket {
	Socket checkOut(int timeout, boolean alive) throws IOException;
}
