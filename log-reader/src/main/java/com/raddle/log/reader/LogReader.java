package com.raddle.log.reader;

public interface LogReader {
	/**
	 * 返回日志文件当前的大小
	 * 
	 * @return
	 */
	public long getFileBytes();

	/**
	 * 读最后多少字节的日志
	 * 
	 * @param rows
	 * @return
	 */
	public String[] readLastBytes(long bytes);

	/**
	 * 读取上次读取之后增加的行
	 * 
	 * @return
	 */
	public String[] readAppendedLines();

	/**
	 * 关闭日志文件
	 * 
	 * @return
	 */
	public void close();

	/**
	 * 整个日志文件另存为
	 * 
	 * @param file
	 */
	public void saveAs(StreamSaver saver);

}
