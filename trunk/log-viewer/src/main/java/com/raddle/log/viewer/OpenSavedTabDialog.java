package com.raddle.log.viewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

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

	/**
	* Auto-generated main method to display this JDialog
	*/
	
	public OpenSavedTabDialog(JFrame frame) {
		super(frame);
		initGUI();
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
							System.out.println("openBtn.actionPerformed, event="+evt);
							//TODO add your code for openBtn.actionPerformed
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
							System.out.println("deleteBtn.actionPerformed, event="+evt);
							//TODO add your code for deleteBtn.actionPerformed
						}
					});
				}
			}
			this.setSize(618, 460);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
