package com.mml.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import com.intellij.openapi.wm.impl.IdeFrameImpl;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mml.plugin.constants.Constants;
import com.mml.plugin.constants.TaskType;
import com.mml.plugin.remote.resp.GitInfo;
import com.mml.plugin.utils.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static com.mml.plugin.constants.Constants.GITTOKENKEY;
import static com.mml.plugin.constants.Constants.GitURlKEY;

public class SimpleDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    public JTextField scanKey;
    public JRadioButton fileMask;
    public JComboBox filecomboBox;
    public JTextField gitlabhost;
    public JTextField token;
    public JTextField usrid;
    public JComboBox groupComboBox;
    public JComboBox projectComboBox;
    public JRadioButton SpecialRadioButton;
    public JLabel project;
    private ActionListener actionListener;
    private String fileType = "*.*";

    public SimpleDialog(ActionListener runTaskListener) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        actionListener = runTaskListener;
        gitlabhost.setText(FileUtil.INSTANCE.getConfigInfo(GitURlKEY));
        token.setText(FileUtil.INSTANCE.getConfigInfo(GITTOKENKEY));

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (gitlabhost.getText() !=null && !gitlabhost.getText().isEmpty()) {
                    FileUtil.INSTANCE.saveConfigInfo(GitURlKEY, gitlabhost.getText());
                }
                if (token.getText() != null && !token.getText().isEmpty()) {
                    FileUtil.INSTANCE.saveConfigInfo(GITTOKENKEY, token.getText());
                }
//                if (!StringUtils.INSTANCE.isEmpty(getScanKey()) &&
//                        !StringUtils.INSTANCE.isEmpty(getGitlabHost()) &&
//                        !StringUtils.INSTANCE.isEmpty(getGitlabToken())) {
                    if (actionListener != null) {
                        actionListener.actionPerformed(new ActionEvent(e, TaskType.runTask.ordinal(), "runTask")); //执行扫描操作
                    }
//                    dispose();
//                }
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        filecomboBox.setEnabled(false);
        fileMask.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (fileMask.isSelected()) {
                    filecomboBox.setEnabled(true);
                } else {
                    filecomboBox.setEnabled(false);
                    filecomboBox.setSelectedIndex(-1);
                }
            }
        });
        filecomboBox.addItemListener(itemEvent -> fileType = (String) filecomboBox.getSelectedItem());
        SpecialRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    if (actionListener != null) {
                        actionListener.actionPerformed(new ActionEvent(Constants.SELECTED, TaskType.selectedSpecialModule.ordinal(), "SelectedSpecialModule"));
                    }
                    groupComboBox.removeAllItems();
                } else if (itemEvent.getStateChange() == ItemEvent.DESELECTED){
                    groupComboBox.setEnabled(false);
                    projectComboBox.setEnabled(false);
                    if (actionListener != null) {
                        actionListener.actionPerformed(new ActionEvent(Constants.UNSELECTED, TaskType.selectedSpecialModule.ordinal(), "SelectedSpecialModule"));
                    }
                }
            }
        });


        groupComboBox.setEnabled(false);
        groupComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                int index = groupComboBox.getSelectedIndex();
                actionListener.actionPerformed(new ActionEvent(
                        index, TaskType.selectedSpecialGroup.ordinal(), "groupSelected"));
                projectComboBox.removeAllItems();
            }
        });

        projectComboBox.setEnabled(false);
        projectComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                int index = projectComboBox.getSelectedIndex();
                actionListener.actionPerformed(new ActionEvent(
                        index, TaskType.selectedSpecialPrpject.ordinal(), "projectSelected"));
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public String getScanKey() {
        return scanKey.getText();
    }

    public String getGitlabHost() {
        return gitlabhost.getText();
    }

    public String getGitlabToken() {
        return token.getText();
    }

    public String getGitlabUsrId() {
        return usrid.getText();
    }

    /**
     * 获取选中的文件类型
     */
    public String getFileType() {
        return fileType == null || fileType.isEmpty() ? "*.*" : fileType;
    }

    /**
     * 设置文件尾缀list
     */
    public void setFileType(ArrayList<String> fileTypeList) {
        filecomboBox.removeAllItems();
        for (String s : fileTypeList) {
            filecomboBox.addItem(s);
        }
        filecomboBox.setEnabled(false);
        filecomboBox.setSelectedIndex(-1);
    }

    public void setGitGroupList(ArrayList<GitInfo> gitGroupList) {
        groupComboBox.removeAllItems();
        for (GitInfo gitInfo : gitGroupList) {
            groupComboBox.addItem(gitInfo.getName());
        }
        groupComboBox.setEnabled(true);
        groupComboBox.setSelectedIndex(0);
    }

    public void setGitProjectList(ArrayList<GitInfo> gitProjectList) {
        projectComboBox.removeAllItems();
        for (GitInfo gitInfo : gitProjectList) {
            projectComboBox.addItem(gitInfo.getName());
        }
        projectComboBox.setEnabled(true);
        projectComboBox.setSelectedIndex(0);
    }

    private Window getParentWindow(Project project) {
        WindowManagerEx windowManager = (WindowManagerEx) WindowManager.getInstance();

        Window window = windowManager.suggestParentWindow(project);
        if (window == null) {
            Window focusedWindow = windowManager.getMostRecentFocusedWindow();
            if (focusedWindow instanceof IdeFrameImpl) {
                window = focusedWindow;
            }
        }
        return window;
    }

    public void setCenter(Project project) {
        this.setLocationRelativeTo(getParentWindow(project));
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(4, 2, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setMinimumSize(new Dimension(600, 100));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel1.add(buttonOK, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("扫描关键字");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scanKey = new JTextField();
        panel3.add(scanKey, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        fileMask = new JRadioButton();
        fileMask.setText("文件类型");
        panel3.add(fileMask, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filecomboBox = new JComboBox();
        panel3.add(filecomboBox, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("GItlab域名");
        panel4.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        gitlabhost = new JTextField();
        panel4.add(gitlabhost, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("usrId");
        panel5.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        usrid = new JTextField();
        panel5.add(usrid, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel6, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Gitlab token");
        panel6.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        token = new JTextField();
        panel6.add(token, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
