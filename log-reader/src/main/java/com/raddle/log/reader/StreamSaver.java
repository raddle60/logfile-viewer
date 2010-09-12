/**
 * 
 */
package com.raddle.log.reader;

import java.io.InputStream;

/**
 * 将二进制流另存为
 * 
 * @author xurong
 * 
 */
public interface StreamSaver {
	/**
	 * 保存二进制流
	 * 
	 * @param in
	 */
	public void saveStream(InputStream in);
}
