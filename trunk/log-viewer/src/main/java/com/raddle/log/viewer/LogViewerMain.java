package com.raddle.log.viewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.filechooser.FileNameExtensionFilter;

import com.raddle.log.reader.file.FileLogReader;
import com.raddle.log.reader.net.NetLogReader;

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
    private JMenu jMenu5;
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
                                    // 测试编码格式
                                    try {
                                        "abc".getBytes(encoding);
                                    } catch (UnsupportedEncodingException e) {
                                        JOptionPane.showMessageDialog(LogViewerMain.this, "不支持的编码格式[" + encoding + "]");
                                        return;
                                    }
                                    LogViewerPanel jPanel1 = new LogViewerPanel(
                                            new FileLogReader(f.getSelectedFile(), encoding));
                                    jTabbedPane1.addTab(null, jPanel1);
                                    TabTitlePanel tabTitlePanel = new TabTitlePanel(f.getSelectedFile().getName(), jPanel1, jTabbedPane1);
                                    jPanel1.setLogChangedListener(tabTitlePanel);
                                    jTabbedPane1.setTabComponentAt(jTabbedPane1.indexOfComponent(jPanel1), tabTitlePanel);
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
                                d.setSize(500, 320);
                                d.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                                d.setModal(true);
                                d.setLocationRelativeTo(LogViewerMain.this);
                                d.setVisible(true);
                                if (d.getLogCodes() != null) {
                                    for (String logCode : d.getLogCodes()) {
                                        LogViewerPanel jPanel1 = new LogViewerPanel(NetLogReader.connectServer(logCode, d
                                                .getIp(), d.getPort()));
                                        jTabbedPane1.addTab(null, jPanel1);
                                        TabTitlePanel tabTitlePanel = new TabTitlePanel(logCode, jPanel1, jTabbedPane1);
                                        jPanel1.setLogChangedListener(tabTitlePanel);
                                        jTabbedPane1.setTabComponentAt(jTabbedPane1.indexOfComponent(jPanel1), tabTitlePanel);
                                        try {
                                            // 延迟一下
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                d.dispose();
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
                    // 关闭tab,关闭网络会话
                    int count = jTabbedPane1.getTabCount();
                    for (int i = 0; i < count; i++) {
                        Component c = jTabbedPane1.getComponentAt(i);
                        if (c instanceof LogViewerPanel) {
                            LogViewerPanel p = (LogViewerPanel) c;
                            p.stopTimer();
                            p.getLogReader().close();
                        }
                    }
                    System.exit(0); //关闭
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
