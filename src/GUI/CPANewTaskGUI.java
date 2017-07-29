package GUI;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

/**
 * Represents the GUI component for a new Task
 * @author gorosgobe
 */
public class CPANewTaskGUI extends JFrame implements ActionListener {

    /* Text fields used in the GUI */
    private CPATextField taskNameField;
    private CPATextField startingTimeField;
    private CPATextField durationField;
    /*
    JLabel status provides a message for the user telling them to fill in the fields.
    In case they don't fill in one of them, it reminds them that they must fill all the fields.
    */
    private JLabel status;
    private JButton createButton;
    

    /* Strings used in the GUI*/
    private static final String TASK_STRING = "Task name";
    private static final String STARTING_TIME_STRING = "Start time";
    private static final String DURATION_STRING = "Duration";
    private static final String NEW_TASK_TITLE = "Create new task";
    private static final String INITIAL_MESSAGE = "Input task name, start time and duration";
    private static final String CREATE_BUTTON_MESSAGE = "Create";

    /*Default fields*/
    private static final int DEFAULT_COLUMNSIZE = 20;
    private static final int DEFAULT_WIDTH = 650;
    private static final int DEFAULT_HEIGHT = 400;
    private static final Color DEFAULT_COLOR = new Color(20, 140, 5);

      /*
        textFieldColumnWidth is an int representing the number of columns in the textfield,
        required for layout purposes. Can be substituted with a private static final variable.
      */

    /**
     * Initialises the CPANewTaskGUI with DEFAULT_COLUMNSIZE for the CPATextField fields. See
     * constructor with one argument for more details
     */
    public CPANewTaskGUI() {
        this(DEFAULT_COLUMNSIZE);
    }

    /**
     * Initialises the CPATextFields with the given argument, blocks resizability, sets a preferred size
     * given by defaults DEFAULT_WIDTH and DEFAULT_HEIGHT, sets the background color to the default color
     * DEFAULT_COLOR, organises the layout of the GUI and adds the components to the content pane of the frame
     * and finally adds copy, cut and paste functionality for the text fields
     *
     * @param textFieldColumnWidth column width of the text fields within the new task GUI
     */
    public CPANewTaskGUI(int textFieldColumnWidth) {

        super();
        //initialises text fields and column width
        setTextFields(textFieldColumnWidth);
        setButton();
        //sets the default button so it is fired when enter is pressed
        this.getRootPane().setDefaultButton(getCreateButton());
        //set resizability
        setResizable(false);

        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setBackground(DEFAULT_COLOR);

        // Sets a gridbag layout for the new Task and adds components
        setCustomGridBagLayout();
        //adds copy, cut and paste functionality to text fields
        createActions();
    }

    /**
     * Initialises and sets the buttons string, action command and font to defaults
     */
    private void setButton() {

        createButton = new JButton(CREATE_BUTTON_MESSAGE);
        createButton.setActionCommand(CREATE_BUTTON_MESSAGE);
        createButton.setFont(FontCollection.DEFAULT_FONT_PLAIN);
        //adds the frame as an action listener to the button
        createButton.addActionListener(this);

    }

