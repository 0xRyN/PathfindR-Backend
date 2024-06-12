package fr.u_paris.gla.project.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.u_paris.gla.project.core.shortest_path_finder.DijkstraPathFinder;
import fr.u_paris.gla.project.core.shortest_path_finder.Graph;
import fr.u_paris.gla.project.core.shortest_path_finder.Node;
import fr.u_paris.gla.project.core.shortest_path_finder.INode;
import fr.u_paris.gla.project.core.shortest_path_finder.NodeNotFoundException;
import fr.u_paris.gla.project.core.shortest_path_finder.PathNotFoundException;

/**
 * Test class for {@link ShortestPathDijkstraFinder}.
 * It includes several scenarios to ensure the correctness of the Dijkstra's algorithm implementation for finding the shortest path in a graph.
 * 
 * @version 1.0
 *
 * @see Node
 * @see Graph
 * @see ShortestPathDijkstraFinder
 * @author: Zineddine CHIKHAOUI.
 */
public class ShortestPathDijkstraFinderTest {

    private Graph graph;

    /**
     * Setup method to initialize the graph before each test.
     */
    @BeforeEach
    public void setUp() {
        graph = new Graph();
    }
    
    @Test
    public void testNullGraph() throws NodeNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createSimpleGraph();
    	
    	Node source = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Source");
        Node destination = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Destination");
        
