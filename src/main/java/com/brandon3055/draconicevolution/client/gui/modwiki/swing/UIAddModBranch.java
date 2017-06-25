package com.brandon3055.draconicevolution.client.gui.modwiki.swing;

import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager;
import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import net.minecraft.util.StringUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class UIAddModBranch extends JFrame implements ActionListener {

    private TreeBranchRoot parentBranch;
    private JPanel contentPane;

    private JPanel buttonPanel;
    private JButton cancelButton;
    private JPanel textPane;
    private JLabel idLabel;
    private JTextField idField;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel selectLabel;
    private JComboBox selectorBox;
    private JButton createButton;

    public UIAddModBranch(TreeBranchRoot branch) {
        this.parentBranch = branch;
        setupUI();
        setTitle("Add Mod");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createButton.addActionListener(this);
        cancelButton.addActionListener(this);
        selectorBox.addActionListener(this);
    }

    private void setupUI() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setMaximumSize(new Dimension(600, 145));
        contentPane.setMinimumSize(new Dimension(600, 145));
        contentPane.setPreferredSize(new Dimension(600, 145));
        textPane = new JPanel();
        textPane.setLayout(new GridLayoutManager(5, 3, new Insets(0, 0, 0, 0), -1, -1));
        textPane.setDoubleBuffered(true);
        textPane.setOpaque(true);
        textPane.setVisible(true);
        contentPane.add(textPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, 1, null, null, null, 0, false));
        textPane.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, new Color(-4473925)));
        idLabel = new JLabel();
        idLabel.setText("Mod ID (Lowercase)");
        textPane.add(idLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        idField = new JTextField();
        textPane.add(idField, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        textPane.add(spacer1, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        nameLabel = new JLabel();
        nameLabel.setText("Mod Name");
        textPane.add(nameLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nameField = new JTextField();
        textPane.add(nameField, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        selectLabel = new JLabel();
        selectLabel.setText("Select From Installed");
        textPane.add(selectLabel, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectorBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        selectorBox.setMaximumRowCount(20);

        for (String modid : ModHelper.getLoadedMods().keySet()) {
            defaultComboBoxModel1.addElement(modid);
        }

        selectorBox.setModel(defaultComboBoxModel1);
        textPane.add(selectorBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        textPane.add(spacer2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(buttonPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        buttonPanel.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        createButton = new JButton();
        createButton.setText("Create");
        buttonPanel.add(createButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        buttonPanel.add(cancelButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        contentPane.add(spacer4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

        setContentPane(contentPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("comboBoxChanged")) {
            String selected = selectorBox.getSelectedItem().toString();
            idField.setText(selected.toLowerCase());
            nameField.setText(ModHelper.getLoadedMods().get(selected));
        }
        if (command.equals("Create")) {
            if (WikiDocManager.modDocMap.containsKey(idField.getText())) {
                JOptionPane.showMessageDialog(this, "There is already documentation for that mod! Just add to the existing documentation.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else if (StringUtils.isNullOrEmpty(idField.getText()) || StringUtils.isNullOrEmpty(nameField.getText())) {
                JOptionPane.showMessageDialog(this, "Please fill in both the id and name fields", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {//TODO Implement multi language support
                try {
                    WikiDocManager.createNewModEntry(idField.getText(), nameField.getText(), "en_US");
                    dispose();
                }
                catch (Exception e1) {
                    LogHelper.error("####################################################################################################################################################################################");
                    LogHelper.error("Looks like something went wrong! Error: [" + e1.getMessage() + "]");
                    LogHelper.error("");
                    e1.printStackTrace();
                    LogHelper.error("");
                    LogHelper.error("####################################################################################################################################################################################");
                    String trace = "";
                    int lines = 0;
                    for (Object o : e1.getStackTrace()) {
                        lines++;
                        trace += o.toString() + "\n";
                        if (lines > 5) {
                            trace += "....";
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(this, "An exception occurred when creating the mod xml.\n\nMessage:\n" + e1.getMessage() + "\n\nTrace: (" + e1.getClass().getName() + ")\n" + trace, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        if (command.equals("Cancel")) {
            dispose();
        }
    }
}