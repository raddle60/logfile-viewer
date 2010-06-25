package com.raddle.log.reader.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.raddle.log.reader.LogReader;

public class FileLogReader implements LogReader {

    private File logFile;
    private long lastPos = -1;
    private String encoding;
    private final static int default_last_bytes = 10 * 1024;

    public FileLogReader(File logFile, String encoding) {
        this.logFile = logFile;
        this.encoding = encoding;
    }

    @Override
    public String[] readAppendedLines() {
        String[] ret = new String[0];
        if (lastPos == -1 || lastPos > logFile.length()) {
            ret = readLastBytes(default_last_bytes);
        } else if (lastPos < logFile.length()) {
            ret = readLastBytes(logFile.length() - lastPos);
        }
        lastPos = logFile.length();
        return ret;
    }

    @Override
    public String[] readLastBytes(long bytes) {
        checkFile();
        long skip = 0;
        if (bytes < logFile.length()) {
            skip = logFile.length() - bytes;
        }
        return readLines(skip);
    }

    @Override
    public void saveAs(File file) {
        try {
            FileInputStream fis = new FileInputStream(logFile);
            OutputStream os = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 4];
            int n = 0;
            while (-1 != (n = fis.read(buffer))) {
                os.write(buffer, 0, n);
            }
        } catch (Exception e) {
            throw new RuntimeException("日志文件另存为失败", e);
        }
    }

    @Override
    public void close() {

    }

    private String[] readLines(long skip) {
        try {
            FileInputStream fis = new FileInputStream(logFile);
            fis.skip(skip);
            BufferedReader reader = null;
            if (encoding != null) {
                reader = new BufferedReader(new InputStreamReader(fis, encoding));
            } else {
                reader = new BufferedReader(new InputStreamReader(fis));
            }
            List<String> lines = new ArrayList<String>();
            String line = reader.readLine();
            while (line != null) {
                if (line.trim().length() > 0) {
                    lines.add(line);
                }
                line = reader.readLine();
            }
            reader.close();
            lastPos = logFile.length();
            return (String[]) lines.toArray(new String[lines.size()]);
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败", e);
        }
    }

    private void checkFile() {
        if (logFile == null) {
            throw new IllegalArgumentException("日志文件不能为null");
        } else if (!logFile.exists()) {
            throw new IllegalArgumentException("文件不存在[" + logFile.getAbsolutePath() + "]");
        }
    }

    public File getLogFile() {
        return logFile;
    }

}
