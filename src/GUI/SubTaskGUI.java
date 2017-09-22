package GUI;

import application.Duration;
import application.OverallTask;
import application.SubTask;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static application.SubTask.findSubTaskInDependencies;

/**
 * GUI representing an application.SubTask
 *
 * @author gorosgobe
 */
public class SubTaskGUI extends TaskGUI implements ActionListener, TreeSelectionListener {

    /** A reference to the dropdown label*/
    private JLabel dropdownLabel;
    /** A reference to the main task dropdown*/
    private JComboBox taskDropdown;
    /** A reference to a Label with a string indicating which node was selected*/
    private JLabel selectedLabel;
    /** A reference to the selected node in the tree, representing a subtask to add a dependency to*/
    private JLabel selectedNode;
    /** A reference to the scroll pane with the tree view*/
    private JScrollPane treePanel;
    /** A reference to the tree view*/
    private JTree tree;
    /** A reference to the create button*/
    private JButton button;
    /** A reference to a mapping from Strings to all OverallTasks*/
    private Map<String, OverallTask> stringTaskMap;

    /** String representing the frame title*/
    private static final String FRAME_TITLE = "Create New Subtask";
    /** String representing the string on the button*/
    private static final String BUTTON_STRING = "Create";
    /** String representing the string on the label*/
    private static final String LABEL_MESSAGE = "Select main task";
    /** String representing the task dropdown message*/
    private static final String TASK_DROPDOWN_STRING = "Task dropdown";
    /** String representing the selected node in the tree view (subTask)*/
    private static final String SELECTED_NODE_STRING = "Add as dependency to";


    /**
     * Constructs a subTaskGUI.
     * @param tasksToShow the list of all OverallTasks held in the GUI
     */
    public SubTaskGUI(List<OverallTask> tasksToShow) {
        //List given must be of ALL Overall tasks
        super();
        setTitle(FRAME_TITLE);
        setStringTaskMap(tasksToShow);
        setDropdownLabel();
        setJComboBox(tasksToShow);
        setSelectedLabelAndSelectedNode();
        OverallTask overallTask = stringTaskMap.get(taskDropdown.getSelectedItem());
        setTreeView(overallTask);
        setScrollPane();
        setButton();
        setSubTaskLayout();
    }

    /**
     * Constructs a new subtaskGUI, with the OverallTask parameter being the one selected by default in the task
     * dropdown
     * @param tasksToShow the list of all OverallTasks held in the GUI
     * @param toBeSelectedTask the Overall Task selected by default
     */
    public SubTaskGUI(List<OverallTask> tasksToShow, OverallTask toBeSelectedTask) {
        //List given must be of ALL Overall tasks
        super();
        setTitle(FRAME_TITLE);
        setStringTaskMap(tasksToShow);
        setDropdownLabel();
        setJComboBox(tasksToShow, toBeSelectedTask.getTaskName());
        setSelectedLabelAndSelectedNode();
        OverallTask overallTask = stringTaskMap.get(taskDropdown.getSelectedItem());
        setTreeView(overallTask);
        setScrollPane();
        setButton();
        setSubTaskLayout();
    }

    /**
     * Initialises the string to overall task map used by the JComboBox
     * @param tasksToShow the list of OverallTasks
     */
    public void setStringTaskMap(List<OverallTask> tasksToShow) {
        this.stringTaskMap = new HashMap<>();
        for (OverallTask t : tasksToShow) {
            stringTaskMap.put(t.getTaskName(), t);
        }
    }

    /**
     * Initialises and sets the dropdown label
     */
    private void setDropdownLabel() {
        this.dropdownLabel = new JLabel(LABEL_MESSAGE + ":");
        dropdownLabel.setLabelFor(taskDropdown);
        dropdownLabel.setFont(FontCollection.DEFAULT_FONT_PLAIN);
    }

    /**
     * Initialises and sets the task dropdown JComboBox
     * @param tasksToShow the list of overall tasks to show in the dropdown
     */
    private void setJComboBox(List<OverallTask> tasksToShow) {
        this.taskDropdown = new JComboBox<>(stringTaskMap.keySet().toArray());
        taskDropdown.setSelectedIndex(0);
        taskDropdown.setEditable(true);
        taskDropdown.addActionListener(this);
        taskDropdown.setActionCommand(TASK_DROPDOWN_STRING);
        taskDropdown.setFont(FontCollection.DEFAULT_FONT_PLAIN);
    }

