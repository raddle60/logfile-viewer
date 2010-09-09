package com.raddle.log.viewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;
import com.raddle.log.reader.net.NetLogFile;
import com.raddle.log.reader.net.NetLogReader;
import com.raddle.log.viewer.utils.FileSizeUtils;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class LogFileSizeDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;
	private JTextArea logServerTxt;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JTable logSizeTable;
	private JButton viewBtn;
	private boolean getting = false;

	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				LogFileSizeDialog inst = new LogFileSizeDialog(frame);
                inst.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                inst.setVisible(true);
			}
		});
	}
	
	public LogFileSizeDialog(JFrame frame) {
		super(frame);
		initGUI();
	}
	
	private void initGUI() {
		try {
			AnchorLayout thisLayout = new AnchorLayout();
			getContentPane().setLayout(thisLayout);
			this.setTitle("\u67e5\u770b\u65e5\u5fd7\u6587\u4ef6\u7a7a\u9593");
			{
				jScrollPane1 = new JScrollPane();
				getContentPane().add(jScrollPane1, new AnchorConstraint(104, 12, 12, 12, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS));
				jScrollPane1.setPreferredSize(new java.awt.Dimension(780, 257));
				{
					logSizeTable = new JTable();
					jScrollPane1.setViewportView(logSizeTable);
					TableModel logSizeTableModel = new DefaultTableModel();
					logSizeTable.setModel(logSizeTableModel);
					logSizeTable.setRowSorter(new TableRowSorter<TableModel>(logSizeTableModel));
				}
			}
			{
				viewBtn = new JButton();
				getContentPane().add(viewBtn, new AnchorConstraint(44, 60, 0, 648, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_NONE));
				viewBtn.setPreferredSize(new java.awt.Dimension(102, 25));
				viewBtn.setText("\u67e5\u770b");
				viewBtn.addActionListener(new ActionListener() {
					@SuppressWarnings("unchecked")
					public void actionPerformed(ActionEvent evt) {
						if(!getting) {
							getting = true;
							viewBtn.setEnabled(false);
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									final DefaultTableModel tableModel  = (DefaultTableModel) logSizeTable.getModel();
									SwingUtilities.invokeLater(new Runnable() {

										@Override
										public void run() {
											// 删除行
											while (tableModel.getRowCount() > 0) {
												tableModel.removeRow(0);
											}
										}
									});
									while (tableModel.getRowCount() > 0) {
										try {
											Thread.sleep(10);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
									SwingUtilities.invokeLater(new Runnable() {

										@Override
										public void run() {
											// 删除列
											tableModel.setColumnCount(0);
										}
									});
									while (tableModel.getColumnCount() > 0) {
										try {
											Thread.sleep(10);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}

									SwingUtilities.invokeLater(new Runnable() {

										@Override
										public void run() {
											// 添加列
											tableModel.addColumn("服务地址");
											tableModel.addColumn("服务器名");
											tableModel.addColumn("文件总大小");
											tableModel.addColumn("文件总大小可读");
										}
									});
									while (tableModel.getColumnCount() < 4) {
										try {
											Thread.sleep(10);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
									//
									TableRowSorter rowSorter = (TableRowSorter) logSizeTable.getRowSorter();
									rowSorter.setComparator(2, new Comparator<Long>() {
										@Override
										public int compare(Long o1, Long o2) {
											return o1.compareTo(o2);
										}
									});
									//
									long allLength = 0;
									int count = 0;
									Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}");
									Matcher matcher = ipPattern.matcher(logServerTxt.getText());
									while (matcher.find()) {
										String serverAddress = matcher.group();
										int index = serverAddress.indexOf(':');
										String ip = serverAddress.substring(0, index);
										String port = serverAddress.substring(index + 1);
										NetLogReader netLogReader = NetLogReader.connectServer("", ip, Integer.parseInt(port));
										try {
											NetLogFile[] logFiles = netLogReader.listFile();
											long length = 0;
											for (NetLogFile netLogFile : logFiles) {
												length += netLogFile.getLength();
											}
											if (logFiles.length == 0) {
												addRow(tableModel, new Object[] { ip, "unknown", "unknown", "unknown" });
											} else {
												addRow(tableModel, new Object[] { ip, logFiles[0].getServerName(), length, FileSizeUtils.readableSize(length)});
											}
											allLength += length;
										} catch(Exception e){
											e.printStackTrace();
											addRow(tableModel, new Object[] { ip, e.getMessage(), "unknown", "unknown" });
										} finally {
											netLogReader.close();
										}
										count++;
									}
									addRow(tableModel, new Object[] { "总大小", "", allLength, FileSizeUtils.readableSize(allLength) });
									if (count == 0) {
										JOptionPane.showMessageDialog(LogFileSizeDialog.this, "请输入日志服务器地址，格式为：\"IP:PORT,IP:PORT\"");
									}
									getting = false;
									viewBtn.setEnabled(true);
								}
							}).start();
						} else {
							JOptionPane.showMessageDialog(LogFileSizeDialog.this, "日志信息未完全获取，请稍后再点");
						}
					}
				});
			}
			{
				jScrollPane2 = new JScrollPane();
				getContentPane().add(jScrollPane2, new AnchorConstraint(12, 189, 0, 12, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
				jScrollPane2.setPreferredSize(new java.awt.Dimension(603, 80));
				{
					logServerTxt = new JTextArea();
					jScrollPane2.setViewportView(logServerTxt);
					logServerTxt.setText("ip:port,ip:port");
				}
			}
			this.setSize(814, 403);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addRow(final DefaultTableModel tableModel, final Object[] data) {
		tableModel.addRow(data);
	}
}
