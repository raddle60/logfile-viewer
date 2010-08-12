/**
 * 
 */
package com.raddle.log.viewer.utils;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.swing.JTabbedPane;

import com.raddle.log.viewer.LogViewerPanel;

/**
 * @author xurong
 * 
 */
public class LogConfigUtils {
	private static Properties config = new Properties();
	private static File configFile = null;
	private final static String TAB_GROUP_PREFIX = "tabs_";
	static {
		File dir = new File(System.getProperty("user.home") + "/.logserver");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		configFile = new File(dir, "logconfig.properties");
		if (configFile.exists()) {
			try {
				config.load(new InputStreamReader(new FileInputStream(configFile), "utf-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void saveTabGroup(String groupName, JTabbedPane jTabbedPane) {
		if(groupName == null || groupName.length() == 0){
			groupName = "default";
		}
		int count = jTabbedPane.getTabCount();
		if (count > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < count; i++) {
				Component c = jTabbedPane.getComponentAt(i);
				if (c instanceof LogViewerPanel) {
					LogViewerPanel p = (LogViewerPanel) c;
					if (i > 0) {
						sb.append(";");
					}
					if(p.getLogFile() != null){
						sb.append("file:").append(p.getLogFile().getAbsolutePath());
						sb.append(":").append(p.getLogFileEncoding());
					}else{
						sb.append("net:").append(p.getLogServerIp());
						sb.append(":").append(p.getLogServerPort());
					}
				}
			}
			config.setProperty(TAB_GROUP_PREFIX+groupName, sb.toString());
			saveConfig();
		}
	}
	
	public static List<String> getTabGroup(){
		List<String> list = new ArrayList<String>();
		for (String key : config.stringPropertyNames()) {
			if(key.startsWith(TAB_GROUP_PREFIX)){
				list.add(key.substring(TAB_GROUP_PREFIX.length()));
			}
		}
		Collections.sort(list);
		return list;
	}
	
	public static List<String> getGroupTabs(String groupName) {
		List<String> list = new ArrayList<String>();
		String group = config.getProperty(TAB_GROUP_PREFIX + groupName);
		if (group != null && group.length() > 0) {
			list.addAll(Arrays.asList(group.split(";")));
		}
		return list;
	}
	
	public static void removeTabGroup(String groupName){
		config.remove(TAB_GROUP_PREFIX + groupName);
	}
	
	private static void saveConfig(){
		try {
			Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), "utf-8");
			config.store(writer, "");
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