    /**
     * Creates a GridBag Layout and organises the different components of the new task GUI. This includes Labels,
     * TextFields and Buttons.
     */
    private void setCustomGridBagLayout() {

        getContentPane().setLayout(new GridBagLayout());
        //Create external margins (Insets objects)
        Insets defaultInsets = new Insets(0, 10, 10, 10);


        // constraints for task name text field
        GridBagConstraints taskNameConstraints =
                createConstraints(GridBagConstraints.NONE, 1, 1, defaultInsets);
        //adds task name text field
        getContentPane().add(getTaskNameField(), taskNameConstraints);

        //constraints for task label
        GridBagConstraints taskLabelConstraints =
                createConstraints(GridBagConstraints.NONE, 0, 1, defaultInsets);
        //sets label for task name field
        JLabel taskLabel = new JLabel(TASK_STRING + ":");
        taskLabel.setLabelFor(taskNameField);
        taskLabel.setFont(FontCollection.DEFAULT_FONT_PLAIN);
        //adds task label
        getContentPane().add(taskLabel, taskLabelConstraints);


        //constraints for starting time text field
        GridBagConstraints startingTimeConstraints =
                createConstraints(GridBagConstraints.NONE, 1, 2, defaultInsets);
        //adds starting time text field
        getContentPane().add(getStartingTimeField(), startingTimeConstraints);

        //constraints for starting time label
        GridBagConstraints startingTimeLabelConstraints =
                createConstraints(GridBagConstraints.NONE, 0, 2, defaultInsets);
        //sets label for starting time field
        JLabel startingTimeLabel = new JLabel(STARTING_TIME_STRING + ":");
        startingTimeLabel.setLabelFor(startingTimeField);
        startingTimeLabel.setFont(FontCollection.DEFAULT_FONT_PLAIN);
        //adds starting time label
        getContentPane().add(startingTimeLabel, startingTimeLabelConstraints);


        //constraints for duration time text field
        GridBagConstraints durationConstraints =
                createConstraints(GridBagConstraints.NONE, 1, 3, defaultInsets);
        //adds duration time text field
        getContentPane().add(getDurationField(), durationConstraints);

        //constrains for duration time label
        GridBagConstraints durationLabelConstraints =
                createConstraints(GridBagConstraints.NONE, 0, 3, defaultInsets);
        //sets label for duration field
        JLabel durationLabel = new JLabel(DURATION_STRING + ":");
        durationLabel.setLabelFor(durationField);
        durationLabel.setFont(FontCollection.DEFAULT_FONT_PLAIN);
        //adds duration time label
        getContentPane().add(durationLabel, durationLabelConstraints);


        //constraints for status
        GridBagConstraints statusConstraints =
                createConstraints(GridBagConstraints.NONE, 0, 4, 2, defaultInsets);
        //adds status label
        getContentPane().add(getStatus(), statusConstraints);

        //constraints for create button
        GridBagConstraints createButtonConstraints =
                createConstraints(GridBagConstraints.NONE, 1, 5, defaultInsets);
        //adds button
        getContentPane().add(getCreateButton(), createButtonConstraints);

    }

    /**
     * Creates GridBagConstraints for the given arguments
     *
     * @param constraintFill type of fill for the contraints
     * @param gridx column of the component
     * @param gridy row of the component
     * @param insets margins for the component
     * @return the GridBagConstraints object for the specified arguments
     */
    private GridBagConstraints createConstraints(int constraintFill, int gridx,
                                                 int gridy, Insets insets) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = constraintFill;
        constraints.gridx = gridx;
        constraints.gridy = gridy;

        //only add margins if not null, else keep to default
        if (insets != null) {
            constraints.insets = insets;
        }