        assertThrows(IllegalArgumentException.class, () -> DijkstraPathFinder.computeShortestPath(null, source, destination));
    }
    
    @Test
    public void testNullSource() throws NodeNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createSimpleGraph();
    	
    	Node destination = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Destination");
        
        assertThrows(IllegalArgumentException.class, () -> DijkstraPathFinder.computeShortestPath(graph, null, destination));
    }
    
    @Test
    public void testNullTarget() throws NodeNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createSimpleGraph();
    	
    	Node source = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Source");
        
        assertThrows(IllegalArgumentException.class, () -> DijkstraPathFinder.computeShortestPath(graph, source, null));
    }
    
    @Test
    public void testSourceMissingFromGraph() throws NodeNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createSimpleGraph();
    	
    	Node missingSource = new Node("missing source");
    	Node destination = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Destination");
        
        assertThrows(NodeNotFoundException.class, () -> DijkstraPathFinder.computeShortestPath(graph, missingSource, destination));
    }
    
    @Test
    public void testDestMissingFromGraph() throws NodeNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createSimpleGraph();
    	
    	Node source = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Source");
        Node missingDest = new Node("missing dest");
    	
        assertThrows(NodeNotFoundException.class, () -> DijkstraPathFinder.computeShortestPath(graph, source, missingDest));
    }

    /**
     * Tests the algorithm's behavior when no path exists between the source and the destination nodes.
     * @throws PathNotFoundException 
     * @throws NodeNotFoundException 
     */
    @Test
    public void testPathFindingOnSimpleGraph() throws NodeNotFoundException, PathNotFoundException {
        graph = ShortestPathDijkstraFinderTestHelper.createSimpleGraph();

        Node source = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Source");
        Node destination = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Destination");
        
        int pathCost = DijkstraPathFinder.computeShortestPathCost(graph, source, destination);
		assertEquals(Integer.valueOf(25), pathCost);
    }
    
    @Test
    public void testWrongPath() throws NodeNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createSimpleGraph();

    	Node source = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Source");
        Node destination = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Destination");
        
        List<INode> path = new LinkedList<INode>();
        path.add(destination);
        path.add(source);
        
        assertThrows(PathNotFoundException.class, () -> DijkstraPathFinder.getPathCost(path));
    }
    
    @Test
    public void testInexistentPath() throws NodeNotFoundException, PathNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createSimpleGraph();

    	Node source = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Source");
        Node destination = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Destination");
        
        assertEquals(null, DijkstraPathFinder.computeShortestPath(graph, destination, source));
    }

    /**
     * Tests shortest path calculation in a graph containing only a single node.
     * @throws PathNotFoundException 
     * @throws NodeNotFoundException 
     */
    @Test
    public void testPathFindingInDisconnectedGraph() throws NodeNotFoundException, PathNotFoundException {
        graph = ShortestPathDijkstraFinderTestHelper.createDisconnectedGraph();

        Node source = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Source");
        Node isolated = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Isolated");
        
		assertEquals(null, DijkstraPathFinder.computeShortestPath(graph, source, isolated));
		
    }
    
    @Test
    public void testPathFindingInDisconnectedGraph2() throws NodeNotFoundException, PathNotFoundException {
        graph = ShortestPathDijkstraFinderTestHelper.createDisconnectedGraph();

        Node source = graph.getNodes().stream().filter(node -> node.getName().equals("Source")).findFirst().orElse(null);
        Node isolated = graph.getNodes().stream().filter(node -> node.getName().equals("Isolated")).findFirst().orElse(null);

        assertEquals(null, DijkstraPathFinder.computeShortestPath(graph, isolated, source));
		
    }
    
    @Test
    public void testPathDisconnectedGraph() throws NodeNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createDisconnectedGraph();
    	
    	Node source = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Source");
        Node isolated = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Isolated");
        
        List<INode> path = new LinkedList<INode>();
        path.add(source);
        path.add(isolated);
        
        assertThrows(PathNotFoundException.class, () -> DijkstraPathFinder.getPathCost(path));
    }
    
    @Test
    public void testPathDisconnectedGraph2() throws NodeNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createDisconnectedGraph();
    	
    	Node source = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Source");
        Node isolated = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Isolated");
        
        List<INode> path = new LinkedList<INode>();
        path.add(isolated);
        path.add(source);
        
        assertThrows(PathNotFoundException.class, () -> DijkstraPathFinder.getPathCost(path));
    }

    /**
     * Tests path finding in a graph with paths of varying weights to ensure the algorithm selects the minimum weight path.
     * @throws PathNotFoundException 
     * @throws NodeNotFoundException 
     */
    @Test
    public void testGraphWithSingleNode() throws NodeNotFoundException, PathNotFoundException {
        Node solo = new Node("Solo");
        graph.addNode(solo);

        assertEquals(Integer.valueOf(0), DijkstraPathFinder.computeShortestPathCost(graph, solo, solo));
    }

    /**
     * Tests the algorithm with a graph that has multiple paths with equivalent total weights from the start to the end node.
     * @throws PathNotFoundException 
     * @throws NodeNotFoundException 
     */
    @Test
    public void testGraphWithVaryingWeightPaths() throws NodeNotFoundException, PathNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createGraphWithVaryingWeightPaths();

    	Node start = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Start");
        Node end = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "End");
        
        assertEquals(Integer.valueOf(15), DijkstraPathFinder.computeShortestPathCost(graph, start, end));
    }

    /**
     * Tests shortest path calculation in a graph that contains loops and dead ends, challenging the algorithm's efficiency and accuracy.
     * @throws PathNotFoundException 
     * @throws NodeNotFoundException 
     */
    @Test
    public void testGraphWithMultipleEquivalentPaths() throws NodeNotFoundException, PathNotFoundException {
        graph = ShortestPathDijkstraFinderTestHelper.createGraphWithMultipleEquivalentPaths();
        
        Node start = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Start");
        Node end = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "End");
        
        assertEquals(Integer.valueOf(15), DijkstraPathFinder.computeShortestPathCost(graph, start, end));
    }

    /**
     * Tests shortest path calculation in a graph that contains loops and dead ends, challenging the algorithm's efficiency and accuracy.
     * @throws PathNotFoundException 
     * @throws NodeNotFoundException 
     */
    @Test
    public void testGraphWithLoopsAndDeadEnds() throws NodeNotFoundException, PathNotFoundException {
        graph = ShortestPathDijkstraFinderTestHelper.createGraphWithLoopsAndDeadEnds();
        
        Node start = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Start");
        Node end = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "End");
        
        assertEquals(Integer.valueOf(15), DijkstraPathFinder.computeShortestPathCost(graph, start, end));
    }

    @Test
    public void testGraphWithFiveNodes() throws NodeNotFoundException, PathNotFoundException {
        graph = ShortestPathDijkstraFinderTestHelper.createGraphWithFiveNodes();

        Node start = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "Start");
        Node end = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "End");
        
        assertEquals(Integer.valueOf(7), DijkstraPathFinder.computeShortestPathCost(graph, start, end));
    }
    
    /**
     * Test the algorithm to see if it returns the correct shortest path cost in graphs that contain cycles.
     * 
     * @throws NodeNotFoundException
     * @throws PathNotFoundException
     */
    @Test
    public void testTwoCycleGraphCost1() throws NodeNotFoundException, PathNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createTwoCycleGraph();

    	Node n1 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n1");
        Node n4 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n4");
        
        assertEquals(Integer.valueOf(3), DijkstraPathFinder.computeShortestPathCost(graph, n1, n4));
    }
    
    @Test
    public void testTwoCycleGraphPath1() throws NodeNotFoundException, PathNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createTwoCycleGraph();

        Node n1 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n1");
        Node n4 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n4");

        List<INode> actualPath = DijkstraPathFinder.computeShortestPath(graph, n1, n4);
        assertEquals(Integer.valueOf(3), actualPath.size());
        
        List<INode> expectedPath = 
        		ShortestPathDijkstraFinderTestHelper.buildPath(graph, List.of("n1", "n2", "n4")); 
        
        for (int i=0; i<3; i++) {
        	assertEquals(expectedPath.get(i), actualPath.get(i));
        }
        
    }

    @Test
    public void testTwoCycleGraphCost2() throws NodeNotFoundException, PathNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createTwoCycleGraph();
    	
    	Node n3 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n3");
        Node n4 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n4");
        assertEquals(Integer.valueOf(4), DijkstraPathFinder.computeShortestPathCost(graph, n3, n4));
    }
    
    @Test
    public void testTwoCycleGraphPath2() throws NodeNotFoundException, PathNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createTwoCycleGraph();

        Node n3 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n3");
        Node n4 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n4");

        List<INode> actualPath = DijkstraPathFinder.computeShortestPath(graph, n3, n4);
        assertEquals(Integer.valueOf(3), actualPath.size());
        
        List<INode> expectedPath = 
        		ShortestPathDijkstraFinderTestHelper.buildPath(graph, List.of("n3", "n2", "n4")); 
        
        for (int i=0; i<3; i++) {
        	assertEquals(expectedPath.get(i), actualPath.get(i));
        }
        
    }
    
    @Test
    public void testTwoCycleGraphCost3() throws NodeNotFoundException, PathNotFoundException {
        graph = ShortestPathDijkstraFinderTestHelper.createTwoCycleGraph();
        
        Node n2 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n2");
        Node n4 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n4");
        assertEquals(Integer.valueOf(8), DijkstraPathFinder.computeShortestPathCost(graph, n4, n2));
    }
    
    @Test
    public void testTwoCycleGraphPath3() throws NodeNotFoundException, PathNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createTwoCycleGraph();

        Node n2 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n2");
        Node n4 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n4");

        List<INode> actualPath = DijkstraPathFinder.computeShortestPath(graph, n4, n2);
        assertEquals(Integer.valueOf(3), actualPath.size());
        
        List<INode> expectedPath = 
        		ShortestPathDijkstraFinderTestHelper.buildPath(graph, List.of("n4", "n1", "n2")); 
        
        for (int i=0; i<3; i++) {
        	assertEquals(expectedPath.get(i), actualPath.get(i));
        }
        
    }
    
    @Test
    public void testTwoCycleGraphCost4() throws NodeNotFoundException, PathNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createTwoCycleGraph();

    	Node n1 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n1");
        Node n4 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n4");
        
        assertEquals(Integer.valueOf(6), DijkstraPathFinder.computeShortestPathCost(graph, n4, n1));
    }
    
    @Test
    public void testTwoCycleGraphPath4() throws NodeNotFoundException, PathNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createTwoCycleGraph();

        Node n1 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n1");
        Node n4 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n4");

        List<INode> actualPath = DijkstraPathFinder.computeShortestPath(graph, n4, n1);
        assertEquals(Integer.valueOf(2), actualPath.size());
        
        List<INode> expectedPath = 
        		ShortestPathDijkstraFinderTestHelper.buildPath(graph, List.of("n4", "n1")); 
        
        for (int i=0; i<2; i++) {
        	assertEquals(expectedPath.get(i), actualPath.get(i));
        }
        
    }
    
    @Test
    public void testTwoCycleGraphCost5() throws NodeNotFoundException, PathNotFoundException {
    	graph = ShortestPathDijkstraFinderTestHelper.createTwoCycleGraph();

    	Node n1 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n1");
        Node n3 = ShortestPathDijkstraFinderTestHelper.findNodeFromName(graph, "n3");
        
        assertEquals(Integer.valueOf(10), DijkstraPathFinder.computeShortestPathCost(graph, n3, n1));
    }
    
    @Test
    public void testNullPathCost() {
    	assertThrows(IllegalArgumentException.class, () -> DijkstraPathFinder.getPathCost(null));
    }
    
    @Test
    public void testEmptyPathCost() {
    	List<INode> path = Collections.emptyList();
    	assertThrows(IllegalArgumentException.class, () -> DijkstraPathFinder.getPathCost(path));
    }
    
    
}