package GUI;

import application.OverallTask;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;

import static GUI.TaskGUI.DEFAULT_INSETS;

/**
 * Class representing the GUI used to select a dependency that will later be edited.
 *
 * @author gorosgobe
 */
public class SelectEditDependencyGUI extends AbstractSelectDependency {

    /** The title of the frame*/
    private static final String FRAME_TITLE = "Select Dependency";
    /** String representing the string on the select button*/
    private static final String SELECT_BUTTON_STRING = "Select";
    /** String representing the string on the cancel button*/
    private static final String CANCEL_BUTTON_STRING = "Cancel";
    /** String representing the string on the label*/
    private static final String SELECT_DEPENDENCY_MESSAGE = "Select dependency";

    public SelectEditDependencyGUI(TaskDataPanel taskDataPanel, OverallTask task) {
        super(taskDataPanel, task, SELECT_DEPENDENCY_MESSAGE);
        setTitle(FRAME_TITLE);

        JButton selectButton = LayoutUtils.setButton(SELECT_BUTTON_STRING, this);
        JButton cancelButton = LayoutUtils.setButton(CANCEL_BUTTON_STRING, this);
        setCustomLayout(selectButton, cancelButton);
    }

    /**
     * Sets the custom layout
     * @param selectButton the select button
     * @param cancelButton the cancel button
     */
    private void setCustomLayout(JButton selectButton, JButton cancelButton) {
        //creates a panel for both buttons
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(selectButton);
        panel.add(cancelButton);
        GridBagConstraints buttonsConstraints = LayoutUtils.createConstraints(1,3, DEFAULT_INSETS,
                GridBagConstraints.LAST_LINE_END);
        add(panel, buttonsConstraints);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case SELECT_BUTTON_STRING: {
                //if the selected task is the overall task, give warning to user and dont do anything
                if (getSelectedNode().getText().equals(getTask().getTaskName())) {
                    MessageGUI messageGUI = new MessageGUI("Warning", "The task that you have selected ("
                            + getTask().getTaskName() + ") is not a dependency. Please choose a dependency instead.");
                    javax.swing.SwingUtilities.invokeLater(messageGUI::createAndShowGUI);
                    break;
                }

                break;
            }
            case CANCEL_BUTTON_STRING: {
                this.close();
            }
        }

    }

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {

    }
}