    /**
     * Initialises and sets the task dropdown JComboBox
     * @param tasksToShow the list of overall tasks to show in the dropdown
     * @param selectedTaskName the name of the task selected by default
     */
    private void setJComboBox(List<OverallTask> tasksToShow, String selectedTaskName) {
        this.taskDropdown = new JComboBox<>(stringTaskMap.keySet().toArray());
        taskDropdown.setSelectedItem(selectedTaskName);
        taskDropdown.setEditable(true);
        taskDropdown.addActionListener(this);
        taskDropdown.setActionCommand(TASK_DROPDOWN_STRING);
        taskDropdown.setFont(FontCollection.DEFAULT_FONT_PLAIN);
    }

    /**
     * Sets the labels representing the text informing the user about selection of a node and the text representation of
     * the node selected.
     */
    private void setSelectedLabelAndSelectedNode() {

        this.selectedLabel = new JLabel(SELECTED_NODE_STRING + ":");
        selectedLabel.setFont(FontCollection.DEFAULT_FONT_PLAIN);
        //the selectedNode text will be determined by the selection of a particular node in the tree view
        //initial is default by task dropdown
        this.selectedNode = new JLabel((String) taskDropdown.getSelectedItem());
        selectedNode.setFont(FontCollection.DEFAULT_FONT_PLAIN);

    }

    /**
     * Sets the JTree to be a representation of all dependencies of the given OverallTask.
     * @param overallTask the task to represent in a tree view (root task)
     */
    private void setTreeView(OverallTask overallTask) {
        //sets root with selected item
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(overallTask.getTaskName());

        for (SubTask task : overallTask.getAllSubTasks()) {
            root.add(setTreeRecursivelyFrom(task));
        }

        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        this.tree = new JTree(treeModel);
        //allows for single selection
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        //adds frame as listener
        tree.addTreeSelectionListener(this);
        //selects first node (OverallTask) by default
        DefaultMutableTreeNode overallNode = (DefaultMutableTreeNode) treeModel.getRoot();
        tree.setSelectionPath(new TreePath(overallNode.getPath()));
    }

    /**
     * Recursive helper for setTreeView. Sets the tree view (DefaultMutableTreeNode) for the given subtask
     * and its dependencies.
     * @param task the subtask to construct a node from
     * @return the constructed tree node from the task given
     */
    private DefaultMutableTreeNode setTreeRecursivelyFrom(SubTask task) {

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(task.getTaskName());

        //base case
        if (task.getDependencies() == null) {
            return new DefaultMutableTreeNode(task.getTaskName());
        }

        //set the tree recursively for each subtask
        for (SubTask t : task.getDependencies()) {
            node.add(setTreeRecursivelyFrom(t));
        }

        return node;

    }

    /**
     * Sets the JScrollPane that contains the tree view.
     */
    private void setScrollPane() {
        //this is done outside of setTreeView because otherwise upon selection of Tasks on the
        //task dropdown JComboBox, the treePanel wouldn't update
        this.treePanel = new JScrollPane(tree);
    }

    /**
     * Sets the create button.
     */
    private void setButton() {

        this.button = new JButton(BUTTON_STRING);
        button.setActionCommand(BUTTON_STRING);
        button.setFont(FontCollection.DEFAULT_FONT_PLAIN);
        //adds the frame as an action listener to the button
        button.addActionListener(this);
        //sets button to be default when pressed enter
        getRootPane().setDefaultButton(button);

    }

