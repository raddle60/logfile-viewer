package com.raddle.log.reader.net;

public class NetReaderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NetLogReader reader = NetLogReader.connectServer("antPath:ipfilter.properties", "127.0.0.1", 6000);
		// String[] lines = reader.listFileId();
		// if (lines != null) {
		// for (String string : lines) {
		// System.out.println(string);
		// }
		// }
		// lines = reader.readLastBytes(1000);
		// if (lines != null) {
		// for (String string : lines) {
		// System.out.println(string);
		// }
		// }
		// System.out.println(reader.getFileBytes());
		NetLogFile[] logFiles = reader.listFile();
		for (NetLogFile netLogFile : logFiles) {
			System.out.println(netLogFile.getFileId() 
					 + "," + netLogFile.getFileName() 
					 + "," + netLogFile.getFilePath()
					 + "," + netLogFile.getServerIp()
					 + "," + netLogFile.getServerName()
					 + "," + netLogFile.getLastModified()
					 + "," + netLogFile.getFileName()
					 + "," + netLogFile.getLength());
		}
		reader.close();
	}

}
