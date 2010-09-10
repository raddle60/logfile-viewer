package com.raddle.log.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.raddle.log.reader.file.FileLogReader;
import com.raddle.log.reader.net.NetLogReader;
import com.raddle.log.viewer.utils.LogConfigUtils;

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
public class LogViewerMain extends javax.swing.JFrame {

    {
        //Set Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    private static final long serialVersionUID = 1L;
    private JMenuItem helpMenuItem;
    private JMenuItem openSavedTabMenuItem;
    private JMenuItem savnOpenTabMenuItem;
    private JMenu jMenu1;
    private JMenuItem closeAllMenuItem;
    private JMenu jMenu5;
    private JMenuItem jMenuItem1;
    private JMenu jMenu2;
    private JMenuItem manageTabMenuItem;
    private JTabbedPane jTabbedPane1;
    private JMenuItem openFileMenuItem;
    private JMenuItem newFileMenuItem;
    private JMenu jMenu3;
    private JMenuBar jMenuBar1;

    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                LogViewerMain inst = new LogViewerMain();
                inst.setLocationRelativeTo(null);
                inst.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                inst.setVisible(true);
            }
        });
    }

    public LogViewerMain() {
        super();
        initGUI();
        initEvent();
    }
    private void initEvent() {
    	jTabbedPane1.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				updateWindowsTitle();
			}
		});
    }
    private void initGUI() {
        try {
            setSize(800, 600);
            setTitle("日志查看");
            {
                jTabbedPane1 = new JTabbedPane();
                getContentPane().add(jTabbedPane1, BorderLayout.CENTER);
                {
                }
            }
            {
                jMenuBar1 = new JMenuBar();
                setJMenuBar(jMenuBar1);
                {
                    jMenu3 = new JMenu();
                    jMenuBar1.add(jMenu3);
                    jMenu3.setText("\u65e5\u5fd7\u8ddf\u8e2a");
                    {
                        newFileMenuItem = new JMenuItem();
                        jMenu3.add(newFileMenuItem);
                        newFileMenuItem.setText("\u6587\u4ef6\u65e5\u5fd7\u8ddf\u8e2a");
                        newFileMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent evt) {
                                JFileChooser f = new JFileChooser();
                                f.addChoosableFileFilter(new FileNameExtensionFilter("日志文件", "txt", "log"));
                                int result = f.showOpenDialog(LogViewerMain.this);
                                if (result == JFileChooser.APPROVE_OPTION) {
                                    String encoding = JOptionPane.showInputDialog("请输入文件编码格式", System
                                            .getProperty("file.encoding"));
                                    if(encoding != null && encoding.length() > 0){
                                        addFileLogTab(f.getSelectedFile(), encoding);
    									jTabbedPane1.setSelectedIndex(jTabbedPane1.getTabCount() - 1);
    									updateWindowsTitle();
                                    }
                                }
                            }
                        });
                    }
                    {
                        openFileMenuItem = new JMenuItem();
                        jMenu3.add(openFileMenuItem);
                        openFileMenuItem.setText("\u7f51\u7edc\u65e5\u5fd7\u8ddf\u8e2a");
                        openFileMenuItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent evt) {
                                NetReaderConfigDialog d = new NetReaderConfigDialog(LogViewerMain.this);
                                d.setSize(500, 464);
                                d.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                                d.setModal(true);
                                d.setLocationRelativeTo(LogViewerMain.this);
                                d.setVisible(true);
                                if (d.getLogCodes() != null) {
                                	int count = 0;
                                    for (String logCode : d.getLogCodes()) {
                                    	if(logCode != null && logCode.length() > 0){
                                    		addNetLogTab(logCode, d.getIp(), d.getPort());
                                    		count++;
                                    	}
                                    }
                                    if(count > 0){
                                    	jTabbedPane1.setSelectedIndex(jTabbedPane1.getTabCount() - 1);
                                    	updateWindowsTitle();
                                    }
                                }
                                d.dispose();
                            }
                        });
                    }
                }
                {
                	jMenu2 = new JMenu();
                	jMenuBar1.add(jMenu2);
                	jMenu2.setText("\u65e5\u5fd7\u7a7a\u95f4");
                	{
                		jMenuItem1 = new JMenuItem();
                		jMenu2.add(jMenuItem1);
                		jMenuItem1.setText("\u67e5\u770b\u5360\u7528\u7a7a\u95f4");
                		jMenuItem1.addActionListener(new ActionListener() {
                			public void actionPerformed(ActionEvent evt) {
                				LogFileSizeDialog fileSizeView = new LogFileSizeDialog(null);
                				fileSizeView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                				fileSizeView.setModal(false);
                				fileSizeView.setLocationRelativeTo(LogViewerMain.this);
                				fileSizeView.setVisible(true);
                			}
                		});
                	}
                }
                {
                	jMenu1 = new JMenu();
                	jMenuBar1.add(jMenu1);
                	jMenu1.setText("\u6807\u7b7e");
                	{
                		savnOpenTabMenuItem = new JMenuItem();
                		jMenu1.add(savnOpenTabMenuItem);
                		savnOpenTabMenuItem.setText("\u4fdd\u5b58\u6253\u5f00\u7684\u6807\u7b7e");
                		savnOpenTabMenuItem.addActionListener(new ActionListener() {
                			public void actionPerformed(ActionEvent evt) {
                				String value = JOptionPane.showInputDialog("请输入保存的标签组名称");
                				if(value != null && value.trim().length() > 0){
                					LogConfigUtils.saveTabGroup(value.trim(), jTabbedPane1);
                				}
                			}
                		});
                	}
                	{
                		openSavedTabMenuItem = new JMenuItem();
                		jMenu1.add(openSavedTabMenuItem);
                		openSavedTabMenuItem.setText("\u6253\u5f00\u4fdd\u5b58\u7684\u6807\u7b7e");
                		openSavedTabMenuItem.addActionListener(new ActionListener() {
                			public void actionPerformed(ActionEvent evt) {
                				OpenSavedTabDialog d = new OpenSavedTabDialog(LogViewerMain.this);
                				d.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                                d.setModal(true);
                                d.setLocationRelativeTo(LogViewerMain.this);
                                d.setVisible(true);
                                if(d.isOpen()){
                                	for (String logTab : d.getTabs()) {
										if(logTab.startsWith("file:")){
											String value = logTab.substring("file:".length());
											String[] ss = value.split(":");
											addFileLogTab(new File(ss[0]), ss[1]);
										}
										if(logTab.startsWith("net:")){
											String value = logTab.substring("net:".length());
											String[] ss = value.split(":");
											String logCode = ss[2];
											if(ss.length == 4){
												logCode = logCode + ":" + ss[3];
											}
											addNetLogTab(logCode, ss[0], Integer.parseInt(ss[1]));
										}
									}
                                }
                                d.dispose();
                			}
                		});
                	}
                	{
                		manageTabMenuItem = new JMenuItem();
                		jMenu1.add(manageTabMenuItem);
                		manageTabMenuItem.setText("\u7ba1\u7406\u6807\u7b7e");
                		manageTabMenuItem.addActionListener(new ActionListener() {
                			public void actionPerformed(ActionEvent evt) {
                				OpenSavedTabDialog d = new OpenSavedTabDialog(LogViewerMain.this);
                				d.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                                d.setModal(true);
                                d.setLocationRelativeTo(LogViewerMain.this);
                                d.setVisible(true);
                                d.dispose();
                			}
                		});
                	}
                	{
                		closeAllMenuItem = new JMenuItem();
                		jMenu1.add(closeAllMenuItem);
                		closeAllMenuItem.setText("\u5168\u90e8\u5173\u95ed");
                		closeAllMenuItem.setBounds(-67, 73, 90, 23);
                		closeAllMenuItem.addActionListener(new ActionListener() {
                			public void actionPerformed(ActionEvent evt) {
                				closeAllTab();
                			}
                		});
                	}
                }
                {
                    jMenu5 = new JMenu();
                    jMenuBar1.add(jMenu5);
                    jMenu5.setText("Help");
                    {
                        helpMenuItem = new JMenuItem();
                        jMenu5.add(helpMenuItem);
                        helpMenuItem.setText("Help");
                    }
                }
            }
            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    closeAllTab();
                    System.exit(0); //关闭
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void updateWindowsTitle() {
		if(jTabbedPane1.getSelectedIndex() != -1){
			Component c = jTabbedPane1.getTabComponentAt(jTabbedPane1.getSelectedIndex());
		    if (c instanceof TabTitlePanel) {
		    	TabTitlePanel p = (TabTitlePanel) c;
		    	if(p.getIp() != null){
					LogViewerMain.this.setTitle(p.getTabTitle() + " - " + p.getIp() + ":" + p.getPort());
		    	}else{
		    		LogViewerMain.this.setTitle(p.getTabTitle());
		    	}
		    }
		}else{
			LogViewerMain.this.setTitle("日志查看");
		}
	}

	private void closeAllTab() {
		// 关闭tab,关闭网络会话
		int count = jTabbedPane1.getTabCount();
		for (int i = 0; i < count; i++) {
		    Component c = jTabbedPane1.getComponentAt(i);
		    if (c instanceof LogViewerPanel) {
		        LogViewerPanel p = (LogViewerPanel) c;
		        p.stopTimer();
		        try {
					p.getLogReader().close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		}
		while(count > 0){
			jTabbedPane1.remove(count - 1);
			count = jTabbedPane1.getTabCount();
		}
	}

	private void addNetLogTab(String logCode, String ip, int port) {
		try {
			LogViewerPanel jPanel1 = new LogViewerPanel(NetLogReader.connectServer(logCode, ip, port));
			jPanel1.setLogServerIp(ip);
			jPanel1.setLogServerPort(port);
			jPanel1.setLogCode(logCode);
			jTabbedPane1.addTab(null, jPanel1);
			TabTitlePanel tabTitlePanel = new TabTitlePanel(logCode, jPanel1, jTabbedPane1);
			tabTitlePanel.setIp(ip);
			tabTitlePanel.setPort(port);
			tabTitlePanel.init();
			jPanel1.setLogChangedListener(tabTitlePanel);
			jTabbedPane1.setTabComponentAt(jTabbedPane1.indexOfComponent(jPanel1), tabTitlePanel);
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(LogViewerMain.this, "打开网络日志文件[" + logCode + ":" + ip + ":" + port + "]失败");
		}
		try {
		    // 延迟一下
		    Thread.sleep(200);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	}

	private void addFileLogTab(File file, String encoding) {
		if(!file.exists()){
			JOptionPane.showMessageDialog(LogViewerMain.this, "文件[" + file.getAbsolutePath() + "]不存在");
		    return;
		}
		// 测试编码格式
		try {
		    "abc".getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
		    JOptionPane.showMessageDialog(LogViewerMain.this, "不支持的编码格式[" + encoding + "]");
		    return;
		}
		try {
			LogViewerPanel jPanel1 = new LogViewerPanel(
			        new FileLogReader(file, encoding));
			jPanel1.setLogFile(file);
			jPanel1.setLogFileEncoding(encoding);
			jTabbedPane1.addTab(null, jPanel1);
			TabTitlePanel tabTitlePanel = new TabTitlePanel(file.getName(), jPanel1, jTabbedPane1);
			jPanel1.setLogChangedListener(tabTitlePanel);
			jTabbedPane1.setTabComponentAt(jTabbedPane1.indexOfComponent(jPanel1), tabTitlePanel);
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(LogViewerMain.this, "打开本地日志文件[" + file.getAbsolutePath() + "]失败");
		}
		try {
		    // 延迟一下
		    Thread.sleep(200);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	}

}
