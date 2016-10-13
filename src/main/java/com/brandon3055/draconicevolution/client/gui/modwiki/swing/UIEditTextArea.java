package com.brandon3055.draconicevolution.client.gui.modwiki.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import net.minecraft.util.text.TextFormatting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static com.intellij.uiDesigner.core.GridConstraints.*;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class UIEditTextArea extends JFrame implements ActionListener {

    public volatile String text;
    public volatile boolean hasChanged = false;
    public volatile boolean isFinished = false;
    private JPanel contentPane;
    private JPanel buttonPane;
    private JButton finishButton;
    private JButton insertButton;
    private JComboBox styleSelector;
    private JTextArea textArea;
    public volatile int linkTimer = 20;

    public UIEditTextArea(final String text) {
        this.text = text;
        setupUI();
        textArea.setText(text);
        for (TextFormatting format : TextFormatting.values()) {
            styleSelector.addItem(format);
        }
        styleSelector.setMaximumRowCount(20);
        setTitle("Edit Text");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        insertButton.addActionListener(this);
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                UIEditTextArea.this.text = UIEditTextArea.this.textArea.getText();
                UIEditTextArea.this.hasChanged = true;
            }
        });
        styleSelector.addActionListener(this);
        finishButton.addActionListener(this);

        //This thread will close the window if it becomes unlinked from its text component.
        //Which can happen if the gui is reloaded or closed.
        Thread timeout = new Thread() {
            @Override
            public void run() {
                while (UIEditTextArea.this.linkTimer > 0) {
                    linkTimer--;
                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                UIEditTextArea.this.dispose();
            }
        };
        timeout.setDaemon(true);
        timeout.start();
    }

    private void setupUI() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setPreferredSize(new Dimension(600, 400));
        contentPane.putClientProperty("html.disable", Boolean.FALSE);
        buttonPane = new JPanel();
        buttonPane.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(buttonPane, new GridConstraints(1, 0, 1, 1, ANCHOR_CENTER, FILL_BOTH, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        finishButton = new JButton();
        finishButton.setText("Finish");
        buttonPane.add(finishButton, new GridConstraints(0, 4, 1, 1, ANCHOR_CENTER, FILL_HORIZONTAL, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        buttonPane.add(spacer1, new GridConstraints(0, 3, 1, 1, ANCHOR_CENTER, FILL_HORIZONTAL, SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        insertButton = new JButton();
        insertButton.setText("Repeat Formatting");
        buttonPane.add(insertButton, new GridConstraints(0, 2, 1, 1, ANCHOR_CENTER, FILL_HORIZONTAL, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, SIZEPOLICY_FIXED, null, null, null, 0, false));
        styleSelector = new JComboBox();
        styleSelector.setRenderer(new CustomCellRenderer());
        buttonPane.add(styleSelector, new GridConstraints(0, 1, 1, 1, ANCHOR_WEST, FILL_HORIZONTAL, SIZEPOLICY_CAN_GROW, SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Add Format Code: ");
        buttonPane.add(label1, new GridConstraints(0, 0, 1, 1, ANCHOR_WEST, FILL_NONE, SIZEPOLICY_FIXED, SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        contentPane.add(scrollPane1, new GridConstraints(0, 0, 1, 1, ANCHOR_CENTER, FILL_BOTH, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_WANT_GROW, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textArea = new JTextArea();
        scrollPane1.setViewportView(textArea);

        setContentPane(contentPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Finish")) {
            isFinished = true;
            dispose();
        }
        else if (command.equals("Repeat Formatting") || command.equals("comboBoxChanged")) {
            textArea.insert(styleSelector.getSelectedItem().toString(), textArea.getCaretPosition());
            text = textArea.getText();
            hasChanged = true;
        }
    }

    private class CustomCellRenderer extends JButton implements ListCellRenderer {
        public CustomCellRenderer() {
            setOpaque(true);

        }

        boolean b = false;

        @Override
        public void setBackground(Color bg) {
            if (!b) {
                return;
            }

            super.setBackground(bg);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            b = true;

            if (value instanceof TextFormatting) {
                switch ((TextFormatting)value) {
                    case BLACK:
                        setBackground(new Color(0x000000));
                        break;
                    case DARK_BLUE:
                        setBackground(new Color(0x0000AA));
                        break;
                    case DARK_GREEN:
                        setBackground(new Color(0x00AA00));
                        break;
                    case DARK_AQUA:
                        setBackground(new Color(0x00AAAA));
                        break;
                    case DARK_RED:
                        setBackground(new Color(0xAA0000));
                        break;
                    case DARK_PURPLE:
                        setBackground(new Color(0xAA00AA));
                        break;
                    case GOLD:
                        setBackground(new Color(0xFFAA00));
                        break;
                    case GRAY:
                        setBackground(new Color(0xAAAAAA));
                        break;
                    case DARK_GRAY:
                        setBackground(new Color(0x555555));
                        break;
                    case BLUE:
                        setBackground(new Color(0x5555FF));
                        break;
                    case GREEN:
                        setBackground(new Color(0x55FF55));
                        break;
                    case AQUA:
                        setBackground(new Color(0x55FFFF));
                        break;
                    case RED:
                        setBackground(new Color(0xFF5555));
                        break;
                    case LIGHT_PURPLE:
                        setBackground(new Color(0xFF55FF));
                        break;
                    case YELLOW:
                        setBackground(new Color(0xFFFF55));
                        break;
                    case WHITE:
                        setBackground(new Color(0xFFFFFF));
                        break;
                    default:
                        setBackground(new Color(0xFFFFFF));
                        break;
                }

                setText(((TextFormatting) value).getFriendlyName());
            }
            else {
                setText(value.toString());
            }
            b = false;
            return this;
        }
    }
}

