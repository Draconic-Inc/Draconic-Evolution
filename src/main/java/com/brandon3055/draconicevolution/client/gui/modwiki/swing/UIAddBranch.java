package com.brandon3055.draconicevolution.client.gui.modwiki.swing;

import com.brandon3055.draconicevolution.client.gui.modwiki.moddata.guidoctree.TreeBranchRoot;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import net.minecraft.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import static com.intellij.uiDesigner.core.GridConstraints.*;

/**
 * Created by brandon3055 on 8/09/2016.
 */
public class UIAddBranch extends JFrame implements ActionListener {

    private final TreeBranchRoot parentBranch;
    private List<String> categories;
    private String parentID;
    private List<String> activeIDs;
    private JPanel contentPane;
    private JPanel buttonPane;
    private JButton cancelButton;
    private JButton createButton;
    private JPanel namePanel;
    private JTextField nameField;
    private JPanel categoryPanel;
    private JTextField categoryField;
    private JComboBox selectCategory;
    private JTextArea categoryDescription;
    private JTextField idField;
    private JLabel idLabel;


    public UIAddBranch(TreeBranchRoot parent, List<String> categories, final String parentID, List<String> activeIDs) {
        this.parentBranch = parent;
        this.categories = categories;
        this.parentID = parentID;
        this.activeIDs = activeIDs;
        setupUI();
        setTitle("Add Branch");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createButton.addActionListener(this);
        cancelButton.addActionListener(this);
        selectCategory.addActionListener(this);
        idLabel.setText(parentID + (parentBranch.isModBranch ? ":" : "/"));
        idField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                idLabel.setText(parentID + (parentBranch.isModBranch ? ":" : "/") + idField.getText());
            }
        });
    }

    private void setupUI() {

        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(5, 1, new Insets(10, 10, 10, 10), -1, -1));
        buttonPane = new JPanel();
        buttonPane.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(buttonPane, new GridConstraints(4, 0, 1, 1, ANCHOR_CENTER, FILL_BOTH, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        buttonPane.add(cancelButton, new GridConstraints(0, 2, 1, 1, ANCHOR_CENTER, FILL_HORIZONTAL, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        buttonPane.add(spacer1, new GridConstraints(0, 0, 1, 1, ANCHOR_CENTER, FILL_HORIZONTAL, SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        createButton = new JButton();
        createButton.setText("Create");
        buttonPane.add(createButton, new GridConstraints(0, 1, 1, 1, ANCHOR_CENTER, FILL_HORIZONTAL, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, SIZEPOLICY_FIXED, null, null, null, 0, false));
        namePanel = new JPanel();
        namePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(namePanel, new GridConstraints(0, 0, 1, 1, ANCHOR_CENTER, FILL_BOTH, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        namePanel.setBorder(BorderFactory.createTitledBorder("Name Branch"));
        nameField = new JTextField();
        namePanel.add(nameField, new GridConstraints(0, 0, 1, 1, ANCHOR_WEST, FILL_HORIZONTAL, SIZEPOLICY_WANT_GROW, SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        categoryPanel = new JPanel();
        categoryPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(categoryPanel, new GridConstraints(2, 0, 1, 1, ANCHOR_CENTER, FILL_BOTH, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        categoryPanel.setBorder(BorderFactory.createTitledBorder("Set Category "));
        categoryField = new JTextField();
        categoryPanel.add(categoryField, new GridConstraints(0, 0, 1, 1, ANCHOR_WEST, FILL_HORIZONTAL, SIZEPOLICY_WANT_GROW, SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        selectCategory = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();

        for (String cat : categories) {
            defaultComboBoxModel1.addElement(cat);
        }

        selectCategory.setModel(defaultComboBoxModel1);
        categoryPanel.add(selectCategory, new GridConstraints(0, 1, 1, 1, ANCHOR_WEST, FILL_HORIZONTAL, SIZEPOLICY_CAN_GROW, SIZEPOLICY_FIXED, null, null, null, 0, false));
        categoryDescription = new JTextArea();
        categoryDescription.setEditable(false);
        categoryDescription.setText("You can select a pre-existing category or create your own.\nNote for items and blocks you should use the built in categories.\nThe built in categories will automaticly be localized if localization is avalible.\nIf localization is not avalible for your language feel free to help out by\nadding or updating the lang file for your language. If you do not know how\nthen ether leave it for someone else or create an issue on github and ask for\nhelp.  ");
        categoryPanel.add(categoryDescription, new GridConstraints(1, 0, 1, 2, ANCHOR_CENTER, FILL_BOTH, SIZEPOLICY_WANT_GROW, SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, ANCHOR_CENTER, FILL_BOTH, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, SIZEPOLICY_CAN_SHRINK | SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder("Branch ID"));
        idField = new JTextField();
        panel1.add(idField, new GridConstraints(1, 0, 1, 1, ANCHOR_WEST, FILL_HORIZONTAL, SIZEPOLICY_WANT_GROW, SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        JTextArea idDescription = new JTextArea();
        idDescription.setEditable(false);
        idDescription.setText("Branch ID is an id that is completely uniqe to this branch.\nIt is made up of a combination of the modid and its parent branch id's \n(if it has parent branches) and the id you specify here. \nThat means the id you specify here does not need to be too special \nas long as it does not conflict with any other id's in this branch. \nFor items and blocks its a good idea to use the item or block's registry name\n(not including modid) You will get an error if the id you choos is already in use.");
        panel1.add(idDescription, new GridConstraints(2, 0, 1, 1, ANCHOR_CENTER, FILL_BOTH, SIZEPOLICY_WANT_GROW, SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        idLabel = new JLabel();
        idLabel.setText("");
        panel1.add(idLabel, new GridConstraints(0, 0, 1, 1, ANCHOR_WEST, FILL_NONE, SIZEPOLICY_FIXED, SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        contentPane.add(spacer2, new GridConstraints(3, 0, 1, 1, ANCHOR_CENTER, FILL_VERTICAL, 1, SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

        setContentPane(contentPane);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("comboBoxChanged")) {
            String selected = selectCategory.getSelectedItem().toString();
            categoryField.setText(selected);
        }

        if (command.equals("Create")) {
            if (StringUtils.isNullOrEmpty(nameField.getText())) {
                JOptionPane.showMessageDialog(this, "The name field can not be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (StringUtils.isNullOrEmpty(idField.getText())) {
                JOptionPane.showMessageDialog(this, "The id field can not be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (activeIDs.contains(parentID + (parentBranch.isModBranch ? ":" : "/") + idField.getText())) {
                JOptionPane.showMessageDialog(this, "That ID is already in use in this branch", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (StringUtils.isNullOrEmpty(categoryField.getText())) {
                int reply = JOptionPane.showOptionDialog(this, "Are you sure you want to leave the category field empty? I wont stop you but you may want to re consider.", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"Yes", "No"}, "No");
                if (reply == 1) {
                    return;
                }
            }

            synchronized (parentBranch) {
                parentBranch.createNewSubBranch(nameField.getText(), idField.getText(), categoryField.getText(), parentBranch);
            }

            dispose();
        }

        if (command.equals("Cancel")) {
            dispose();
        }
    }
}

