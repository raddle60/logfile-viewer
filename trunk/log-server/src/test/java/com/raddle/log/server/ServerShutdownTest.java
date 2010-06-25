package com.raddle.log.server;

public class ServerShutdownTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NetLogServer.shutdown("127.0.0.1", 6000);
	}

}
