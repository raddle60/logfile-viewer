package com.raddle.log.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.raddle.log.reader.file.FileLogReader;
import com.raddle.log.reader.net.LogCommand;
import com.raddle.log.reader.net.LogResult;
import com.raddle.log.reader.net.NetLogFile;
import com.raddle.utils.AntPathFileUtils;

public class NetLogServer {

    public final static String         COMMAND_ENCODING = "utf-8";
    private final static Pattern       fileListPattern  = Pattern.compile("(?:(.*)=)?([^=,]+)(?:,(.*))?");
    private ServerSocket               server;
    private boolean                    started          = false;
    private boolean                    stop             = false;
    private Map<String, FileLogReader> logs             = new HashMap<String, FileLogReader>();
    private Map<String, LogFile>       logFiles         = new LinkedHashMap<String, LogFile>();
    private int port;

    public synchronized void startUp(final int port) {
    	this.port = port;
        if (!started) {
        	started = true;
        	new Thread(){
				@Override
				public void run() {
					try {
						System.out.println("log server starting");
						// 给关闭留点时间,保证重启时服务已经停止
						Thread.sleep(200);
						reloadFileList();
						server = new ServerSocket(port);
						System.out.println("log server listening on : " + port);
						while (!stop) {
							Socket client = null;
							client = server.accept();
							if (!stop) {
								LogSendThread t = new LogSendThread(client);
								t.start();
							}
						}
						server.close();
						System.out.println("log server[" + port + "] shutdown ");
					} catch (Exception e) {
						System.out.println("log server[" + port + "] shutdown on exception: " + e.getMessage());
						throw new RuntimeException(e.getMessage(), e);
					}
				}
        	}.start();
        }
    }

