package application;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskGraph {

  private TaskGraphNode start;
  private TaskGraphNode end;

  public TaskGraph() {
    this.start = new TaskGraphNode();
    this.end   = new TaskGraphNode();
  }

  /**
   * It proceeds forward and recursively sets the degree of each
   * node to be the number of its incoming edges.
   *
   * @param node initially should be the TaskGraph's start node
   */
  private void setIncomingDegree(TaskGraphNode node) {
    //post: It sets the degree of each node to be equal to the number of
    // incoming edges of that node.

    //set current node degree to number of incoming arcs
    node.setDegree(node.getIncomingArcs().size());
    //recursively set the degree going forward
    node.getOutgoingArcs().forEach(i -> setIncomingDegree(i.getChild()));
  }

  /**
   * This topologicalSort is an implementation of Kahn's Algorithm. It returns
   * a queue of TaskGraphNodes whose FIFO ordering reflects the
   * order in which the earliest completion time for the nodes has to be
   * computed.
   */
  //PRE: Graph is acyclic
  private TopologicalSorts topologicalSort() {
    //post: It returns a queue of TaskGraphNode objects such that a node
    // added in the queue before another node indicates that there is a path
    // from the former node to the latter node in the task graph.

    //initialise degree of each TaskGraphNode
    setIncomingDegree(start);

    //Queue to store all nodes with no incoming edge (zero degree)
    Queue<TaskGraphNode> temp = new ArrayDeque<>();
    //Empty Queue storing sorted elements
    Queue<TaskGraphNode> result = new ArrayDeque<>();

    //all nodes with zero degree placed in temp (only start node initially)
    temp.add(start);
    while (!temp.isEmpty()) {
      TaskGraphNode dequeued = temp.remove();
      result.add(dequeued);
      dequeued.getOutgoingArcs().forEach(i -> {
        //decrement degree of each child by 1
        TaskGraphNode childNode = i.getChild();
        childNode.setDegree(childNode.getDegree() - 1);
        //if updated degree reaches zero, place in temp
        if (childNode.getDegree() == 0) {temp.add(childNode);}
      });
    }

    return null;
  }

  /**
   * It computes the earliest completion time for each node in the order given
   * by the FIFO queue generated by the topological sort.
   */
  private void computeEarliestCompletionTime(Queue<TaskGraphNode>
                                                 sortedNodes) {
    //TODO Naman:
    //post: Sets the earliest completion time for the start node to be zero. Sets the
    //      earliest completion time for each node, different from the start node, to be the
    //      maximum of the sum of the earliest completion time for its precedent node and the
    //      duration of the connecting edge, over all its precedent nodes.
    TaskGraphNode start = sortedNodes.poll();
    start.setEarliestCompletionTime(new Time(0,0));

    while (!sortedNodes.isEmpty())  {
      TaskGraphNode node = sortedNodes.poll();
      List<Time> times = node.getIncomingArcs().stream()
              .map(i -> i.getParent().getEarliestCompletionTime().addDuration(i.getTask().getDuration()))
              .collect(Collectors.toList());

      Time maxTime = Collections.max(times);

      node.setEarliestCompletionTime(maxTime);
    }
  }

  /**
   * It computes the latest completion time for each node
   */
  private void computeLatestCompletionTime(Stack<TaskGraphNode>
                                               sortedNodes) {
    //TODO Naman:
    //post: Sets the latest completion time for each node.
    TaskGraphNode end = sortedNodes.pop();
    end.setLatestCompletionTime(end.getEarliestCompletionTime());

    while (!sortedNodes.isEmpty()) {
      TaskGraphNode node = sortedNodes.pop();
      List<Time> times = node.getOutgoingArcs().stream()
              .map(i -> i.getChild().getLatestCompletionTime().subDuration(i.getTask().getDuration()))
              .collect(Collectors.toList());

      Time minTime = Collections.min(times);

      node.setLatestCompletionTime(minTime);
    }
  }

  public TaskGraphNode getStartNode() {
    return start;
  }

  public TaskGraphNode getEndNode() {
    return end;
  }

  private class TopologicalSorts {
    private Queue<TaskGraphNode> graphTopologicalOrder;
    private Stack<TaskGraphNode> transposeGraphTopologicalOrder;

    public TopologicalSorts(Queue<TaskGraphNode> graphTopologicalOrder, Stack<TaskGraphNode> transposeGraphTopologicalOrder) {
      this.graphTopologicalOrder = graphTopologicalOrder;
      this.transposeGraphTopologicalOrder = transposeGraphTopologicalOrder;
    }

    public Queue<TaskGraphNode> getGraphTopologicalOrder() {
      return graphTopologicalOrder;
    }

    public Stack<TaskGraphNode> getTransposeGraphTopologicalOrder() {
      return transposeGraphTopologicalOrder;
    }
  }

  public static void main(String[] args) {
    //graph for test
    TaskGraph graph = new TaskGraph();

    //nodes for test
    TaskGraphNode e1 = new TaskGraphNode();
    TaskGraphNode e2 = new TaskGraphNode();
    TaskGraphNode e3 = new TaskGraphNode();
    TaskGraphNode e4 = new TaskGraphNode();
    TaskGraphNode e5 = new TaskGraphNode();
    TaskGraphNode e6 = new TaskGraphNode();
    TaskGraphNode e7 = new TaskGraphNode();
    TaskGraphNode e8 = new TaskGraphNode();
    TaskGraphNode e9 = new TaskGraphNode();
    TaskGraphNode e10 = new TaskGraphNode();
    TaskGraphNode e6d = new TaskGraphNode();
    TaskGraphNode e7d = new TaskGraphNode();
    TaskGraphNode e8d = new TaskGraphNode();
    TaskGraphNode e10d = new TaskGraphNode();

    //arcs for test
    TaskGraphArc a = new TaskGraphArc(new SubTask("A", new Duration(3, 0)),
        e1, e2);
    TaskGraphArc b = new TaskGraphArc(new SubTask("B", new Duration(2, 0)),
        e1, e3);
    TaskGraphArc c = new TaskGraphArc(new SubTask("C", new Duration(3, 0)),
        e2, e4);
    TaskGraphArc d = new TaskGraphArc(new SubTask("D", new Duration(2, 0)),
        e6d, e6);
    TaskGraphArc e = new TaskGraphArc(new SubTask("E", new Duration(1, 0)),
        e3, e5);
    TaskGraphArc f = new TaskGraphArc(new SubTask("F", new Duration(3, 0)),
        e7d, e7);
    TaskGraphArc g = new TaskGraphArc(new SubTask("G", new Duration(2, 0)),
        e8d, e8);
    TaskGraphArc h = new TaskGraphArc(new SubTask("H", new Duration(1, 0)),
        e10d, e10);
    TaskGraphArc k = new TaskGraphArc(new SubTask("K", new Duration(4, 0)),
        e5, e9);
    TaskGraphArc e2e6dDummy = new TaskGraphArc(new SubTask("e2e6dDummy", new
        Duration(0, 0)), e2, e6d);
    TaskGraphArc e3e6dDummy = new TaskGraphArc(new SubTask("e3e6dDummy", new
        Duration(0, 0)), e3, e6d);
    TaskGraphArc e4e7dDummy = new TaskGraphArc(new SubTask("e4e7dDummy", new
        Duration(0, 0)), e4, e7d);
    TaskGraphArc e6e7dDummy = new TaskGraphArc(new SubTask("e6e7dDummy", new
        Duration(0, 0)), e6, e7d);
    TaskGraphArc e6e8dDummy = new TaskGraphArc(new SubTask("e6e8dDummy", new
        Duration(0, 0)), e6, e8d);
    TaskGraphArc e5e8dDummy = new TaskGraphArc(new SubTask("e5e8dDummy", new
        Duration(0, 0)), e5, e8d);
    TaskGraphArc e7e10dDummy = new TaskGraphArc(new SubTask("e7e10dDummy", new
        Duration(0, 0)), e7, e10d);
    TaskGraphArc e8e10dDummy = new TaskGraphArc(new SubTask("e8e10dDummy", new
        Duration(0, 0)), e8, e10d);
    TaskGraphArc e9e10dDummy = new TaskGraphArc(new SubTask("e9e10dDummy", new
        Duration(0, 0)), e9, e10d);

    //set incoming for each node
    e2.setIncoming(Stream.of(a).collect(Collectors.toCollection(HashSet::new)));
    e3.setIncoming(Stream.of(b).collect(Collectors.toCollection(HashSet::new)));
    e6d.setIncoming(Stream.of(e2e6dDummy, e3e6dDummy).collect(Collectors.toCollection
        (HashSet::new)));
    e4.setIncoming(Stream.of(c).collect(Collectors.toCollection(HashSet::new)));
    e6.setIncoming(Stream.of(d).collect(Collectors.toCollection(HashSet::new)));
    e5.setIncoming(Stream.of(e).collect(Collectors.toCollection(HashSet::new)));
    e7d.setIncoming(Stream.of(e4e7dDummy, e6e7dDummy).collect(Collectors.toCollection
        (HashSet::new)));
    e8d.setIncoming(Stream.of(e6e8dDummy, e5e8dDummy).collect(Collectors.toCollection
        (HashSet::new)));
    e7.setIncoming(Stream.of(f).collect(Collectors.toCollection
        (HashSet::new)));
    e8.setIncoming(Stream.of(g).collect(Collectors.toCollection(HashSet::new)));
    e9.setIncoming(Stream.of(k).collect(Collectors.toCollection(HashSet::new)));
    e10d.setIncoming(Stream.of(e7e10dDummy, e8e10dDummy, e9e10dDummy).collect(Collectors
        .toCollection
        (HashSet::new)));
    e10.setIncoming(Stream.of(h).collect(Collectors.toCollection
        (HashSet::new)));

    //set outgoing for each node
    e1.setOutgoing(Stream.of(a, b).collect(Collectors.toCollection
        (HashSet::new)));
    e2.setOutgoing(Stream.of(c, e2e6dDummy).collect(Collectors.toCollection
        (HashSet::new)));
    e3.setOutgoing(Stream.of(e3e6dDummy, e).collect(Collectors.toCollection
        (HashSet::new)));
    e6d.setOutgoing(Stream.of(d).collect(Collectors.toCollection
        (HashSet::new)));
    e4.setOutgoing(Stream.of(e4e7dDummy).collect(Collectors.toCollection
        (HashSet::new)));
    e6.setOutgoing(Stream.of(e6e7dDummy, e6e8dDummy).collect(Collectors.toCollection
        (HashSet::new)));
    e5.setOutgoing(Stream.of(e5e8dDummy, k).collect(Collectors.toCollection
        (HashSet::new)));
    e7d.setOutgoing(Stream.of(f).collect(Collectors.toCollection
        (HashSet::new)));
    e8d.setOutgoing(Stream.of(g).collect(Collectors.toCollection
        (HashSet::new)));
    e7.setOutgoing(Stream.of(e7e10dDummy).collect(Collectors.toCollection
        (HashSet::new)));
    e8.setOutgoing(Stream.of(e8e10dDummy).collect(Collectors.toCollection
        (HashSet::new)));
    e9.setOutgoing(Stream.of(e9e10dDummy).collect(Collectors.toCollection
        (HashSet::new)));
    e10d.setOutgoing(Stream.of(h).collect(Collectors.toCollection
        (HashSet::new)));


    graph.start = e1;
    graph.end = e10;
  }
}