        return constraints;
    }


    /**
     * Creates GridBagConstraints for the given arguments
     *
     * @param constraintFill type of fill for the contraints
     * @param gridx column of the component
     * @param gridy row of the component
     * @param gridwidth width of the component (number of columns spanned)
     * @param insets margins for the component
     * @return the GridBagConstraints object for the specified arguments
     */
    private GridBagConstraints createConstraints(int constraintFill, int gridx,
                                                 int gridy, int gridwidth, Insets insets) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = constraintFill;
        constraints.gridx = gridx;
        constraints.gridy = gridy;

        //only add gridwidth if non negative, else keep to default
        if (gridwidth >= 0) {
            constraints.gridwidth = gridwidth;
        }

        //only add margins if not null, else keep to default
        if (insets != null) {
            constraints.insets = insets;
        }

        return constraints;
    }

    /**
     * Initialises the CPATextField objects of the new task GUI.
     * @param textFieldColumnWidth column width of the text fields within the new task GUI
     */
    private void setTextFields(int textFieldColumnWidth) {

        //creates and initialises task name text field
        this.taskNameField = new CPATextField(textFieldColumnWidth);
        taskNameField.setPreferredSize(new Dimension(150, 20));
        taskNameField.setActionCommand(TASK_STRING);
        taskNameField.setFont(FontCollection.DEFAULT_FONT_PLAIN);

        //creates and initialises starting time text field
        this.startingTimeField = new CPATextField(textFieldColumnWidth);
        startingTimeField.setActionCommand(STARTING_TIME_STRING);
        startingTimeField.setActionCommand(STARTING_TIME_STRING);
        startingTimeField.setFont(FontCollection.DEFAULT_FONT_PLAIN);

        this.durationField = new CPATextField(textFieldColumnWidth);
        durationField.setActionCommand(DURATION_STRING);
        durationField.setActionCommand(DURATION_STRING);
        durationField.setFont(FontCollection.DEFAULT_FONT_PLAIN);

        this.status = new JLabel(INITIAL_MESSAGE, SwingConstants.LEFT);
        status.setFont(FontCollection.DEFAULT_FONT_PLAIN);
    }

    /**
     * Associates CTRL+C, CTRL+V, CTRL+X to copy, paste and cut functions for every text field
     */
    private void createActions() {

      //copy operations
      createActionForTextFields(KeyEvent.VK_C, new DefaultEditorKit.CopyAction());

      //cut operations
      createActionForTextFields(KeyEvent.VK_X, new DefaultEditorKit.CutAction());

      //paste operations
      createActionForTextFields(KeyEvent.VK_V, new DefaultEditorKit.PasteAction());

    }

    /**
     * Associates a key pressed given by its integer representation with the action given and adds it to
     * every text field
     * @param keyInt the integer representing the key pressed (i.e. KeyEvent.VK_C)
     * @param action the action to associate (i.e. new DefaultEditorKit.PasteAction())
     */
    private void createActionForTextFields(int keyInt, Action action) {
        taskNameField.supportControlAction(keyInt, action);
        startingTimeField.supportControlAction(keyInt, action);
        durationField.supportControlAction(keyInt, action);
    }

    /**
     * Sets the title and close operations of the GUI and shows it
     */
    public void createAndShowNewTaskGUI() {
        //pre: Frame has been initialised before in the constructor

        setTitle(NEW_TASK_TITLE);
        //DISPOSE_ON_CLOSE so the whole application doesn't close
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //sets up frame
        pack();
        setVisible(true);

    }

    /**
     * Overriden method for implementation of ActionListener interface.
     * @param actionEvent the event
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        //only action supported currently is from button

        String labelText = generateCompleteString(isTextEmpty(getTaskNameField()),
                isTextEmpty(getStartingTimeField()), isTextEmpty(getDurationField()));

        if (labelText.equals("")) {
            //No error so all fields are complete
            /*
            TODO: Generate event and save it in the GUI
            */

            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

        }

        getStatus().setText(labelText);

    }

    /**
     * Generates a custom string for the error message shown to the user, of
     * the form "Complete Task name, Start time and Duration fields." depending on the number of fields to complete.
     * @param taskEmpty is the task name field empty
     * @param startTimeEmpty is the starting time field empty
     * @param durationEmpty is the duration field empty
     * @return the string generated taking the arguments given into account
     */
    public static String generateCompleteString(boolean taskEmpty, boolean startTimeEmpty, boolean durationEmpty) {

        int count = howManyTrue(taskEmpty, startTimeEmpty, durationEmpty);

        if (count == 1) {
            String addition;

            if (taskEmpty) {
                addition = TASK_STRING;
            } else if (startTimeEmpty) {
                addition = STARTING_TIME_STRING;
            } else {
                addition = DURATION_STRING;
            }

            return "Complete " + addition + " field.";
        }

        if (count == 2) {
            String addition1, addition2;

            if (taskEmpty) {
                addition1 = TASK_STRING;

                if (startTimeEmpty) {
                    addition2 = STARTING_TIME_STRING;
                } else {
                    addition2 = DURATION_STRING;
                }

                return "Complete " + addition1 + " and " + addition2 + " fields.";
            } else {
                return "Complete " + STARTING_TIME_STRING + " and " + DURATION_STRING + " fields.";
            }
        }

        if (count == 3) {
            return "Complete " + TASK_STRING + ", " + STARTING_TIME_STRING + " and " + DURATION_STRING + " fields.";
        }

        return "";
    }

    /**
     * Returns the number of arguments that are true
     * @param taskEmpty is the task name field empty
     * @param startTimeEmpty is the starting time field empty
     * @param durationEmpty is the duration field empty
     * @return the number of arguments that are true
     */
    private static int howManyTrue(boolean taskEmpty, boolean startTimeEmpty, boolean durationEmpty) {

        int count = 0;

        if (taskEmpty) {
            count++;
        }

        if (startTimeEmpty) {
            count++;
        }

        if (durationEmpty) {
            count++;
        }

        return count;
    }

    private boolean isTextEmpty(CPATextField textField) {
        return textField.getText().equals("");
    }

    /**
     * Gets the taskName textField
     * @return the CPATextField object representing the task name field
     */
    public CPATextField getTaskNameField() {
        return taskNameField;
    }

    /**
     * Gets the startingTime textField
     * @return the CPATextField object representing the starting time field
     */
    public CPATextField getStartingTimeField() {
        return startingTimeField;
    }

    /**
     * Gets the duration textField
     * @return the CPATextField object representing the duration field
     */
    public CPATextField getDurationField() {
        return durationField;
    }

    /**
     * Gets the status label
     * @return the JLabel object representing the status field
     */
    public JLabel getStatus() {
        return status;
    }

    /**
     * Gets the create button
     * @return the JButton object representing the create button
     */
    public JButton getCreateButton() {
        return createButton;
    }

}
