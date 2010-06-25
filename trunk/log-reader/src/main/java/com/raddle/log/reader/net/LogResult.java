package com.raddle.log.reader.net;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LogResult implements Serializable {

    public final static String        ATTR_LINES       = "lines";
    public final static String        ATTR_FILE_IDS    = "fileIds";

    private static final long         serialVersionUID = 1L;
    private boolean                   success;
    private String                    message;

    private Map<String, Serializable> attrs            = new HashMap<String, Serializable>();

    public LogResult setAttr(String key, Serializable value) {
        attrs.put(key, value);
        return this;
    }

    public Serializable getAttr(String key) {
        return attrs.get(key);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
