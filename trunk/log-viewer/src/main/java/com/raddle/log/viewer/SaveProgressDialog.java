package com.raddle.log.viewer;
import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;
import com.raddle.log.reader.LogReader;
import com.raddle.log.reader.StreamSaver;
import com.raddle.log.viewer.utils.FileSizeUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;


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
public class SaveProgressDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;
	//
	private LogReader logReader;
	//
    private File logFile;
    private File saveTo;
    private String logServerIp;
    private int logServerPort;
    private String logCode;
	//
	private JLabel fromLeb;
	private JButton completeBtn;
	private JButton stopBtn;
	private JProgressBar progressBar;
	private JLabel progressLeb;
	private JLabel targetLeb;
	private boolean stop = false;

	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				SaveProgressDialog inst = new SaveProgressDialog(frame);
				inst.setVisible(true);
			}
		});
	}
	
	public SaveProgressDialog(JFrame frame) {
		super(frame);
		initGUI();
	}
	
	private void initGUI() {
		try {
			{
				AnchorLayout thisLayout = new AnchorLayout();
				getContentPane().setLayout(thisLayout);
				this.setTitle("\u4fdd\u5b58\u6587\u4ef6");
				{
					completeBtn = new JButton();
					getContentPane().add(completeBtn, new AnchorConstraint(120, 706, 0, 539, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_REL));
					completeBtn.setText("\u5b8c\u6210");
					completeBtn.setPreferredSize(new java.awt.Dimension(105, 25));
					completeBtn.setEnabled(false);
					completeBtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							SaveProgressDialog.this.dispose();
						}
					});
				}
				{
					stopBtn = new JButton();
					getContentPane().add(stopBtn, new AnchorConstraint(120, 468, 0, 307, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_REL));
					stopBtn.setText("\u505c\u6b62");
					stopBtn.setPreferredSize(new java.awt.Dimension(101, 25));
					stopBtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							stop = true;
						}
					});
				}
				{
					progressBar = new JProgressBar();
					getContentPane().add(progressBar, new AnchorConstraint(75, 12, 0, 12, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
					progressBar.setPreferredSize(new java.awt.Dimension(605, 33));
				}
				{
					progressLeb = new JLabel();
					getContentPane().add(progressLeb, new AnchorConstraint(54, 12, 0, 12, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
					progressLeb.setText("\u8fdb\u5ea6");
					progressLeb.setPreferredSize(new java.awt.Dimension(607, 15));
				}
				{
					targetLeb = new JLabel();
					getContentPane().add(targetLeb, new AnchorConstraint(33, 12, 0, 12, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
					targetLeb.setText("\u76ee\u6807\u6587\u4ef6");
					targetLeb.setPreferredSize(new java.awt.Dimension(609, 15));
				}
				{
					fromLeb = new JLabel();
					getContentPane().add(fromLeb, new AnchorConstraint(12, 12, 0, 12, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_ABS, AnchorConstraint.ANCHOR_NONE, AnchorConstraint.ANCHOR_ABS));
					fromLeb.setText("\u6e90\u6587\u4ef6");
					fromLeb.setPreferredSize(new java.awt.Dimension(609, 15));
				}
			}
			this.setSize(639, 198);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startSave() {
		if (logFile != null) {
			setTitle("正在保存" + logFile.getName());
			fromLeb.setText("从："+logFile.getAbsolutePath() + "    [" + FileSizeUtils.readableSize(logReader.getFileBytes()) + "]");
			targetLeb.setText("到："+saveTo.getAbsolutePath());
			progressLeb.setText("状态：正在保存文件...");
		} else {
			setTitle("正在保存" + logServerIp + ":" + logServerPort + "-" + logCode);
			fromLeb.setText("从："+logServerIp + ":" + logServerPort + "-" + logCode + "    [" + FileSizeUtils.readableSize(logReader.getFileBytes()) + "]");
			targetLeb.setText("到："+saveTo.getAbsolutePath());
			progressLeb.setText("状态：正在保存文件...");
		}
		setVisible(true);
		new Thread(){
			@Override
			public void run() {
				logReader.saveAs(new StreamSaver() {
					@Override
					public void saveStream(InputStream in) {
						try {
							long fileSize = logReader.getFileBytes();
							progressBar.setMinimum(0);
							progressBar.setMaximum(toKByte(fileSize));
							progressBar.setStringPainted(true);
							OutputStream os = new FileOutputStream(saveTo);
							byte[] buffer = new byte[1024];
							int n = 0;
							long writedBytes = 0;
							long start = System.currentTimeMillis();
							long spanStart = System.currentTimeMillis();
							long spanWritedBytes = 0;
							while (-1 != (n = in.read(buffer))) {
								os.write(buffer, 0, n);
								writedBytes += n;
								spanWritedBytes += n;
								progressBar.setValue(toKByte(writedBytes));
								progressBar.setString(FileSizeUtils.readableSize(writedBytes) + "/" + FileSizeUtils.readableSize(fileSize));
								long spanTime = System.currentTimeMillis() - spanStart;
								if (spanTime > 1000) {
									progressLeb.setText("状态：正在保存文件...  [" + FileSizeUtils.readableSize(bytePerSecond(spanStart, spanWritedBytes))
											+ "/秒]");
									spanStart = System.currentTimeMillis();
									spanWritedBytes = 0;
								}
								if (stop) {
									os.flush();
									os.close();
									progressLeb.setText("状态：已停止,平均[" + FileSizeUtils.readableSize(bytePerSecond(start, writedBytes)) + "/秒]");
									setTitle("已停止保存" + logFile.getName());
									return;
								}
							}
							os.flush();
							os.close();
							progressLeb.setText("状态：文件已保存,平均[" + FileSizeUtils.readableSize(bytePerSecond(start, writedBytes)) + "/秒]");
						} catch (Exception e) {
							e.printStackTrace();
							progressLeb.setText("状态：保存失败:"+e.getMessage());
						} finally {
							stopBtn.setEnabled(false);
							completeBtn.setEnabled(true);
						}
					}
				});
			}
		}.start();
	}

	public int toKByte(long bytes) {
		return (int) Math.ceil((double) bytes / 1024);
	}

	public long bytePerSecond(long start,long bytes){
		long time = System.currentTimeMillis() - start;
		double perSecond = (double) bytes / ((double) time / 1000);
		return (long)perSecond;
	}
	
	public LogReader getLogReader() {
		return logReader;
	}

	public void setLogReader(LogReader logReader) {
		this.logReader = logReader;
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

	public String getLogCode() {
		return logCode;
	}

	public void setLogCode(String logCode) {
		this.logCode = logCode;
	}

	public File getSaveTo() {
		return saveTo;
	}

	public void setSaveTo(File saveTo) {
		this.saveTo = saveTo;
	}

}