    private void reloadFileList() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(NetLogServer.class.getResourceAsStream("/log-file-list.properties"), "utf-8"));
        logFiles.clear();
        String line = null;
        Set<File> addedFile = new HashSet<File>();
        while (null != (line = reader.readLine())) {
            line = line.trim();
            if (line.length() > 0) {
                Matcher matcher = fileListPattern.matcher(line);
                if (matcher.matches()) {
                    String nameSpace = matcher.group(1);
                    String pattern = matcher.group(2);
                    String encoding = matcher.group(3);
                    if (encoding == null) {
                        encoding = System.getProperty("file.encoding");
                    }
                    // 根据ant路径匹配
                    Set<File> matchedFiles = AntPathFileUtils.findFiles(pattern);
                    for (File file : matchedFiles) {
                    	if(file.isFile()){
                    		if (addedFile.contains(file)) {
                                // 一个文件只加一遍
                                continue;
                            } else {
                                addedFile.add(file);
                            }
                            if (nameSpace != null && nameSpace.trim().length() > 0) {
                                logFiles.put(nameSpace + ":" + file.getName(), new LogFile(file, encoding));
                            } else if (logFiles.containsKey(file.getName())) {
                                logFiles.put(file.getName() + "-" + logFiles.size(), new LogFile(file, encoding));
                            } else {
                                logFiles.put(file.getName(), new LogFile(file, encoding));
                            }
                    	}
                    }
                } else {

                }
            }
        }
        reader.close();
    }

    public synchronized void shutdown() {
        if (started) {
        	stop = true;
        	// 连接一下，打断阻塞
        	try {
				Socket socket = new Socket("127.0.0.1", port);
				socket.setSoTimeout(100);
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }
    
	public static void shutdown(String ip, int port) {
		Socket socket = null;
		try {
			System.out.println("shutdown log server[" + ip + ":" + port + "]");
			socket = new Socket(ip, port);
			socket.setSoTimeout(2000);
			LogCommand cmd = new LogCommand();
			cmd.setCmdCode(LogCommand.CMD_SHUTDOWN);
			// 发送命令
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(cmd);
			// 获得结果
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			LogResult result = (LogResult) in.readObject();
			in.close();
			out.close();
			if (result.isSuccess()) {
				System.out.println("shutdown log server[" + ip + ":" + port + "] successed");
			} else {
				System.out.println("shutdown log server[" + ip + ":" + port + "] failed :" + result.getMessage());
			}
		} catch (Exception e) {
			System.out.println("shutdown log server[" + ip + ":" + port + "] failed :" + e.getMessage());
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}

    private class LogSendThread extends Thread {

        private Socket client;

        public LogSendThread(Socket client){
            this.client = client;
        }

        @Override
        public void run() {
            try {
                client.setSoTimeout(2000);
                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                LogCommand command = (LogCommand) in.readObject();
                LogResult result = new LogResult();
                if (isIpAllowed(client)) {
                    LogFile logFile = logFiles.get(command.getFileId());
                    if (LogCommand.CMD_LIST_FILE_ID.equals(command.getCmdCode())) {
                        reloadFileList();
                        // 列出日志
                        result.setAttr(LogResult.ATTR_FILE_IDS, new ArrayList<String>(logFiles.keySet()).toArray(new String[0]));
                        result.setSuccess(true);
                    } else if (LogCommand.CMD_LIST_FILE.equals(command.getCmdCode())) {
                        reloadFileList();
                        // 列出日志文件
                        List<NetLogFile> fileList = new ArrayList<NetLogFile>();
                        String localIp = getLocalIp();
                        String localName = getLocalHostName();
						for (String fileId : logFiles.keySet()) {
							File localFile = logFiles.get(fileId).getFile();
							// 文件信息
							NetLogFile netLogFile = new NetLogFile();
							netLogFile.setFileId(fileId);
							netLogFile.setFileName(localFile.getName());
							netLogFile.setFilePath(localFile.getAbsolutePath());
							netLogFile.setServerIp(localIp);
							netLogFile.setServerName(localName);
							if(localFile.exists()){
								netLogFile.setLastModified(localFile.lastModified());
//								netLogFile.setCreatedTime(localFile.);//无方法获得
								netLogFile.setLength(localFile.length());
							}
							fileList.add(netLogFile);
						}
						result.setAttr(LogResult.ATTR_FILES, (NetLogFile[]) fileList.toArray(new NetLogFile[fileList.size()]));
                        result.setSuccess(true);
                    } else if (LogCommand.CMD_CLOSE.equals(command.getCmdCode())) {
                        // 移除跟踪
                        if (logs.containsKey(command.getSessionId())) {
                            logs.remove(command.getSessionId());
                            System.out.println("close session : " + command.getSessionId());
                        }
                        result.setSuccess(true);
					} else if (LogCommand.CMD_SHUTDOWN.equals(command.getCmdCode())) {
						stop = true;
						result.setSuccess(true);
					} else if (logFile != null) {
                        if (logFile.getFile().exists()) {
                            // 不存在就创建
                            if (!logs.containsKey(command.getSessionId())) {
                                logs.put(command.getSessionId(), new FileLogReader(logFile.getFile(), logFile.getEncoding()));
                                System.out.println("create session :" + command.getSessionId() + ",trace log : " + command.getFileId());
                            }
                            if (LogCommand.CMD_READ_LAST_BYTES.equals(command.getCmdCode())) {
                                long lastBytes = (Long) command.getAttr(LogCommand.ATTR_BYTES);
                                // 读取最后的行数
                                result.setAttr(LogResult.ATTR_LINES, logs.get(command.getSessionId()).readLastBytes((lastBytes)));
                                result.setSuccess(true);
                            } else if (LogCommand.CMD_READ_APPENDED_LINES.equals(command.getCmdCode())) {
                                // 读取最近更新
                                result.setAttr(LogResult.ATTR_LINES, logs.get(command.getSessionId()).readAppendedLines());
                                result.setSuccess(true);
                            } else if (LogCommand.CMD_SAVE_AS.equals(command.getCmdCode())) {
                                // 传输日志文件
                                InputStream logFileIn = new FileInputStream(logs.get(command.getSessionId()).getLogFile());
                                OutputStream os = client.getOutputStream();
                                byte[] buffer = new byte[1024 * 4];
                                int n = 0;
                                while (-1 != (n = logFileIn.read(buffer))) {
                                    os.write(buffer, 0, n);
                                }
                                logFileIn.close();
                            } else if (LogCommand.CMD_FILE_LENGTH.equals(command.getCmdCode())) {
        						result.setAttr(LogResult.ATTR_LENGTH, logFile.getFile().length());
        						result.setSuccess(true);
        					} else {
                                result.setSuccess(false);
                                result.setMessage("unknow command [" + command.getCmdCode() + "]");
                                System.out.println("unknow command [" + command.getCmdCode() + "]");
                            }
                        } else {
                            result.setSuccess(false);
                            result.setMessage("can't found log file [" + logFile.getFile().getAbsolutePath() + "]");
                            System.out.println("can't find log file [" + logFile.getFile().getAbsolutePath() + "]");
                        }
                    } else {
                        result.setSuccess(false);
                        result.setMessage("can't find log configuration for [" + command.getFileId() + "]");
                        System.out.println("can't find log configuration for [" + command.getFileId() + "]");
                    }
                } else {
                    result.setSuccess(false);
                    result.setMessage("permission is denied");
                    System.out.println("permission is denied");
                }
                // 如果是保存文件，直接写了文件流，不写入结果对象
                if(!LogCommand.CMD_SAVE_AS.equals(command.getCmdCode())){
                	ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                	out.writeObject(result);
                }
                client.close();
                // 发送后关闭监听
                if(stop){
                	shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogResult result = new LogResult();
                result.setSuccess(false);
                result.setMessage(e.getMessage());
                try {
                    ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                    out.writeObject(result);
                    client.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private boolean isIpAllowed(Socket socket) throws IOException {
        InputStream in = NetLogServer.class.getResourceAsStream("/ipfilter.properties");
        boolean isAllowed = false;
        if (in != null) {
            String remoteIP = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().getHostAddress();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            Properties p = new Properties();
            p.load(reader);
            // 本机ip
            if(remoteIP.equals("127.0.0.1")){
            	return true;
            }
            // 允许的ip
            if (isNotBlank(p.getProperty("allow"))) {
                String[] ss = p.getProperty("allow").split(";");
                for (String regex : ss) {
                    if (isNotBlank(regex) && Pattern.matches(regex, remoteIP)) {
                        isAllowed = true;
                    }
                }
            }
            if (!isAllowed) {
                System.out.println("IP[" + remoteIP + "] is not allowed");
                return false;
            }
            // 不允许的ip
            if (isNotBlank(p.getProperty("deny"))) {
                String[] ss = p.getProperty("deny").split(";");
                for (String regex : ss) {
                    if (isNotBlank(regex) && Pattern.matches(regex, remoteIP)) {
                        // 不允许的直接返回
                        System.out.println("IP[" + remoteIP + "] is denied by filter[" + regex + "]");
                        return false;
                    }
                }
            }
        } else {
            // 不存在ip过滤文件就不过滤
            return true;
        }
        return isAllowed;
    }

    private boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    private boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    class LogFile {

        private File   file;
        private String encoding;

        public LogFile(File file){
            this.file = file;
        }

        public LogFile(File file, String encoding){
            this.file = file;
            this.encoding = encoding;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

    }
    
	public static String getLocalIp() {
		Enumeration<NetworkInterface> allNetInterfaces = null;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		while (allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
			Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress inetAddress = addresses.nextElement();
				if (inetAddress != null && inetAddress instanceof Inet4Address) {
					String ip = inetAddress.getHostAddress();
					if (ip != null && !ip.equals("127.0.0.1")) {
						return ip;
					}
				}
			}
		}
		return null;
	}
	
	public static String getLocalHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return null;
		}
	}
}
