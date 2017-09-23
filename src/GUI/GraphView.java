package GUI;


import application.*;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class GraphView extends JFrame implements ActionListener {

    private OverallTask task;
    private Action action;
    private mxGraph graph;
    private mxGraphComponent graphComponent;
    private Map<String, Task> idToTask;
    private int id = 0;

    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 40;
    private static final int VERTICAL_SCROLL_SPEED = 18;
    private static final int HORIZONTAL_SCROLL_SPEED = 18;

    public GraphView(OverallTask task, Action action) {
        super("Graph View");

        this.task = task;
        this.action = action;
        this.idToTask = new HashMap<>();
        this.graph = new mxGraph();
        Object parent = graph.getDefaultParent();


        graph.setCellsEditable(false);
        graph.setCellsMovable(false);
        graph.setCellsResizable(false);
        graph.setCellsSelectable(false);
        graph.setConnectableEdges(false);

        //sets up the graph component and draws the graph
        this.graphComponent = insertAndDrawAllTasks(parent, task);
        graphComponent.setPreferredSize(graphComponent.getPreferredSize());
        JPanel panel = new JPanel();
        panel.add(graphComponent);
        panel.setPreferredSize(panel.getPreferredSize());
        this.setLayout(new GridBagLayout());
        setCustomLayout(panel);

    }

    private void setCustomLayout(JPanel panel) {
        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.gridx = 0;
        panelConstraints.gridy = 0;
        panelConstraints.weightx = 1;
        panelConstraints.weighty = 1;
        panelConstraints.fill = GridBagConstraints.BOTH;
        add(panel, panelConstraints);
    }

    public mxGraph getGraph() {
        return graph;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    private mxGraphComponent insertAndDrawAllTasks(Object parent, OverallTask task) {

        Map<SubTask, Object> subTaskToNode = new HashMap<>();

        graph.getModel().beginUpdate();

        try {

            Object overallTaskNode = createAndInsertVertex(parent, task);

            for (SubTask child : task.getAllSubTasks()) {
                //insert first child
                Object childNode = createAndInsertVertex(parent, child);
                //connect the overall task with its children
                insertEdge(parent, overallTaskNode, childNode);
                //child node has been added
                subTaskToNode.put(child, childNode);
                //insert all children in each of the branches
                insertRecursivelyIntoGraph(parent, child, childNode, subTaskToNode);
            }

        } finally {
            graph.getModel().endUpdate();
        }

        //adds the graph to the frame
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.setConnectable(false);
        graphComponent.getVerticalScrollBar().setUnitIncrement(VERTICAL_SCROLL_SPEED);
        graphComponent.getHorizontalScrollBar().setUnitIncrement(HORIZONTAL_SCROLL_SPEED);

        //sets up the hierarchical layout
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.setOrientation(SwingConstants.WEST);
        layout.execute(parent);

        return graphComponent;
    }

    private void insertRecursivelyIntoGraph(Object parent, SubTask t, Object parentNode, Map<SubTask, Object> subTaskToNode) {
        //base case
        if (t.getDependencies().isEmpty()) {
            return;
        }

        for (SubTask child : t.getDependencies()) {

            if (subTaskToNode.keySet().contains(child)) {
                //child has alread been added, no need to draw another object, just the edge from the parent task to
                // the child

                //in the case when there is alread an edge between parentNode and the childNode, then dont draw the edge
                //this solves the case when a child node is hit multiple times by an edge from a parent node due to the
                //parent node being hit by different edges from differen upper nodes
                if (graph.getEdgesBetween(parentNode, subTaskToNode.get(child)).length == 0) {
                    //no edges then insert edge
                    insertEdge(parent, parentNode, subTaskToNode.get(child));
                    insertRecursivelyIntoGraph(parent, child, subTaskToNode.get(child), subTaskToNode);
                    continue;
                } else {
                    //avoids duplication of edges
                    insertRecursivelyIntoGraph(parent, child, subTaskToNode.get(child), subTaskToNode);
                    continue;
                }
            }

            //child has not been added
            Object childNode = createAndInsertVertex(parent, child);
            insertEdge(parent, parentNode, childNode);
            //add child
            subTaskToNode.put(child, childNode);
            //recurse
            insertRecursivelyIntoGraph(parent, child, childNode, subTaskToNode);
        }

    }

    public Object createAndInsertVertex(Object parent, OverallTask task) {
        //precondition is that id == 0
        idToTask.put(String.valueOf(id), task);
        //increments id after putting the overall task into the map
        return graph.insertVertex(parent, String.valueOf(++id), task.getTaskName(), 0, 0,
                DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public Object createAndInsertVertex(Object parent, SubTask subTask) {
        idToTask.put(String.valueOf(id), subTask);
        Object vertex = graph.insertVertex(parent, String.valueOf(++id), subTask.getTaskName(), 0, 0,
                DEFAULT_WIDTH, DEFAULT_HEIGHT);
        return vertex;
    }

    public SubTask getSubTask(String id) {
        if (Integer.parseInt(id) == 0) {
            throw new IllegalArgumentException("ID must not be 0, use getOverallTask()");
        }
        return (SubTask) idToTask.get(id);
    }

    public OverallTask getOverallTask() {
        return task;
    }

    public void insertEdge(Object parent, Object node1, Object node2) {
       graph.insertEdge(parent, String.valueOf(++id), null, node1, node2);
    }


    public static OverallTask createTest() {
        Duration dummyDuration = new Duration(0, 10);
        Time dummyTime = new Time(0, 10);

        OverallTask overallTask = new OverallTask("A", dummyDuration, dummyTime);

        SubTask B = new SubTask("B", dummyDuration);
        SubTask C = new SubTask("C", dummyDuration);
        SubTask D = new SubTask("D", dummyDuration);
        SubTask E = new SubTask("E", dummyDuration);
        SubTask F = new SubTask("F", dummyDuration);
        SubTask G = new SubTask("G", dummyDuration);
        SubTask H = new SubTask("H", dummyDuration);
        SubTask I = new SubTask("I", dummyDuration);
        SubTask J = new SubTask("J", dummyDuration);
        SubTask K = new SubTask("K", dummyDuration);
        SubTask L = new SubTask("L", dummyDuration);

        overallTask.addSubTask(B);
        overallTask.addSubTask(C);
        overallTask.addSubTask(D);
        overallTask.addSubTask(E);

        B.addDependency(F);
        C.addDependency(F);
        D.addDependency(F);
        D.addDependency(I);
        D.addDependency(K);
        E.addDependency(G);
        F.addDependency(H);
        F.addDependency(I);
        H.addDependency(J);
        I.addDependency(J);
        I.addDependency(H);
        J.addDependency(K);
        G.addDependency(K);
        G.addDependency(L);

        
        return overallTask;
    }

    public static void main(String[] args) {


        GraphView frame = new GraphView(createTest(), null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
    }
}
