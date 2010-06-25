package com.raddle.log.server;

public class LogServerMain {

	/**
	 * @param args
	 *            args[0]是端口号
	 */
	public static void main(String[] args) {
		if (args == null || args.length < 1) {
			System.out.println("wrong command , available command are :");
			System.out.println("LogServerMain 6000");
			System.out.println("LogServerMain shutdown 6000");
			System.out.println("LogServerMain shutdown ip 6000");
			return;
		}
		if (args.length == 1) {
			NetLogServer s = new NetLogServer();
			s.startUp(Integer.parseInt(args[0]));
		} else if (args.length == 2) {
			if ("shutdown".equals(args[0])) {
				NetLogServer.shutdown("127.0.0.1", Integer.parseInt(args[1]));
			}
		} else if (args.length == 3) {
			if ("shutdown".equals(args[0])) {
				NetLogServer.shutdown(args[1], Integer.parseInt(args[2]));
			}
		}
	}

}
