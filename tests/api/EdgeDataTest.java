package api;

import gameClient.util.Point3D;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.support.hierarchical.Node;

import static org.junit.jupiter.api.Assertions.*;

/** Test Class for EdgeData
 * List of all the test in this Test class:
 * basicFunctions1():
 *  - Test 1.1 - create new edge from 0 -> 1 with weight of 7.8, the new edge shouldn't be null.
 *  - Test 1.2 - edge.getSrc() should be 0.
 *  - Test 1.3 - edge.getDest() should be 1.
 *  - Test 1.4 - edge.getWeight() should be 7.8.
 *  - Test 1.5 - set the tag of the edge to 17, getTag should be 17 .
 *  - Test 1.6 - set the info of the edge to "INFO_1", getInfo should be "INFO_1".
 * basicFunctions2():
 *  - Test 1.1 - create new edge from 5 -> 3 with weight of 3.6, the new edge shouldn't be null.
 *  - Test 1.2 - edge.getSrc() should be 5.
 *  - Test 1.3 - edge.getDest() should be 3.
 *  - Test 1.4 - edge.getWeight() should be 3.6.
 *  - Test 1.5 - set the tag of the edge to -12, getTag should be -12 .
 *  - Test 1.6 - set the info of the edge to "INFO_2", getInfo should be "INFO_2".
 *  * **/
class EdgeDataTest {

    @Test
    void basicFunction1(){
        //Test 1.1//
        EdgeData edge = new EdgeData(0,1,7.8);
        assertNotNull(edge);

        //Test 1.2//
        assertEquals(edge.getSrc(), 0);

        //Test 1.3//
        assertEquals(edge.getDest(), 1);

        //Test 1.4//
        assertEquals(edge.getWeight(), 7.8);

        //Test 1.5//
        edge.setTag(17);
        assertEquals(edge.getTag(), 17);

        //Test 1.6//
        edge.setInfo("INFO_1");
        assertEquals(edge.getInfo(), "INFO_1");

    }
    @Test
    void basicFunction2(){
        //Test 1.1//
        EdgeData edge = new EdgeData(5,3,3.6);
        assertNotNull(edge);

        //Test 1.2//
        assertEquals(edge.getSrc(), 5);

        //Test 1.3//
        assertEquals(edge.getDest(), 3);

        //Test 1.4//
        assertEquals(edge.getWeight(), 3.6);

        //Test 1.5//
        edge.setTag(-12);
        assertEquals(edge.getTag(), -12);

        //Test 1.6//
        edge.setInfo("INFO_2");
        assertEquals(edge.getInfo(), "INFO_2");

    }
}