package com.raddle.log.reader.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import com.raddle.log.reader.LogReader;

public class NetLogReader implements LogReader {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
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
    public void saveAs(final File file) {
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
                OutputStream os = new FileOutputStream(file);
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int n = 0;
                while (-1 != (n = in.read(buffer))) {
                    os.write(buffer, 0, n);
                }
                os.close();
                in.close();
                out.close();
                return null;
            }
        });
    }

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
            socket.setSoTimeout(1000);
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
