import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

    /** Test Class for DWGraph_DS
     * List of all the test in this Test class:
     * basicFunctions():
     *  - Test 1.1 - adding 100 nodes:   getNode(i)!=null, nodeSize()=100, edgeSize()=O;
     *  - Test 1.2 - connect each node i with node i+1 (except for 99):   edgeSize()=99, hasEdge(i,i+1)!=null, hasEdge(i+1,i)==null;
     *  - Test 1.3 - removing edge between 2 connected nodes:   edgeSize()=98, hasEdge()==null;
     *  - Test 1.4 - trying to remove an edge between 2 unconnected nodes, should not change anything:  edgeSize()=98, mc_beforeChange == mc_afterChange;
     *  - Test 1.5 - trying to remove an edge from a->b when a and b are connected only from b->a: edgeSize()=98, mc_beforeChange == mc_afterChange;
     *  - Test 1.6 - removing node 80:   getEdge(79,80)==null, getEdge(80,81)==null;
     * connections():
     *  - Test 2.1 - create an edge from i->10 for each i<10, and an edge from 10->i for each i>10:   getE(10).containsAll(edge(10,i))==true, for each i>10;
     *  - Test 2.2 - remove an edge from 10->16:   getE(10).containsAll(edge(10,i))==false, for each i>10;
     *  - Test 2.3 - remove node 10, no edges should be from or to 10. for each i<10 getEdge(i,10)==null, for each i>10 getEdge(10,i)==null,
     * **/
public class DWGraph_DS_Test {

    @Test
    public void basicFunctions(){
        boolean flagA, flagB;
        int mc_beforeChange;
        int mc_afterChange;
        directed_weighted_graph DWG = new DWGraph_DS();
        for(int i=0; i<100; i++){
            node_data curr = new NodeData();
            DWG.addNode(curr);
        }
        //Test 1.1//
        flagA = true;
        for(int i=0; i<100; i++){
            flagA &= (DWG.getNode(i)!=null);
        }
        assertTrue(flagA);
        assertEquals(DWG.nodeSize(), 100);
        assertEquals(DWG.edgeSize(), 0);

        //Test 1.2//
        for(int i=0; i<99; i++){
            DWG.connect(i, i+1, 3);
        }
        flagA = true;
        flagB = true;
        for(int i=0; i<99; i++){
            flagA &= (DWG.getEdge(i,i+1)!=null);
            flagB &= (DWG.getEdge(i+1,i)==null);
        }
        assertEquals(DWG.edgeSize(), 99);
        assertTrue(flagA);
        assertTrue(flagB);

        //Test 1.3//
        DWG.removeEdge(0,1);
        assertNull(DWG.getEdge(0,1));
        assertEquals(DWG.edgeSize(), 98);

        //Test 1.4//
        mc_beforeChange = DWG.getMC();
        DWG.removeEdge(0,56);
        mc_afterChange = DWG.getMC();
        assertEquals(DWG.edgeSize(), 98);
        assertEquals(mc_beforeChange, mc_afterChange);

        //Test 1.5//
        mc_beforeChange = DWG.getMC();
        DWG.removeEdge(4,3);
        mc_afterChange = DWG.getMC();
        assertEquals(DWG.edgeSize(), 98);
        assertEquals(mc_beforeChange, mc_afterChange);

        //Test 1.6//
        DWG.removeNode(80);
        assertNull(DWG.getEdge(79,80));
        assertNull(DWG.getEdge(80,81));

    }

    @Test
    public void connections(){
        directed_weighted_graph DWG = new DWGraph_DS();
        ArrayList<edge_data> comp = new ArrayList<>();
        boolean flag;

        for(int i=0; i<20; i++){
            node_data curr = new NodeData();
            DWG.addNode(curr);
        }

        //Test 2.1//
        for(int i=0; i<20; i++){
            if(i<10){
                DWG.connect(i,10, 5);
            }
            else if(i>10){
                DWG.connect(10,i, 4);
                edge_data edge = DWG.getEdge(10,i);
                comp.add(edge);
            }
        }
        Collection<edge_data> edges = DWG.getE(10);
        flag = true;
        flag &= edges.containsAll(comp);
        flag &= comp.containsAll(edges);
        assertTrue(flag);

        //Test2.2//
        DWG.removeEdge(10, 16);
        flag = true;
        flag &= edges.containsAll(comp);
        flag &= comp.containsAll(edges);
        assertFalse(flag);

        //Test 2.3//
        DWG.removeNode(10);
        flag = true;
        for(int i=0; i<20; i++){
            if(i<10){
                flag &= (DWG.getEdge(i,10)==null);
            }
            else if(i>10){
                flag &= (DWG.getEdge(10,i)==null);
            }
        }
        assertTrue(flag);
    }


}
