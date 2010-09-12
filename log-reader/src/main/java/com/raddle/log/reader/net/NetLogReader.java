package com.raddle.log.reader.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;

import com.raddle.log.reader.LogReader;
import com.raddle.log.reader.StreamSaver;

public class NetLogReader implements LogReader {

    private String           fileId;
    private String           ip;
    private int              port;
    private String           id;

    public static NetLogReader connectServer(String fileId, String ip, int port) {
        return new NetLogReader(fileId, ip, port);
    }

    private NetLogReader(String fileId, String ip, int port){
        this.ip = ip;
        this.port = port;
        this.fileId = fileId;
        this.id = UUID.randomUUID().toString();
    }

    @Override
	public long getFileBytes() {
    	return (Long) doInSocket(new SocketCallback() {

            @Override
            public Object connected(Socket socket) throws Exception {
                LogCommand cmd = new LogCommand();
                cmd.setSessionId(id);
                cmd.setFileId(fileId);
                cmd.setCmdCode(LogCommand.CMD_FILE_LENGTH);
                // 发送命令
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(cmd);
                //获得结果
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                LogResult result = (LogResult) in.readObject();
                in.close();
                out.close();
				if (result.isSuccess()) {
					return result.getAttr(LogResult.ATTR_LENGTH);
				} else {
					throw new RuntimeException("获得文件大小失败：" + result.getMessage());
				}
            }
        });
	}

	@Override
    public String[] readAppendedLines() {
        return (String[]) doInSocket(new SocketCallback() {

            @Override
            public Object connected(Socket socket) throws Exception {
                LogCommand cmd = new LogCommand();
                cmd.setSessionId(id);
                cmd.setFileId(fileId);
                cmd.setCmdCode(LogCommand.CMD_READ_APPENDED_LINES);
                // 发送命令
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(cmd);
                //获得结果
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                LogResult result = (LogResult) in.readObject();
                in.close();
                out.close();
                if(result.isSuccess()){
                    return result.getAttr(LogResult.ATTR_LINES);
                }else{
                    return new String[]{result.getMessage()};
                }
            }
        });
    }

    @Override
    public String[] readLastBytes(final long bytes) {
        return (String[]) doInSocket(new SocketCallback() {

            @Override
            public Object connected(Socket socket) throws Exception {
                LogCommand cmd = new LogCommand();
                cmd.setSessionId(id);
                cmd.setFileId(fileId);
                cmd.setCmdCode(LogCommand.CMD_READ_LAST_BYTES);
                cmd.setAttr(LogCommand.ATTR_BYTES, bytes);
                // 发送命令
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(cmd);
                //获得结果
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                LogResult result = (LogResult) in.readObject();
                in.close();
                out.close();
                if(result.isSuccess()){
                    return result.getAttr(LogResult.ATTR_LINES);
                }else{
                    return new String[]{result.getMessage()};
                }
            }
        });
    }

    @Override
    public void saveAs(final StreamSaver saver) {
        doInSocket(new SocketCallback() {

            @Override
            public Object connected(Socket socket) throws Exception {
                LogCommand cmd = new LogCommand();
                cmd.setSessionId(id);
                cmd.setFileId(fileId);
                cmd.setCmdCode(LogCommand.CMD_SAVE_AS);
                // 发送命令
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(cmd);
                //获得结果
                InputStream in = socket.getInputStream();
				try {
					saver.saveStream(in);
				} finally {
					in.close();
					out.close();
				}
                return null;
            }
        });
    }

    @Deprecated
    public String[] listFileId() {
        return (String[]) doInSocket(new SocketCallback() {

            @Override
            public Object connected(Socket socket) throws Exception {
                LogCommand cmd = new LogCommand();
                cmd.setSessionId(id);
                cmd.setCmdCode(LogCommand.CMD_LIST_FILE_ID);
                // 发送命令
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(cmd);
                //获得结果
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                LogResult result = (LogResult) in.readObject();
                in.close();
                out.close();
                if(result.isSuccess()){
                    return result.getAttr(LogResult.ATTR_FILE_IDS);
                }else{
                    return new String[]{result.getMessage()};
                }
            }
        });
    }
    
    public NetLogFile[] listFile() {
        return (NetLogFile[]) doInSocket(new SocketCallback() {

            @Override
            public Object connected(Socket socket) throws Exception {
                LogCommand cmd = new LogCommand();
                cmd.setSessionId(id);
                cmd.setCmdCode(LogCommand.CMD_LIST_FILE);
                // 发送命令
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(cmd);
                //获得结果
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                LogResult result = (LogResult) in.readObject();
                in.close();
                out.close();
                if(result.isSuccess()){
                    return result.getAttr(LogResult.ATTR_FILES);
                }else{
                    throw new RuntimeException(result.getMessage());
                }
            }
        });
    }
    
    public void close() {
        doInSocket(new SocketCallback() {

            @Override
            public Object connected(Socket socket) throws Exception {
                LogCommand cmd = new LogCommand();
                cmd.setSessionId(id);
                cmd.setCmdCode(LogCommand.CMD_CLOSE);
                // 发送命令
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(cmd);
				// 获得结果
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				in.readObject();
				in.close();
				out.close();
				return null;
            }
        });
    }

    private Object doInSocket(SocketCallback callback) {
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
            socket.setSoTimeout(2000);
            return callback.connected(socket);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (socket != null) {
                try {
                    socket.getInputStream().close();
                } catch (IOException e) {
                }
                try {
                    socket.getOutputStream().close();
                } catch (IOException e) {
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private interface SocketCallback {

        public Object connected(Socket socket) throws Exception;
    }
}
