import api.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/** Test Class for api.DWGraph_Algo
 * List of all the test in this Test class:
 * basicFunctions():
 * init() -
 * getGraph() -
 * copy() -
 * isConnected() - checks empty graph, graph with 1 node and graph with 2 nodes
 * isConnected2() - adding 5 nodes: add edges: 3->1 , 3->2, 3->4, 3->5 : graph is not connected
 * isConnected3() - adding 5 nodes: add edges: 1->3, 3->1 , 4->3, 3->4, 5->3, 3->5 : graph is not connected
 * isConnected4() - adding 5 nodes: check if graph is connected or not in after changes in graph edges
 *
 *
 *
 * **/
class DWGraph_AlgoTest {

    @Test
    void init() {
        directed_weighted_graph dwg = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        for(int i=0; i<5; i++){
            node_data node = new NodeData();
            dwg.addNode(node);
        }
        ga.init(dwg);

    }

    @Test
    void getGraph() {
    }

    @Test
    void copy() {
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
    void shortestPathDist() {
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
        for(node_data ni : path){
            System.out.println(ni.getKey());
        }
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
        for(node_data ni : path){
            System.out.println(ni.getKey());
        }

    }
    @Test
    void save() {
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
        ga.save("data\\A6");
        dw_graph_algorithms ga2 = new DWGraph_Algo();
        ga2.load("data\\A6");
        assertEquals(ga.getGraph().toString(),ga2.getGraph().toString());




    }

    @Test
    void load() {
        directed_weighted_graph dwg = new DWGraph_DS();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(dwg);
        ga.load("data\\A5");
    }
}