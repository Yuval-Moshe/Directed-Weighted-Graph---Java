package api;

import api.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/** Test Class for api.DWGraph_Algo
 * List of all the test in this Test class:
 * basicFunctions():
 *  - Test 1.1 - test init() and getGraph(), graphs_should be equal.
 *  - Test 1.2 - remove 1 node from dwg_comp, graphs shouldn't be equal.
 *  - Test 1.3 - test copy(), graphs should be equal.
 *  - Test 1.4 - test that the copy is indeed a deep copy, remove 1 node from the copied, graphs shouldn't be equal.
 *  - Test 1.5 - trying to copy() or getGraph() of an uninitialized graph should return null.
 * isConnected() - checks empty graph, graph with 1 node and graph with 2 nodes
 * isConnected2() - adding 5 nodes: add edges: 3->1 , 3->2, 3->4, 3->5 : graph is not connected
 * isConnected3() - adding 5 nodes: add edges: 1->3, 3->1 , 4->3, 3->4, 5->3, 3->5 : graph is not connected
 * isConnected4() - adding 5 nodes: check if graph is connected or not in after changes in graph edges
 * shortestPath1() -  a graph which 0 and 1 are connected by an edge from 0 to 1 with weight 10, but the shortest path is 0->2->3->1 with dist 8.
 * shortestPath1() -  shortest path from 0 to 5 is 0->1->3->4->5 with pathDist of 9.
 * shortestPath3() - a graph with 30 nodes wich each node i is connected to i+1 by an edge i-> i+1, shortestPath(29,0)==null, shortestPathDist(29,0)==-1;
 * saveNload() - create a graph with 6 nodes and 11 edges, save him to a file, load him back to another graph algo and see if both graph's are equal
 * **/
class DWGraph_AlgoTest {

