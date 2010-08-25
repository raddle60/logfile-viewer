package com.raddle.log.viewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;
import com.raddle.log.reader.LogReader;
import com.raddle.log.viewer.listener.LogChangedListener;

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
public class LogViewerPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private JScrollPane jScrollPane1;
    private JLabel fileLengthLeb;
    private JLabel updateTimeLeb;
    private JButton clearBtn;
    private JLabel jLabel1;
    private JTextField lastBytesTxt;
    private JButton lastLineBtn;
    private JButton saveAsBtn;
    private JTextField keywordTxt;
    private JCheckBox autoScrollChk;
    private JButton nextBtn;
    private JButton preBtn;
    private JButton searchBtn;
    private JList logList;
    private LogReader logReader;
    private Timer timer = new Timer();
    private int searchedIndex = -1;
    private int previousIndex = -1;
    private int ppreviousIndex = -1;
    private final static int MAX_LINES = 2000;
    private LogChangedListener logChangedListener;
    private int refreshSecend = 5;
    //////
    private File logFile;
    private String logFileEncoding;
    private String logServerIp;
    private int logServerPort;
    private String logCode;

    public LogViewerPanel(final LogReader logReader) {
        this(logReader, null);
    }

    public LogViewerPanel(final LogReader logReader , LogChangedListener logChangedListener) {
        super();
        this.logReader = logReader;
        this.logChangedListener = logChangedListener;
        initGUI();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    if (autoScrollChk.isSelected()) {
                        readAppendLines();
                    }
                } catch (Throwable e) {
                    // 出异常了也继续执行
                    e.printStackTrace();
                    addElement("自动滚动异常:" + e.getMessage());
                }
            }
        }, 1000, refreshSecend * 1000);
    }

    @SuppressWarnings("unchecked")
    private void initGUI() {
        try {
            AnchorLayout thisLayout = new AnchorLayout();
            this.setLayout(thisLayout);
            setPreferredSize(new Dimension(800, 300));
            {
            	fileLengthLeb = new JLabel();
            	this.add(fileLengthLeb, new AnchorConstraint(45, 0, 0, 634, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
            	fileLengthLeb.setText("\u6587\u4ef6\u5927\u5c0f");
            	fileLengthLeb.setPreferredSize(new java.awt.Dimension(170, 23));
            }
            {
                updateTimeLeb = new JLabel();
                this.add(updateTimeLeb, new AnchorConstraint(46, 0, 0, 460, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
                updateTimeLeb.setPreferredSize(new java.awt.Dimension(174, 22));
                updateTimeLeb.setToolTipText("刷新间隔："+refreshSecend+"秒");
            }
            {
                clearBtn = new JButton();
                this.add(clearBtn, new AnchorConstraint(46, 421, 228, 348, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
                clearBtn.setText("\u6e05\u9664");
                clearBtn.setPreferredSize(new java.awt.Dimension(78, 22));
                clearBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        DefaultComboBoxModel m = (DefaultComboBoxModel) logList.getModel();
                        m.removeAllElements();
                        previousIndex = -1;
                        ppreviousIndex = -1;
                    }
                });
            }
            {
                jLabel1 = new JLabel();
                this.add(jLabel1, new AnchorConstraint(49, 346, 215, 281, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
                jLabel1.setText("KB");
                jLabel1.setPreferredSize(new java.awt.Dimension(21, 15));
            }
            {
                lastBytesTxt = new JTextField();
                this.add(lastBytesTxt, new AnchorConstraint(46, 268, 228, 185, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
                lastBytesTxt.setPreferredSize(new java.awt.Dimension(90, 22));
                lastBytesTxt.setText("20");
            }
            {
                lastLineBtn = new JButton();
                this.add(lastLineBtn, new AnchorConstraint(49, 141, 225, 15, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
                lastLineBtn.setText("\u83b7\u5f97\u6587\u4ef6\u5c3e\u90e8");
                lastLineBtn.setPreferredSize(new java.awt.Dimension(159, 21));
                lastLineBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        readLastBytes();
                    }
                });
            }
            {
                saveAsBtn = new JButton();
                this.add(saveAsBtn, new AnchorConstraint(11, 878, 108, 634, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
                saveAsBtn.setText("\u65e5\u5fd7\u6587\u4ef6\u53e6\u5b58\u4e3a...");
                saveAsBtn.setPreferredSize(new java.awt.Dimension(155, 22));
                saveAsBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        JFileChooser f = new JFileChooser();
                        f.addChoosableFileFilter(new FileNameExtensionFilter("日志文件", "txt", "log"));
                        int result = f.showSaveDialog(LogViewerPanel.this);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File sf = f.getSelectedFile();
                            if (!sf.getName().endsWith(".txt") && !sf.getName().endsWith(".log")) {
                                sf = new File(sf.getParentFile(), sf.getName() + ".log");
                            }
                            logReader.saveAs(sf);
                        }
                    }
                });
            }
            {
                autoScrollChk = new JCheckBox();
                this.add(autoScrollChk, new AnchorConstraint(12, 971, 108, 460, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
                autoScrollChk.setText("\u81ea\u52a8\u6eda\u52a8");
                autoScrollChk.setPreferredSize(new java.awt.Dimension(101, 19));
                autoScrollChk.setSelected(true);
            }
            {
                nextBtn = new JButton();
                this.add(nextBtn, new AnchorConstraint(12, 801, 115, 348, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
                nextBtn.setText("\u4e0b\u4e00\u4e2a");
                nextBtn.setPreferredSize(new java.awt.Dimension(80, 22));
                nextBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        int size = logList.getModel().getSize();
                        for (int i = searchedIndex + 1; i < size; i++) {
                            String s = logList.getModel().getElementAt(i).toString();
                            if (keywordTxt.getText().trim().length() > 0
                                    && s.toLowerCase().indexOf(keywordTxt.getText().toLowerCase()) != -1) {
                                searchedIndex = i;
                                logList.setSelectedIndex(i);
                                Rectangle rect = logList.getCellBounds(i, i);
                                logList.scrollRectToVisible(rect);
                                break;
                            }
                        }
                    }
                });
            }
            {
                preBtn = new JButton();
                this.add(preBtn, new AnchorConstraint(12, 648, 115, 260, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
                preBtn.setText("\u4e0a\u4e00\u4e2a");
                preBtn.setPreferredSize(new java.awt.Dimension(77, 22));
                preBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        for (int i = Math.max(searchedIndex - 1, 0); i > 0; i--) {
                            String s = logList.getModel().getElementAt(i).toString();
                            if (keywordTxt.getText().trim().length() > 0
                                    && s.toLowerCase().indexOf(keywordTxt.getText().toLowerCase()) != -1) {
                                searchedIndex = i;
                                logList.setSelectedIndex(i);
                                Rectangle rect = logList.getCellBounds(i, i);
                                logList.scrollRectToVisible(rect);
                                break;
                            }
                        }
                    }
                });
            }
            {
                searchBtn = new JButton();
                this.add(searchBtn, new AnchorConstraint(12, 496, 115, 186, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
                searchBtn.setText("\u67e5\u627e");
                searchBtn.setPreferredSize(new java.awt.Dimension(63, 22));
                searchBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        searchKeyword();
                    }
                });
            }
            {
                keywordTxt = new JTextField();
                this.add(keywordTxt, new AnchorConstraint(12, 341, 115, 12, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
                keywordTxt.setPreferredSize(new java.awt.Dimension(162, 22));
                keywordTxt.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent evt) {
                        if (evt.getKeyChar() == '\n') {
                            searchKeyword();
                        }
                    }
                });
            }
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(LogViewerPanel.class.getResourceAsStream("/line-color.properties")));
                final Map<Pattern, Color> colorPattern = new LinkedHashMap();
                String line = reader.readLine();
                while (line != null) {
                    int index = line.indexOf('=');
                    String colorStr = line.substring(0, index);
                    String pattern = line.substring(index + 1);
                    String regxStr = pattern.substring(1, pattern.length() - 1);
                    if(colorStr.indexOf(',') != -1){
                    	String[] rgbStr = colorStr.split(",");
                    	int[] rgb = new int[3];
                    	rgb[0] = Integer.parseInt(rgbStr[0]);
                    	rgb[1] = Integer.parseInt(rgbStr[1]);
                    	rgb[2] = Integer.parseInt(rgbStr[2]);
                    	colorPattern.put(Pattern.compile(regxStr, Pattern.CASE_INSENSITIVE), new Color(rgb[0], rgb[1], rgb[2]));
                    }else if(colorStr.startsWith("0x")){
                    	String rgbStr = colorStr.substring(2);
                    	colorPattern.put(Pattern.compile(regxStr, Pattern.CASE_INSENSITIVE), new Color(Integer.parseInt(rgbStr,16)));
                    }
                    line = reader.readLine();
                }

                jScrollPane1 = new JScrollPane();
                this.add(jScrollPane1, new AnchorConstraint(80, 12, 12, 12, AnchorConstraint.ANCHOR_ABS,
                        AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS));
                jScrollPane1.setPreferredSize(new java.awt.Dimension(576, 242));
                {
                    DefaultComboBoxModel logListModel = new DefaultComboBoxModel(new String[] {});
                    logList = new JList();
                    jScrollPane1.setViewportView(logList);
                    logList.setCellRenderer(new DefaultListCellRenderer() {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public Component getListCellRendererComponent(JList list, Object value, int index,
                                boolean isSelected, boolean cellHasFocus) {
                            Component c = super.getListCellRendererComponent(list, value, index, isSelected,
                                    cellHasFocus);
                            for (Pattern pattern : colorPattern.keySet()) {
                                if (value != null) {
                                    Matcher matcher = pattern.matcher(value.toString());
                                    if (matcher.find()) {
                                        c.setBackground(colorPattern.get(pattern));
                                    }
                                }
                            }
							if (index > ppreviousIndex && index <= previousIndex) {
								c.setForeground(new Color(0x993366));
							} else if (index > previousIndex) {
								c.setForeground(new Color(0x0000FF));
							}
                            return c;
                        }

                    });
                    logList.setModel(logListModel);
                    if (autoScrollChk.isSelected()) {
                        readLastBytes();
                    }
                }
            }
            {
            	logList.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                    	int size = logList.getModel().getSize();
                    	if(size -1 > previousIndex){
                    		 ppreviousIndex = previousIndex;
                             previousIndex = size -1;
                             logList.repaint();
                    	}
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchKeyword() {
        int size = logList.getModel().getSize();
        for (int i = 0; i < size; i++) {
            String s = logList.getModel().getElementAt(i).toString();
            if (keywordTxt.getText().trim().length() > 0
                    && s.toLowerCase().indexOf(keywordTxt.getText().toLowerCase()) != -1) {
                searchedIndex = i;
                logList.setSelectedIndex(i);
                Rectangle rect = logList.getCellBounds(i, i);
                logList.scrollRectToVisible(rect);
                break;
            }
        }
    }

    private void addElement(String s) {
        DefaultComboBoxModel m = (DefaultComboBoxModel) logList.getModel();
        m.addElement(s.trim());
        if (m.getSize() > MAX_LINES) {
            m.removeElementAt(0);
	   		ppreviousIndex --;
	        previousIndex --;
        }
    }

    public void stopTimer() {
        timer.cancel();
    }

    public LogReader getLogReader() {
        return logReader;
    }

    private void readAppendLines() {
        String[] ss = LogViewerPanel.this.logReader.readAppendedLines();
        if (ss.length > 0) {
            for (String s : ss) {
                addElement(s);
            }
            int logListSize = logList.getModel().getSize();
            if (logListSize > 0) {
                scrollToBottom(logListSize);
            }
            if(logChangedListener != null){
                logChangedListener.logChanged();
            }
        }
        updateTime();
        updateFileSize();
    }

	private void scrollToBottom(int logListSize) {
		JScrollBar scrollBar = jScrollPane1.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
	}

    private void readLastBytes() {
        try {
            int kBytes = Integer.parseInt(lastBytesTxt.getText());
            if (kBytes < 0) {
                JOptionPane.showMessageDialog(LogViewerPanel.this, "尾部字节数不能小于0");
                return;
            }
            if (kBytes > 1024) {
                JOptionPane.showMessageDialog(LogViewerPanel.this, "尾部字节数不能大于1024KB");
                return;
            }
            DefaultComboBoxModel m = (DefaultComboBoxModel) logList.getModel();
            m.removeAllElements();
            long lastBytes = kBytes * 1024;
            String[] ss = LogViewerPanel.this.logReader.readLastBytes(lastBytes);
            for (String s : ss) {
                addElement(s);
            }
            int logListSize = logList.getModel().getSize();
            previousIndex = logListSize - 1;
            ppreviousIndex = previousIndex;
            if (ss.length > 0) {
                if (logListSize > 0) {
                    scrollToBottom(logListSize);
                }
            }
            updateTime();
            updateFileSize();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(LogViewerPanel.this, "错误的数字格式");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(LogViewerPanel.this, "读取日志文件异常：" + e.getMessage());
        }
    }

	private void updateTime() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        updateTimeLeb.setText("最后刷新时间："+timeFormat.format(new Date()));
	}

    private void updateFileSize(){
		try {
			long length = logReader.getFileBytes();
			long k = 1024;
			long m = 1024 * k;
			long g = 1024 * m;
			String lengthDesc = "";
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
			fileLengthLeb.setText("文件大小：" + lengthDesc);
			fileLengthLeb.setToolTipText(null);
		} catch (Exception e) {
			e.printStackTrace();
			fileLengthLeb.setText("文件大小：error");
			fileLengthLeb.setToolTipText(e.getMessage());
		}
    }
    public LogChangedListener getLogChangedListener() {
        return logChangedListener;
    }

    public void setLogChangedListener(LogChangedListener logChangedListener) {
        this.logChangedListener = logChangedListener;
    }

    public JList getLogList() {
        return logList;
    }

	public File getLogFile() {
		return logFile;
	}

	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}

	public String getLogServerIp() {
		return logServerIp;
	}

	public void setLogServerIp(String logServerIp) {
		this.logServerIp = logServerIp;
	}

	public int getLogServerPort() {
		return logServerPort;
	}

	public void setLogServerPort(int logServerPort) {
		this.logServerPort = logServerPort;
	}

	public String getLogFileEncoding() {
		return logFileEncoding;
	}

	public void setLogFileEncoding(String logFileEncoding) {
		this.logFileEncoding = logFileEncoding;
	}

	public String getLogCode() {
		return logCode;
	}

	public void setLogCode(String logCode) {
		this.logCode = logCode;
	}
}
