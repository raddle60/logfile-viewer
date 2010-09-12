/**
 * 
 */
package com.raddle.log.reader.saver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.raddle.log.reader.StreamSaver;

/**
 * 另存为文件
 * 
 * @author xurong
 * 
 */
public class FileStreamSaver implements StreamSaver {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private File file;

	public FileStreamSaver(File file) {
		this.file = file;
	}

	@Override
	public void saveStream(InputStream in) {
		try {
			OutputStream os = new FileOutputStream(file);
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int n = 0;
			while (-1 != (n = in.read(buffer))) {
				os.write(buffer, 0, n);
			}
			os.flush();
			os.close();
		} catch (Exception e) {
			throw new RuntimeException("文件另存为失败", e);
		}
	}

}
