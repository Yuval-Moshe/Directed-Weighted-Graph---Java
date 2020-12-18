package api;

import gameClient.util.Point3D;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.support.hierarchical.Node;

import static org.junit.jupiter.api.Assertions.*;

/** Test Class for NodeData
 * List of all the test in this Test class:
 * basicFunctions():
 *  - Test 1.1 - create new node, the new node shouldn't be null.
 *  - Test 1.2 - node.getKey should be a number >= 0.
 *  - Test 1.3 - create a new node, the new node should have a different key from the previously created node.
 *  - Test 1.4 - set the weight of node_1 to 5.3, getWeight should be 5.3 .
 *  - Test 1.5 - set the tag of node_1 to 17, getTag should be 17 .
 *  - Test 1.6 - set the info of node_1 to "INFO", getInfo should be "INFO".
 *  - Test 1.7 - create a new 3D point with x=11, y=12, z=13 and set it as node_1's location, getLocation should be equals to that point.
 *  * **/
class NodeDataTest {

    @Test
    void basicFunction1(){
        //Test 1.1//
        NodeData node_1 = new NodeData();
        assertNotNull(node_1);

        //Test 1.2//
        assertTrue(node_1.getKey()>=0);

        //Test 1.3//
        NodeData node_2 = new NodeData();
        assertNotEquals(node_1.getKey(), node_2.getKey());

        //Test 1.4//
        node_1.setWeight(5.3);
        assertEquals(node_1.getWeight(), 5.3);

        //Test 1.5//
        node_1.setTag(17);
        assertEquals(node_1.getTag(), 17);

        //Test 1.6//
        node_1.setInfo("INFO");
        assertEquals(node_1.getInfo(), "INFO");

        //Test 1.7//
        geo_location gl = new Point3D(11,12,13);
        node_1.setLocation(gl);
        assertEquals(node_1.getLocation(), gl);


    }
}