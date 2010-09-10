package com.raddle.log.viewer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.raddle.log.reader.net.NetLogFile;
import com.raddle.log.reader.net.NetLogReader;
import com.raddle.log.viewer.utils.FileSizeUtils;
import com.raddle.log.viewer.utils.ProperiesExt;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class NetReaderConfigDialog extends javax.swing.JDialog {
    private static final long serialVersionUID = 1L;
    private JLabel jLabel2;
    private JScrollPane jScrollPane1;
    private JButton traceBtn;
    private JList logFileList;
    private JButton connectBtn;
    private JTextField portTxt;
    private JComboBox ipCBox;
    private int port;
    private JRadioButton sizeOrderBtn;
    private ButtonGroup orderBtnGroup;
    private JRadioButton nameOrderBtn;
    private JRadioButton timeOrderBtn;
    private JRadioButton nonOrderBtn;
    private JLabel jLabel3;
    private JLabel fileSizeLeb;
    private JButton filterBtn;
    private JCheckBox regexChk;
    private JTextField filterTxt;
    private String ip;
    private String[] logCodes;
    private JLabel jLabel1;
    private List<LogFileWrapper> logFiles = new LinkedList<LogFileWrapper>();

    /**
     * Auto-generated main method to display this JDialog
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                NetReaderConfigDialog inst = new NetReaderConfigDialog(frame);
                inst.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                inst.setVisible(true);
            }
        });
    }

    public NetReaderConfigDialog(JFrame frame) {
        super(frame);
        initGUI();
    }

    private void initGUI() {
        try {
            {
                this.setSize(594, 464);
                this.setTitle("\u7f51\u7edc\u65e5\u5fd7\u6587\u4ef6");
                getContentPane().setLayout(null);
                {
                    jLabel1 = new JLabel();
                    getContentPane().add(jLabel1);
                    jLabel1.setText("IP\u5730\u5740\uff1a");
                    jLabel1.setBounds(12, 12, 81, 15);
                }
                {
                    jLabel2 = new JLabel();
                    getContentPane().add(jLabel2);
                    jLabel2.setText("\u7aef\u53e3\u53f7\uff1a");
                    jLabel2.setBounds(12, 39, 81, 15);
                }
                {
                    portTxt = new JTextField();
                    getContentPane().add(portTxt);
                    portTxt.setBounds(105, 36, 181, 22);
                }
                {
                    connectBtn = new JButton();
                    getContentPane().add(connectBtn);
                    connectBtn.setText("\u83b7\u53d6\u6587\u4ef6\u5217\u8868");
                    connectBtn.setBounds(298, 36, 137, 22);
                    connectBtn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            try {
                                NetLogReader r = NetLogReader.connectServer("", ipCBox.getSelectedItem() + "", Integer
                                        .parseInt(portTxt.getText()));
                                logFiles.clear();
                                NetLogFile[] ss = r.listFile();
                                long allLength = 0;
                                for (NetLogFile s : ss) {
                                    logFiles.add(new LogFileWrapper(s));
                                    if(s.getLength() > 0){
                                    	allLength += s.getLength();
                                    }
                                }
                                r.close();
                                fileSizeLeb.setText("日志总大小："+ FileSizeUtils.readableSize(allLength));
                                ip = ipCBox.getSelectedItem() + "";
                                port = Integer.parseInt(portTxt.getText());
                                filterLogFiles();
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(NetReaderConfigDialog.this, "获取日志文件列表失败,"
                                        + e.getMessage());
                            }
                        }
                    });
                }
                {
                    jScrollPane1 = new JScrollPane();
                    getContentPane().add(jScrollPane1);
                    jScrollPane1.setBounds(12, 149, 457, 275);
                    {
                        ListModel logFileListModel = new DefaultComboBoxModel(new String[] { "" });
                        logFileList = new JList();
                        jScrollPane1.setViewportView(logFileList);
                        logFileList.setModel(logFileListModel);
                    }
                }
                {
                    traceBtn = new JButton();
                    getContentPane().add(traceBtn);
                    traceBtn.setText("\u8ddf\u8e2a");
                    traceBtn.setBounds(397, 81, 72, 22);
                    traceBtn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            if (logFileList.getSelectedValue() != null) {
                                Object[] selecteds = logFileList.getSelectedValues();
                                logCodes = new String[selecteds.length];
                                for (int i = 0; i < selecteds.length; i++) {
                                    logCodes[i] = ((LogFileWrapper)selecteds[i]).getLogFile().getFileId();
                                }
                                NetReaderConfigDialog.this.setVisible(false);
                            } else {
                                JOptionPane.showMessageDialog(NetReaderConfigDialog.this, "请选择要跟踪的日志文件");
                            }
                        }
                    });
                }
                {
                    final ProperiesExt sp = new ProperiesExt();
                    List<Object> servers = new ArrayList<Object>();
                    try {
                        InputStream is = NetReaderConfigDialog.class.getResourceAsStream("/log-server.properties");
                        if(is != null){
                            sp.load(is);
                            servers.add("");
                            servers.addAll(sp.getPropertyNameList());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ComboBoxModel jComboBox1Model = new DefaultComboBoxModel(servers.toArray());
                    ipCBox = new JComboBox();
                    getContentPane().add(ipCBox);
                    ipCBox.setModel(jComboBox1Model);
                    ipCBox.setBounds(105, 9, 181, 22);
                    ipCBox.setEditable(true);
                    ipCBox.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            String key = ipCBox.getSelectedItem().toString();
                            String value = sp.getProperty(key);
                            if (value != null) {
                                int index = value.indexOf(':');
                                if (index != -1) {
                                    ipCBox.setSelectedItem(value.substring(0, index));
                                    portTxt.setText(value.substring(index + 1));
                                } else {
                                    ipCBox.setSelectedItem("");
                                    portTxt.setText("");
                                }
                            }
                        }
                    });
                    ipCBox.setRenderer(new BasicComboBoxRenderer() {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public Component getListCellRendererComponent(JList list, Object value, int index,
                                boolean isSelected, boolean cellHasFocus) {
                            if (isSelected && value != null) {
                                list.setToolTipText(value.toString());
                            } else {
                                list.setToolTipText("");
                            }
                            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        }
                    });
                }
                {
                    filterTxt = new JTextField();
                    getContentPane().add(filterTxt);
                    filterTxt.setBounds(12, 81, 186, 22);
                    filterTxt.addKeyListener(new KeyAdapter() {
                        public void keyPressed(KeyEvent evt) {
                            if (evt.getKeyChar() == '\n') {
                                filterLogFiles();
                            }
                        }
                    });
                }
                {
                    regexChk = new JCheckBox();
                    getContentPane().add(regexChk);
                    regexChk.setText("\u6b63\u5219");
                    regexChk.setBounds(210, 82, 76, 19);
                }
                {
                    filterBtn = new JButton();
                    getContentPane().add(filterBtn);
                    filterBtn.setText("\u8fc7\u6ee4");
                    filterBtn.setBounds(297, 81, 72, 22);
                    filterBtn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            filterLogFiles();
                        }
                    });
                }
                {
                	fileSizeLeb = new JLabel();
                	getContentPane().add(fileSizeLeb);
                	fileSizeLeb.setText("");
                	fileSizeLeb.setBounds(298, 10, 226, 15);
                }
                {
                	jLabel3 = new JLabel();
                	getContentPane().add(jLabel3);
                	getContentPane().add(getNonOrderBtn());
                	getContentPane().add(getTimeOrderBtn());
                	getContentPane().add(getNameOrderBtn());
                	getContentPane().add(getSizeOrderBtn());
                	jLabel3.setText("\u6392\u5e8f\uff1a");
                	jLabel3.setBounds(12, 117, 67, 15);
                }
            }
            pack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public String[] getLogCodes() {
        return logCodes;
    }

    private void filterLogFiles() {
        final DefaultComboBoxModel m = (DefaultComboBoxModel) logFileList.getModel();
        m.removeAllElements();
        Pattern p = null;
        if (filterTxt.getText().length() > 0) {
            try {
                p = Pattern.compile(filterTxt.getText());
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(NetReaderConfigDialog.this, "正则表达式不正确," + e.getMessage());
            }
        }
        long allLength = 0;
        final List<LogFileWrapper> fileWrappers = new ArrayList<LogFileWrapper>();
        for (LogFileWrapper fw : logFiles) {
        	String string = fw.toString();
            if (filterTxt.getText().length() > 0) {
                if (regexChk.isSelected() && p.matcher(string).find()) {
                	fileWrappers.add(fw);
                    if(fw.getLogFile().getLength() > 0){
                    	allLength += fw.getLogFile().getLength();
                    }
                } else if (string.indexOf(filterTxt.getText()) != -1) {
                	fileWrappers.add(fw);
                    if(fw.getLogFile().getLength() > 0){
                    	allLength += fw.getLogFile().getLength();
                    }
                }
            } else {
            	fileWrappers.add(fw);
                if(fw.getLogFile().getLength() > 0){
                	allLength += fw.getLogFile().getLength();
                }
            }
        }
        // 排序
        if(timeOrderBtn.isSelected()){
        	// 时间
            Collections.sort(fileWrappers,new Comparator<LogFileWrapper>(){
    			@Override
    			public int compare(LogFileWrapper o1, LogFileWrapper o2) {
    				return new Long(o2.getLogFile().getLastModified()).compareTo(o1.getLogFile().getLastModified());
    			}
            });
        } else if(nameOrderBtn.isSelected()){
            // 名称
            Collections.sort(fileWrappers,new Comparator<LogFileWrapper>(){
    			@Override
    			public int compare(LogFileWrapper o1, LogFileWrapper o2) {
    				return o1.getLogFile().getFileId().compareTo(o2.getLogFile().getFileId());
    			}
            });
        } else if(sizeOrderBtn.isSelected()){
            // 大小
            Collections.sort(fileWrappers,new Comparator<LogFileWrapper>(){
    			@Override
    			public int compare(LogFileWrapper o1, LogFileWrapper o2) {
    				return new Long(o2.getLogFile().getLength()).compareTo(o1.getLogFile().getLength());
    			}
            });
        }
        new Thread(){
			@Override
			public void run() {
		        for (final LogFileWrapper fw : fileWrappers) {
		        	SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							m.addElement(fw);
						}
					});
		        }
			}
        }.start();

        fileSizeLeb.setText("日志总大小："+ FileSizeUtils.readableSize(allLength));
    }
    
    private JRadioButton getNonOrderBtn() {
    	if(nonOrderBtn == null) {
    		nonOrderBtn = new JRadioButton();
    		nonOrderBtn.setText("\u65e0");
    		nonOrderBtn.setBounds(57, 113, 46, 23);
    		nonOrderBtn.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent evt) {
    				filterLogFiles();
    			}
    		});
    		getOrderBtnGroup().add(nonOrderBtn);
    	}
    	return nonOrderBtn;
    }
    
    private JRadioButton getTimeOrderBtn() {
    	if(timeOrderBtn == null) {
    		timeOrderBtn = new JRadioButton();
    		timeOrderBtn.setText("\u66f4\u65b0\u65f6\u95f4");
    		timeOrderBtn.setBounds(124, 113, 77, 23);
    		timeOrderBtn.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent evt) {
    				filterLogFiles();
    			}
    		});
    		getOrderBtnGroup().add(timeOrderBtn);
    	}
    	return timeOrderBtn;
    }
    
    private JRadioButton getNameOrderBtn() {
    	if(nameOrderBtn == null) {
    		nameOrderBtn = new JRadioButton();
    		nameOrderBtn.setText("\u540d\u79f0");
    		nameOrderBtn.setBounds(222, 113, 51, 23);
    		nameOrderBtn.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent evt) {
    				filterLogFiles();
    			}
    		});
    		getOrderBtnGroup().add(nameOrderBtn);
    	}
    	return nameOrderBtn;
    }
    
    private ButtonGroup getOrderBtnGroup() {
    	if(orderBtnGroup == null) {
    		orderBtnGroup = new ButtonGroup();
    	}
    	return orderBtnGroup;
    }
    
    private JRadioButton getSizeOrderBtn() {
    	if(sizeOrderBtn == null) {
    		sizeOrderBtn = new JRadioButton();
    		sizeOrderBtn.setText("\u6587\u4ef6\u5927\u5c0f");
    		sizeOrderBtn.setBounds(297, 113, 77, 23);
    		sizeOrderBtn.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent evt) {
    				filterLogFiles();
    			}
    		});
    		getOrderBtnGroup().add(sizeOrderBtn);
    	}
    	return sizeOrderBtn;
    }

	private class LogFileWrapper {
		private NetLogFile logFile;

		public LogFileWrapper(NetLogFile logFile) {
			this.logFile = logFile;
		}

		public NetLogFile getLogFile() {
			return logFile;
		}

		@Override
		public String toString() {
			return logFile.getFileId() + "    [" + FileSizeUtils.readableSize(logFile.getLength()) + "]";
		}
	}

}
