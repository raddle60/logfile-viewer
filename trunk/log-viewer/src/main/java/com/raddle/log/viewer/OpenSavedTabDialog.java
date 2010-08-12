package com.raddle.log.viewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import com.raddle.log.viewer.utils.LogConfigUtils;

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
public class OpenSavedTabDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPane1;
	private JButton deleteBtn;
	private JList groupJList;
	private JButton openBtn;
	private boolean open = false;
	private List<String> tabs;

	/**
	* Auto-generated main method to display this JDialog
	*/
	
	public OpenSavedTabDialog(JFrame frame) {
		super(frame);
		initGUI();
		reloadGroups();
	}
	
	private void initGUI() {
		try {
			{
				getContentPane().setLayout(null);
				{
					jScrollPane1 = new JScrollPane();
					getContentPane().add(jScrollPane1);
					jScrollPane1.setBounds(12, 12, 438, 406);
					{
						ListModel groupJListModel = 
							new DefaultComboBoxModel(
									new String[] {});
						groupJList = new JList();
						jScrollPane1.setViewportView(groupJList);
						groupJList.setModel(groupJListModel);
					}
				}
				{
					openBtn = new JButton();
					getContentPane().add(openBtn);
					openBtn.setText("\u6253\u5f00");
					openBtn.setBounds(467, 12, 114, 25);
					openBtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							if (groupJList.getSelectedValue() != null) {
								Object[] selecteds = groupJList.getSelectedValues();
								tabs = new ArrayList<String>();
								for (int i = 0; i < selecteds.length; i++) {
									tabs.addAll(LogConfigUtils.getGroupTabs(selecteds[i].toString()));
								}
								open = true;
								OpenSavedTabDialog.this.setVisible(false);
                            } else {
                                JOptionPane.showMessageDialog(OpenSavedTabDialog.this, "请选择要打开的标签组");
                            }
						}
					});
				}
				{
					deleteBtn = new JButton();
					getContentPane().add(deleteBtn);
					deleteBtn.setText("\u5220\u9664");
					deleteBtn.setBounds(467, 69, 114, 25);
					deleteBtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							if (groupJList.getSelectedValue() != null) {
								int ret = JOptionPane.showConfirmDialog(OpenSavedTabDialog.this, "确定要删除选中的标签组吗");
								if(ret == JOptionPane.YES_OPTION){
									Object[] selecteds = groupJList.getSelectedValues();
									for (int i = 0; i < selecteds.length; i++) {
										LogConfigUtils.removeTabGroup(selecteds[i].toString());
									}
									reloadGroups();
								}
                            } else {
                                JOptionPane.showMessageDialog(OpenSavedTabDialog.this, "请选择要删除的标签组");
                            }
						}
					});
				}
			}
			this.setSize(618, 460);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void reloadGroups(){
		DefaultComboBoxModel model = (DefaultComboBoxModel) groupJList.getModel();
		model.removeAllElements();
		for (String groupName : LogConfigUtils.getTabGroup()) {
			model.addElement(groupName);
		}
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public List<String> getTabs() {
		return tabs;
	}

	public void setTabs(List<String> tabs) {
		this.tabs = tabs;
	}

}
