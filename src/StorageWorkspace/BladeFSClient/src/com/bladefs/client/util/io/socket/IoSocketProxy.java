package com.bladefs.client.util.io.socket;

import com.bladefs.client.util.io.IoSocket;

public interface IoSocketProxy extends IoSocket {
	public boolean setSocketClient(IoSocket client);
}