    @Test
    void basicFunctions() {
        boolean flag;
        directed_weighted_graph dwg = new DWGraph_DS();
        directed_weighted_graph dwg_comp = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        int first_key = 0;
        for(int i=0; i<20; i++) {
            node_data curr = new NodeData();
            dwg.addNode(curr);
            dwg_comp.addNode(curr);
            if (i == 0) {
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        ga.init(dwg);
        directed_weighted_graph ga_dwg = ga.getGraph();
        //Test 1.1//
        assertTrue(ga_dwg.equals(dwg));

        //Test 1.2//
        dwg_comp.removeNode(first_key);
        assertFalse(ga_dwg.equals(dwg_comp));

        //Test 1.3//
        directed_weighted_graph dwg_copy = ga.copy();
        assertTrue(ga_dwg.equals(dwg_copy));

        //Test 1.4//
        int first_node = dwg_copy.getV().iterator().next().getKey();
        dwg_copy.removeNode(first_node);
        assertFalse(ga_dwg.equals(dwg_copy));

        //Test 1.5//
        dw_graph_algorithms dwg_empty = new DWGraph_Algo();
        assertNull(dwg_empty.getGraph());
        assertNull(dwg_empty.copy());



    }

    @Test
    void isConnected() {
        directed_weighted_graph dwg = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(dwg);
        assertTrue(ga.isConnected());

        node_data node = new NodeData();
        dwg.addNode(node);
        ga.init(dwg);
        assertTrue(ga.isConnected());

        node_data node2 = new NodeData();
        dwg.addNode(node2);
        ga.init(dwg);
        assertFalse(ga.isConnected());

        dwg.connect(node.getKey(), node2.getKey(), 2.5);
        ga.init(dwg);
        assertFalse(ga.isConnected());


        dwg.connect(node2.getKey(), node.getKey(),2.5);
        ga.init(dwg);
        assertTrue(ga.isConnected());
    }

    @Test
    void isConnected2() {
        directed_weighted_graph dwg = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        int first_key =0;
        for(int i=0; i<5; i++){
            node_data node = new NodeData();
            dwg.addNode(node);
            if(i==0){
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        ga.init(dwg);

        dwg.connect(first_key+2,first_key+0,5);
        dwg.connect(first_key+2,first_key+1,5);
        dwg.connect(first_key+2,first_key+3,5);
        dwg.connect(first_key+2,first_key+4,5);

        assertFalse(ga.isConnected());
    }

    @Test
    void isConnected3() {
        directed_weighted_graph dwg = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        int first_key =0;
        for(int i=0; i<5; i++){
            node_data node = new NodeData();
            dwg.addNode(node);
            if(i==0){
                first_key = dwg.getV().iterator().next().getKey();
            }
        }

        dwg.connect(first_key+2,first_key+0,5);
        dwg.connect(first_key+0,first_key+2,5);
        dwg.connect(first_key+2,first_key+3,5);
        dwg.connect(first_key+3,first_key+2,5);
        dwg.connect(first_key+2,first_key+4,5);
        dwg.connect(first_key+4,first_key+2,5);
        ga.init(dwg);
        assertFalse(ga.isConnected());
    }

    @Test
    void isConnected4() {
        directed_weighted_graph dwg = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        int first_key =0;
        for(int i=0; i<5; i++){
            node_data node = new NodeData();
            dwg.addNode(node);
            if(i==0){
                first_key = dwg.getV().iterator().next().getKey();
            }
        }

        dwg.connect(first_key+1,first_key+4,5);
        dwg.connect(first_key+4,first_key+3,5);
        dwg.connect(first_key+3,first_key+2,5);
        dwg.connect(first_key+2,first_key+0,5);
        dwg.connect(first_key+0,first_key+1,5);
        ga.init(dwg);

        assertTrue(ga.isConnected());

        dwg.removeEdge(first_key+0,first_key+1);
        ga.init(dwg);

        assertFalse(ga.isConnected());

        dwg.connect(first_key+0,first_key+4,3);
        dwg.connect(first_key+4,first_key+1,9);
        ga.init(dwg);

        assertTrue(ga.isConnected());
    }


    @Test
    void shortestPath1() {
        directed_weighted_graph dwg = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        int first_key =0;
        for(int i=0; i<5; i++){
            node_data node = new NodeData();
            dwg.addNode(node);
            if(i==0){
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key+0,first_key+1,10);
        dwg.connect(first_key+0,first_key+2,3);
        dwg.connect(first_key+2,first_key+3,4);
        dwg.connect(first_key+3,first_key+1,1);
        ga.init(dwg);
        List<node_data> path = ga.shortestPath(first_key+0,first_key+1);
        List<node_data> comp = new ArrayList<>();
        comp.add(dwg.getNode(first_key+0));
        comp.add(dwg.getNode(first_key+2));
        comp.add(dwg.getNode(first_key+3));
        comp.add(dwg.getNode(first_key+1));
        boolean flag = true;
        for(int i=0; i<path.size(); i++){
            flag &= path.get(i).getKey()==comp.get(i).getKey();
        }
        assertTrue(flag);
        assertEquals(ga.shortestPathDist(first_key+0,first_key+1), 8);
    }

    @Test
    void shortestPath2() {
        directed_weighted_graph dwg = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        int first_key =0;
        for(int i=0; i<6; i++){
            node_data node = new NodeData();
            dwg.addNode(node);
            if(i==0){
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key+0,first_key+1,2);
        dwg.connect(first_key+1,first_key+0,1);
        dwg.connect(first_key+1,first_key+3,3);
        dwg.connect(first_key+1,first_key+2,8);
        dwg.connect(first_key+1,first_key+5,10);
        dwg.connect(first_key+2,first_key+1,1);
        dwg.connect(first_key+2,first_key+4,3);
        dwg.connect(first_key+3,first_key+0,1);
        dwg.connect(first_key+3,first_key+1,1);
        dwg.connect(first_key+3,first_key+4,2);
        dwg.connect(first_key+4,first_key+5,2);
        ga.init(dwg);
        List<node_data> path = ga.shortestPath(first_key+0,first_key+5);
        List<node_data> comp = new ArrayList<>();
        comp.add(dwg.getNode(first_key+0));
        comp.add(dwg.getNode(first_key+1));
        comp.add(dwg.getNode(first_key+3));
        comp.add(dwg.getNode(first_key+4));
        comp.add(dwg.getNode(first_key+5));
        boolean flag = true;
        for(int i=0; i<path.size(); i++){
            flag &= path.get(i).getKey()==comp.get(i).getKey();
        }
        assertTrue(flag);
        assertEquals(ga.shortestPathDist(first_key+0, first_key+5),9);
    }
    @Test
    void shortestPath3() {
        directed_weighted_graph dwg = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        int first_key = 0;
        for (int i = 0; i < 30; i++) {
            node_data node = new NodeData();
            dwg.addNode(node);
            if (i == 0) {
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        for (int i = 0; i < 29; i++) {
            dwg.connect(first_key+i,first_key+i+1, 5);
        }
        ga.init(dwg);
        assertNull(ga.shortestPath(first_key+29,first_key+0));
        assertEquals(ga.shortestPathDist(first_key+29,first_key+0),-1);

    }


    @Test
    void saveNload() {
        directed_weighted_graph dwg = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        int first_key =0;
        for(int i=0; i<6; i++){
            node_data node = new NodeData();
            dwg.addNode(node);
            if(i==0){
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key+0,first_key+1,2);
        dwg.connect(first_key+1,first_key+0,1);
        dwg.connect(first_key+1,first_key+3,3);
        dwg.connect(first_key+1,first_key+2,8);
        dwg.connect(first_key+1,first_key+5,10);
        dwg.connect(first_key+2,first_key+1,1);
        dwg.connect(first_key+2,first_key+4,3);
        dwg.connect(first_key+3,first_key+0,1);
        dwg.connect(first_key+3,first_key+1,1);
        dwg.connect(first_key+3,first_key+4,2);
        dwg.connect(first_key+4,first_key+5,2);
        ga.init(dwg);
        ga.save("data\\save_test");
        dw_graph_algorithms ga2 = new DWGraph_Algo();
        ga2.load("data\\save_test");
        assertEquals(ga.getGraph(),ga2.getGraph());

    }
}