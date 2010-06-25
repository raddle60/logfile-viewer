package com.raddle.log.server;

public class ServerTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        NetLogServer s = new NetLogServer();
        s.startUp(6000);
    }

}
