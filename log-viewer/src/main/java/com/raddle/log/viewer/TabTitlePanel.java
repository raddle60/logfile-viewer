package com.raddle.log.viewer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

import com.raddle.log.reader.LogReader;
import com.raddle.log.reader.net.NetLogReader;
import com.raddle.log.viewer.listener.LogChangedListener;

public class TabTitlePanel extends JPanel implements LogChangedListener {
    private static final long serialVersionUID = 1L;
    private JLabel titleLebel;
    private CloseButton closebutton;
    private final JTabbedPane tabbedPane;
    private final LogViewerPanel pane;

    public TabTitlePanel(String s, LogViewerPanel pane,JTabbedPane tabbedPane) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.tabbedPane = tabbedPane;
        this.pane = pane;
        titleLebel = new JLabel(s);
        closebutton = new CloseButton();
        add(titleLebel);
        add(closebutton);
        titleLebel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setOpaque(false);
        this.pane.getLogList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                titleLebel.setForeground(Color.BLACK);
            }
        });
    }

    private class CloseButton extends JButton {
        private static final long serialVersionUID = 1L;

        //        private ImageIcon icon;

        public CloseButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("关闭");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            setText("关闭");
            //Close the proper tab by clicking the button
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int ret = JOptionPane.showConfirmDialog(tabbedPane, "你确定关闭标签[" + titleLebel.getText() + "]吗？");
                    if (ret == JOptionPane.YES_OPTION) {
                        Component c = tabbedPane.getSelectedComponent();
                        if (c instanceof LogViewerPanel) {
                            LogViewerPanel p = (LogViewerPanel) c;
                            p.stopTimer();
                            LogReader r = p.getLogReader();
                            if (r instanceof NetLogReader) {
                                ((NetLogReader) r).close();
                            }
                        }
                        tabbedPane.remove(tabbedPane.indexOfTabComponent(TabTitlePanel.this));
                    }
                }
            });
        }
        
        //we don't want to update UI for this button
        public void updateUI() {
        }

        //paint the cross
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
        
    }
    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };

    @Override
    public void logChanged() {
        titleLebel.setForeground(Color.RED);
    }
}
