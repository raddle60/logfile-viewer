package com.raddle.log.reader.net;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络通信命令
 *
 * @author xurong
 */
public class LogCommand implements Serializable {

    public final static String        CMD_READ_APPENDED_LINES = "readAppendedLines";
    public final static String        CMD_READ_LAST_BYTES     = "readLastBytes";
    public final static String        CMD_SAVE_AS             = "saveAs";
    @Deprecated
    public final static String        CMD_LIST_FILE_ID        = "listFileId";
    public final static String        CMD_LIST_FILE        	  = "listFile";
    public final static String        CMD_CLOSE               = "close";
	public final static String        CMD_SHUTDOWN            = "shutdown";
	public final static String        CMD_FILE_LENGTH         = "fileLength";
    public final static String        ATTR_BYTES              = "bytes";

    private static final long         serialVersionUID        = 1L;
    private String                    sessionId;
    private String                    fileId;
    private String                    cmdCode;
    private Map<String, Serializable> attrs                   = new HashMap<String, Serializable>();

    public LogCommand setAttr(String key, Serializable value) {
        attrs.put(key, value);
        return this;
    }

    public Serializable getAttr(String key) {
        return attrs.get(key);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(String cmdCode) {
        this.cmdCode = cmdCode;
    }

}