    /**
     * Sets the GridBagConstraints for the specific components of the SubTask GUI. For more information on
     * GridBagLayout and GridBagConstraint see the Java tutorials.
     */
    private void setSubTaskLayout() {

        //dropdown label constraints
        GridBagConstraints dropdownLabelConstraints = new GridBagConstraints();
        dropdownLabelConstraints.gridx = 0;
        dropdownLabelConstraints.gridy = 2;
        dropdownLabelConstraints.weightx = 0.5;
        dropdownLabelConstraints.weighty = 0.5;
        dropdownLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
        dropdownLabelConstraints.insets = DEFAULT_INSETS;
        add(dropdownLabel, dropdownLabelConstraints);

        //dropdown JComboBox constraints with tasks
        GridBagConstraints taskDropdownConstraints = new GridBagConstraints();
        taskDropdownConstraints.gridx = 1;
        taskDropdownConstraints.gridy = 2;
        taskDropdownConstraints.weightx = 0.5;
        taskDropdownConstraints.weighty = 0.5;
        taskDropdownConstraints.fill = GridBagConstraints.HORIZONTAL;
        taskDropdownConstraints.insets = DEFAULT_INSETS;
        taskDropdownConstraints.gridwidth = 2;
        add(taskDropdown, taskDropdownConstraints);

        //select label constraints
        GridBagConstraints selectLabelConstraints = new GridBagConstraints();
        selectLabelConstraints.gridx = 0;
        selectLabelConstraints.gridy = 3;
        selectLabelConstraints.weightx = 0.5;
        selectLabelConstraints.weighty = 0.5;
        selectLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
        selectLabelConstraints.insets = DEFAULT_INSETS;
        add(selectedLabel, selectLabelConstraints);

        //selectedNode label constraints
        GridBagConstraints selectedNodeConstraints = new GridBagConstraints();
        selectedNodeConstraints.gridx = 1;
        selectedNodeConstraints.gridy = 3;
        selectedNodeConstraints.weightx = 0.5;
        selectedNodeConstraints.weighty = 0.5;
        selectedNodeConstraints.fill = GridBagConstraints.HORIZONTAL;
        selectedNodeConstraints.insets = DEFAULT_INSETS;
        add(selectedNode, selectedNodeConstraints);

        //scroll pane constraints
        GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
        scrollPaneConstraints.gridx = 0;
        scrollPaneConstraints.gridy = 4;
        scrollPaneConstraints.weightx = 0.5;
        scrollPaneConstraints.weighty = 0.5;
        scrollPaneConstraints.gridwidth = 2;
        scrollPaneConstraints.gridheight = 2;
        //no weightx and weight y as we want it in the middle
        scrollPaneConstraints.fill = GridBagConstraints.BOTH;
        scrollPaneConstraints.insets = DEFAULT_INSETS;
        add(treePanel, scrollPaneConstraints);

        //button constraints
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 1;
        buttonConstraints.gridy = 6;
        buttonConstraints.weightx = 0.5;
        buttonConstraints.weighty = 0.5;
        buttonConstraints.fill = GridBagConstraints.NONE;
        buttonConstraints.anchor = GridBagConstraints.LAST_LINE_END;
        buttonConstraints.insets = DEFAULT_INSETS;
        add(button, buttonConstraints);

    }

    /**
     * Overriden method that responds to events such as the change of selection in the main task dropdown,
     * the selection of a subtask in the tree view or the pressing of the create button.
     * @param actionEvent the event generated
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        switch (actionEvent.getActionCommand()) {
            case TASK_DROPDOWN_STRING:
                //creates new dropdown task's tree
                setTreeView(stringTaskMap.get(taskDropdown.getSelectedItem()));
                //sets the scroll panel's viewport to be the tree
                treePanel.setViewportView(tree);
                treePanel.revalidate();
                return;
        }

        //action from the button
        String taskName = getTaskNameText();
        Duration duration = getDuration();
        if (taskName.equals("") || duration == null) {
            //don't do anything
            return;
        }

        //task is valid
        String associatedOverallTaskString = (String) taskDropdown.getSelectedItem();
        String addToDependencyTask = selectedNode.getText();

        if (associatedOverallTaskString.equals(addToDependencyTask)) {
            // selected item in task dropdown (OverallTask) is equal to the only
            //OverallTask in the tree, therefore we want to add it to an OverallTask

            //get task to add to, in this case overallTask
            OverallTask overallTask = stringTaskMap.get(addToDependencyTask);
            overallTask.addSubTask(new SubTask(taskName, duration));
        } else {
            //task is not same as the one in the dropdown, therefore we want to add it to a SubTask
            OverallTask overallTask = stringTaskMap.get(taskDropdown.getSelectedItem());
            SubTask subTask = findSubTaskInDependencies(overallTask, addToDependencyTask);
            subTask.addDependency(new SubTask(taskName, duration));
        }

        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Overriden method that responds to events such as selection in the JTree shown in the scroll pane.
     * @param treeSelectionEvent the event generated.
     */
    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node == null) {
            //Nothing is selected.
            return;
        }

        selectedNode.setText((String) node.getUserObject());
    }
}
