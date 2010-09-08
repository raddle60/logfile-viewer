/**
 * 
 */
package com.raddle.log.viewer.utils;

import java.text.DecimalFormat;

/**
 * @author xurong
 *
 */
public class FileSizeUtils {
	public static String readableSize(long length) {
		long k = 1024;
		long m = 1024 * k;
		long g = 1024 * m;
		String lengthDesc = null;
		DecimalFormat format = new DecimalFormat("0.###");
		if (length < k) {
			lengthDesc = String.valueOf(length) + "Byte";
		} else if (length >= k && length < m) {
			lengthDesc = format.format((double) length / k) + "KB";
		} else if (length >= m && length < g) {
			lengthDesc = format.format((double) length / m) + "MB";
		} else if (length > g) {
			lengthDesc = format.format((double) length / g) + "GB";
		}
		return lengthDesc;
	}
}
